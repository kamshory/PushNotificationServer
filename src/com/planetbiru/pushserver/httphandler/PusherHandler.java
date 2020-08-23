package com.planetbiru.pushserver.httphandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.json.JSONException;
import org.json.JSONObject;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.database.Database;
import com.planetbiru.pushserver.database.DatabaseConfig;
import com.planetbiru.pushserver.database.DatabaseTypeException;
import com.planetbiru.pushserver.notification.Notification;
import com.planetbiru.pushserver.utility.HTTPIO;
import com.planetbiru.pushserver.utility.QueryParserException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
/**
 * Notification pusher
 * @author Kamshory, MT
 *
 */
public class PusherHandler implements HttpHandler 
{
	/**
	 * Primary database configuration
	 */
	private DatabaseConfig databaseConfig1;
	/**
	 * Secondary database configuration
	 */
	private DatabaseConfig databaseConfig2;
	/**
	 * Tertiary database configuration
	 */
	private DatabaseConfig databaseConfig3;
	/**
	 * Primary database for pusher
	 */
	private Database databasePusher1;
	/**
	 * Secondary database for pusher
	 */
	private Database databasePusher2;
	/**
	 * Tertiary database for pusher
	 */
	private Database databasePusher3;
	/**
	 * Primary database
	 */
	private Database database1;
	/**
	 * Secondary database
	 */
	private Database database2;
	/**
	 * Tertiary database
	 */
	private Database database3;
	/**
	 * Data
	 */
	private JSONObject data;
	/**
	 * Flag whether database connection is per push or not
	 */
	private boolean connectionPerPush = false;
	private String command = "push-notification";
	
	public DatabaseConfig getDatabaseConfig1() {
		return databaseConfig1;
	}
	public void setDatabaseConfig1(DatabaseConfig databaseConfig1) {
		this.databaseConfig1 = databaseConfig1;
	}
	public DatabaseConfig getDatabaseConfig2() {
		return databaseConfig2;
	}
	public void setDatabaseConfig2(DatabaseConfig databaseConfig2) {
		this.databaseConfig2 = databaseConfig2;
	}
	public DatabaseConfig getDatabaseConfig3() {
		return databaseConfig3;
	}
	public void setDatabaseConfig3(DatabaseConfig databaseConfig3) {
		this.databaseConfig3 = databaseConfig3;
	}
	public Database getDatabasePusher1() {
		return databasePusher1;
	}
	public void setDatabasePusher1(Database databasePusher1) {
		this.databasePusher1 = databasePusher1;
	}
	public Database getDatabasePusher2() {
		return databasePusher2;
	}
	public void setDatabasePusher2(Database databasePusher2) {
		this.databasePusher2 = databasePusher2;
	}
	public Database getDatabasePusher3() {
		return databasePusher3;
	}
	public void setDatabasePusher3(Database databasePusher3) {
		this.databasePusher3 = databasePusher3;
	}
	public Database getDatabase1() {
		return database1;
	}
	public void setDatabase1(Database database1) {
		this.database1 = database1;
	}
	public Database getDatabase2() {
		return database2;
	}
	public void setDatabase2(Database database2) {
		this.database2 = database2;
	}
	public Database getDatabase3() {
		return database3;
	}
	public void setDatabase3(Database database3) {
		this.database3 = database3;
	}
	public JSONObject getData() {
		return data;
	}
	public void setData(JSONObject data) {
		this.data = data;
	}
	public boolean isConnectionPerPush() {
		return connectionPerPush;
	}
	public void setConnectionPerPush(boolean connectionPerPush) {
		this.connectionPerPush = connectionPerPush;
	}
	
