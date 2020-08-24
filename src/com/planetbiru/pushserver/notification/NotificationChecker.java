package com.planetbiru.pushserver.notification;

import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.database.DatabaseTypeException;

/**
 * Notification checker
 * @author Kamshory, MT
 *
 */
public class NotificationChecker extends Thread
{
	/**
	 * Client socket
	 */
	public Socket socket = new Socket();
	/**
	 * Interval
	 */
	public long interval = 3600000;
	/**
	 * Notification handler. The object is carried when constructor is invoked
	 */
	private NotificationHandler notificationHandler;
	/**
	 * Default constructor
	 */
	public NotificationChecker()
	{		
	}
	/**
	 * Constructor with client socket, NotificationHandler object and interval
	 * @param socket Client Socket
	 * @param notificationHandler NotificationHandler object
	 * @param interval Interval
	 */
	public NotificationChecker(Socket socket, NotificationHandler notificationHandler, long interval)
	{
		this.socket = socket;
		this.interval = interval;
		this.notificationHandler = notificationHandler;
	}
	/**
	 * Override run method
	 */
	public void run()
	{
		while(this.notificationHandler.isRunning())
		{
			try 
			{
				this.notificationHandler.downloadLastNotification();
				this.notificationHandler.downloadLastDeleteLog();
			} 
			catch (IOException e) 
			{
				if(Config.isPrintStackTrace()) 
				{
					e.printStackTrace();
				}
			}
			catch (SQLException e) 
			{
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
			catch (InvalidKeyException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
			} 
			catch (NoSuchAlgorithmException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
			} 
			catch (NoSuchPaddingException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
			} 
			catch (IllegalBlockSizeException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
			} 
			catch(JSONException e)
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
			}
			catch(BadPaddingException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
			}
			try 
			{
				Thread.sleep(this.interval);
			} 
			catch (InterruptedException e) 
			{
				if(Config.isPrintStackTrace()) 
				{
					e.printStackTrace();
				}
			}
		}
	}	
}