package com.planetbiru.pushserver.gc;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.utility.Utility;

/**
 * GC manager to manage garbage collector
 * @author Kamshory, MT
 *
 */
public class GCManager extends Thread
{
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
	public GCManager()
	{
	}
	/**
	 * Override run method
	 */
	
	@Override
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
