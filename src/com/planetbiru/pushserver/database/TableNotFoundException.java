package com.planetbiru.pushserver.database;

@SuppressWarnings("serial")
public class TableNotFoundException extends Exception
{
	/**
	 * Default constructor
	 */
	public TableNotFoundException() 
	{ 
		super(); 
	}
	/**
	 * Constructor with the message
	 * @param message Message
	 */
	public TableNotFoundException(String message) 
	{ 
		super(message); 
	}
	/**
	 * Constructor with the message and cause
	 * @param message Message
	 * @param cause Cause
	 */
	public TableNotFoundException(String message, Throwable cause) 
	{ 
		super(message, cause); 
	}
	/**
	 * Constructor with cause
	 * @param cause Cause
	 */
	public TableNotFoundException(Throwable cause) 
	{ 
		super(cause); 
	}
}
