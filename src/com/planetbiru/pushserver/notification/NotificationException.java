package com.planetbiru.pushserver.notification;

/**
 * NotificationException
 * @author Kamshory, MT
 *
 */
@SuppressWarnings("serial")
public class NotificationException extends Exception
{
	/**
	 * Default constructor
	 */
	public NotificationException() 
	{ 
		super(); 
	}
	/**
	 * Constructor with the message
	 * @param message Message
	 */
	public NotificationException(String message) 
	{ 
		super(message); 
	}
	/**
	 * Constructor with the message and cause
	 * @param message Message
	 * @param cause Cause
	 */
	public NotificationException(String message, Throwable cause) 
	{ 
		super(message, cause); 
	}
	/**
	 * Constructor with cause
	 * @param cause Cause
	 */
	public NotificationException(Throwable cause) 
	{ 
		super(cause); 
	}
}
