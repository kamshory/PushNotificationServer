package com.planetbiru.pushserver.database;

@SuppressWarnings("serial")
public class DatabaseTypeException extends Exception 
{
	/**
	 * Default constructor
	 */
	public DatabaseTypeException() 
	{ 
		super(); 
	}
	/**
	 * Constructor with the message
	 * @param message Message
	 */
	public DatabaseTypeException(String message) 
	{ 
		super(message); 
	}
	/**
	 * Constructor with the message and cause
	 * @param message Message
	 * @param cause Cause
	 */
	public DatabaseTypeException(String message, Throwable cause) 
	{ 
		super(message, cause); 
	}
	/**
	 * Constructor with cause
	 * @param cause Cause
	 */
	public DatabaseTypeException(Throwable cause) 
	{ 
		super(cause); 
	}
}
