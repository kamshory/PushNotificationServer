package com.planetbiru.pushserver.application;

import java.io.IOException;
import java.net.ServerSocket;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.notification.NotificationHandler;

public class NotificationServer extends Thread
{
	public NotificationServer()
	{
		/**
		 * Constructor
		 */
	}
	
	@Override
	public void run()
	{
		try(
				ServerSocket serverSocket = new ServerSocket(Config.getNotificationPort());	
		) 
		{
		    	    
	        do 
	        {
	        	NotificationHandler handler;
	        	handler = new NotificationHandler(serverSocket.accept(), Application.getRequestID());
	        	handler.start(); 
	        	Application.setRequestID(Application.getRequestID() + 1);
	        }
	        while(true);
			
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
