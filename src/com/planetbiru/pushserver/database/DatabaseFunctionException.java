package com.planetbiru.pushserver.database;

@SuppressWarnings("serial")
public class DatabaseFunctionException extends Exception {
	/**
	 * Default constructor
	 */
	public DatabaseFunctionException() 
	{ 
		super(); 
	}
	/**
	 * Constructor with the message
	 * @param message Message
	 */
	public DatabaseFunctionException(String message) 
	{ 
		super(message); 
	}
	/**
	 * Constructor with the message and cause
	 * @param message Message
	 * @param cause Cause
	 */
	public DatabaseFunctionException(String message, Throwable cause) 
	{ 
		super(message, cause); 
	}
	/**
	 * Constructor with cause
	 * @param cause Cause
	 */
	public DatabaseFunctionException(Throwable cause) 
	{ 
		super(cause); 
	}
}
