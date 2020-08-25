package com.planetbiru.pushserver.gc;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.database.Database;
import com.planetbiru.pushserver.database.QueryBuilder;
import com.planetbiru.pushserver.database.DatabaseTypeException;
import com.planetbiru.pushserver.utility.Utility;

/**
 * Garbage collector
 * @author Kamshory, MT
 *
 */
public class GC extends Thread
{
	/**
	 * Constructor with database initialization
	 */
	public GC()
	{
		/**
		 * Default constructor
		 */
	}
	/**
	 * Override run method
	 */
	@Override
	public void run()
	{
		this.cleanUp();
	}
	private void cleanUp()
	{
		this.cleanUpNotification();
		this.cleanUpTrash();
	}
	/**
	 * Clean up notification
	 * @throws DatabaseTypeException if database type is not supported
	 * @throws SQLException if any SQL errors
	 */
	private void cleanUpNotification()
	{
		Database database1 = new Database(Config.getDatabaseConfig1());
		Statement stmt = null;
		try
		{
			database1.connect();
			QueryBuilder query1 = new QueryBuilder(database1.getDatabaseType());
			String sqlCommand;
			long dayInMS = 86400000;		
			String expireSent = "";
			String expireCreate = "";		
			expireSent = Utility.date("yyyy-MM-dd HH:mm:ss", new Date(System.currentTimeMillis() - (Config.getDeleteNotifSent() * dayInMS)));
			expireCreate = Utility.date("yyyy-MM-dd HH:mm:ss", new Date(System.currentTimeMillis() - (Config.getDeleteNotifNotSent() * dayInMS)));		
			sqlCommand = query1.newQuery()
					.delete()
					.from(Config.getTablePrefix()+"notification")
					.where("(is_sent = 1 and time_sent < '"+expireSent+"') or time_create < '"+expireCreate+"' ")
					.toString();
			stmt = database1.getDatabaseConnection().createStatement();
			stmt.execute(sqlCommand);
		}
		catch(DatabaseTypeException | NullPointerException | IllegalArgumentException | ClassNotFoundException | SQLException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
		finally {
			Database.closeStatement(stmt);
			database1.disconnect();
		}
	}
	/**
	 * Clean up trash
	 * @throws DatabaseTypeException if database type is not supported
	 * @throws SQLException if any SQL errors
	 */
	private void cleanUpTrash()
	{
		Database database1 = new Database(Config.getDatabaseConfig1());
		Statement stmt = null;
		try
		{
			database1.connect();
			QueryBuilder query1 = new QueryBuilder(database1.getDatabaseType());
			String sqlCommand;
			long dayInMS = 86400000;		
			String expireCreate = "";		
			expireCreate = Utility.date("yyyy-MM-dd HH:mm:ss", new Date(System.currentTimeMillis() - (Config.getDeleteNotifNotSent() * dayInMS)));		
			sqlCommand = query1.newQuery()
					.delete()
					.from(Config.getTablePrefix()+"trash")
					.where("time_delete < '"+expireCreate+"' ")
					.toString();
			stmt = database1.getDatabaseConnection().createStatement();
			stmt.execute(sqlCommand);
		}
		catch(DatabaseTypeException | NullPointerException | IllegalArgumentException | ClassNotFoundException | SQLException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
		finally {
			Database.closeStatement(stmt);
			database1.disconnect();
		}
	}
}
