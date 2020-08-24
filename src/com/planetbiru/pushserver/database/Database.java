package com.planetbiru.pushserver.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.utility.Encryption;
import com.planetbiru.pushserver.utility.Utility;

/**
 * This class is used to access the databases
 * @author Kamshory, MT
 *
 */
public final class Database {
	/**
	 * Database connection mode. The value can be:<br>
	 * 1 = one connection for all transaction<br>
	 * 2 = one connection for one client/member<br>
	 * 3 = one connection for one transaction<br>
	 * Each of these value options has advantages and disadvantages.
	 */
	private static int databaseConnectionMode =  1;
	/**
	 * Database driver.
	 * Supported database type are: mysql, mariadb, postgresql
	 */
	private String databaseType = "mysql";
	/**
	 * Database connection
	 */
	private Connection databaseConnection;
	/**
	 * Database host name
	 */
	private String databaseHostName = "localhost";
	/**
	 * Database port
	 */
	private int databasePortNumber = 3066;
	/**
	 * Database user
	 */
	private String databaseUserName = "root";
	/**
	 * Database password
	 */
	private String databaseUserPassword = "";
	/**
	 * Database name
	 */
	private String databaseName = "";
	/**
	 * Connection status
	 */
	private boolean connected = false;
	/**
	 * Force disconect
	 */
	private static boolean forceDisconnect = false;
	/**
	 * Database configuration
	 */
	private DatabaseConfig databaseConfig = new DatabaseConfig();
	/**
	 * Flag whether database is used or not
	 */
	private boolean databaseUsed = false;
	
