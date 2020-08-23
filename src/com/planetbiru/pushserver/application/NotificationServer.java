package com.planetbiru.pushserver.application;

import java.io.IOException;
import java.net.ServerSocket;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.notification.NotificationHandler;

public class NotificationServer extends Thread
{
	public NotificationServer()
	{
	}
	
	@Override
	public void run()
	{
		ServerSocket serverSocket = null;
		try 
		{
		    serverSocket = new ServerSocket(Config.getNotificationPort());		    
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
		finally 
		{
			if(serverSocket != null)
			{
				try 
				{
					serverSocket.close();
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
	}
}
