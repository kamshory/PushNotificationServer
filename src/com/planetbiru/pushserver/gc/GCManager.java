package com.planetbiru.pushserver.gc;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.database.DatabaseConfig;
import com.planetbiru.pushserver.utility.Utility;

/**
 * GC manager to manage garbage collector
 * @author Kamshory, MT
 *
 */
public class GCManager extends Thread
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
	 * Garbage collection
	 */
	private GC gc;
	/**
	 * Constructor
	 * @param databaseConfig1 Primary database configuration
	 * @param databaseConfig2 Secondary database configuration
	 * @param databaseConfig3 Tertiary database configuration
	 */
	public GCManager(DatabaseConfig databaseConfig1, DatabaseConfig databaseConfig2, DatabaseConfig databaseConfig3)
	{
		this.databaseConfig1 = databaseConfig1;
		this.databaseConfig2 = databaseConfig2;
		this.databaseConfig3 = databaseConfig3;
	}
	/**
	 * Override run method
	 */
	public void run()
	{
		String now = "";
		while(true)
		{
			try 
			{
				Thread.sleep(Config.getGcInterval());
				now = Utility.now("HH:mm");
				if(now.equals(Config.getCleanUpTime()))
				{
					this.gc = new GC();
					this.gc.start();
				}
			} 
			catch (InterruptedException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
			}
			catch(IllegalArgumentException e)
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
			}
		}
	}
}
