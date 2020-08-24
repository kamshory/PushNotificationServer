package com.planetbiru.pushserver.httphandler;

import java.io.IOException;

import com.planetbiru.pushserver.config.Config;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class WelcomeHandler implements HttpHandler
{
	private String path;
	/**
	 * Default constructor
	 * @param path 
	 */
	public WelcomeHandler(String path)
	{
		this.path = path;
	}
	/**
	 * Override handle method
	 */
	@Override
	public void handle(HttpExchange httpExchange) throws IOException 
	{
		Headers responseHeaders = httpExchange.getResponseHeaders();
		if(this.path.equals("welcome"))
		{
	        responseHeaders.add("Location", Config.getRedirectHome());
	        httpExchange.sendResponseHeaders(301, -1);
		}
		else
		{
			httpExchange.sendResponseHeaders(200, -1);
		}
        httpExchange.close();
	}

}