	public String getAction() {
		return command;
	}
	public void setAction(String command) {
		this.command = command;
	}
	/**
	 * Constructor for multiple database connection
	 * @param databaseConfig1 Primary database configuration
	 * @param databaseConfig2 Secondary database configuration
	 * @param databaseConfig3 Tertiary database configuration
	 * @param database1 Primary Database object
	 * @param database2 Secondary Database object
	 * @param database3 Tertiary Database object
	 */
	public PusherHandler(String command, DatabaseConfig databaseConfig1, DatabaseConfig databaseConfig2, DatabaseConfig databaseConfig3, Database database1, Database database2, Database database3)
	{
		this.command = command;
		this.databaseConfig1 = databaseConfig1;
		this.databaseConfig2 = databaseConfig2;
		this.databaseConfig3 = databaseConfig2;	
		this.database1 = database1;
		this.database1 = database2;
		this.database1 = database3;
		this.connectionPerPush = true;
	}
	/**
	 * Constructor for single database connection
	 * @param database1 Primary Database object
	 * @param database2 Secondary Database object
	 * @param database3 Tertiary Database object
	 */
	public PusherHandler(String command, Database database1, Database database2, Database database3)
	{
		this.command = command;
		this.databasePusher1 = database1;
		this.databasePusher2 = database2;
		this.databasePusher3 = database3;	
		this.connectionPerPush = false;
	}
	/**
	 * Make sure that all database connection is connected and ready to be used
	 * @return true if success and false if failed
	 */
	public boolean connect()
	{
		boolean dbok1 = true;
		boolean dbok2 = false;
		if(this.connectionPerPush)
		{
			// Connect databasePusher
			dbok1 = false;
			this.databasePusher1 = new Database(this.databaseConfig1);
			this.databasePusher2 = new Database(this.databaseConfig2);
			this.databasePusher3 = new Database(this.databaseConfig3);
			try 
			{
				this.databasePusher1.connect();
				this.databasePusher2.connect();
				this.databasePusher3.connect();
				dbok1 = true;
			} 
			catch (ClassNotFoundException | SQLException | DatabaseTypeException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				dbok1 = false;
				do
				{
					try 
					{
						Thread.sleep(Config.getWaitDatabaseReconnect());
					} 
					catch (InterruptedException e1) 
					{
						if(Config.isPrintStackTrace())
						{
							e1.printStackTrace();
						}
					}
					dbok1 = this.databasePusher1.justReconnect();
					this.databasePusher2.justReconnect();
					this.databasePusher3.justReconnect();
				}
				while(!dbok1);
			}
			/**
			 * Check connection for database1
			 * If not connected, reconnect
			 */
			do 
			{
				dbok2 = this.database1.checkConnection();
				if(!dbok2)
				{
					try 
					{
						Thread.sleep(Config.getWaitDatabaseReconnect());
					} 
					catch (InterruptedException e) 
					{
						if(Config.isPrintStackTrace()) 
						{
							e.printStackTrace();
						}
					}
					try 
					{
						dbok2 = this.database1.connect();
					} 
					catch (ClassNotFoundException | SQLException | DatabaseTypeException e) 
					{
						if(Config.isPrintStackTrace()) 
						{
							e.printStackTrace();
						}
					}
				}
			}
			while(!dbok2);
		}
		else
		{
			/**
			 * Check connection for databasePusher1
			 * If not connected, reconnect
			 */
			do 
			{
				dbok2 = this.databasePusher1.checkConnection();
				if(!dbok2)
				{
					try 
					{
						Thread.sleep(Config.getWaitDatabaseReconnect());
					} 
					catch (InterruptedException e) 
					{
						if(Config.isPrintStackTrace()) 
						{
							e.printStackTrace();
						}
					}
					try 
					{
						dbok2 = this.databasePusher1.connect();
					} 
					catch (ClassNotFoundException | SQLException | DatabaseTypeException e) 
					{
						if(Config.isPrintStackTrace()) 
						{
							e.printStackTrace();
						}
					}
				}
			}
			while(!dbok2);
		}
		return dbok1 & dbok2;
	}
	/**
	 * Override handle method
	 */
	public void handle(HttpExchange httpExchange) throws IOException 
	{
		this.connect();
		Headers requestHeaders = httpExchange.getRequestHeaders();		
		String authorization = requestHeaders.getFirst("Authorization");
		if(authorization.startsWith("Bearer "))
		{
			authorization = authorization.substring("Bearer ".length());
		}
		Headers responseHeaders = new Headers();
		responseHeaders.set("Server", Config.getApplicationname());
		if("POST,PUT".contains(httpExchange.getRequestMethod())) 
		{
			try 
			{
				String body = HTTPIO.getHTTPRequestBody(httpExchange);	
				Notification notification = new Notification(this.databasePusher1, this.databasePusher2, this.databasePusher3);			
				String remoteAddress = httpExchange.getRemoteAddress().getAddress().getHostAddress();
				if(Config.isHTTPProxyEnabled())
		    	{
		    		String ra = requestHeaders.getFirst(Config.getHTTPAddressForwarder());
		    		if(ra == null)
		    		{
		    			ra = "";
		    		}
	    			if(ra.length() > 2)
	    			{
	    				remoteAddress = ra;
	    			}
		    	}
				String applicationName = requestHeaders.getFirst("X-Application-Name");
				String applicationVersion = requestHeaders.getFirst("X-Application-Version");
				String userAgent = requestHeaders.getFirst("User-Agent");
				if(notification.authentication(authorization, remoteAddress, applicationName, applicationVersion, userAgent))
				{
					JSONObject responseJSON = new JSONObject();
					if(this.command.equals("push-notification"))
					{
						responseJSON = this.insert(notification, body);
					}
					else if(this.command.equals("remove-notification"))
					{
						responseJSON = this.delete(notification, body);
					}
					else if(this.command.equals("register-device"))
					{
						responseJSON = this.registerDevice(notification, body);
					}
					else if(this.command.equals("unregister-device"))
					{
						responseJSON = this.unregisterDevice(notification, body);
					}
					else if(this.command.equals("create-group"))
					{
						responseJSON = this.createGroup(notification, body, remoteAddress, applicationName, applicationVersion, userAgent);
					}
					String response = responseJSON.toString();	
					HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_OK, response);
				}
				else
				{
					HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_NOT_AUTHORITATIVE);
				}
			}
			catch (NoSuchAlgorithmException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_INTERNAL_ERROR);
			}
			catch (QueryParserException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_BAD_REQUEST);
			}
			catch (JSONException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_BAD_REQUEST);
			}
			catch (SQLException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_INTERNAL_ERROR);
			}
			catch (IOException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_ACCEPTED);
			} 
			catch (DatabaseTypeException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_INTERNAL_ERROR);
			}
			catch (AddressException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_NOT_AUTHORITATIVE);
			} 
			catch (ClassCastException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_ACCEPTED);
			} 
			catch (NullPointerException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_ACCEPTED);
			} 
			catch (IllegalArgumentException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_ACCEPTED);
			} 
			catch (MessagingException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_BAD_REQUEST);
			}
		}
		else
		{
			responseHeaders.set("Allow", "POST, PUT");
			HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_BAD_METHOD);
		}
		httpExchange.close();
	}
	/**
	 * Insert notification
	 * @param notification Notification object
	 * @param body String contains data sent by the application
	 * @return JSONArray contains notification ID and destination device ID
	 * @throws SQLException if any SQL errors
	 * @throws JSONException if any JSON errors
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public JSONObject insert(Notification notification, String body) throws JSONException, SQLException, DatabaseTypeException
	{
		return notification.insert(body);
	}
	/**
	 * Delete notifications
	 * @param notification Notification object
	 * @param body String contains data sent by the application
	 * @return JSONArray contains notification ID and destination device ID
	 * @throws SQLException if any SQL errors
	 * @throws JSONException if any JSON errors
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public JSONObject delete(Notification notification, String body) throws SQLException, JSONException, DatabaseTypeException
	{
		JSONObject data = notification.delete(body);
		return data;
	}
	private JSONObject registerDevice(Notification notification, String body) throws JSONException, SQLException, DatabaseTypeException
	{
		JSONObject data = notification.registerDevice(body);
		return data;
	}
	private JSONObject unregisterDevice(Notification notification, String body) throws JSONException, SQLException, DatabaseTypeException
	{
		JSONObject data = notification.unregisterDevice(body);
		return data;
	}
	/**
	 * Create group
	 * @param notification Notification object
	 * @param body Data sent by pusher
	 * @param remoteAddress Remote address
	 * @param applicationName Application version of the pusher
	 * @param applicationVersion Application name of the pusher
	 * @param userAgent User agent of the pusher
	 * @return JSONObject contains group creation information
	 * @throws JSONException if any JSON errors
	 * @throws SQLException if any SQL errors
	 * @throws DatabaseTypeException if database type is not found
	 * @throws AddressException if any invalid address
	 * @throws NullPointerException if any NULL pointer
	 * @throws IllegalArgumentException if any illegal arguments
	 * @throws MessagingException if any errors occurred while send message
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 */
	public JSONObject createGroup(Notification notification, String body, String remoteAddress, String applicationName, String applicationVersion, String userAgent) throws JSONException, SQLException, DatabaseTypeException, AddressException, NullPointerException, IllegalArgumentException, MessagingException, NoSuchAlgorithmException
	{
		return notification.createGroup(body, remoteAddress, applicationName, applicationVersion, userAgent);
	}
}
