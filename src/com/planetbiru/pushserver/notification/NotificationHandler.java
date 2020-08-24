package com.planetbiru.pushserver.notification;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.planetbiru.code.ResponseCode;
import com.planetbiru.pushserver.client.Client;
import com.planetbiru.pushserver.client.ClientException;
import com.planetbiru.pushserver.client.Device;
import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.database.Database;
import com.planetbiru.pushserver.database.QueryBuilder;
import com.planetbiru.pushserver.database.DatabaseTypeException;
import com.planetbiru.pushserver.evaluator.ConnectionEvaluator;
import com.planetbiru.pushserver.evaluator.SocketkBreaker;
import com.planetbiru.pushserver.utility.Encryption;
import com.planetbiru.pushserver.utility.QueryParserException;
import com.planetbiru.pushserver.utility.SocketIO;
import com.planetbiru.pushserver.utility.Utility;

/**
 * NotificationHandler
 * @author Kamshory, MT
 *
 */
public class NotificationHandler extends Thread
{
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
	 * String contains question for second authentication
	 */
	private String question = "";
	/**
	 * String contains answer for second authentication
	 */
	private String answer = "";
	/**
	 * Hash password stored in the database
	 */
	private String hashPasswordClient = "";
	/**
	 * Token for PushClien when request is valid
	 */
	private String token = "";
	/**
	 * ClientEvaluator to evaluate the connection on the future
	 */
	private ConnectionEvaluator connectionEvaluator = new ConnectionEvaluator();
	/**
	 * Flag that client is still connected
	 */
	private boolean connected = false;
	/**
	 * Flag that process is still running
	 */
	private boolean running = true;
	/**
	 * Encryption key
	 */
	private String key = "";
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
	 * @param database1 Primary Database object
	 * @param database2 Secondary Database object
	 * @param database3 Tertiary Database object
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
		catch (QueryParserException e) 
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
		catch(NoSuchAlgorithmException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		} 
		catch (JSONException e) 
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
	 * Generate secure key
	 * @param deviceID Device ID
	 * @param hashPassword Hash password
	 * @return Key generated
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 */
	private String generateSecureKey(String deviceID, String hashPassword) throws NoSuchAlgorithmException
	{
		return Utility.sha1(deviceID+Utility.random(111111, 999999)+hashPassword);
	}
	/**
	 * Process the request
	 * @throws QueryParserException if any errors while parse the query string
	 * @throws NoSuchAlgorithmException  if algorithm is not found
	 * @throws NotificationException if notification data is invalid
	 * @throws JSONException if any JSON errors
	 */
	private void acceptRequest() throws QueryParserException, NoSuchAlgorithmException, NotificationException, JSONException
	{
		SocketIO socketIO = new SocketIO(this.getSocket());
		SocketkBreaker socketkBreaker = new SocketkBreaker(this, Config.getWaitForAnswer());
		socketkBreaker.start();
		boolean validClient = false;
		Notification notification;
		this.token = Utility.sha1(Math.random()*1000000+"");
		String firstCommand = "";
		try 
		{
			socketIO.read();
			String message = socketIO.getBody();
			String[] headers = socketIO.getHeaders();
			firstCommand = Utility.getFirst(headers, "Command").toLowerCase().trim();
			if(firstCommand.equals("ping"))
			{
				this.replyPing();
				this.getSocket().close();
			}
			else
			{
				if(message.length() > 15)
				{
					this.deviceID = this.getDeviceID(message);
					notification = new Notification(this.requestID);
					String authorization = "";
					authorization = socketIO.getFirst(headers, "Authorization");
					if(authorization.startsWith("Bearer "))
					{
						authorization = authorization.substring("Bearer ".length());
					}
					try 
					{
						if(notification.authentication(authorization))
						{
							this.hashPasswordClient = notification.getHashPasswordClient();
							this.apiID = notification.getApiID();
							this.groupID = notification.getGroupID();
							validClient = this.validatingClient();
							if(validClient)
							{
								Device device;
								if(Config.isContentSecure())
								{
									/**
									 * Add security here
									 */
									this.key  = this.generateSecureKey(this.deviceID, this.hashPasswordClient);
									this.sendKey(this.key);
									device = new Device(this.deviceID, this.requestID, this.getSocket(), this.key, this.hashPasswordClient);
								}
								else
								{
									device = new Device(this.deviceID, this.requestID, this.getSocket());
								}
								Client.add(this.deviceID, this.apiID, this.groupID, device, this.requestID);									
								NotificationChecker notificationChecker = new NotificationChecker(this.getSocket(), this, Config.getInspectionInterval());
								notificationChecker.start();					
								String[] heads;
								String body = "";
								while(this.getSocket().isConnected() && !this.getSocket().isClosed() && this.isRunning())
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
							}
							Client.remove(this.deviceID, this.apiID, this.groupID, this.requestID);
						}
						else
						{
							this.getSocket().close();
							Client.remove(this.deviceID, this.apiID, this.groupID, this.requestID);
						}
					} 
					catch (UnsupportedOperationException | ClassCastException | NullPointerException | SQLException e) 
					{
						this.getSocket().close();
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
					this.getSocket().close();
					Client.remove(this.deviceID, this.apiID, this.groupID, this.requestID);
					throw new NotificationException("Request not contains device ID");
				}
			}
		} 
		catch (IOException e1) 
		{
			try 
			{
				this.getSocket().close();
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
		SocketIO socketIO = new SocketIO(this.getSocket());
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader("Command", "ping-reply");
		socketIO.addRequestHeader("Content-Type", "application/json");
		JSONObject jo = new JSONObject();
		jo.put("service", "alive");
		jo.put("database", "connected");
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
		return jo.optString("deviceID", "").trim();
	}
	/**
	 * Download notification that sent while PushClient is offline
	 * @throws JSONException if any JSON errors
	 * @throws SQLException if any SQL errors
	 * @throws IOException if any IO errors
	 * @throws DatabaseTypeException if database type not supported 
	 * @throws NoSuchPaddingException if cipher text not multiply of 16 character
	 * @throws NoSuchAlgorithmException if algorithm not found
	 * @throws InvalidKeyException if invalid key
	 * @throws BadPaddingException if cipher text not multiply of 16 character
	 * @throws IllegalBlockSizeException if cipher text not multiply of 16 character
	 */
	public void downloadLastNotification() throws JSONException, SQLException, IOException, SocketException, DatabaseTypeException, InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{
		SocketIO socketIO = new SocketIO(this.getSocket());
		Notification notification = new Notification(this.requestID);
		String offileNotification = "";
		while(notification.countNotification(this.apiID, this.deviceID, this.groupID) > 0)
		{
			offileNotification = notification.select(this.apiID, this.deviceID, this.groupID, Config.getLimitNotification()).toString();
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader("Content-Type", "application/json");
			socketIO.addRequestHeader("Command", "notification");
			if(Config.isContentSecure())
			{
				String tmp = offileNotification;
				socketIO.addRequestHeader("Content-Secure", "yes");
				Encryption encryption = new Encryption(this.key+this.hashPasswordClient); 
				offileNotification = encryption.encrypt(tmp, true);
			}
			if(socketIO.write(offileNotification))
			{
				this.markAsSent(notification.getOfflineID());
			}
		}
	}
	/**
	 * Download last notification deletion log
	 * @throws JSONException if any JSON errors
	 * @throws SQLException if any SQL errors
	 * @throws IOException if any IO errors
	 * @throws DatabaseTypeException if database type not supported 
	 * @throws NoSuchPaddingException if cipher text size not multiply of 16 bytes
	 * @throws NoSuchAlgorithmException if algorithm not found
	 * @throws InvalidKeyException if key is invalid
	 * @throws BadPaddingException if cipher text size not multiply of 16 bytes
	 * @throws IllegalBlockSizeException if cipher text size not multiply of 16 bytes
	 */
	public void downloadLastDeleteLog() throws JSONException, SQLException, IOException, SocketException, DatabaseTypeException, InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{
		SocketIO socketIO = new SocketIO(this.getSocket());
		Notification notification = new Notification(this.requestID);
		JSONArray trash;
		String offileNotification = "";
		while(notification.countDeletionLog(this.apiID, this.deviceID, this.groupID) > 0)
		{
			trash = notification.selectDeletionLog(this.apiID, this.deviceID, this.groupID, Config.getLimitTrash());
			offileNotification = trash.toString();
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader("Content-Type", "application/json");
			socketIO.addRequestHeader("Command", "delete-notification");
			if(Config.isContentSecure())
			{
				String tmp = offileNotification;
				socketIO.addRequestHeader("Content-Secure", "yes");
				Encryption encryption = new Encryption(this.key+this.hashPasswordClient); 
				offileNotification = encryption.encrypt(tmp, false);
			}
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
		String data = "";
		boolean read = true;
		try 
		{
			do
			{
				this.getSocket().getInputStream().read(buf);
				data += new String(buf);
				if(data.contains("\r\n\r\n"))
				{
					read = false;
				}
			}
			while(read);
		} 
		catch (IOException e) 
		{
			this.getSocket().close();
		}
		return data;
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
		String data = "";
		long length = Long.parseLong(contentLength);
		if(length > 0)
		{
			int buf;
			long i;
			try 
			{
				for(i = 0; i < length; i++)
				{
					buf = this.getSocket().getInputStream().read();
					data += String.format("%c", buf);
				}
			} 
			catch (IOException e) 
			{
				this.getSocket().close();
			}
		}
		return data;
	}
	/**
	 * Validating client
	 * @param data String contains client answer
	 * @return true if client is valid and false if client is invalid
	 * @throws DatabaseTypeException if database type not supported 
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 */
	private boolean validatingClient(String data) throws DatabaseTypeException, NoSuchAlgorithmException
	{
		JSONObject jo = new JSONObject();
		boolean sendToken = false;
		jo = new JSONObject(data);
		String clientAnswer = jo.optString("answer", "");
		String lQuestion = jo.optString("question", "");
		String serverAnswer = this.buildAnswer(this.hashPasswordClient, lQuestion, this.deviceID);
		if(serverAnswer.equals(clientAnswer))
		{
			this.token = Utility.sha1(this.deviceID+this.answer+(Math.random()*1000000));
			SocketIO socketIO = new SocketIO(this.getSocket());
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader("Command", "token");
			socketIO.addRequestHeader("Content-Type", "application/json");				
			TimeZone timeZone = TimeZone.getDefault();
			timeZone.getID();
			long timeZoneOffset = (timeZone.getRawOffset() / 60000);
			String time = Utility.now("yyyy-MM-dd HH:mm:ss")+"."+(System.nanoTime()%1000000000/1000);
			JSONObject jo2 = new JSONObject();
			long waitToNext = Math.round(Config.getInspectionInterval() * 1.05);
			/**
			 * The server promises no later than waitToNext milliseconds to send a new token
			 */
			jo2.put("waitToNext", waitToNext);
			jo2.put("deviceID", this.deviceID);
			jo2.put("token", this.token);
			jo2.put("time", time);
			jo2.put("timeZone", timeZoneOffset);
			try 
			{
				sendToken = socketIO.write(jo2.toString());	
				String address = this.getSocket().getInetAddress().getHostAddress().replace("/", "").trim();
				if(sendToken)
				{
					this.setConnected(true);
					this.updateDeviceToken(this.apiID, this.deviceID, this.groupID, this.token, address, time);
					this.connectionEvaluator = new ConnectionEvaluator(this, Config.getInspectionInterval());
					this.connectionEvaluator.start();
				}
				return sendToken;
			} 
			catch (IOException e)
			{
				if(Config.isPrintStackTrace()) 
				{
					e.printStackTrace();
				}
				try 
				{
					this.getSocket().close();
				} 
				catch (IOException e2) 
				{
					if(Config.isPrintStackTrace())
					{
						e2.printStackTrace();
					}
				}
			}
		}
		else
		{
			try 
			{
				this.getSocket().close();
			} 
			catch (IOException e) 
			{
				if(Config.isPrintStackTrace()) 
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	/**
	 * Update device token
	 * @param apiID API ID
	 * @param deviceID Device ID
	 * @param groupID Group ID
	 * @param token Token
	 * @param address IP Address
	 * @param time Time
	 * @throws SQLException if any SQL errors
	 * @throws DatabaseTypeException if database type not supported 
	 */
	private void updateDeviceToken(long apiID, String deviceID, long groupID, String token, String address, String time)
	{
		Database database1 = new Database(Config.getDatabaseConfig1());
		try
		{		
			database1.connect();
			QueryBuilder query1 = new QueryBuilder(database1.getDatabaseType());
			deviceID = query1.escapeSQL(deviceID);
			token = query1.escapeSQL(token);
			address = query1.escapeSQL(address);
			time = query1.escapeSQL(time);
			
		
			int connection = 0;
			List<Device> cons;
			try 
			{
				cons = Client.get(deviceID, apiID, groupID);
				if(cons != null)
				{
					connection = cons.size();			
				}
				else
				{
					connection = 1;
				}
			} 
			catch (ClientException e) 
			{
				connection = 1;
			}
			if(connection == 0)
			{
				connection = 1;
			}
			String sqlUpdate = query1.newQuery()
					.update(Config.getTablePrefix()+"client")
					.set("connection = '"+connection+"', last_token = '"+token+"', last_ip = '"+address+"', last_time = '"+time+"' ")
					.where("device_id = '"+deviceID+"' and api_id = "+apiID+" ")
					.toString();
			database1.execute(sqlUpdate);
		}
		catch(SQLException | ClassNotFoundException | DatabaseTypeException | NullPointerException | IllegalArgumentException e)
		{
			
		}
		finally {
			database1.disconnect();
		}
		
	}
	/**
	 * Validating client
	 * @return true if client is valid and false if client is invalid
	 * @throws IOException if any IO errors
	 * @throws DatabaseTypeException if database type not supported 
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 */
	private boolean validatingClient() throws IOException, DatabaseTypeException, NoSuchAlgorithmException
	{
		String data = "";
		this.sendQuestion();
		data = this.getClientAnswer();		
		return this.validatingClient(data);
	}
	/**
	 * Send key to the client
	 * @param key Encryption key
	 * @throws IOException if any IO errors
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 * @throws NullPointerException if any NULL pointer
	 * @throws IllegalArgumentException if any illegal arguments
	 */
	private void sendKey(String key) throws IOException, NoSuchAlgorithmException, NullPointerException, IllegalArgumentException 
	{
		this.buildRandomQuestion();
		SocketIO socketIO = new SocketIO(this.getSocket());	
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader("Content-Type", "application/json");
		socketIO.addRequestHeader("Command", "key");
		JSONObject jo = new JSONObject();
		jo.put("key", key);
		socketIO.write(jo.toString());
	}
	/**
	 * Send question
	 * @throws IOException if any IO errors
	 * @throws IllegalArgumentException if any illegal argument
	 * @throws NullPointerException if any null pointer
	 * @throws NoSuchAlgorithmException if algorithm not found
	 */
	public void sendQuestion() throws IOException, JSONException, NoSuchAlgorithmException, NullPointerException, IllegalArgumentException 
	{
		this.buildRandomQuestion();
		SocketIO socketIO = new SocketIO(this.getSocket());	
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader("Content-Type", "application/json");
		socketIO.addRequestHeader("Command", "question");
		JSONObject jo = new JSONObject();
		jo.put("question", this.question);
		jo.put("deviceID", this.deviceID);
		socketIO.write(jo.toString());
	}
	/**
	 * Build random question to be sent to the client
	 * @return Random question
	 * @throws IllegalArgumentException if any illegal arguments
	 * @throws NullPointerException if any NULL pointer
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 */
	private String buildRandomQuestion() throws NoSuchAlgorithmException, NullPointerException, IllegalArgumentException
	{
		this.question = Utility.sha1(this.deviceID+Utility.now("yyyyMMddHHmmss.SSS")+(Math.random() * 1000000));
		return this.question;
	}
	/**
	 * Get client answer
	 * @return Read answer from the client to be validate
	 * @throws IOException if any IO errors
	 */
	private String getClientAnswer() throws IOException
	{
		SocketIO socketIO = new SocketIO(this.getSocket());
		socketIO.read();
		return socketIO.getBody();
	}
	/**
	 * Build answer
	 * @param hashPassword Hash password
	 * @param question Question
	 * @param deviceID Device ID
	 * @return Answer
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 */
	private String buildAnswer(String hashPassword, String question, String deviceID) throws NoSuchAlgorithmException
	{
		this.answer = Utility.sha1((hashPassword+"-"+question+"-"+deviceID));		
		return this.answer;
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
		String deviceID = jo.optString("deviceID", "");
		String command = Utility.getFirst(headers, "Command");
		boolean success = false;
		if(command.compareToIgnoreCase("register-device") == 0)
		{
			try
			{
				success = this.registerDevice(deviceID);
				if(success)
				{
					this.onRegisterDeviceSuccess(deviceID, ResponseCode.REGISTRATION_DEVICE_SUCCESS, "Register device success");
				}
				else
				{
					this.onRegisterDeviceError(deviceID, ResponseCode.DEVICE_ALREADY_EXISTS, "Failed", "Fail to register device");
				}
			}
			catch(NotificationException e1)
			{
				this.onRegisterDeviceError(deviceID, ResponseCode.INTERNAL_SERVER_ERROR, "Failed", e1.getMessage());
			}
		}
		else if(command.compareToIgnoreCase("unregister-device") == 0)
		{
			try
			{
				success = this.unregisterDevice(deviceID);
				if(success)
				{
					this.onUnregisterDeviceSuccess(deviceID, ResponseCode.UNREGISTRATION_DEVICE_SUCCESS, "Unregister device success");
				}
				else
				{
					this.onUnregisterDeviceError(deviceID, ResponseCode.DEVICE_NOT_EXISTS, "Failed", "Fail to unregister device");
				}
			}
			catch(NotificationException e1)
			{
				this.onUnregisterDeviceError(deviceID, ResponseCode.INTERNAL_SERVER_ERROR, "Failed", e1.getMessage());
			}
		}
		else if(command.compareToIgnoreCase("answer") == 0)
		{
			this.validatingClient(body);
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
		SocketIO socketIO = new SocketIO(this.getSocket());
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader("Content-Type", "application/json");
		socketIO.addRequestHeader("Command", "unregister-device-error");
		JSONObject jo = new JSONObject();
		jo.put("responseCode", responseCode);
		jo.put("deviceID", deviceID);
		jo.put("message", message);
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
		SocketIO socketIO = new SocketIO(this.getSocket());
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader("Content-Type", "application/json");
		socketIO.addRequestHeader("Command", "unregister-device-success");
		JSONObject jo = new JSONObject();
		jo.put("responseCode", responseCode);
		jo.put("deviceID", deviceID);
		jo.put("message", message);
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
		SocketIO socketIO = new SocketIO(this.getSocket());
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader("Content-Type", "application/json");
		socketIO.addRequestHeader("Command", "register-device-error");
		JSONObject jo = new JSONObject();
		jo.put("responseCode", responseCode);
		jo.put("deviceID", deviceID);
		jo.put("message", message);
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
		SocketIO socketIO = new SocketIO(this.getSocket());
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader("Content-Type", "application/json");
		socketIO.addRequestHeader("Command", "register-device-success");
		JSONObject jo = new JSONObject();
		jo.put("responseCode", responseCode);
		jo.put("deviceID", deviceID);
		jo.put("message", message);
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
		try
		{
			database1.connect();
			QueryBuilder query1 = new QueryBuilder(database1.getDatabaseType());
			String address = this.getSocket().getInetAddress().getHostAddress().toString().replace("/", "").trim();
			deviceID = query1.escapeSQL(deviceID);
			String lToken = query1.escapeSQL(this.token);
			address = query1.escapeSQL(address);
			String sqlSelect = query1.newQuery()
					.select("device_id")
					.from(Config.getTablePrefix()+"client")
					.where("device_id = '"+deviceID+"' and api_id = "+this.apiID+" ")
					.toString();
			rs = database1.executeQuery(sqlSelect);
			if(!rs.isBeforeFirst())
			{
				String slqInsert = query1.newQuery()
						.insert()
						.into(Config.getTablePrefix()+"client")
						.fields("(device_id, api_id, last_token, last_time, last_ip, time_create)")
						.values("('"+deviceID+"', "+this.apiID+", '"+lToken+"', now(), '"+address+"', now())")
						.toString();
				database1.execute(slqInsert);				
				success = true;
				this.sendQuestion();
			}
			else
			{
				throw new NotificationException("Device already exists");
			}
		}
		catch(SQLException | ClassNotFoundException | DatabaseTypeException | NoSuchAlgorithmException | NullPointerException | IllegalArgumentException e)
		{
			
		}
		finally {
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
		try
		{
			database1.connect();
			QueryBuilder query1 = new QueryBuilder(database1.getDatabaseType());
			deviceID = query1.escapeSQL(deviceID);
			String sqlSelect = query1.newQuery()
					.select("device_id")
					.from(Config.getTablePrefix()+"client")
					.where("device_id = '"+deviceID+"' and api_id = "+this.apiID+" ")
					.toString();
			rs = database1.executeQuery(sqlSelect);
			if(rs.isBeforeFirst())
			{
				String slqDelete = query1.newQuery()
						.delete()
						.from(Config.getTablePrefix()+"client")
						.where("device_id = '"+deviceID+"' and api_id = "+this.apiID+" ")
						.toString();
				database1.execute(slqDelete);
				success = true;
			}
			else
			{
				throw new NotificationException("Device not exists");
			}
		}
		catch(SQLException | ClassNotFoundException | DatabaseTypeException | NullPointerException | IllegalArgumentException e)
		{
			
		}
		finally {
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
		ResultSet rs = null;
		Database database1 = new Database(Config.getDatabaseConfig1());
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
				database1.execute(sqlCommand);
			}		
		}
		catch(SQLException | ClassNotFoundException | DatabaseTypeException | NullPointerException | IllegalArgumentException e)
		{
			
		}
		finally {
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
