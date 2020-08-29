package com.planetbiru.pushserver.client;

import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import com.planetbiru.pushserver.config.Config;

/**
 * <strong>Device</strong> is object contains client socket, device ID, request ID, and active flag.
 * @author Kamshory, MT
 *
 */
public class Device {
	private long index = 0;
	/**
	 * Client socket
	 */
	private Socket socket;
	/**
	 * Device ID
	 */
	private String deviceID;
	/**
	 * Request ID
	 */
	private long requestID;
	/**
	 * Encryption key
	 */
	private String key = "";
	/**
	 * Hash password client
	 */
	private String hashPasswordClient = "";
	/**
	 * Flag active
	 */
	private boolean active = true;
	
	
	
	public long getIndex() {
		return index;
	}
	public void setIndex(long index) {
		this.index = index;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public long getRequestID() {
		return requestID;
	}
	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getHashPasswordClient() {
		return hashPasswordClient;
	}
	public void setHashPasswordClient(String hashPasswordClient) {
		this.hashPasswordClient = hashPasswordClient;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	/**
	 * Default constructor
	 */
	public Device() 
	{
	}
	/**
	 * Constructor with device ID, request ID and socket
	 * @param deviceID Device ID
	 * @param requestID Request ID
	 * @param socket SClient socket
	 */
	public Device(String deviceID, long requestID, Socket socket)
	{
		this.deviceID = deviceID;
		this.requestID = requestID;
		this.socket = socket;
	}
	/**
	 * Constructor with device ID, request ID and socket
	 * @param deviceID Device ID
	 * @param requestID Request ID
	 * @param socket SClient socket
	 * @param key Encryption key
	 * @param hashPasswordClient Hash password key
	 */
	public Device(String deviceID, long requestID, Socket socket, String key, String hashPasswordClient)
	{
		this.deviceID = deviceID;
		this.requestID = requestID;
		this.socket = socket;
		this.key = key;
		this.hashPasswordClient = hashPasswordClient;
	}
	/**
	 * Overrides <strong>toString</strong> method to convert object to JSON String. This method is useful to debug or show value of each properties of the object.
	 */
	@Override
	public String toString()
	{
		String result = "";
		JSONObject res = new JSONObject();
		try {
			res.put("socket", socket.toString());
			res.put("deviceID", deviceID);
			res.put("requestID", socket.toString());
			res.put("active", active);
			result = res.toString(4);
		} catch (JSONException e) {
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
