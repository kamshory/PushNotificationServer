package com.planetbiru.pushserver.utility;

@SuppressWarnings("serial")
public class QueryParserException extends Exception 
{
	/**
	 * Default constructor
	 */
	public QueryParserException() 
	{ 
		super(); 
	}
	/**
	 * Constructor with the message
	 * @param message Message
	 */
	public QueryParserException(String message) 
	{ 
		super(message); 
	}
	/**
	 * Constructor with the message and cause
	 * @param message Message
	 * @param cause Cause
	 */
	public QueryParserException(String message, Throwable cause) 
	{ 
		super(message, cause); 
	}
	/**
	 * Constructor with cause
	 * @param cause Cause
	 */
	public QueryParserException(Throwable cause) 
	{ 
		super(cause); 
	}
}
