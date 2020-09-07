package com.planetbiru.pushserver.messenger;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.planetbiru.pushserver.client.Client;
import com.planetbiru.pushserver.client.Device;
import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.notification.Notification;
import com.planetbiru.pushserver.utility.SocketIO;
/**
 * BroadcastDelete
 * @author Kamshory, MT
 *
 */
public class MessengerDelete extends Thread
{
	/**
	 * Data
	 */
	private JSONArray data;
	/**
	 * Device ID
	 */
	private String deviceID;
	/**
	 * API ID
	 */
	private long apiID = 0;
	/**
	 * Group ID
	 */
	private long groupID = 0;
	/**
	 * Notification ID
	 */
	private long notificationID = 0;
	/**
	 * Command
	 */
	private String command = "";
	/**
	 * Request ID
	 */
	private long requestID = 0;	
	
	public JSONArray getData() {
		return data;
	}
	public void setData(JSONArray data) {
		this.data = data;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public long getApiID() {
		return apiID;
	}
	public void setApiID(long apiID) {
		this.apiID = apiID;
	}
	public long getGroupID() {
		return groupID;
	}
	public void setGroupID(long groupID) {
		this.groupID = groupID;
	}
	public long getNotificationID() {
		return notificationID;
	}
	public void setNotificationID(long notificationID) {
		this.notificationID = notificationID;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public long getRequestID() {
		return requestID;
	}
	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}
	/**
	 * Constructor with API ID, device ID, group ID, data, notification ID and database object.
	 * <p>After create an object, caller can invoke start method to run this thread and notification deletion history will sent to the destination device asynchronously.</p>
	 * @param apiID API ID
	 * @param deviceID Device ID
	 * @param requestID Request ID
	 * @param groupID Group ID
	 * @param data Data to send to the client
	 * @param command Command
	 */
	public MessengerDelete(long apiID, String deviceID, long groupID, long requestID, JSONArray data, String command)
	{
		this.apiID = apiID;
		this.data = data;
		this.requestID = requestID;
		if(command.equals(""))
		{
			command = "delete-notification";
		}
		this.command = command;		
	}
	/**
	 * Insert delete history
	 * @param notification Notification
	 * @param apiID API ID
	 * @param groupID Group ID
	 * @param data Data to sent to the client
	 */
	public void inserDeleteHistory(Notification notification, long apiID, long groupID, JSONArray data) 
	{
		notification.insertDeletionLog(this.apiID, groupID, this.data);
	}
	/**
	 * Override run method
	 */
	@Override
	public void run()
	{
		Notification notification = new Notification();
		this.inserDeleteHistory(notification, this.apiID, this.groupID, this.data);
		try
		{
			List<Device> deviceList;
			int length = this.data.length();
			int j;
			JSONObject jo;
			String lDeviceID = "";
			long lNotificationID = 0;
			for(j = 0; j < length; j++)
			{
				jo = this.data.optJSONObject(j);
				lDeviceID = jo.optString("deviceID", "");
				lNotificationID = jo.optLong("id", 0);
				deviceList = Client.get(lDeviceID, this.apiID, this.groupID);
				Iterator<Device> iterator = deviceList.iterator();
				Device device;
				Socket socket;
				SocketIO socketIO = null;
				boolean success = false;
				int i = 0;
				String stringNotification = "";
				while(iterator.hasNext())
				{
					try
					{
						device = iterator.next();
						if(device != null)
						{
							if(device.isActive())
							{
								socket = device.getSocket();
								socketIO = new SocketIO(socket);
								try
								{
									socketIO.resetRequestHeader();
									socketIO.addRequestHeader("Content-Type", "application/json");
									socketIO.addRequestHeader("Command", this.command);
									stringNotification = this.data.toString();
									success = socketIO.write(stringNotification);
									if(success && i == 0)
									{
										notification.clearDeleteLog(this.apiID, lDeviceID, lNotificationID);
										i++;
									}
								}
								catch(IOException e)
								{
									device.setActive(false);
									Client.remove(lDeviceID, this.apiID, this.groupID, this.requestID);
									if(Config.isPrintStackTrace())
									{
										e.printStackTrace();
									}
								}
							}
							else
							{
								Client.remove(lDeviceID, this.apiID, this.groupID, this.requestID);
							}
						}
					}
					catch(NoSuchElementException e)
					{
						/*
						 * Do nothing
						 */
					}
				}
			}			
		} 
		catch (Exception e) 
		{
			if(Config.isPrintStackTrace()) 
			{
				e.printStackTrace();
			}
		}
	}		
}