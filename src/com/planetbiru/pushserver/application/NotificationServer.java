package com.planetbiru.pushserver.application;

import java.io.IOException;
import java.net.ServerSocket;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.database.Database;
import com.planetbiru.pushserver.notification.NotificationHandler;

public class NotificationServer extends Thread
{
	private Database database1;
	private Database database2;
	private Database database3;
	public NotificationServer(Database database1, Database database2, Database database3)
	{
		this.database1 = database1;
		this.database2 = database2;
		this.database3 = database3;
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
	        	handler = new NotificationHandler(serverSocket.accept(), Application.getRequestID(), this.database1, this.database2, this.database3);
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
