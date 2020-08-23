package com.planetbiru.pushserver.database;

@SuppressWarnings("serial")
public class DatabaseFunctionFoundException extends Exception {
	/**
	 * Default constructor
	 */
	public DatabaseFunctionFoundException() 
	{ 
		super(); 
	}
	/**
	 * Constructor with the message
	 * @param message Message
	 */
	public DatabaseFunctionFoundException(String message) 
	{ 
		super(message); 
	}
	/**
	 * Constructor with the message and cause
	 * @param message Message
	 * @param cause Cause
	 */
	public DatabaseFunctionFoundException(String message, Throwable cause) 
	{ 
		super(message, cause); 
	}
	/**
	 * Constructor with cause
	 * @param cause Cause
	 */
	public DatabaseFunctionFoundException(Throwable cause) 
	{ 
		super(cause); 
	}
}
