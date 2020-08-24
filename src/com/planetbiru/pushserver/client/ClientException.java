package com.planetbiru.pushserver.client;

/**
 * <strong>ClientException</strong> derived from Exception class. It will throw exception when error occurred while getting client by device ID.
 * @author Kamshory, MT
 *
 */
@SuppressWarnings("serial")
public class ClientException extends Exception 
{
	/**
	 * Default constructor
	 */
	public ClientException() 
	{ 
		super(); 
	}
	/**
	 * Constructor with the message
	 * @param message Message
	 */
	public ClientException(String message) 
	{ 
		super(message); 
	}
	/**
	 * Constructor with the message and cause
	 * @param message Message
	 * @param cause Cause
	 */
	public ClientException(String message, Throwable cause) 
	{ 
		super(message, cause); 
	}
	/**
	 * Constructor with cause
	 * @param cause Cause
	 */
	public ClientException(Throwable cause) 
	{ 
		super(cause); 
	}
}
