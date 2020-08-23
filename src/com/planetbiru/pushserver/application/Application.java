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
import com.planetbiru.pushserver.gc.GCManager;
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
	
			boolean dbok1 = false;
			boolean dbok2 = false;
			boolean dbok3 = false;
	
			Database database1 = new Database(Config.getDatabaseConfig1());
			Database database2 = new Database(Config.getDatabaseConfig2());
			Database database3 = new Database(Config.getDatabaseConfig3());
	
			if(configured)
			{
				if(debugMode.toUpperCase().trim().equals("TRUE"))
	    		{
	    			Config.setDebugMode(true);
	    		}
	    		else if(debugMode.toUpperCase().trim().equals("FALSE"))
	    		{
	    			Config.setDebugMode(false);
	    		}
				GCManager gcManager = new GCManager(Config.getDatabaseConfig1(), Config.getDatabaseConfig2(), Config.getDatabaseConfig3());
				gcManager.start();			
				try 
				{
					dbok1 = database1.connect();
					dbok2 = database2.connect();
					dbok3 = database3.connect();
				} 
				catch (ClassNotFoundException | SQLException e1) 
				{
					if(Config.isPrintStackTrace())
					{	
						e1.printStackTrace();
					}
					dbok1 = false;
					dbok2 = false;
					dbok3 = false;
					while(!dbok1)
					{
						if(Config.isDebugMode())
						{
							System.out.println("Database Error: "+e1.getMessage());
							System.out.println("Reconnect in  : "+Config.getWaitDatabaseReconnect()+" miliseconds");
						}
						try 
						{
							Thread.sleep(Config.getWaitDatabaseReconnect());
						} 
						catch (InterruptedException e) 
						{
							if(Config.isPrintStackTrace())
							{
								e.printStackTrace();
							}
						}
						try 
						{
							dbok1 = database1.connect();
							dbok2 = database2.connect();
							dbok3 = database3.connect();
						} 
						catch (ClassNotFoundException | SQLException e) 
						{
							if(Config.isPrintStackTrace())
							{
								e.printStackTrace();
							}
							dbok1 = false;
							dbok2 = false;
							dbok3 = false;
						} 
						catch (DatabaseTypeException e) 
						{
							if(Config.isDebugMode())
							{
								System.err.println("Unsupported database type. Make sure that you only use MariaDB, MySQL or PostgreSQL");
							}
							if(Config.isPrintStackTrace())
							{
								e.printStackTrace();
							}
						}
					}
				} 
				catch (DatabaseTypeException e) 
				{
					if(Config.isDebugMode())
					{
						System.err.println("Unsupported database type. Make sure that you only use MariaDB, MySQL or PostgreSQL");
					}
					if(Config.isPrintStackTrace())
					{
						e.printStackTrace();
					}
				}
			}
			else
			{
				if(Config.isDebugMode())
				{
					System.err.println("Database configuration error. Please fix it first!");
				}
			}
			
			if(dbok1)
			{				
				boolean databaseValid = false;
				try 
				{
					if(Application.checkDatabases(database1, database2, database3))
					{
						databaseValid = false;						
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
								    try 
								    {
										SSLContext sslContext = SSLContext.getInstance("TLS");
										keyStore.load (fileInputStream, password);
									    KeyManagerFactory keyManagementFactory = KeyManagerFactory.getInstance("SunX509");
									    keyManagementFactory.init (keyStore, password);
									    TrustManagerFactory trustFactory = TrustManagerFactory.getInstance("SunX509");
									    trustFactory.init (keyStore);
									    sslContext.init(keyManagementFactory.getKeyManagers(), trustFactory.getTrustManagers(), null);							    
										HttpsConfigurator httpsConfigurator = new HttpsConfigurator(sslContext);
										requestHandlerHTTPS.setHttpsConfigurator(httpsConfigurator);										
										if(Config.isConnectionPerPush())
										{
											requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextPusher(), new PusherHandler("push-notification", Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), database1, database2, database3));
											requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRemover(), new PusherHandler("delete-notification", Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), database1, database2, database3));	
											requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextCreateGroup(), new PusherHandler("create-group", Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), database1, database2, database3));		
											requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRegisterDevice(), new PusherHandler("register-device", Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), database1, database2, database3));		
											requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextUnregisterDevice(), new PusherHandler("unregister-device", Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), database1, database2, database3));		
										}
										else
										{
											requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextPusher(), new PusherHandler("push-notification", database1, database2, database3));
											requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRemover(), new PusherHandler("delete-notification", database1, database2, database3));
											requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextCreateGroup(), new PusherHandler("create-group", database1, database2, database3));	
											requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRegisterDevice(), new PusherHandler("register-device", database1, database2, database3));	
											requestHandlerHTTPS.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextUnregisterDevice(), new PusherHandler("register-device", database1, database2, database3));	
										}
										requestHandlerHTTPS.createContext("/", new WelcomeHandler("welcome"));
										requestHandlerHTTPS.createContext("/ping", new WelcomeHandler("ping"));
										requestHandlerHTTPS.start();
										System.out.println("SSL Service for pusher is started");
									} 
								    catch (NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) 
								    {
										e.printStackTrace();
									} 						
								} 
								catch (KeyStoreException e) 
								{
									e.printStackTrace();
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
							if(Config.isConnectionPerPush())
							{
								requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextPusher(), new PusherHandler("push-notification", Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), database1, database2, database3));
								requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRemover(), new PusherHandler("delete-notification", Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), database1, database2, database3));			
								requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextCreateGroup(), new PusherHandler("create-group", Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), database1, database2, database3));			
								requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRegisterDevice(), new PusherHandler("register-device", Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), database1, database2, database3));			
								requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextUnregisterDevice(), new PusherHandler("unregister-device", Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), Config.getDatabaseConfig1(), database1, database2, database3));			
							}
							else
							{
								requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextPusher(), new PusherHandler("push-notification", database1, database2, database3));
								requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRemover(), new PusherHandler("delete-notification", database1, database2, database3));
								requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextCreateGroup(), new PusherHandler("create-group", database1, database2, database3));
								requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextRegisterDevice(), new PusherHandler("register-device", database1, database2, database3));
								requestHandlerHTTP.createContext("/"+Config.getApiDocumentRoot()+"/"+Config.getVersion()+"/"+Config.getPusherContextUnregisterDevice(), new PusherHandler("unregister-device", database1, database2, database3));
							}
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
							Application.notificationServerSSL = new NotificationServerSSL(database1, database2, database3);
							Application.notificationServerSSL.start();
							System.out.println("SSL Service for notification is started");
						}
						
						Application.notificationServer = new NotificationServer(database1, database2, database3);
						Application.notificationServer.start();
						System.out.println("Service for notification is started");
						
					}
					else
					{
						databaseValid = false;
					}
				} 
				catch (DatabaseTypeException e1) 
				{
					if(Config.isPrintStackTrace())
					{
						e1.printStackTrace();
					}
					databaseValid = false;
					System.err.println("Database type in unsupported.");
				} 
				catch (SQLException e1) 
				{
					if(Config.isPrintStackTrace())
					{
						e1.printStackTrace();
					}
					databaseValid = false;
					System.err.println("Database exists but errors occured while access id.");
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
				catch (TableNotFoundException e1) 
				{
					if(Config.isPrintStackTrace())
					{
						e1.printStackTrace();
					}
					databaseValid = false;
					System.err.println("Database exists but required table of function is not exists. Please import database first.");
					System.err.println(e1.getMessage());
				}
				catch (DatabaseFunctionFoundException e1) 
				{
					if(Config.isPrintStackTrace())
					{
						e1.printStackTrace();
					}
					databaseValid = false;
					System.err.println("Database exists but required function of function is not exists. Please import database or define function first.");
					System.err.println(e1.getMessage());
				}
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
	public static boolean checkTables(Database database1, Database database2, Database database3) throws DatabaseTypeException, SQLException, IndexOutOfBoundsException, TableNotFoundException
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
		
		QueryBuilder query1;
		query1 = new QueryBuilder(Config.getDatabaseConfig1().getDatabaseType());
		String sqlCommand = "";
		String tableName = "";
		ResultSet rs;
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
		boolean valid = false;
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
	public static boolean checkFunctions(Database database1, Database database2, Database database3) throws DatabaseTypeException, SQLException, IndexOutOfBoundsException, TableNotFoundException, DatabaseFunctionFoundException
	{
		
		QueryBuilder query1;
		query1 = new QueryBuilder(Config.getDatabaseConfig1().getDatabaseType());
		String sqlCommand = "";
		ResultSet rs;
		sqlCommand = query1.newQuery()
				.select("sha1('123') as result")
				.toString();
		try
		{
			rs = database1.executeQuery(sqlCommand);
			if(rs.isBeforeFirst())
			{
				rs.next();
				String val = rs.getString("result");
				if(val.length() > 20)
				{
					return true;
				}
				else
				{
					throw new DatabaseFunctionFoundException("Function sha1 not found.");
				}
			}
			else
			{
				throw new DatabaseFunctionFoundException("Function sha1 not found.");
			}
		}
		catch(SQLException e)
		{
			throw new DatabaseFunctionFoundException("Function sha1 not found.");		
		}
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
	public static boolean checkDatabases(Database database1, Database database2, Database database3) throws DatabaseTypeException, SQLException, IndexOutOfBoundsException, TableNotFoundException, DatabaseFunctionFoundException
	{
		boolean valid2 = Application.checkFunctions(database1, database2, database3);
		boolean valid1 = Application.checkTables(database1, database2, database3);
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
