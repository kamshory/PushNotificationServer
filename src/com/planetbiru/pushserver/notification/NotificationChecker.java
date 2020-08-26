package com.planetbiru.pushserver.notification;

import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
	 * Interval
	 */
	private long interval = 3600000;
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
		this.interval = interval;
		this.notificationHandler = notificationHandler;
	}
	/**
	 * Override run method
	 */
	@Override
	public void run()
	{
		while(this.notificationHandler.isRunning())
		{
			try 
			{
				this.notificationHandler.downloadLastNotification();
				this.notificationHandler.downloadLastDeleteLog();
				Thread.sleep(this.interval);
			} 
			catch (IOException | SQLException | DatabaseTypeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) 
			{
				if(Config.isPrintStackTrace()) 
				{
					e.printStackTrace();
				}
			}
			catch (InterruptedException e) 
			{
				if(Config.isPrintStackTrace()) 
				{
					e.printStackTrace();
				}
				Thread.currentThread().interrupt();
			}
		}
	}	
}