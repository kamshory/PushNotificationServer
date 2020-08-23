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
import com.planetbiru.pushserver.utility.Utility;

/**
 * Database configuration instead of JSONObject
 * @author Kamshory, MT
 *
 */
public class DatabaseConfig {
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
	public DatabaseConfig initConfig(String databaseType, String databaseHostName, int databasePortNumber, String databaseUserName, String databaseUserPassword, String databaseName, boolean databaseUsed)
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
	 */
	public DatabaseConfig decryptConfigurationNative(String configuration) throws JSONException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, NoSuchPaddingException
	{
		DatabaseConfig databaseConfig = new DatabaseConfig();
		Encryption decryptor = new Encryption(Config.getEncryptionPassword());
		String plainConfiguration = decryptor.decrypt(configuration, true);
		JSONObject json;
		if(plainConfiguration != null)
		{
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
		}
		return databaseConfig;
	}
	/**
	 * Overrides <strong>toString</strong> method to convert object to JSON String. This method is useful to debug or show value of each properties of the object.
	 */
	public String toString()
	{
		Field[] fields = this.getClass().getDeclaredFields();
		int i;
		int max = fields.length;
		String fieldName = "";
		String fieldType = "";
		String ret = "";
		String value = "";
		boolean skip = false;
		int j = 0;
		for(i = 0; i < max; i++)
		{
			fieldName = fields[i].getName();
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
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
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
}
