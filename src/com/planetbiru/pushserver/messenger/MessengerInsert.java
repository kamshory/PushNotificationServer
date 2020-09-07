package com.planetbiru.pushserver.messenger;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.planetbiru.pushserver.client.Client;
import com.planetbiru.pushserver.client.Device;
import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.database.Database;
import com.planetbiru.pushserver.database.QueryBuilder;
import com.planetbiru.pushserver.database.DatabaseTypeException;
import com.planetbiru.pushserver.utility.SocketIO;
import com.planetbiru.pushserver.utility.Utility;

/**
 * MessagngerInsert is class to deliver the notification to its destination device. It will search the device from the Device object. If the device is found, it will send notification to its socket and mark it as "sent". If the device not found, it will not mark the notification as "sent". 
 * @author Kamshory, MT
 *
 */
public class MessengerInsert extends Thread
{
	/**
	 * Data to be sent
	 */
	private String data;
	/**
	 * API ID
	 */
	private long apiID = 0;
	/**
	 * Group ID
	 */
	private long groupID = 0;
	/**
	 * Notification
	 */
	private long notificationID = 0;
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
	private List<Device> deviceList = new ArrayList<>();
	/**
	 * Constructor with API ID, device list, group ID, data, notification ID and database object.
	 * <p>After create an object, caller can invoke start method to run this thread and notification will sent to the destination device asynchronously.</p>
	 * @param apiID API ID
	 * @param deviceList Device list
	 * @param groupID Group ID
	 * @param data Data to send to the client
	 * @param notificationID Notification ID
	 * @param command Command
	 */
	public MessengerInsert(long apiID, List<Device> deviceList, long groupID, String data, long notificationID, String command)
	{
		this.deviceList  = deviceList;
		this.apiID = apiID;
		this.groupID = groupID;
		this.data = data;
		this.notificationID = notificationID;
		if(command.equals(""))
		{
			command = "message";
		}
	}
	/**
	 * Override run method
	 */
	@Override
	public void run()
	{
		Device device;
		String deviceID = "";
		Socket socket;
		int idx = 0;
		boolean success = false;
		Iterator<Device> iterator = this.deviceList.iterator();
		SocketIO socketIO = null;
		String stringNotification = "";
		while(iterator.hasNext())
		{
			try
			{
				device = iterator.next();
				if(device != null)
				{
					deviceID = device.getDeviceID();
					if(device.isActive())
					{
						socket = device.getSocket();
						socketIO = new SocketIO(socket);
						try
						{
							socketIO.resetRequestHeader();
							socketIO.addRequestHeader("Content-Type", "application/json");
							socketIO.addRequestHeader("Command", "notification");
							stringNotification = this.data;
							success = socketIO.write(stringNotification);
							if(success && idx == 0 && this.notificationID != 0)
							{
								this.markAsSent(this.notificationID);
								idx++;
							}
							if(!success)
							{
								device.setActive(false);
							}
						}
						catch(SocketException e)
						{
							device.setActive(false);
							Client.remove(deviceID, this.apiID, this.groupID, device.getRequestID());
							if(Config.isPrintStackTrace())
							{
								e.printStackTrace();
							}
						} 
						catch(IOException e)
						{
							device.setActive(false);
							Client.remove(deviceID, this.apiID, this.groupID, device.getRequestID());
							if(Config.isPrintStackTrace())
							{
								e.printStackTrace();
							}
						} 
						catch (SQLException | DatabaseTypeException e) 
						{
							if(Config.isPrintStackTrace())
							{
								e.printStackTrace();
							}
						} 
					}
					else
					{
						Client.remove(deviceID, this.apiID, this.groupID, device.getRequestID());
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
	/**
	 * Mark notification as "sent"
	 * @param notificationID Notification ID
	 * @throws SQLException if any SQL errors
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public void markAsSent(long notificationID) throws SQLException, DatabaseTypeException 
	{
		Database database1 = new Database(Config.getDatabaseConfig1());
		Statement stmt = null;
		try
		{
			database1.connect();
			QueryBuilder query1 = new QueryBuilder(Config.getDatabaseConfig1().getDatabaseType());
			String sqlCommand = query1.newQuery()
					.update(Config.getTablePrefix()+"notification")
					.set("is_sent = 1, time_sent = now()")
					.where("notification_id = "+notificationID)
					.toString();
			stmt = database1.getDatabaseConnection().createStatement();
			stmt.execute(sqlCommand);
		}
		catch(DatabaseTypeException | NullPointerException | IllegalArgumentException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
		finally {
			Utility.closeResource(stmt);
			database1.disconnect();
		}

	}	
}
