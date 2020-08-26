package com.planetbiru.pushserver.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.planetbiru.pushserver.config.Config;

/**
 * Process killer to kill current service. 
 * On Windows, it will kill Java. 
 * On Linux, it will kill only process with same image name.
 * @author Kamshory, MT
 *
 */
public class ProcessKiller {
	private Logger logger = LoggerFactory.getLogger(ProcessKiller.class);
	/**
	 * Image name of this application
	 */
	public String path = "";
	private boolean exceptThis = false;	
	
	/**
	 * Default constuctor 
	 */
	public ProcessKiller()
	{		
	}
	/**
	 * Construtor with path initialization
	 * @param path Application path to be killed
	 */
	public ProcessKiller(String path)
	{
		this.path = path;
	}
	/**
	 * Construtor with path initialization
	 * @param path Application path to be killed
	 * @param exceptThis Except this
	 */
	public ProcessKiller(String path, boolean exceptThis)
	{
		this.path = path;
		this.exceptThis = exceptThis;
	}
	/**
	 * Get basename of specified path
	 * @param path Path name
	 * @return Base name
	 */ 
	public String baseName(String path)
	{
		path = path.replace("\\", "/");
		String name = "";
		if(!path.contains("/"))
		{
			name = path;
		}
		else
		{	
			String [] arr = path.split("\\/");
			name = arr[arr.length-1];
		}
		return name;
	}
	/**
	 * Stop process
	 * @return true if success and false if failed
	 */ 
	public boolean stop()
	{
		ArrayList<String> processListWindows = new ArrayList<>();
		ArrayList<String> processList = new ArrayList<>();
		String operatingSystem = System.getProperty("os.name").toLowerCase();
		String commandLine = "";
		String datetime = "";
		String pid = "";
        int i = 0;
        int prcosessCount = 0;
        int processToKill = 0;
 		if(operatingSystem.contains("windows"))
		{
			commandLine = "wmic process get processid,creationdate,name,commandline /format:csv";
			try
	        {            
				Runtime rt = Runtime.getRuntime();
		        Process proc = rt.exec(commandLine);
	            InputStream simpuStream = proc.getInputStream();
	            InputStreamReader simpuStreamReader = new InputStreamReader(simpuStream);
	            BufferedReader bufferedReader = new BufferedReader(simpuStreamReader);
				String line = null;
	            while ((line = bufferedReader.readLine()) != null)
	            {
 	            	String basename = this.baseName(this.path).toLowerCase();
 					if(line.toLowerCase().contains("java") && line.toLowerCase().contains(basename ))
	            	{
	            		String[] arr = line.split(",");
	            		if(arr.length > 4)
	            		{
	            			if(arr[1].toLowerCase().contains("java") && arr[1].toLowerCase().contains(basename) && arr[3].toLowerCase().contains("java.exe"))
	            			{
	            				datetime = arr[2];
		            			pid = arr[4];
		            			processListWindows.add(datetime+","+pid);	  
	            			}
	            		}
	            	}
	            }
	            bufferedReader.close();	 
	            // Sort by date and time
	            Collections.sort(processListWindows);
		        prcosessCount = processListWindows.size();
	            
	            for(i = 0; i<prcosessCount; i++)
	            {
	            	line = processListWindows.get(i);
            		String[] arr = line.split(",");
        			pid = arr[1];
        			processList.add(pid.trim());	            				            				            	
	            }
	            
		        prcosessCount = processList.size();
		        if(this.exceptThis)
		        {
		        	processToKill = prcosessCount - 1;
		        }
		        else
		        {
		        	processToKill = prcosessCount;
		        }
	        	for(i = 0; i < processToKill; i++)
	        	{
	        		pid = processList.get(i);
		            String commandLine2 = "taskkill /PID "+pid+" /F";
	    			Runtime rt2 = Runtime.getRuntime();
			        rt2.exec(commandLine2);
			        logger.info("Killing PID {}", pid);
	        	}
	        } 
			catch (Throwable t)
	        {
	            t.printStackTrace();
	        }
		}
		else
		{
			try 
			{
				commandLine = "ps -ef --sort=start_time";
				try
		        {            
					Runtime rt = Runtime.getRuntime();
			        Process proc = rt.exec(commandLine);
			        proc.waitFor();
		            InputStream simpuStream = proc.getInputStream();
		            InputStreamReader simpuStreamReader = new InputStreamReader(simpuStream);
		            BufferedReader bufferedReader = new BufferedReader(simpuStreamReader);
					String line = null;
					String lineLower = "";
		            while ((line = bufferedReader.readLine()) != null)
		            {
		            	if(line.toLowerCase().contains("java ") && line.toLowerCase().contains("-jar ") && line.toLowerCase().contains(this.baseName(this.path).toLowerCase()))
		            	{
		            		line = line.replace("\t", " ");
		            		line = line.replace("  ", " ").trim();
		            		line = line.replace("  ", " ").trim();
		            		line = line.replace("  ", " ").trim();
		            		line = line.replace("  ", " ").trim();
		            		line = line.replace("  ", " ").trim();
		            		String[] arr = line.split(" ");
		            		if(arr.length > 1)
		            		{
		            			lineLower = line;
		            			lineLower = lineLower.toLowerCase();
		            			pid = arr[1];
		            			processList.add(pid.trim());	            				            			

		            		}
		            	}
		            }
		            bufferedReader.close();
			        prcosessCount = processList.size();
			        if(this.exceptThis)
			        {
			        	processToKill = prcosessCount - 1;
			        }
			        else
			        {
			        	processToKill = prcosessCount;
			        }
		        	for(i = 0; i < processToKill; i++)
		        	{
		        		pid = processList.get(i);
	        			String commandLine2 = "kill -9 "+pid+"";
	        			Runtime rt2 = Runtime.getRuntime();
	    		        rt2.exec(commandLine2);
	    		        logger.info("Killing PID {}", pid);
		        	}
		        } 
				catch (Throwable t)
		        {
		            t.printStackTrace();
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
		return true;
	}
}
