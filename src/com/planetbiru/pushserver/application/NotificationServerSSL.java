package com.planetbiru.pushserver.application;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocketFactory;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.database.Database;
import com.planetbiru.pushserver.notification.NotificationHandler;

public class NotificationServerSSL extends Thread{
	private Database database1;
	private Database database2;
	private Database database3;
	public NotificationServerSSL(Database database1, Database database2, Database database3)
	{
		this.database1 = database1;
		this.database2 = database2;
		this.database3 = database3;
	}
	@Override
	public void run()
	{
		ServerSocket serverSocketSSL = null;
		try 
		{
			System.setProperty("javax.net.ssl.keyStore", Config.getKeystoreFile());
		    System.setProperty("javax.net.ssl.keyStorePassword", Config.getKeystorePassword());
		    SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		    serverSocketSSL = ssf.createServerSocket(Config.getNotificationPortSSL());		    
	        do 
	        {
	        	NotificationHandler handler;
	        	handler = new NotificationHandler(serverSocketSSL.accept(), Application.getRequestID(), this.database1, this.database2, this.database3);
	        	handler.start(); 
	        	Application.setRequestID(Application.getRequestID() + 1);
	        }
	        while(true);		
		}
		catch(SecurityException | NullPointerException | IllegalArgumentException | IOException e)
		{
			if(Config.isPrintStackTrace()) 
			{
				e.printStackTrace();
			}			
		}
		finally {
			if(serverSocketSSL != null)
			{
				try 
				{
					serverSocketSSL.close();
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
