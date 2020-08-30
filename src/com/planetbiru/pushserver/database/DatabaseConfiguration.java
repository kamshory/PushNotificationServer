package com.planetbiru.pushserver.database;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.planetbiru.pushserver.config.Config;
import com.planetbiru.pushserver.utility.Encryption;

/**
 * Database configuration instead of JSONObject
 * @author Kamshory, MT
 *
 */
public class DatabaseConfiguration {
	/**
	 * Database driver.
	 * Supported database type are: mysql, mariadb, postgresql
	 */
	private String databaseType = "mysql";
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
	 * On off database
	 */
	private boolean databaseUsed = false;
	/**
	 * 
	 * @param databaseType Database type
	 * @param databaseHostName Database host name
	 * @param databasePortNumber Database server port
	 * @param databaseUserName Database username
	 * @param databaseUserPassword Database user password
	 * @param databaseName Database name
	 * @param databaseUsed Database usage
	 * @return DatabaseConfiguration object
	 */
	public DatabaseConfiguration initConfig(String databaseType, String databaseHostName, int databasePortNumber, String databaseUserName, String databaseUserPassword, String databaseName, boolean databaseUsed)
	{
		this.setDatabaseType(databaseType);
		this.setDatabaseHostName(databaseHostName);
		this.setDatabasePortNumber(databasePortNumber);
		this.setDatabaseUserName(databaseUserName);
		this.setDatabaseUserPassword(databaseUserPassword);
		this.setDatabaseName(databaseName);
		this.setDatabaseUsed(databaseUsed);
		return this;
	}
	/**
	 * Decrypt native configuration
	 * @param configuration String contains database configuration
	 * @return Database configuration
	 * @throws UnsupportedEncodingException if encoding is not supported
	 * @throws BadPaddingException if padding is invalid
	 * @throws IllegalBlockSizeException if block size is invalid
	 * @throws NoSuchPaddingException if padding is invalid
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 * @throws InvalidKeyException if key is invalid
	 * @throws JSONException if any JSON errors
	 */
	public DatabaseConfiguration decryptConfigurationNative(String configuration) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, JSONException
	{
		DatabaseConfiguration databaseConfig = new DatabaseConfiguration();
		Encryption decryptor = new Encryption(Config.getEncryptionPassword());
		String plainConfiguration = decryptor.decrypt(configuration, true);
		JSONObject json;
		if(configuration == null)
		{
			return databaseConfig;
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
	
	
	
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
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
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public boolean isDatabaseUsed() {
		return databaseUsed;
	}
	public void setDatabaseUsed(boolean databaseUsed) {
		this.databaseUsed = databaseUsed;
	}
	/**
	 * Overrides <strong>toString</strong> method to convert object to JSON String. This method is useful to debug or show value of each properties of the object.
	 */
	@Override
	public String toString()
	{
		String resultStr = "";
		try
		{
			JSONObject result = new JSONObject();
			Field[] fields = this.getClass().getDeclaredFields();
			int i;
			int max = fields.length;
			String fieldName = "";
			String fieldType = "";
			for(i = 0; i < max; i++)
			{
				fieldName = fields[i].getName();
				fieldType = fields[i].getType().toString();
				if(fieldType.equals("int") || fieldType.equals("long") || fieldType.equals("float") || fieldType.equals("double") || fieldType.equals("boolean") || fieldType.contains("String"))
				{
					result.put(fieldName, fields[i].get(this).toString());
				}
			}
			resultStr = result.toString(4);
		}
		catch(JSONException | IllegalArgumentException | IllegalAccessException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
		return resultStr;
	}
	
}
