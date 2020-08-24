package com.planetbiru.pushserver.evaluator;

import java.io.IOException;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.notification.NotificationHandler;

/**
 * SocketBreaker
 * @author Kamshory, MT
 *
 */
public class SocketkBreaker extends Thread
{
	/**
	 * Notification handler
	 */
	private NotificationHandler notificationHandler;
	/**
	 * Timeout
	 */
	private long timeout = Config.getWaitForAnswer();
	/**
	 * Default constructor
	 */
	public SocketkBreaker()
	{
	}
	/**
	 * Constructor with notification handler and timeout
	 * @param notificationHandler Notification handler
	 * @param timeout Timeout
	 */
	public SocketkBreaker(NotificationHandler notificationHandler, long timeout)
	{
		this.notificationHandler = notificationHandler;
		this.timeout = timeout;
	}
	/**
	 * Override run method
	 */
	@Override
	public void run()
	{
		try 
		{
			Thread.sleep(this.timeout);
			if(!this.notificationHandler.isConnected())
			{
				try 
				{
					this.notificationHandler.setRunning(false);
					this.notificationHandler.getSocket().close();
				} 
				catch (IOException e) 
				{
					if(Config.isPrintStackTrace()) 
					{
						e.printStackTrace();
					}
				}
			}
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