	public static int getDatabaseConnectionMode() {
		return databaseConnectionMode;
	}
	public static void setDatabaseConnectionMode(int databaseConnectionMode) {
		Database.databaseConnectionMode = databaseConnectionMode;
	}
	public Connection getDatabaseConnection() {
		return databaseConnection;
	}
	public void setDatabaseConnection(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}
	public String getDatabaseHostName() {
		return databaseHostName;
	}
	public void setDatabaseHostName(String databaseHostName) {
		this.databaseHostName = databaseHostName;
	}
	public int getDatabasePortNumber() {
		return databasePortNumber;
	}
	public void setDatabasePortNumber(int databasePortNumber) {
		this.databasePortNumber = databasePortNumber;
	}
	public String getDatabaseUserName() {
		return databaseUserName;
	}
	public void setDatabaseUserName(String databaseUserName) {
		this.databaseUserName = databaseUserName;
	}
	public String getDatabaseUserPassword() {
		return databaseUserPassword;
	}
	public void setDatabaseUserPassword(String databaseUserPassword) {
		this.databaseUserPassword = databaseUserPassword;
	}
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	public static boolean isForceDisconnect() {
		return forceDisconnect;
	}
	public static void setForceDisconnect(boolean forceDisconnect) {
		Database.forceDisconnect = forceDisconnect;
	}
	public boolean isDatabaseUsed() {
		return databaseUsed;
	}
	public void setDatabaseUsed(boolean databaseUsed) {
		this.databaseUsed = databaseUsed;
	}
	public void setDatabaseConfig(DatabaseConfig databaseConfig) {
		this.databaseConfig = databaseConfig;
	}
	/**
	 * Default constructor
	 */
	public Database()
	{
	}
	/**
	 * Construct an object and initialize configuration using property file
	 * @param configPath Configuration file path
	 * @throws IOException if any IO errors
	 */
	public Database(String configPath) throws IOException
	{
		this.setConfigurationPath(configPath);
	}
	/**
	 * Constructor with database configuration
	 * @param databaseConfig Database configuration
	 */
	public Database(DatabaseConfig databaseConfig)
	{
		this.databaseConfig = databaseConfig;
		this.databaseHostName = databaseConfig.getDatabaseHostName();
		this.databasePortNumber = databaseConfig.getDatabasePortNumber();
		this.databaseType = databaseConfig.getDatabaseType();
		this.databaseUserName = databaseConfig.getDatabaseUserName();
		this.databaseUserPassword = databaseConfig.getDatabaseUserPassword();
		this.databaseName = databaseConfig.getDatabaseName();
		this.databaseUsed  = databaseConfig.isDatabaseUsed();
	}
	public DatabaseConfig getDatabaseConfig()
	{
		return this.databaseConfig;
	}
	/**
	 * Connect to the database threads
	 * @param databaseType Database type supported. i.e postgresql, mysql, mariadb
	 * @param databaseHostName Database host name
	 * @param databasePortNumber Database threads port
	 * @param databaseUserName User name to connect to the database
	 * @param databaseUserPassword User password to connect to the database
	 * @param databaseName Database name
	 * @return true if success and false if failed.
	 * @throws SQLException if any SQL errors
	 * @throws ClassNotFoundException if class not found
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public boolean connect(String databaseType, String databaseHostName, int databasePortNumber, String databaseUserName, String databaseUserPassword, String databaseName) throws ClassNotFoundException, SQLException, DatabaseTypeException
	{
		this.databaseType = databaseType;
		this.databaseHostName = databaseHostName;
		this.databasePortNumber = databasePortNumber;
		this.databaseUserName = databaseUserName;
		this.databaseUserPassword = databaseUserPassword;
		this.databaseName = databaseName;
		return this.connect();
	}
	/**
	 * Connect to the database threads
	 * @return true if success and false is failed.
	 * @throws ClassNotFoundException if class not found
	 * @throws SQLException if any SQL errors 
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public boolean connect() throws ClassNotFoundException, SQLException, DatabaseTypeException
	{
		if(this.databaseUsed)
		{
			if(this.databaseType.equals("postgresql"))
			{				
				Class.forName("org.postgresql.Driver");
				this.databaseConnection = DriverManager.getConnection(
					"jdbc:postgresql"
					+"://"+this.databaseHostName+":"
					+this.databasePortNumber+"/"
					+this.databaseName, 
					this.databaseUserName, 
					this.databaseUserPassword
					);
				if(this.databaseConnection == null)
				{
					this.connected = false;
				}
			}		
			else if(this.databaseType.equals("mysql"))
			{		
			    Class.forName("com.mysql.jdbc.Driver");
			    this.databaseConnection = DriverManager.getConnection(
					"jdbc:mysql"
					+"://"+this.databaseHostName+":"
					+this.databasePortNumber+"/"
					+this.databaseName, 
					this.databaseUserName, 
					this.databaseUserPassword
					);	
				if(this.databaseConnection == null)
				{
					this.connected = false;				
				}
			}
			else
			{
				this.connected = false;		
				throw new DatabaseTypeException("Unsupported database type ("+this.databaseType+")");
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * Reconnect database after delay
	 * @param wait Delay
	 * @return true if success and false if failed
	 * @throws ClassNotFoundException if class not found
	 * @throws SQLException if any SQL errors
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public boolean reconnect(long wait) throws ClassNotFoundException, SQLException, DatabaseTypeException
	{
		boolean coonected = false;
		if(this.databaseUsed)
		{
			try 
			{
				Thread.sleep(wait);
				coonected = this.connect();
				if(!coonected)
				{
					if(Config.isDebugMode())
					{
						System.out.println("Reconnect to the database server");
					}
				}
			} 
			catch (InterruptedException e) 
			{
				if(Config.isPrintStackTrace())
				{
					e.printStackTrace();
				}
			}
		}
		return coonected;
	}
	/**
	 * Connect database without throws exception 
	 * @return true if success and false if failed
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public boolean justConnect() throws DatabaseTypeException
	{
		if(this.databaseUsed)
		{
			if(this.databaseType.equals("postgresql"))
			{				
				try 
				{
					Class.forName("org.postgresql.Driver");
					this.databaseConnection = DriverManager.getConnection(
							"jdbc:postgresql"
							+"://"+this.databaseHostName+":"
							+this.databasePortNumber+"/"
							+this.databaseName, 
							this.databaseUserName, 
							this.databaseUserPassword
							);
				} 
				catch (ClassNotFoundException | SQLException e) 
				{
					if(Config.isPrintStackTrace())
					{
						e.printStackTrace();
					}
					return false;
				}
				if(this.databaseConnection == null)
				{
					this.connected = false;
				}
			}	
			else if(this.databaseType.equals("mysql"))
			{		
				try 
				{
				    Class.forName("com.mysql.jdbc.Driver");
				    this.databaseConnection = DriverManager.getConnection(
						"jdbc:mysql"
						+"://"+this.databaseHostName+":"
						+this.databasePortNumber+"/"
						+this.databaseName, 
						this.databaseUserName, 
						this.databaseUserPassword
						);	
				}
				catch (ClassNotFoundException | SQLException e) 
				{
					if(Config.isPrintStackTrace())
					{
						e.printStackTrace();
					}
					return false;
				}
				if(this.databaseConnection == null)
				{
					this.connected = false;				
				}
			}
			else
			{
				throw new DatabaseTypeException("Unsupported database type ("+this.databaseType+")");
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * Reconnect database without throws exception 
	 * @return true if success and false if failed
	 */
	public boolean justReconnect()
	{
		if(this.databaseUsed)
		{
			if(this.databaseType.equals("postgresql"))
			{				
				try 
				{
					Class.forName("org.postgresql.Driver");
					this.databaseConnection = DriverManager.getConnection(
						"jdbc:postgresql"
						+"://"+this.databaseHostName+":"
						+this.databasePortNumber+"/"
						+this.databaseName, 
						this.databaseUserName, 
						this.databaseUserPassword
						);
				} 
				catch (ClassNotFoundException | SQLException e) 
				{
					if(Config.isPrintStackTrace())
					{
						e.printStackTrace();
					}
					return false;
				}
				if(this.databaseConnection != null)
				{
					this.connected = true;
				}
			}	
			else if(this.databaseType.equals("mysql"))
			{		
				try 
				{
				    Class.forName("com.mysql.jdbc.Driver");
				    this.databaseConnection = DriverManager.getConnection(
						"jdbc:mysql"
						+"://"+this.databaseHostName+":"
						+this.databasePortNumber+"/"
						+this.databaseName, 
						this.databaseUserName, 
						this.databaseUserPassword
						);	
				}
				catch (ClassNotFoundException | SQLException e) 
				{
					if(Config.isPrintStackTrace())
					{
						e.printStackTrace();
					}
					return false;
				}
				if(this.databaseConnection != null)
				{
					this.connected = true;				
				}
			}	
			else
			{
				this.connected = true;
				return false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * Disconnect from the database threads
	 * @return true if success and false if failed
	 */
	public boolean disconnect()
	{
		try
		{
			if(this.connected)
			{
				if(!this.databaseConnection.isClosed())
				{
					this.databaseConnection.close();
					return true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		catch(SQLException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
			return false;
		}
	}
	/**
	 * Set database configuration using file property
	 * @param configPath Configuration file path
	 * @return true if success and false if failed
	 * @throws IOException if any errors
	 */
	public boolean setConfigurationPath(String configPath) throws IOException
	{
		Properties prop = new Properties();
		InputStream inputStream; 
		inputStream = getClass().getClassLoader().getResourceAsStream(configPath);
		if (inputStream != null) 
		{
			prop.load(inputStream);
		} 
		else 
		{
			return false;
		}	
		String databaseType = "";
		String databaseHostName = "";
		String databasePortNumber = "";
		String databaseUserName = "";
		String databaseUserPassword = "";
		String databaseName = "";		
		databaseType = prop.getProperty("DATABASE_TYPE");
		databaseHostName = prop.getProperty("DATABASE_HOST_NAME");
		databasePortNumber = prop.getProperty("DATABASE_PORT_NUMBER");
		databaseUserName = prop.getProperty("DATABASE_USER_NAME");
		databaseUserPassword = prop.getProperty("DATABASE_USER_PASSWORD");
		databaseName = prop.getProperty("DATABASE_NAME");
		if(databasePortNumber.equals(""))
		{
			databasePortNumber = "0";
		}
		this.databaseType = databaseType.trim();
		this.databaseHostName = databaseHostName.trim();
		this.databasePortNumber = Integer.parseInt(databasePortNumber.trim());
		this.databaseUserName = databaseUserName.trim();
		this.databaseUserPassword = databaseUserPassword.trim();
		this.databaseName = databaseName.trim();
		return true;
	}
	/**
	 * Load configuration from string
	 * @param configuration Base64 encoded string
	 * @return true if success and false if failed
	 * @throws JSONException if any JSON errors
	 * @throws NumberFormatException if any number format errors
	 */
	public boolean setConfigurationString(String configuration) throws JSONException, NumberFormatException
	{
		JSONObject json = new JSONObject(configuration);
		String lDatabaseType = json.optString("DATABASE_TYPE", "");
		String lDatabaseHostName = json.optString("DATABASE_HOST_NAME", "");
		String lDatabasePortNumber = json.optString("DATABASE_PORT_NUMBER", "0");
		String lDatabaseUserName = json.optString("DATABASE_USER_NAME", "");
		String lDatabaseUserPassword = json.optString("DATABASE_USER_PASSWORD", "");
		String lDatabaseName = json.optString("DATABASE_NAME", "");
		if(lDatabasePortNumber.equals(""))
		{
			lDatabasePortNumber = "0";
		}			
		this.databaseType = lDatabaseType.trim();
		this.databaseHostName = lDatabaseHostName.trim();
		this.databasePortNumber = Integer.parseInt(lDatabasePortNumber.trim());
		this.databaseUserName = lDatabaseUserName.trim();
		this.databaseUserPassword = lDatabaseUserPassword.trim();
		this.databaseName = lDatabaseName.trim();
		return true;
	}
	/**
	 * Load configuration from encrypted string
	 * @param configuration Base64 encoded string
	 * @return true if success and false if failed
	 * @throws NoSuchPaddingException if padding is invalid
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 * @throws InvalidKeyException if key is invalid
	 * @throws NullPointerException if data is null
	 * @throws UnsupportedEncodingException if encoding is not supported
	 * @throws BadPaddingException if padding is invalid
	 * @throws IllegalBlockSizeException if block size is invalid
	 */
	public boolean setConfigurationStringEncrypted(String configuration) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, IllegalArgumentException, BadPaddingException, UnsupportedEncodingException, NullPointerException, IllegalArgumentException
	{
		Encryption decryptor = new Encryption(Config.getEncryptionPassword());
		String plainConfiguration = decryptor.decrypt(decryptor.base64Decode(configuration), true);
		JSONObject json = new JSONObject();
		if(plainConfiguration != null)
		{
			if(plainConfiguration.length() > 10)
			{
				if(plainConfiguration.trim().substring(0, 1).equals("{"))
				{
					try 
					{
						json = new JSONObject(plainConfiguration);
						String lDatabaseType = json.optString("DATABASE_TYPE", "");
						String lDatabaseHostName = json.optString("DATABASE_HOST_NAME", "");
						String lDatabasePortNumber = json.optString("DATABASE_PORT_NUMBER", "0");
						String lDatabaseUserName = json.optString("DATABASE_USER_NAME", "");
						String lDatabaseUserPassword = json.optString("DATABASE_USER_PASSWORD", "");
						String lDatabaseName = json.optString("DATABASE_NAME", "");
						if(lDatabasePortNumber.equals(""))
						{
							lDatabasePortNumber = "0";
						}			
						this.databaseType = lDatabaseType.trim();
						this.databaseHostName = lDatabaseHostName.trim();
						this.databasePortNumber = Integer.parseInt(lDatabasePortNumber.trim());
						this.databaseUserName = lDatabaseUserName.trim();
						this.databaseUserPassword = lDatabaseUserPassword.trim();
						this.databaseName = lDatabaseName.trim();
						return true;
					} 
					catch (Exception e) 
					{
						if(Config.isPrintStackTrace()) 
						{
							e.printStackTrace();
						}
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}		
	}
	/**
	 * Get encrypted database configuration
	 * @return String contains base64 encoded cipher text
	 * @throws JSONException if any JSON errors 
	 * @throws BadPaddingException if padding is invalid
	 * @throws IllegalBlockSizeException if block size is invalid
	 * @throws UnsupportedEncodingException if encoding is not supported
	 * @throws NullPointerException if data is null
	 * @throws NoSuchPaddingException if padding is invalid 
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 * @throws InvalidKeyException if key is invalid
	 */
	public String getEncryptedConfiguration() throws JSONException, NullPointerException, UnsupportedEncodingException, IllegalBlockSizeException, IllegalArgumentException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException
	{
		Encryption encryptor = new Encryption(Config.getEncryptionPassword());
		JSONObject json = new JSONObject();
		json.put("DATABASE_TYPE", this.databaseType);
		json.put("DATABASE_HOST_NAME", this.databaseHostName);
		json.put("DATABASE_PORT_NUMBER", this.databasePortNumber);
		json.put("DATABASE_USER_NAME", this.databaseUserName);
		json.put("DATABASE_USER_PASSWORD", this.databaseUserPassword);
		json.put("DATABASE_NAME", this.databaseName);
		String plainConfiguration = json.toString();
		String configuration = encryptor.base64Encode(encryptor.encrypt(plainConfiguration, true));
		return configuration;
	}
	/**
	 * Decrypt configuration
	 * @param configuration Cipher text contains database configuration
	 * @return JSONObject contains database configurations
	 * @throws Exception if any errors
	 */
	public JSONObject decryptConfigurationJSON(String configuration) throws Exception, IllegalArgumentException
	{
		Encryption decryptor = new com.planetbiru.pushserver.utility.Encryption(Config.getEncryptionPassword());
		String plainConfiguration = decryptor.decrypt(configuration, true);
		JSONObject json = new JSONObject();
		if(plainConfiguration != null)
		{
			if(plainConfiguration.length() > 10)
			{
				if(plainConfiguration.trim().substring(0, 1).equals("{"))
				{
					try 
					{
						json = new JSONObject(plainConfiguration);
					} 
					catch (Exception e) 
					{
						throw new Exception("Invalid database key");
					}
				}
			}
		}
		return json;		
	}
	/**
	 * Decrypt configuration
	 * @param configuration Cipher text contains database configuration
	 * @return String contains database configurations
	 * @throws NoSuchPaddingException if padding is invalid
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 * @throws InvalidKeyException if key is invalid
	 * @throws UnsupportedEncodingException if encoding is not supported
	 * @throws BadPaddingException if padding is invalid
	 * @throws IllegalBlockSizeException if block size is invalid
	 * @throws JSONException if any JSON errors
	 */
	public DatabaseConfig decryptConfigurationNative(String configuration) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, IllegalArgumentException, BadPaddingException, UnsupportedEncodingException, JSONException 
	{
		DatabaseConfig databaseConfig = new DatabaseConfig();
		Encryption decryptor = new Encryption(Config.getEncryptionPassword());
		String plainConfiguration = decryptor.decrypt(configuration, true);
		JSONObject json;
		if(plainConfiguration == null)
		{
			plainConfiguration = "";
		}
		if(plainConfiguration.length() > 10 && plainConfiguration.trim().substring(0, 1).equals("{"))
		{
			json = new JSONObject(plainConfiguration);						
			String lDatabaseType = "";
			String lDatabaseHostName = "";
			String lDatabasePortNumber = "";
			String lDatabaseUserName = "";
			String lDatabaseUserPassword = "";
			String lDatabaseName = "";		
			lDatabaseType = json.optString("DATABASE_TYPE", "");
			lDatabaseHostName = json.optString("DATABASE_HOST_NAME", "");
			lDatabasePortNumber = json.optString("DATABASE_PORT_NUMBER", "0");
			lDatabaseUserName = json.optString("DATABASE_USER_NAME", "");
			lDatabaseUserPassword = json.optString("DATABASE_USER_PASSWORD", "");
			lDatabaseName = json.optString("DATABASE_NAME", "");						
			databaseConfig.setDatabaseType(lDatabaseType);
			databaseConfig.setDatabaseHostName(lDatabaseHostName);
			databaseConfig.setDatabasePortNumber(Integer.parseInt(lDatabasePortNumber));
			databaseConfig.setDatabaseUserName(lDatabaseUserName);
			databaseConfig.setDatabaseUserPassword(lDatabaseUserPassword);
			databaseConfig.setDatabaseName(lDatabaseName);
		}
		return databaseConfig;
	}
	/**
	 * Encrypt database configuration
	 * @param databaseType Database type
	 * @param databaseHostName Host name
	 * @param databasePortNumber Port number
	 * @param databaseUserName Username
	 * @param databaseUserPassword Password
	 * @param databaseName Database name
	 * @return String contains encrypted database configuration
	 * @throws JSONException if any JSON errors
	 * @throws NoSuchPaddingException if padding is invalid
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 * @throws InvalidKeyException if key is invalid
	 * @throws BadPaddingException if padding is invalid
	 * @throws IllegalBlockSizeException if block size is invalid
	 * @throws UnsupportedEncodingException id encoding is not supported
	 */
	public String encryptConfiguration(String databaseType, String databaseHostName, int databasePortNumber, String databaseUserName, String databaseUserPassword, String databaseName) throws JSONException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalArgumentException, IllegalBlockSizeException, BadPaddingException
	{
		Encryption encryptor = new Encryption(Config.getEncryptionPassword());
		JSONObject json = new JSONObject();
		json.put("DATABASE_TYPE", databaseType);
		json.put("DATABASE_HOST_NAME", databaseHostName);
		json.put("DATABASE_PORT_NUMBER", databasePortNumber);
		json.put("DATABASE_USER_NAME", databaseUserName);
		json.put("DATABASE_USER_PASSWORD", databaseUserPassword);
		json.put("DATABASE_NAME", databaseName);
		String plainConfiguration = json.toString();
		return encryptor.encrypt(plainConfiguration, true);
	}
	/**
	 * Set the driver type of the database
	 * @param driver Database driver type
	 */
	public void setDriverType(String driver)
	{
		this.databaseType = driver;
	}
	/**
	 * Set the driver type of the database
	 * @param driver Database driver type
	 */
	public void setDatabaseType(String driver)
	{
		this.databaseType = driver;
	}
	/**
	 * Get the database driver type
	 * @return Database driver type
	 */
	public String getDriverType()
	{
		return this.databaseType;
	}
	/**
	 * Get the database driver type
	 * @return Database driver type
	 */
	public String getDatabaseType()
	{
		return this.databaseType;
	}
	/**
	 * Get Database
	 * @return Database object
	 */
	public Database getDatabase()
	{
		return this;
	}
	/**
	 * Get Query
	 * @return Query object
	 */
	/**
	 * Set port number of the database
	 * @param databasePortNumber Port number of the database
	 */
	public void setPortNumber(int databasePortNumber)
	{
		this.databasePortNumber = databasePortNumber;
	}
	/**
	 * Get port number of the database
	 * @return Database port number
	 */
	public int getPortNumber()
	{
		return this.databasePortNumber;
	}
	/**
	 * Set username to access the database
	 * @param databaseUserName Username to access the database
	 */
	public void setUserName(String databaseUserName)
	{
		this.databaseUserName = databaseUserName;
	}
	/**
	 * Set password to access the database
	 * @param databaseUserPassword Password to access the database
	 */
	public void setUserPassword(String databaseUserPassword)
	{
		this.databaseUserPassword = databaseUserPassword;
	}
	/**
	 * Set the database name
	 * @param databaseName Name of the database
	 */
	public void setDatabaseName(String databaseName)
	{
		this.databaseName = databaseName;
	}
	/**
	 * Get the name of the database
	 * @return Database name
	 */
	public String getDatabaseName()
	{
		return this.databaseName;
	}
	/**
	 * Execute query on database and return result set
	 * @param sqlCommand Query to be executed
	 * @return ResultSet returned from executeQuery
	 * @throws SQLException if any SQL errors 
	 */
	public ResultSet executeQuery(String sqlCommand) throws SQLException 
	{
		Statement db_s;
		ResultSet db_rs = null;
		db_s = this.databaseConnection.createStatement();
		db_rs = db_s.executeQuery(sqlCommand);
		return db_rs;		
	}
	/**
	 * Execute query on database and return result set
	 * @param query Query to be executed
	 * @return ResultSet returned from executeQuery
	 * @throws SQLException if any SQL errors 
	 */
	public ResultSet executeQuery(QueryBuilder query) throws SQLException
	{
		Statement db_s;
		ResultSet db_rs;
		db_s = this.databaseConnection.createStatement();
		db_rs = db_s.executeQuery(query.toString());
		return db_rs;	
	}
	/**
	 * Execute query on database
	 * @param sqlCommand Query to be executed
	 * @return true or false
	 * @throws SQLException if any SQL errors 
	 */
	public boolean execute(String sqlCommand) throws SQLException
	{
		PreparedStatement db_ps;
		db_ps = this.databaseConnection.prepareStatement(sqlCommand);
		return db_ps.execute();			
	}
	/**
	 * Execute query on database
	 * @param query Query to be executed
	 * @return true or false
	 * @throws SQLException if any SQL errors
	 */
	public boolean execute(QueryBuilder query) throws SQLException
	{
		PreparedStatement db_ps;
		db_ps = this.databaseConnection.prepareStatement(query.toString());
		return db_ps.execute();					
	}
	/**
	 * Lock table
	 * @param tableList Table name followed by the operation to be locked
	 * @return true if success and false if failed
	 * @throws SQLException if any SQL errors 
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public boolean lockTable(String tableList) throws SQLException, DatabaseTypeException
	{
		QueryBuilder query1 = new QueryBuilder(this.databaseType);
		return this.execute(query1.lockTable(tableList));
	}
	/**
	 * Lock tables
	 * @param tableList Table name followed by the operation to be locked
	 * @return true if success and false if failed
	 * @throws SQLException if any SQL errors 
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public boolean lockTables(String tableList) throws SQLException, DatabaseTypeException
	{
		QueryBuilder query1 = new QueryBuilder(this.databaseType);
		return this.execute(query1.lockTables(tableList));
	}
	/**
	 * Unlock tables
	 * @return true if success and false if failed
	 * @throws SQLException if any SQL errors 
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public boolean unlockTables() throws SQLException, DatabaseTypeException
	{
		QueryBuilder query1 = new QueryBuilder(this.databaseType);
		return this.execute(query1.unlockTables());
	}
	/**
	 * Check the database connection by executing simple SQL
	 * @return true if success and false if failed
	 */
	public boolean checkConnection()
	{
		String sqlCommand = "select 1 as test";
		int test = 0;
		ResultSet ldb_rs;
		try
		{
			ldb_rs = this.executeQuery(sqlCommand);
			if(ldb_rs.isBeforeFirst())
			{
				if(ldb_rs.next())
				{
					test = ldb_rs.getInt("test");
					if(test == 1)
					{						
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		catch(SQLException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
			return false;
		}
	}
	/**
	 * Get last auto increment 
	 * @return Last auto increment from current connection
	 * @throws SQLException if any SQL errors
	 */
	public long getLastID() throws SQLException
	{
		String sqlCommand = "";
		if(this.databaseType.equals("mysql"))
		{
			sqlCommand = "select last_insert_id() as last_id\r\n";
		}
		if(this.databaseType.equals("postgresql"))
		{
			sqlCommand = "select lastval() as last_id\r\n";
		}
		ResultSet ldb_rs;
		ldb_rs = this.executeQuery(sqlCommand);
		if(ldb_rs.isBeforeFirst())
		{
			ldb_rs.next();
			return ldb_rs.getLong("last_id");
		}
		return 0;
	}
	/**
	 * Get last auto increment 
	 * @return Last auto increment from current connection
	 * @throws SQLException if any SQL errors 
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public long getLastAutoIncrement() throws SQLException, DatabaseTypeException
	{
		QueryBuilder query1 = new QueryBuilder(this.databaseType);
		String sqlCommand = query1.lastID().alias("last_id").toString();
		ResultSet ldb_rs;
		ldb_rs = this.executeQuery(sqlCommand);
		if(ldb_rs.isBeforeFirst())
		{
			ldb_rs.next();
			return ldb_rs.getLong("last_id");
		}
		else
		{
			return 0;
		}
	}
	/**
	 * Execute start transaction query
	 * @return true is success and false if failed
	 * @throws SQLException if any SQL errors 
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public boolean startTransaction() throws SQLException, DatabaseTypeException
	{
		QueryBuilder query1 = new QueryBuilder(this.databaseType);
		this.execute(query1.startTransaction());
		return true;
	}
	/**
	 * Execute commit query
	 * @return true is success and false if failed
	 * @throws SQLException if any SQL errors 
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public boolean commit() throws SQLException, DatabaseTypeException
	{
		QueryBuilder query1 = new QueryBuilder(this.databaseType);
		this.execute(query1.commit());
		return true;
	}
	/**
	 * Execute rollback query
	 * @return true is success and false if failed
	 * @throws SQLException if any SQL errors 
	 * @throws DatabaseTypeException if database type not supported 
	 */
	public boolean rollback() throws SQLException, DatabaseTypeException
	{
		QueryBuilder query1 = new QueryBuilder(this.databaseType);
		this.execute(query1.rollback());
		return true;
	}
	/**
	 * Generate SHA-256 hash code from a string
	 * @param input Input string
	 * @return SHA-256 hash code
	 * @throws NoSuchAlgorithmException if algorithm not found
	 */
	public static String sha256(String input) throws NoSuchAlgorithmException
	{
		String output = "";
		if(input == null)
		{
			input = "";
		}
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
		output = Database.bytesToHex(encodedhash);
		return output;
	}
	/**
	 * Generate SHA-1 hash code from a string
	 * @param input Input string
	 * @return SHA-1 hash code
	 * @throws NoSuchAlgorithmException if algorithm not found
	 */
	public static String sha1(String input) throws NoSuchAlgorithmException
	{
		String output = "";
		if(input == null)
		{
			input = "";
		}
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
		output = Database.bytesToHex(encodedhash);
		return output;
	}
	/**
	 * Generate MD5 hash code from a string
	 * @param input Input string
	 * @return MD5 hash code
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 */
	public static String md5(String input) throws NoSuchAlgorithmException
	{
		String output = "";
		if(input == null)
		{
			input = "";
		}
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
		output = Database.bytesToHex(encodedhash);
		return output;
	}
	/**
	 * Convert byte to hexadecimal number
	 * @param hash Byte to be converted
	 * @return String containing hexadecimal number
	 */
	public static String bytesToHex(byte[] hash) 
	{
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) 
	    {
	    	String hex = Integer.toHexString(0xff & hash[i]);
	    	if(hex.length() == 1) hexString.append('0');
	    	{
	    		hexString.append(hex);
	    	}
	    }
	    return hexString.toString();
	}
	/**
	 * Encode string with base 64 encoding
	 * @param input String to be encoded
	 * @return Encoded string
	 */
	public static String base64Encode(String input)
	{
		byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes());
		String output = new String(encodedBytes);
		return output;
	}
	/**
	 * Decode string with base 64 encoding
	 * @param input String to be decoded
	 * @return Decoded string
	 */
	public static String base64Decode(String input)
	{
		byte[] decodedBytes = Base64.getDecoder().decode(input.getBytes());
		String output = new String(decodedBytes);
		return output;
	}
	/**
	 * Overrides <strong>toString</strong> method to convert object to JSON String. This method is useful to debug or show value of each properties of the object.
	 */
	public String toString()
	{
		Field[] fields = this.getClass().getDeclaredFields();
		int i, max = fields.length;
		String fieldName = "";
		String fieldType = "";
		String ret = "";
		String value = "";
		boolean skip = false;
		int j = 0;
		for(i = 0; i < max; i++)
		{
			fieldName = fields[i].getName().toString();
			fieldType = fields[i].getType().toString();
			if(i == 0)
			{
				ret += "{";
			}
			if(fieldType.equals("int") || fieldType.equals("long") || fieldType.equals("float") || fieldType.equals("double") || fieldType.equals("boolean"))
			{
				try 
				{
					value = fields[i].get(this).toString();
				}  
				catch (Exception e) 
				{
					value = "0";
				}
				skip = false;
			}
			else if(fieldType.contains("String"))
			{
				try 
				{
					value = "\""+Utility.escapeJSON((String) fields[i].get(this))+"\"";
				} 
				catch (Exception e) 
				{
					value = "\""+"\"";
				}
				skip = false;
			}
			else
			{
				value = "\""+"\"";
				skip = true;
			}
			if(!skip)
			{
				if(j > 0)
				{
					ret += ",";
				}
				j++;
				ret += "\r\n\t\""+fieldName+"\":"+value;
			}
			if(i == max-1)
			{
				ret += "\r\n}";
			}
		}
		return ret;
	}
}