package com.planetbiru.code;

/**
 * Response code
 * @author Kamshory, MT
 *
 */
public class ResponseCode 
{
	private ResponseCode()
	{
		
	}
	public static final int DEVICE_ALREADY_EXISTS = 101;
	public static final int DEVICE_NOT_EXISTS = 102;
	public static final int DEVICE_BLOCKED = 103;
	public static final int REGISTRATION_DEVICE_SUCCESS = 201;
	public static final int UNREGISTRATION_DEVICE_SUCCESS = 202;
	public static final int INTERNAL_SERVER_ERROR = 301;
}
