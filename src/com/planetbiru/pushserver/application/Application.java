package com.planetbiru.pushserver.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONException;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.database.Database;
import com.planetbiru.pushserver.database.DatabaseFunctionFoundException;
import com.planetbiru.pushserver.database.DatabaseTypeException;
import com.planetbiru.pushserver.database.QueryBuilder;
import com.planetbiru.pushserver.database.TableNotFoundException;
import com.planetbiru.pushserver.httphandler.PusherHandler;
import com.planetbiru.pushserver.httphandler.WelcomeHandler;
import com.planetbiru.pushserver.utility.ProcessKiller;
import com.planetbiru.pushserver.utility.Utility;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

/**
 * <p><strong>Push Notification</strong> is a notification that is forcibly sent by the server to the client so that the notification sent to the client without waiting for the client to request it. In order for the notification to be accepted by the client, the client and server must always be connected through socket communication.</p>
 * <p>The notification server can be part of the application server and can also be provided by third parties.</p>
 * <p>The application server must know the device ID of each user. When the application server sends notifications to users, the application server sends notifications to the notification server that is addressed to the user's device.</p>
 * @author Kamshory, MT
 * @version 1.1.0
 */
public class Application 
{
	private static NotificationServer notificationServer;
	private static NotificationServerSSL notificationServerSSL;
	private static long requestID = 0;
	
