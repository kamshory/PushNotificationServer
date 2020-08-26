package com.planetbiru.pushserver.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cache {
	private Cache()
	{
		
	}
	/**
	 * Cache to save device ID
	 */
	private static Map<String, Boolean> cacheDeviceID = new HashMap<>();
	/**
	 * Cache to save API
	 */
	private static Map<String, Boolean> cacheAPI = new HashMap<>();
	/**
	 * Maximum size of cache of device ID
	 */
	private static long cacheDeviceIDMax = 200;
	/**
	 * Maximum size of cache of API
	 */
	private static long cacheAPIMax = 200;
	
	/**
	 * Cache to store pusher source
	 */
	private static List<String> cachePusherSource = new ArrayList<>();
	
	
	public static Map<String, Boolean> getCacheDeviceID() {
		return cacheDeviceID;
	}
	public static void setCacheDeviceID(Map<String, Boolean> cacheDeviceID) {
		Cache.cacheDeviceID = cacheDeviceID;
	}
	public static Map<String, Boolean> getCacheAPI() {
		return cacheAPI;
	}
	public static void setCacheAPI(Map<String, Boolean> cacheAPI) {
		Cache.cacheAPI = cacheAPI;
	}
	public static long getCacheDeviceIDMax() {
		return cacheDeviceIDMax;
	}
	public static void setCacheDeviceIDMax(long cacheDeviceIDMax) {
		Cache.cacheDeviceIDMax = cacheDeviceIDMax;
	}
	public static long getCacheAPIMax() {
		return cacheAPIMax;
	}
	public static void setCacheAPIMax(long cacheAPIMax) {
		Cache.cacheAPIMax = cacheAPIMax;
	}
	public static List<String> getCachePusherSource() {
		return cachePusherSource;
	}
	public static void setCachePusherSource(List<String> cachePusherSource) {
		Cache.cachePusherSource = cachePusherSource;
	}
	/**
	 * Check whether device registered or not
	 * @param apiID API ID 
	 * @param deviceID Device ID
	 * @return true if device is registered and false if device is not registered
	 */
	public static boolean isRegisteredDevice(long apiID, String deviceID)
	{
		return Cache.cacheDeviceID.getOrDefault(deviceID+"_"+apiID, false);
	}
	/**
	 * Register device
	 * @param apiID API ID
	 * @param deviceID Device ID
	 */
	public static void registerDevice(long apiID, String deviceID)
	{
		long length = Cache.cacheDeviceID.size();
		Boolean registered = false;
		if(length > Cache.cacheDeviceIDMax)
		{
			registered = false;
			Cache.cacheDeviceID = new HashMap<>();
		}
		else
		{
			registered = Cache.cacheDeviceID.getOrDefault(deviceID+"_"+apiID, false);
		}
		if(!registered.booleanValue())
		{
			Cache.cacheDeviceID.put(deviceID+"_"+apiID, true);
		}
	}
	/**
	 * Check whether API is registered or not
	 * @param apiKey API key
	 * @param address API address
	 * @return true if API is registered and false if not
	 */
	public static boolean isRegisteredAPI(String apiKey, String address)
	{
		return Cache.cacheAPI.getOrDefault(address+"_"+apiKey, false);
	}
	/**
	 * Register API
	 * @param apiKey API key
	 * @param address API address
	 */
	public static void registerAPI(String apiKey, String address)
	{
		long length = Cache.cacheAPI.size();
		Boolean registered = false;
		if(length > Cache.cacheAPIMax)
		{
			registered = false;
			Cache.cacheAPI = new HashMap<>();
		}
		else
		{
			registered = Cache.cacheAPI.getOrDefault(address+"_"+apiKey, false);
		}
		if(!registered.booleanValue())
		{
			Cache.cacheDeviceID.put(address+"_"+apiKey, true);
		}
	}
	
}
