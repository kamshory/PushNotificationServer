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
	 * Constructor
	 */
	public GCManager()
	{
		/**
		 * Constructor
		 */
	}
	/**
	 * Override run method
	 */
	
	@Override
	public void run()
	{
		GC gc;
		String now = "";
		while(true)
		{
			try 
			{
				Thread.sleep(Config.getGcInterval());
				now = Utility.now("HH:mm");
				if(now.equals(Config.getCleanUpTime()))
				{
					gc = new GC();
					gc.start();
				}
			} 
			catch (IllegalArgumentException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
			}
			catch(InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}
}
