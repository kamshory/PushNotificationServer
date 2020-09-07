package com.planetbiru.pushserver.notification;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.planetbiru.pushserver.client.Client;
import com.planetbiru.pushserver.client.Device;
import com.planetbiru.pushserver.code.ConstantString;
import com.planetbiru.pushserver.code.DatabaseTable;
import com.planetbiru.pushserver.code.JsonKey;
import com.planetbiru.pushserver.code.ResponseCode;
import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.database.Database;
import com.planetbiru.pushserver.database.QueryBuilder;
import com.planetbiru.pushserver.database.DatabaseTypeException;
import com.planetbiru.pushserver.evaluator.SocketkBreaker;
import com.planetbiru.pushserver.utility.SocketIO;
import com.planetbiru.pushserver.utility.Utility;

/**
 * NotificationHandler
 * @author Kamshory, MT
 *
 */
public class NotificationHandler extends Thread
{
	private static final String COMMAND = "Command";
	private static final String BEARER = "Bearer ";
	/**
	 * Client socket
	 */
	private Socket socket = new Socket();
	/**
	 * Request ID
	 */
	private long requestID = 0;
	/**
	 * Device ID
	 */
	private String deviceID = "";
	/**
	 * API ID
	 */
	private long apiID = 0;
	/**
	 * Group ID
	 */
	private long groupID = 0;
	/**
	 * Token for PushClien when request is valid
	 */
	private String token = "";
	/**
	 * Flag that client is still connected
	 */
	private boolean connected = false;
	/**
	 * Flag that process is still running
	 */
	private boolean running = true;
	/**
	 * Default constructor
	 */
	public NotificationHandler()
	{
		
	}
	/**
	 * Constructor with client socket, request ID, and database object 
	 * @param socket Client socket accepted 
	 * @param requestID Request ID. It will be auto increment
	 */
	public NotificationHandler(Socket socket, long requestID)
	{
		this.setSocket(socket);
		this.requestID = requestID;
	}

