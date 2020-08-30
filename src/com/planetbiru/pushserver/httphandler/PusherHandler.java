package com.planetbiru.pushserver.httphandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.SQLException;

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
	 * Command
	 */
	private String command = "push-notification";
	
	/**
	 * Constructor for single database connection
	 * @param command Command
	 */
	public PusherHandler(String command)
	{
		this.command = command;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException 
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();		
		String authorization = requestHeaders.getFirst("Authorization");
		if(authorization.startsWith("Bearer "))
		{
			authorization = authorization.substring("Bearer ".length());
		}
		Headers responseHeaders = new Headers();
		responseHeaders.set("Server", Config.getApplicationName());
		if("POST,PUT".contains(httpExchange.getRequestMethod())) 
		{
			try 
			{
				String body = HTTPIO.getHTTPRequestBody(httpExchange);	
				Notification notification = new Notification();			
				String remoteAddress = httpExchange.getRemoteAddress().getAddress().getHostAddress();
				if(Config.isHttpProxyEnabled())
		    	{
		    		String ra = requestHeaders.getFirst(Config.getHttpAddressForwarder());
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
					else if(this.command.equals("create-api"))
					{
						responseJSON = this.createAPI(notification, body, remoteAddress, applicationName, applicationVersion, userAgent);
					}
					String response = responseJSON.toString();	
					HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_OK, response);
				}
				else
				{
					HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_NOT_AUTHORITATIVE);
				}
			}
			catch (SQLException | DatabaseTypeException | ClassCastException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
				HTTPIO.sendHTTPResponse(httpExchange, responseHeaders, HttpURLConnection.HTTP_INTERNAL_ERROR);
			}
			catch (JSONException e) 
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
	 * @throws DatabaseTypeException if database type not supported 
	 * @throws JSONException 
	 */
	private JSONObject insert(Notification notification, String body) throws SQLException, DatabaseTypeException, JSONException
	{
		return notification.insert(body);
	}
	/**
	 * Delete notifications
	 * @param notification Notification object
	 * @param body String contains data sent by the application
	 * @return JSONObject contains notification ID and destination device ID
	 * @throws JSONException 
	 */
	private JSONObject delete(Notification notification, String body) throws JSONException
	{
		return notification.delete(body);
	}
	/**
	 * Register device
	 * @param notification Notification object
	 * @param body String contains data sent by the application
	 * @return JSONObject contains registered device ID
	 * @throws JSONException 
	 */
	private JSONObject registerDevice(Notification notification, String body) throws JSONException
	{
		return notification.registerDevice(body);
	}
	/**
	 * Unregister device
	 * @param notification Notification object
	 * @param body String contains data sent by the application
	 * @return JSONObject contains unregistered device ID
	 * @throws JSONException 
	 */
	private JSONObject unregisterDevice(Notification notification, String body) throws JSONException
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
	 * @throws JSONException 
	 */
	private JSONObject createGroup(Notification notification, String body, String remoteAddress, String applicationName, String applicationVersion, String userAgent) throws JSONException
	{
		return notification.createGroup(body, remoteAddress, applicationName, applicationVersion, userAgent);
	}
	/**
	 * Create API
	 * @param notification Notification object
	 * @param body Data sent by pusher
	 * @param remoteAddress Remote address
	 * @param applicationName Application version of the pusher
	 * @param applicationVersion Application name of the pusher
	 * @param userAgent User agent of the pusher
	 * @return JSONObject contains group creation information
	 */
	private JSONObject createAPI(Notification notification, String body, String remoteAddress, String applicationName, String applicationVersion, String userAgent)
	{
		return notification.createAPI(body, remoteAddress, applicationName, applicationVersion, userAgent);
	}
}
