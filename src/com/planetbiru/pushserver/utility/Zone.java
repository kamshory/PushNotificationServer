package com.planetbiru.pushserver.utility;

import java.util.TimeZone;

public abstract class Zone extends TimeZone{

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1L;
	public static long timeZoneOffset()
	{
		TimeZone timeZone = TimeZone.getDefault();
		timeZone.getID();
		return (timeZone.getRawOffset() / 60000);
	}

}
