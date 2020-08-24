package com.planetbiru.pushserver.evaluator;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.notification.NotificationHandler;
/**
 * ClientEvaluator to evaluate the connection
 * @author Kamshory, MT
 *
 */
public class ConnectionEvaluator extends Thread
{
	/**
	 * Timeout
	 */
	private long timeout = 3600000;
	/**
	 * Notification handler
	 */
	private NotificationHandler notificationHandler;
	/**
	 * Default constructor
	 */
	public ConnectionEvaluator()
	{
		
	}
	/**
	 * Constructor with client socket, notification handler and interval
	 * @param notificationHandler Notification handler
	 * @param interval Interval to evaluate client socket
	 */
	public ConnectionEvaluator(NotificationHandler notificationHandler, long interval)
	{
		this.notificationHandler = notificationHandler;
		this.timeout = interval;
	}
	/**
	 * Override run method
	 */
	public void run()
	{
		try 
		{
			Thread.sleep(this.timeout);
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
			this.notificationHandler.sendQuestion();
		} 
		catch (IOException | JSONException | NoSuchAlgorithmException | NullPointerException | IllegalArgumentException e) 
		{
			if(Config.isPrintStackTrace()) 
			{
				e.printStackTrace();
			}
		}
	}
	
}
