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
import com.planetbiru.pushserver.database.DatabaseTypeException;
import com.planetbiru.pushserver.notification.Notification;
import com.planetbiru.pushserver.utility.HTTPIO;
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
	 * Data
	 */
	private JSONObject data;
	/**
	 * Flag whether database connection is per push or not
	 */
	private boolean connectionPerPush = false;
	private String command = "push-notification";
	
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
	 * Constructor for single database connection
	 * @param database1 Primary Database object
	 * @param database2 Secondary Database object
	 * @param database3 Tertiary Database object
	 */
	public PusherHandler(String command)
	{
		this.command = command;
		this.connectionPerPush = false;
	}
	/**
	 * Override handle method
	 */
	public void handle(HttpExchange httpExchange) throws IOException 
	{
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
				Notification notification = new Notification();			
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
			catch (NoSuchAlgorithmException | SQLException | DatabaseTypeException | ClassCastException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_INTERNAL_ERROR);
			}
			catch (JSONException | MessagingException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_BAD_REQUEST);
			}
			catch (IOException | NullPointerException | IllegalArgumentException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_ACCEPTED);
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
		return notification.delete(body);
	}
	private JSONObject registerDevice(Notification notification, String body) throws JSONException, SQLException, DatabaseTypeException
	{
		return notification.registerDevice(body);
	}
	private JSONObject unregisterDevice(Notification notification, String body) throws JSONException, SQLException, DatabaseTypeException
	{
		return notification.unregisterDevice(body);
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
