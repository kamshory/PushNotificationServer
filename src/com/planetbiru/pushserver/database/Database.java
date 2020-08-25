package com.planetbiru.pushserver.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.planetbiru.pushserver.code.ConstantString;
import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.utility.Encryption;

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
	private DatabaseConfiguration databaseConfig = new DatabaseConfiguration();
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
	public void setDatabaseConfig(DatabaseConfiguration databaseConfig) {
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
	public Database(DatabaseConfiguration databaseConfig)
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
	public DatabaseConfiguration getDatabaseConfig()
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
		String lDatabaseType = "";
		String lDatabaseHostName = "";
		String lDatabasePortNumber = "";
		String lDatabaseUserName = "";
		String lDatabaseUserPassword = "";
		String lDatabaseName = "";		
		lDatabaseType = prop.getProperty(ConstantString.DATABASE_TYPE);
		lDatabaseHostName = prop.getProperty(ConstantString.DATABASE_HOST_NAME);
		lDatabasePortNumber = prop.getProperty(ConstantString.DATABASE_PORT_NUMBER);
		lDatabaseUserName = prop.getProperty(ConstantString.DATABASE_USER_NAME);
		lDatabaseUserPassword = prop.getProperty(ConstantString.DATABASE_USER_PASSWORD);
		lDatabaseName = prop.getProperty(ConstantString.DATABASE_NAME);
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
	 * Load configuration from string
	 * @param configuration Base64 encoded string
	 * @return true if success and false if failed
	 * @throws JSONException if any JSON errors
	 * @throws NumberFormatException if any number format errors
	 */
	public boolean setConfigurationString(String configuration) throws JSONException, NumberFormatException
	{
		JSONObject json = new JSONObject(configuration);
		String lDatabaseType = json.optString(ConstantString.DATABASE_TYPE, "");
		String lDatabaseHostName = json.optString(ConstantString.DATABASE_HOST_NAME, "");
		String lDatabasePortNumber = json.optString(ConstantString.DATABASE_PORT_NUMBER, "0");
		String lDatabaseUserName = json.optString(ConstantString.DATABASE_USER_NAME, "");
		String lDatabaseUserPassword = json.optString(ConstantString.DATABASE_USER_PASSWORD, "");
		String lDatabaseName = json.optString(ConstantString.DATABASE_NAME, "");
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
		JSONObject json;
		if(plainConfiguration != null)
		{
			if(plainConfiguration.length() > 10)
			{
				if(plainConfiguration.trim().substring(0, 1).equals("{"))
				{
					try 
					{
						json = new JSONObject(plainConfiguration);
						String lDatabaseType = json.optString(ConstantString.DATABASE_TYPE, "");
						String lDatabaseHostName = json.optString(ConstantString.DATABASE_HOST_NAME, "");
						String lDatabasePortNumber = json.optString(ConstantString.DATABASE_PORT_NUMBER, "0");
						String lDatabaseUserName = json.optString(ConstantString.DATABASE_USER_NAME, "");
						String lDatabaseUserPassword = json.optString(ConstantString.DATABASE_USER_PASSWORD, "");
						String lDatabaseName = json.optString(ConstantString.DATABASE_NAME, "");
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
	public String getEncryptedConfiguration() throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException
	{
		Encryption encryptor = new Encryption(Config.getEncryptionPassword());
		JSONObject json = new JSONObject();
		json.put(ConstantString.DATABASE_TYPE, this.databaseType);
		json.put(ConstantString.DATABASE_HOST_NAME, this.databaseHostName);
		json.put(ConstantString.DATABASE_PORT_NUMBER, this.databasePortNumber);
		json.put(ConstantString.DATABASE_USER_NAME, this.databaseUserName);
		json.put(ConstantString.DATABASE_USER_PASSWORD, this.databaseUserPassword);
		json.put(ConstantString.DATABASE_NAME, this.databaseName);
		String plainConfiguration = json.toString();
		return encryptor.base64Encode(encryptor.encrypt(plainConfiguration, true));
	}
	/**
	 * Decrypt configuration
	 * @param configuration Cipher text contains database configuration
	 * @return JSONObject contains database configurations
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws UnsupportedEncodingException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws Exception if any errors
	 */
	public JSONObject decryptConfigurationJSON(String configuration) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
	{
		Encryption decryptor = new com.planetbiru.pushserver.utility.Encryption(Config.getEncryptionPassword());
		String plainConfiguration = decryptor.decrypt(configuration, true);
		JSONObject json = new JSONObject();
		if(plainConfiguration != null)
		{
			if(plainConfiguration.length() > 10 && plainConfiguration.trim().substring(0, 1).equals("{"))
			{
				try 
				{
					json = new JSONObject(plainConfiguration);
				} 
				catch (Exception e) 
				{
					throw new IllegalArgumentException("Invalid database key");
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
	public DatabaseConfiguration decryptConfigurationNative(String configuration) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, IllegalArgumentException, BadPaddingException, UnsupportedEncodingException, JSONException 
	{
		DatabaseConfiguration lDatabaseConfig = new DatabaseConfiguration();
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
			lDatabaseType = json.optString(ConstantString.DATABASE_TYPE, "");
			lDatabaseHostName = json.optString(ConstantString.DATABASE_HOST_NAME, "");
			lDatabasePortNumber = json.optString(ConstantString.DATABASE_PORT_NUMBER, "0");
			lDatabaseUserName = json.optString(ConstantString.DATABASE_USER_NAME, "");
			lDatabaseUserPassword = json.optString(ConstantString.DATABASE_USER_PASSWORD, "");
			lDatabaseName = json.optString(ConstantString.DATABASE_NAME, "");						
			lDatabaseConfig.setDatabaseType(lDatabaseType);
			lDatabaseConfig.setDatabaseHostName(lDatabaseHostName);
			lDatabaseConfig.setDatabasePortNumber(Integer.parseInt(lDatabasePortNumber));
			lDatabaseConfig.setDatabaseUserName(lDatabaseUserName);
			lDatabaseConfig.setDatabaseUserPassword(lDatabaseUserPassword);
			lDatabaseConfig.setDatabaseName(lDatabaseName);
		}
		return lDatabaseConfig;
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
	public String encryptConfiguration(String databaseType, String databaseHostName, int databasePortNumber, String databaseUserName, String databaseUserPassword, String databaseName) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalArgumentException, IllegalBlockSizeException, BadPaddingException
	{
		Encryption encryptor = new Encryption(Config.getEncryptionPassword());
		JSONObject json = new JSONObject();
		json.put(ConstantString.DATABASE_TYPE, databaseType);
		json.put(ConstantString.DATABASE_HOST_NAME, databaseHostName);
		json.put(ConstantString.DATABASE_PORT_NUMBER, databasePortNumber);
		json.put(ConstantString.DATABASE_USER_NAME, databaseUserName);
		json.put(ConstantString.DATABASE_USER_PASSWORD, databaseUserPassword);
		json.put(ConstantString.DATABASE_NAME, databaseName);
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
	 * Overrides <strong>toString</strong> method to convert object to JSON String. This method is useful to debug or show value of each properties of the object.
	 */
	
	public static void closeResultSet(ResultSet rs) {
		if(rs != null)
		{
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	public static void closeStatement(Statement stmt) {
		if(stmt != null)
		{
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
}