	public static long getRequestID() {
		return requestID;
	}
	public static void setRequestID(long requestID) {
		Application.requestID = requestID;
	}
	/**
	 * Main method
	 * @param args Arguments when application is executed
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
    	String pathName = new java.io.File(Application.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();    	
    	if(pathName.length() < 5)
    	{
	    	File f = new File(System.getProperty("java.class.path"));
	    	File dir = f.getAbsoluteFile().getAbsoluteFile();
	    	pathName = dir.toString();
    	}
    	if(pathName.toLowerCase().contains(".jar"))
		{
    		pathName = Application.fixPath(pathName);
		}
    	
    	String action = "";
    	String configPath = "";
    	String debugMode = "";
    	boolean configured = false;
    	action = Utility.getFirst(args, "action", "", "=").toLowerCase();
    	configPath = Utility.getFirst(args, "config", "", "=").trim();
    	debugMode = Utility.getFirst(args, "debug-mode", "", "=").trim();
    	if(args.length > 0)
    	{
	    	if(args[0].contains("-h") || args[0].contains("--help"))
	    	{
	    		System.out.println("Push Notification Server version "+Config.getVersion()+"\r\n");	    		
	    		System.out.println("|-----------------------------|--------------------------------------|");
	    		System.out.println("| Argument                    | Meaning                              |");
	    		System.out.println("|-----------------------------|--------------------------------------|");
	    		System.out.println("| config={config-path-file}   | Set configuration path file          |");
	    		System.out.println("| action=start                | Start service                        |");
	    		System.out.println("| action=stop                 | Stop service/kill service            |");
	    		System.out.println("| debug-mode=true             | Run service in debug mode            |");
	    		System.out.println("|-----------------------------|--------------------------------------|");
	    		return;
	    	}
    	}
    	
    	if(action.equals("stop"))
    	{
    		try 
    		{
				Application.killProcess(pathName);
			} 
    		catch (IOException e) 
    		{
    			System.out.println("Can not kill the process. "+e.getMessage()+"\r\nPlease try again");
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
			}
    	}
    	else
    	{
    		if(action.equals("stop-other"))
        	{
        		try 
        		{
    				Application.killProcess(pathName, true);
    			} 
        		catch (IOException e) 
        		{
        			System.out.println("Can not kill the process. "+e.getMessage()+"\r\nPlease try again");
    				if(Config.isPrintStackTrace())
    				{
    					e.printStackTrace();
    				}
    			}
        		try 
        		{
					Thread.sleep(Config.getWaitFreeUpPort());
				}
        		catch(IllegalArgumentException | InterruptedException e)
        		{
        			if(Config.isPrintStackTrace())
					{
						e.printStackTrace();
					}
        		}
        	}
	    	
	    	if(configPath.length() > 0)
	    	{
	    		try 
	    		{
					configured = Config.loadExternalConfig(configPath);
				} 
	    		catch (InvalidKeyException | NumberFormatException | JSONException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException e) 
	    		{
	    			if(Config.isPrintStackTrace())
					{
						e.printStackTrace();
					}
				} 
	    	}
	    	else
	    	{
	    		try 
	    		{
					configured = Config.loadInternalConfig("config.ini");
				} 
	    		catch (InvalidKeyException | NumberFormatException | JSONException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) 
	    		{
	    			if(Config.isPrintStackTrace())
					{
						e.printStackTrace();
					}
				} 
	    	}
	
	    	Config.setDebugMode(debugMode.equals("true") || debugMode.equals("yes") || debugMode.equals("ok"));
			
			boolean databaseValid = false;
			try 
			{
				if(Config.isPusherSSLEnabled())
				{
					HttpsServer requestHandlerHTTPS;
					try 
					{
						requestHandlerHTTPS = HttpsServer.create(new InetSocketAddress(Config.getPusherPortSSL()), 0);
					    char[] password = Config.getKeystorePassword().toCharArray();
					    KeyStore keyStore;
						try 
						{
							keyStore = KeyStore.getInstance("JKS");
							FileInputStream fileInputStream = new FileInputStream (Config.getKeystoreFile());
							SSLContext sslContext = SSLContext.getInstance("TLS");
							keyStore.load (fileInputStream, password);
						    KeyManagerFactory keyManagementFactory = KeyManagerFactory.getInstance("SunX509");
						    keyManagementFactory.init (keyStore, password);
						    TrustManagerFactory trustFactory = TrustManagerFactory.getInstance("SunX509");
						    trustFactory.init (keyStore);
						    sslContext.init(keyManagementFactory.getKeyManagers(), trustFactory.getTrustManagers(), null);							    
							HttpsConfigurator httpsConfigurator = new HttpsConfigurator(sslContext);
							requestHandlerHTTPS.setHttpsConfigurator(httpsConfigurator);										
							requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextPusher(), new PusherHandler("push-notification"));
							requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRemover(), new PusherHandler("delete-notification"));	
							requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextCreateGroup(), new PusherHandler("create-group"));		
							requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRegisterDevice(), new PusherHandler("register-device"));		
							requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextUnregisterDevice(), new PusherHandler("unregister-device"));		
							requestHandlerHTTPS.createContext("/", new WelcomeHandler("welcome"));
							requestHandlerHTTPS.createContext("/ping", new WelcomeHandler("ping"));
							requestHandlerHTTPS.start();
							System.out.println("SSL Service for pusher is started");
						} 
					    catch (NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) 
					    {
					    	if(Config.isPrintStackTrace())
							{
					    		e.printStackTrace();
							}
						} 						
						catch (KeyStoreException e) 
						{
							if(Config.isPrintStackTrace())
							{
					    		e.printStackTrace();
							}
						}
					} 
					catch (IOException e) 
					{
						if(Config.isPrintStackTrace())
						{
							e.printStackTrace();
						}
					}
				}						
				HttpServer requestHandlerHTTP;
				try 
				{
					requestHandlerHTTP = HttpServer.create(new InetSocketAddress(Config.getPusherPort()), 0);
					requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextPusher(), new PusherHandler("push-notification"));
					requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRemover(), new PusherHandler("delete-notification"));			
					requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextCreateGroup(), new PusherHandler("create-group"));			
					requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRegisterDevice(), new PusherHandler("register-device"));			
					requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextUnregisterDevice(), new PusherHandler("unregister-device"));			
					requestHandlerHTTP.createContext("/", new WelcomeHandler("welcome"));
					requestHandlerHTTP.createContext("/ping", new WelcomeHandler("ping"));
					requestHandlerHTTP.start();
					System.out.println("Service for pusher is started");
				} 
				catch (IOException e) 
				{
					if(Config.isPrintStackTrace())
					{
						e.printStackTrace();
					}
				}
				
				if(Config.isNotificationSSLEnabled())
				{
					Application.notificationServerSSL = new NotificationServerSSL();
					Application.notificationServerSSL.start();
					System.out.println("SSL Service for notification is started");
				}
				
				Application.notificationServer = new NotificationServer();
				Application.notificationServer.start();
				System.out.println("Service for notification is started");
				
			} 
			catch(IndexOutOfBoundsException e1)
			{
				if(Config.isPrintStackTrace())
				{
					e1.printStackTrace();
				}
				databaseValid = false;
				System.err.println("Database exists but errors occured while access id.");
			}
		}
	}
	/**
	 * Fix file path
	 * @param path File path to be fixed
	 * @return Fixed file path
	 */
	private static String fixPath(String path) 
	{
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.contains("windows"))
		{
			path = path.replace("/", "\\");
			while(path.startsWith("\\"))
			{
				path = path.substring(1);
			}
		}
		else
		{
		}
		return path;
	}
	/**
	 * Table validation
	 * @param database1 Primary database object
	 * @param database2 Secondary database object
	 * @param database3 Tertiary database object
	 * @return true if valid and false if invalid
	 * @throws DatabaseTypeException if database type is not supported
	 * @throws SQLException if any SQL errors
	 * @throws IndexOutOfBoundsException if out of bound
	 * @throws TableNotFoundException if required table is not exists
	 */
	public static boolean checkTables() throws DatabaseTypeException, SQLException, IndexOutOfBoundsException, TableNotFoundException
	{
		String prefix = Config.getTablePrefix().trim();
		ArrayList<String> tables = new ArrayList<>();
		ArrayList<String> list = new ArrayList<>();

		list.add(prefix+"api");
		list.add(prefix+"client");
		list.add(prefix+"client_group");
		list.add(prefix+"notification");
		list.add(prefix+"pusher_address");
		list.add(prefix+"trash");

		boolean valid = false;

		Database database1 = new Database(Config.getDatabaseConfig1());
		ResultSet rs = null;
		try
		{
			database1.connect();
			QueryBuilder query1 = new QueryBuilder(Config.getDatabaseConfig1().getDatabaseType());
			String sqlCommand = "";
			String tableName = "";
			if(Config.getDatabaseConfig1().getDatabaseType().equals("postgresql"))
			{			
				sqlCommand = query1.newQuery()
						.select("*")
						.from("pg_catalog.pg_tables")
						.where("schemaname != 'pg_catalog' and schemaname != 'information_schema'")
						.toString();
				rs = database1.executeQuery(sqlCommand);
				if(rs.isBeforeFirst())
				{
					while(rs.next())
					{
						tableName = rs.getString("tablename");
						tables.add(tableName);
					}
				}
			}
			else if(Config.getDatabaseConfig1().getDatabaseType().equals("mysql") || Config.getDatabaseConfig1().getDatabaseType().equals("mariadb"))
			{
				sqlCommand = query1.newQuery("show tables")
						.toString();
				
				rs = database1.executeQuery(sqlCommand);
				if(rs.isBeforeFirst())
				{
					while(rs.next())
					{
						tableName = rs.getString(1);
						tables.add(tableName);
					}
				}
			}
			int i;
			for(i = 0; i < list.size(); i++)
			{
				if(tables.contains(list.get(i)))
				{
					valid = true;
				}
				else
				{
					throw new TableNotFoundException("Table "+list.get(i)+" not found.");
				}
			}
		}
		catch(DatabaseTypeException | NullPointerException | IllegalArgumentException | ClassNotFoundException | SQLException e)
		{
			
		}
		finally {
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			database1.disconnect();
		}
		return valid;
	}
	/**
	 * Function validation
	 * @param database1 Primary database object
	 * @param database2 Secondary database object
	 * @param database3 Tertiary database object
	 * @return true if valid and false if invalid
	 * @throws DatabaseTypeException if database type is not supported
	 * @throws SQLException if any SQL errors
	 * @throws IndexOutOfBoundsException if out of bound
	 * @throws TableNotFoundException if table is not exists
	 * @throws DatabaseFunctionFoundException if function is not exists
	 */
	public static boolean checkFunctions()
	{
		
		boolean valid = false;

		Database database1 = new Database(Config.getDatabaseConfig1());
		ResultSet rs = null;
		try
		{
			database1.connect();
			QueryBuilder query1 = new QueryBuilder(Config.getDatabaseConfig1().getDatabaseType());
			String sqlCommand = "";
			sqlCommand = query1.newQuery()
					.select("sha1('123') as result")
					.toString();
			rs = database1.executeQuery(sqlCommand);
			if(rs.isBeforeFirst())
			{
				rs.next();
				String val = rs.getString("result");
				valid = (val.length() > 20);
			}
		}
		catch(DatabaseTypeException | NullPointerException | IllegalArgumentException | ClassNotFoundException | SQLException e)
		{
			
		}
		finally {
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			database1.disconnect();
		}
		return valid;	
	}
	/**
	 * Database validation
	 * @param database1 Primary database object
	 * @param database2 Secondary database object
	 * @param database3 Tertiary database object
	 * @return true if valid and false if invalid
	 * @throws DatabaseTypeException if database type is not supported
	 * @throws SQLException if any SQL errors
	 * @throws IndexOutOfBoundsException if out of bound
	 * @throws TableNotFoundException if required table is not exists
	 * @throws DatabaseFunctionFoundException if function is not exists
	 */
	public static boolean checkDatabases() throws DatabaseTypeException, SQLException, IndexOutOfBoundsException, TableNotFoundException, DatabaseFunctionFoundException
	{
		boolean valid2 = Application.checkFunctions();
		boolean valid1 = Application.checkTables();
		return valid1 && valid2;
	}
	/**
	 * Kill process
	 * @param path File path
	 * @throws IOException if any IO errors
	 */
	public static void killProcess(String path) throws IOException
	{
		ProcessKiller killer = new ProcessKiller(path);
		killer.stop();
	} 
	/**
	 * Kill process
	 * @param path File path
	 * @throws IOException if any IO errors
	 */
	public static void killProcess(String path, boolean exceptThis) throws IOException
	{		
		ProcessKiller killer = new ProcessKiller(path, exceptThis);
		killer.stop();
	} 
}
