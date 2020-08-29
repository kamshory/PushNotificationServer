package com.planetbiru.pushserver.application;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocketFactory;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.notification.NotificationHandler;

public class NotificationServerSSL extends Thread{
	private int port = 97;
	public NotificationServerSSL(int port)
	{
		/**
		 * Constructor
		 */
		this.port = port;
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
		    serverSocketSSL = ssf.createServerSocket(this.port);		    
	        do 
	        {
	        	NotificationHandler handler;
	        	handler = new NotificationHandler(serverSocketSSL.accept(), Application.getRequestID());
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
