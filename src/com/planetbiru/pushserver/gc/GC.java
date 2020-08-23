package com.planetbiru.pushserver.gc;

import java.sql.SQLException;
import java.util.Date;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.database.Database;
import com.planetbiru.pushserver.database.DatabaseConfig;
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
	 * Primary database configuration
	 */
	public DatabaseConfig databaseConfig1;
	/**
	 * Secondary database configuration
	 */
	public DatabaseConfig databaseConfig2;
	/**
	 * Tertiary database configuration
	 */
	public DatabaseConfig databaseConfig3;
	/**
	 * Primary Database object
	 */
	private Database database1;
	/**
	 * Secondary Database object
	 */
	private Database database2;
	/**
	 * Tertiary Database object
	 */
	private Database database3;
	/**
	 * Database connection indicator for primary database object
	 */
	private boolean dbok1 = false;
	/**
	 * Database connection indicator for secondary database object
	 */
	@SuppressWarnings("unused")
	private boolean dbok2 = false;
	/**
	 * Database connection indicator for tertiary database object
	 */
	@SuppressWarnings("unused")
	private boolean dbok3 = false;
	/**
	 * Constructor with database initialization
	 * @param databaseConfig1 Primary database configuration
	 * @param databaseConfig2 Secondary database configuration
	 * @param databaseConfig3 Tertiary database configuration
	 * @throws DatabaseTypeException if database type is not supported
	 * @throws SQLException if any SQL errors
	 * @throws ClassNotFoundException if class not found
	 */
	public GC(DatabaseConfig databaseConfig1, DatabaseConfig databaseConfig2, DatabaseConfig databaseConfig3) throws ClassNotFoundException, SQLException, DatabaseTypeException
	{
		this.databaseConfig1 = databaseConfig1;
		this.databaseConfig2 = databaseConfig2;
		this.databaseConfig3 = databaseConfig3;		
		this.database1 = new Database(this.databaseConfig1);
		this.database2 = new Database(this.databaseConfig2);
		this.database3 = new Database(this.databaseConfig3);
		this.dbok1 = this.database1.connect();
		this.dbok2 = this.database2.connect();
		this.dbok3 = this.database3.connect();
	}
	/**
	 * Override run method
	 */
	public void run()
	{
		try 
		{
			if(this.dbok1)
			{
				this.cleanUp();
			}
		} 
		catch (DatabaseTypeException e) 
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		} 
		catch (SQLException e) 
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
	}
	private void cleanUp() throws DatabaseTypeException, SQLException
	{
		this.cleanUpNotification();
		this.cleanUpTrash();
	}
	/**
	 * Clean up notification
	 * @throws DatabaseTypeException if database type is not supported
	 * @throws SQLException if any SQL errors
	 */
	private void cleanUpNotification()  throws NullPointerException, IllegalArgumentException, DatabaseTypeException, SQLException
	{
		QueryBuilder query1 = new QueryBuilder(this.databaseConfig1.getDatabaseType());
		String sqlCommand;
		long DAY_IN_MS = 86400000;		
		String expire_sent = "";
		String expire_create = "";		
		expire_sent = Utility.date("yyyy-MM-dd HH:mm:ss", new Date(System.currentTimeMillis() - (Config.getDeleteNotifSent() * DAY_IN_MS)));
		expire_create = Utility.date("yyyy-MM-dd HH:mm:ss", new Date(System.currentTimeMillis() - (Config.getDeleteNotifNotSent() * DAY_IN_MS)));		
		sqlCommand = query1.newQuery()
				.delete()
				.from(Config.getTablePrefix()+"notification")
				.where("(is_sent = 1 and time_sent < '"+expire_sent+"') or time_create < '"+expire_create+"' ")
				.toString();
		this.database1.execute(sqlCommand);
	}
	/**
	 * Clean up trash
	 * @throws DatabaseTypeException if database type is not supported
	 * @throws SQLException if any SQL errors
	 */
	private void cleanUpTrash() throws NullPointerException, IllegalArgumentException, DatabaseTypeException, SQLException
	{
		QueryBuilder query1 = new QueryBuilder(this.databaseConfig1.getDatabaseType());
		String sqlCommand;
		long DAY_IN_MS = 86400000;		
		String expire_create = "";		
		expire_create = Utility.date("yyyy-MM-dd HH:mm:ss", new Date(System.currentTimeMillis() - (Config.getDeleteNotifNotSent() * DAY_IN_MS)));		
		sqlCommand = query1.newQuery()
				.delete()
				.from(Config.getTablePrefix()+"trash")
				.where("time_delete < '"+expire_create+"' ")
				.toString();
		this.database1.execute(sqlCommand);
	}
}