	/**
	 * Override run method
	 */
	@Override
	public void run()
	{
		try 
		{
			this.acceptRequest();
		} 
		catch(NoSuchAlgorithmException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		} 
		catch (NotificationException e) 
		{
			/**
			 * Do nothing
			 */
		}
	}
	/**
	 * Process the request
	 * @throws NoSuchAlgorithmException  if algorithm is not found
	 * @throws NotificationException if notification data is invalid
	 */
	private void acceptRequest() throws NoSuchAlgorithmException, NotificationException
	{
		SocketIO socketIO = new SocketIO(this.socket);
		SocketkBreaker socketkBreaker = new SocketkBreaker(this, Config.getWaitForAnswer());
		socketkBreaker.start();
		Notification notification;
		this.token = Utility.sha1(Math.random()*1000000+"");
		String firstCommand = "";
		try 
		{
			socketIO.read();
			String message = socketIO.getBody();
			String[] headers = socketIO.getHeaders();
			firstCommand = Utility.getFirst(headers, NotificationHandler.COMMAND).toLowerCase().trim();
			if(firstCommand.equals("ping"))
			{
				this.replyPing();
				this.socket.close();
			}
			else
			{
				if(message.length() > 15)
				{
					this.deviceID = this.getDeviceID(message);
					notification = new Notification(this.requestID);
					String authorization = "";
					authorization = socketIO.getFirst(headers, "Authorization");
					if(authorization.startsWith(NotificationHandler.BEARER))
					{
						authorization = authorization.substring(NotificationHandler.BEARER.length());
					}
					try 
					{
						if(notification.authentication(authorization))
						{
							notification.getHashPasswordClient();
							this.apiID = notification.getApiID();
							this.groupID = notification.getGroupID();
							Device device = new Device(this.deviceID, this.requestID, this.socket);
							Client.add(this.deviceID, this.apiID, this.groupID, device, this.requestID);									
							NotificationChecker notificationChecker = new NotificationChecker(this.socket, this, Config.getInspectionInterval());
							notificationChecker.start();					
							String[] heads;
							String body = "";
							while(this.socket.isConnected() && !this.socket.isClosed() && this.isRunning())
							{
								try 
								{
									heads = this.head().split("\\r?\\n");
									body = this.body(heads);
									if(body.length() > 2)
									{
										this.processRequest(heads, body);
									}
								} 
								catch (IOException | JSONException e) 
								{
									if(Config.isPrintStackTrace()) 
									{
										e.printStackTrace();
									}
									break;
								}
							}
							Client.remove(this.deviceID, this.apiID, this.groupID, this.requestID);
						}
						else
						{
							this.socket.close();
							Client.remove(this.deviceID, this.apiID, this.groupID, this.requestID);
						}
					} 
					catch (UnsupportedOperationException | ClassCastException | NullPointerException | SQLException e) 
					{
						this.socket.close();
						Client.remove(this.deviceID, this.apiID, this.groupID, this.requestID);
						if(Config.isPrintStackTrace()) 
						{
							e.printStackTrace();
						}
					} 
					catch (DatabaseTypeException e) 
					{
						if(Config.isPrintStackTrace()) 
						{
							e.printStackTrace();
						}
					}
				}
				else
				{
					this.socket.close();
					Client.remove(this.deviceID, this.apiID, this.groupID, this.requestID);
					throw new NotificationException("Request not contains device ID");
				}
			}
		} 
		catch (IOException | JSONException e1) 
		{
			try 
			{
				this.socket.close();
			} 
			catch (IOException e) 
			{
				if(Config.isPrintStackTrace()) 
				{
					e.printStackTrace();
				}
			}
			try
			{
				Client.remove(this.deviceID, this.apiID, this.groupID, this.requestID);
			}
			catch(UnsupportedOperationException | ClassCastException | NullPointerException e2)
			{
				if(Config.isPrintStackTrace()) 
				{
					e2.printStackTrace();
				}
			}
			if(Config.isPrintStackTrace()) 
			{
				e1.printStackTrace();
			}
		}
	}
	/**
	 * Reply PING to the client
	 * @throws IOException if any IO errors
	 * @throws JSONException if any JSON errors
	 */
	private void replyPing() throws IOException, SocketException, JSONException 
	{
		SocketIO socketIO = new SocketIO(this.socket);
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader(NotificationHandler.COMMAND, "ping-reply");
		socketIO.addRequestHeader(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		JSONObject jo = new JSONObject();
		jo.put(JsonKey.SERVICE, "alive");
		jo.put(JsonKey.DATABASE, "connected");
		socketIO.write(jo.toString());
	}
	/**
	 * Get device ID
	 * @param message Raw message
	 * @return Device ID
	 */
	private String getDeviceID(String message) throws JSONException
	{
		JSONObject jo;
		jo = new JSONObject(message);
		return jo.optString(JsonKey.DEVICE_ID, "").trim();
	}
	/**
	 * Download notification that sent while PushClient is offline
	 * @throws IllegalArgumentException if parameter is invalid 
	 * @throws SQLException if any SQL errors
	 * @throws IOException if any IO errors
	 * @throws DatabaseTypeException if database type not supported 
	 * @throws NoSuchPaddingException if cipher text not multiply of 16 character
	 * @throws NoSuchAlgorithmException if algorithm not found
	 * @throws InvalidKeyException if invalid key
	 * @throws BadPaddingException if cipher text not multiply of 16 character
	 * @throws IllegalBlockSizeException if cipher text not multiply of 16 character
	 * @throws SocketException if any socket errors
	 */
	public void downloadLastNotification() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalArgumentException, SocketException, IOException, SQLException, DatabaseTypeException, IllegalBlockSizeException, BadPaddingException
	{
		SocketIO socketIO = new SocketIO(this.socket);
		Notification notification = new Notification(this.requestID);
		String offileNotification = "";
		while(notification.countNotification(this.apiID, this.deviceID, this.groupID) > 0)
		{
			offileNotification = notification.select(this.apiID, this.deviceID, this.groupID, Config.getLimitNotification()).toString();
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
			socketIO.addRequestHeader(NotificationHandler.COMMAND, "notification");
			if(socketIO.write(offileNotification))
			{
				this.markAsSent(notification.getOfflineID());
			}
		}
	}
	/**
	 * Download last notification deletion log
	 * @throws SQLException if any SQL errors
	 * @throws IOException if any IO errors
	 * @throws DatabaseTypeException if database type not supported 
	 * @throws NoSuchPaddingException if cipher text size not multiply of 16 bytes
	 * @throws NoSuchAlgorithmException if algorithm not found
	 * @throws InvalidKeyException if key is invalid
	 * @throws BadPaddingException if cipher text size not multiply of 16 bytes
	 * @throws IllegalBlockSizeException if cipher text size not multiply of 16 bytes
	 * @throws SocketException if any socket errors
	 */
	public void downloadLastDeleteLog() throws SQLException, IOException, SocketException, DatabaseTypeException, InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{
		SocketIO socketIO = new SocketIO(this.socket);
		Notification notification = new Notification(this.requestID);
		JSONArray trash;
		String offileNotification = "";
		while(notification.countDeletionLog(this.apiID, this.deviceID, this.groupID) > 0)
		{
			trash = notification.selectDeletionLog(this.apiID, this.deviceID, this.groupID, Config.getLimitTrash());
			offileNotification = trash.toString();
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
			socketIO.addRequestHeader(NotificationHandler.COMMAND, "delete-notification");
			if(socketIO.write(offileNotification))
			{
				notification.clearDeleteLog(this.apiID, trash);
			}
		}
	}
	/**
	 * Get the header of the request
	 * @return String contains header of the request
	 * @throws IOException if any IO errors
	 */
	private String head() throws IOException
	{
		byte[] buf;
		buf = new byte[1];
		StringBuilder buff = new StringBuilder();
		boolean read = true;
		try 
		{
			int nByte = 0;
			do
			{
				nByte = this.socket.getInputStream().read(buf);
				buff.append(new String(buf));
				if(buff.toString().contains("\r\n\r\n"))
				{
					read = false;
				}
			}
			while(read && nByte > 0);
		} 
		catch (IOException e) 
		{
			this.socket.close();
		}
		return buff.toString();
	}
	/**
	 * Get the body of the request
	 * @param heads Array string contains request header
	 * @return String contains body of the request
	 * @throws IOException if any IO errors
	 */	
	private String body(String[] heads) throws IOException, NumberFormatException 
	{
		String contentLength = Utility.getFirst(heads, "Content-Length");
		contentLength = contentLength.trim();
		if(contentLength.equals(""))
		{
			contentLength = "0";
		}
		StringBuilder buff = new StringBuilder();
		long length = Long.parseLong(contentLength);
		int buf;
		long i;
		try 
		{
			for(i = 0; i < length; i++)
			{
				buf = this.socket.getInputStream().read();
				buff.append(String.format("%c", buf));
			}
		} 
		catch (IOException e) 
		{
			this.socket.close();
		}
		return buff.toString();
	}
	/**
	 * Process request
	 * @param headers Header
	 * @param body Entity body
	 * @throws SQLException if any SQL errors
	 * @throws IOException if ant IO errors
	 * @throws DatabaseTypeException if database type not supported 
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 */
	private void processRequest(String[] headers, String body) throws JSONException, SQLException, IOException, DatabaseTypeException, NoSuchAlgorithmException
	{
		JSONObject jo;
		jo = new JSONObject(body);
		String lDeviceID = jo.optString(JsonKey.DEVICE_ID, "");
		String command = Utility.getFirst(headers, NotificationHandler.COMMAND);
		boolean success = false;
		if(command.compareToIgnoreCase("register-device") == 0)
		{
			try
			{
				success = this.registerDevice(lDeviceID);
				if(success)
				{
					this.onRegisterDeviceSuccess(lDeviceID, ResponseCode.REGISTRATION_DEVICE_SUCCESS, "Register device success");
				}
				else
				{
					this.onRegisterDeviceError(lDeviceID, ResponseCode.DEVICE_ALREADY_EXISTS, ConstantString.FAILED, "Fail to register device");
				}
			}
			catch(NotificationException e1)
			{
				this.onRegisterDeviceError(lDeviceID, ResponseCode.INTERNAL_SERVER_ERROR, ConstantString.FAILED, e1.getMessage());
			}
		}
		else if(command.compareToIgnoreCase("unregister-device") == 0)
		{
			try
			{
				success = this.unregisterDevice(lDeviceID);
				if(success)
				{
					this.onUnregisterDeviceSuccess(lDeviceID, ResponseCode.UNREGISTRATION_DEVICE_SUCCESS, "Unregister device success");
				}
				else
				{
					this.onUnregisterDeviceError(lDeviceID, ResponseCode.DEVICE_NOT_EXISTS, ConstantString.FAILED, "Fail to unregister device");
				}
			}
			catch(NotificationException e1)
			{
				this.onUnregisterDeviceError(lDeviceID, ResponseCode.INTERNAL_SERVER_ERROR, ConstantString.FAILED, e1.getMessage());
			}
		}
	}
	/**
	 * Send unregister device error message to PushClient
	 * @param deviceID Device ID
	 * @param responseCode Response code
	 * @param message Message
	 * @param cause Cause
	 * @throws IOException if any IO errors
	 */
	private void onUnregisterDeviceError(String deviceID, int responseCode, String message, String cause) throws IOException, SocketException, JSONException 
	{
		SocketIO socketIO = new SocketIO(this.socket);
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		socketIO.addRequestHeader(NotificationHandler.COMMAND, "unregister-device-error");
		JSONObject jo = new JSONObject();
		jo.put(JsonKey.RESPONSE_CODE, responseCode);
		jo.put(JsonKey.DEVICE_ID, deviceID);
		jo.put(JsonKey.MESSAGE, message);
		jo.put("cause", cause);
		socketIO.write(jo.toString());
	}
	/**
	 * Send unregister device success message to PushClient
	 * @param deviceID Device ID
	 * @param responseCode Response code
	 * @param message Message
	 * @throws IOException if any IO errors
	 */
	private void onUnregisterDeviceSuccess(String deviceID, int responseCode, String message) throws IOException, SocketException, JSONException 
	{
		SocketIO socketIO = new SocketIO(this.socket);
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		socketIO.addRequestHeader(NotificationHandler.COMMAND, "unregister-device-success");
		JSONObject jo = new JSONObject();
		jo.put(JsonKey.RESPONSE_CODE, responseCode);
		jo.put(JsonKey.DEVICE_ID, deviceID);
		jo.put(JsonKey.MESSAGE, message);
		socketIO.write(jo.toString());
	}
	/**
	 * Send register device error message to PushClient
	 * @param deviceID Device ID
	 * @param responseCode Response code
	 * @param message Message
	 * @param cause Cause
	 * @throws IOException if any IO errors
	 */
	private void onRegisterDeviceError(String deviceID, int responseCode, String message, String cause) throws IOException, SocketException, JSONException 
	{
		SocketIO socketIO = new SocketIO(this.socket);
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		socketIO.addRequestHeader(NotificationHandler.COMMAND, "register-device-error");
		JSONObject jo = new JSONObject();
		jo.put(JsonKey.RESPONSE_CODE, responseCode);
		jo.put(JsonKey.DEVICE_ID, deviceID);
		jo.put(JsonKey.MESSAGE, message);
		jo.put("cause", cause);
		socketIO.write(jo.toString());
	}
	/**
	 * Send register device error message to PushClient
	 * @param deviceID Device ID
	 * @param responseCode Response code
	 * @param message Message
	 * @throws IOException if any IO errors
	 */
	private void onRegisterDeviceSuccess(String deviceID, int responseCode, String message) throws IOException, SocketException, JSONException 
	{
		SocketIO socketIO = new SocketIO(this.socket);
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		socketIO.addRequestHeader(NotificationHandler.COMMAND, "register-device-success");
		JSONObject jo = new JSONObject();
		jo.put(JsonKey.RESPONSE_CODE, responseCode);
		jo.put(JsonKey.DEVICE_ID, deviceID);
		jo.put(JsonKey.MESSAGE, message);
		socketIO.write(jo.toString());
	}
	/**
	 * Register device
	 * @param deviceID Device ID
	 * @return true if success and false if failed
	 * @throws NotificationException if device already exists
	 * @throws SQLException if any SQL errors
	 * @throws IOException if any IO errors
	 * @throws DatabaseTypeException if database type not supported 
	 * @throws IllegalArgumentException if any illegal arguments
	 * @throws NullPointerException if any NULL pointer
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 */
	private boolean registerDevice(String deviceID) throws NotificationException, SQLException, IOException, SocketException, JSONException, DatabaseTypeException, NoSuchAlgorithmException, NullPointerException, IllegalArgumentException 
	{
		boolean success = false;
		ResultSet rs = null;
		Database database1 = new Database(Config.getDatabaseConfig1());
		Statement stmt = null;
		try
		{
			database1.connect();
			QueryBuilder query1 = new QueryBuilder(database1.getDatabaseType());
			String address = this.socket.getInetAddress().getHostAddress().replace("/", "").trim();
			deviceID = query1.escapeSQL(deviceID);
			String lToken = query1.escapeSQL(this.token);
			address = query1.escapeSQL(address);
			String sqlSelect = query1.newQuery()
					.select("device_id")
					.from(Config.getTablePrefix()+DatabaseTable.CLIENT)
					.where("device_id = '"+deviceID+"' and api_id = "+this.apiID+" ")
					.toString();
			stmt = database1.getDatabaseConnection().createStatement();
			rs = stmt.executeQuery(sqlSelect);
			if(!rs.isBeforeFirst())
			{
				String slqInsert = query1.newQuery()
						.insert()
						.into(Config.getTablePrefix()+DatabaseTable.CLIENT)
						.fields("(device_id, api_id, last_token, last_time, last_ip, time_create)")
						.values("('"+deviceID+"', "+this.apiID+", '"+lToken+"', now(), '"+address+"', now())")
						.toString();
				Utility.closeResource(stmt);
				stmt = database1.getDatabaseConnection().createStatement();
				stmt.execute(slqInsert);
				success = true;
			}
			else
			{
				throw new NotificationException("Device already exists");
			}
		}
		catch(SQLException | DatabaseTypeException | NullPointerException | IllegalArgumentException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
		finally {
			Utility.closeResource(rs);
			Utility.closeResource(stmt);
			database1.disconnect();
		}
		return success;
	}
	/**
	 * Unregister device
	 * @param deviceID Device ID
	 * @return true if success and false if failed
	 * @throws NotificationException if device not is found
	 * @throws SQLException if any SQL errors
	 * @throws DatabaseTypeException if database type not supported 
	 */
	private boolean unregisterDevice(String deviceID) throws NotificationException
	{
		boolean success = false;
		ResultSet rs = null;
		Database database1 = new Database(Config.getDatabaseConfig1());
		Statement stmt = null;
		try
		{
			database1.connect();
			QueryBuilder query1 = new QueryBuilder(database1.getDatabaseType());
			deviceID = query1.escapeSQL(deviceID);
			String sqlSelect = query1.newQuery()
					.select("device_id")
					.from(Config.getTablePrefix()+DatabaseTable.CLIENT)
					.where("device_id = '"+deviceID+"' and api_id = "+this.apiID+" ")
					.toString();
			stmt = database1.getDatabaseConnection().createStatement();
			rs = stmt.executeQuery(sqlSelect);
			if(rs.isBeforeFirst())
			{
				String slqDelete = query1.newQuery()
						.delete()
						.from(Config.getTablePrefix()+DatabaseTable.CLIENT)
						.where("device_id = '"+deviceID+"' and api_id = "+this.apiID+" ")
						.toString();
				Utility.closeResource(stmt);
				stmt = database1.getDatabaseConnection().createStatement();
				stmt.execute(slqDelete);
				success = true;
			}
			else
			{
				throw new NotificationException("Device not exists");
			}
		}
		catch(SQLException | DatabaseTypeException | NullPointerException | IllegalArgumentException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
		finally {
			Utility.closeResource(rs);
			Utility.closeResource(stmt);
			database1.disconnect();
		}
		return success;
	}
	/**
	 * Mark notification as sent
	 * @param ids Array list contains notification ID
	 * @throws SQLException if any SQL errors
	 * @throws DatabaseTypeException if database type not supported 
	 */
	private void markAsSent(List<String> ids) throws SQLException, DatabaseTypeException 
	{
		Database database1 = new Database(Config.getDatabaseConfig1());
		Statement stmt = null;
		try
		{
			database1.connect();
			StringBuilder offline = new StringBuilder();
			int max = ids.size();
			int i;
			for(i = 0; i < max; i++)
			{
				if(i > 0)
				{
					offline.append(",");
				}
				offline.append(ids.get(i));			
			}
			if(max > 0)
			{
				QueryBuilder query1 = new QueryBuilder(database1.getDatabaseType());
				String sqlCommand = query1.newQuery() 
						.update(Config.getTablePrefix()+"notification")
						.set("is_sent = 1, time_sent = now()")
						.where("notification_id in ("+offline.toString()+")")
						.toString();
				stmt = database1.getDatabaseConnection().createStatement();
				stmt.execute(sqlCommand);
			}		
		}
		catch(SQLException | DatabaseTypeException | NullPointerException | IllegalArgumentException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
		finally {
			Utility.closeResource(stmt);
			database1.disconnect();
		}
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}
