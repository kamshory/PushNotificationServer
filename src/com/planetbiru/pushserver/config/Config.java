package com.planetbiru.pushserver.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.planetbiru.pushserver.application.Application;
import com.planetbiru.pushserver.code.ConstantString;
import com.planetbiru.pushserver.database.Database;
import com.planetbiru.pushserver.database.DatabaseConfiguration;
import com.planetbiru.pushserver.utility.Encryption;

/**
 * Configuration class
 * @author Kamshor, MT
 *
 */
public class Config {
	private static Logger logger = LoggerFactory.getLogger(Config.class);
	private static String applicationName = "Planet Notif";
	/**
	 * Create encrypted database configuration
	 */
	private static boolean createConfiguration = false;
	/**
	 * Run service in development mode
	 */
	private static boolean developmentMode = false;
	/**
	 * Run service in debug mode
	 */
	private static boolean debugMode = true;
	/**
	 * Print stack trace
	 */
	private static boolean printStackTrace = false;
	/**
	 * Indicate that configuration is loaded successfully
	 */
	private static boolean readConfigSuccess = false;
	/**
	 * Document root for notification pusher and remover
	 */
	private static String apiDocumentRoot = "notif";
	/**
	 * Pusher
	 */
	private static String pusherContextPusher = "pusher";
	/**
	 * Remover
	 */
	private static String pusherContextRemover = "remover";
	/**
	 * Create group
	 */
	private static String pusherContextCreateGroup = "create-group";
	/**
	 * Register device
	 */
	private static String pusherContextRegisterDevice = "register-device";
	/**
	 * Unregister device
	 */
	private static String pusherContextUnregisterDevice = "unregister-device";

	/**
	 * API version
	 */
	private static String version = "1.0.0";
	private static boolean httpProxyEnabled = false;
	private static String httpAddressForwarder = "X-Remote-Address-Forwarder";
	/**
	 * Database connection per push
	 */
	private static boolean connectionPerPush = false;
	/**
	 * Primary database configuration
	 */
	private static DatabaseConfiguration databaseConfig1 = new DatabaseConfiguration();
	/**
	 * Secondary database configuration
	 */
	private static DatabaseConfiguration databaseConfig2 = new DatabaseConfiguration();
	/**
	 * Tertiary database configuration
	 */
	private static DatabaseConfiguration databaseConfig3 = new DatabaseConfiguration();
	/**
	 * Properties
	 */
	static Properties properties = new Properties();
	/**
	 * HTTP server port
	 */
	private static int pusherPort = 94;
	/**
	 * HTTPS server port
	 */
	private static int pusherPortSSL = 95;
	/**
	 * Client port
	 */
	private static int notificationPort = 96;
	/**
	 * Client port
	 */
	private static int notificationPortSSL = 97;
	/**
	 * Inspection interval
	 */
	private static long inspectionInterval = 1800000;
	/**
	 * Wait for answer. Server will wait until a connection reply with valid answer
	 */
	private static long waitForAnswer = 30000;
	/**
	 * Filter pusher source
	 */
	private static boolean filterSource = false;
	/**
	 * Require group approval
	 */
	private static boolean groupCreationApproval = true;
	/**
	 * Maximum number of notification on once load
	 */
	private static long limitNotification = 50;
	/**
	 * Maximum number of notification deletion on once load
	 */
	private static long limitTrash = 100;
	/**
	 * Wait for reconnect to database
	 */
	private static long waitDatabaseReconnect = 10000;
	/**
	 * Table prefix. By the table prefix, user can integrate push notification database to their own system without any conflict
	 */
	private static String tablePrefix = "push_";
	/**
	 * Clean up time
	 */
	private static String cleanUpTime = "03:00";
	/**
	 * Keystore file of HTTPS server
	 */
	private static String keystoreFile = "/var/www/keystore.jks";
	/**
	 * Keystore password of HTTPS server
	 */
	private static String keystorePassword = "planet123";
	/**
	 * Encrypted keystore file of HTTPS server
	 */
	private static String keystorePasswordEncrypted = "planet123";	
	/**
	 * Flag that HTTPS server is active or not
	 */
	private static boolean pusherSSLEnabled = false;
	/**
	 * Garbage collection interval
	 */
	private static long gcInterval = 59980;
	/**
	 * Period to delete sent notification (in day). All sent notifications will be deleted after this period
	 */
	private static long deleteNotifSent = 2;
	/**
	 * Period to delete unsent notification (in day). All unsent notifications will be deleted after this period
	 */
	private static long deleteNotifNotSent = 10;
	/**
	 * Key to encrypt and decrypt database configuration.<br>
	 * On the production server, this key is stored in the database and can only be accessed when the service is started. Once the service is running, the database that stores the key must be turned off.
	 */
	private static String encryptionPassword = "1234567890";
	/**
	 * Flag secure content. If set as true, notification will be encrypted using combination of key sent from server and client hash password
	 */
	private static boolean contentSecure = false;
	/**
	 * Redirect user when access document root
	 */
	private static String redirectHome = "https://www.planetbiru.com";
	/**
	 * Mail host
	 */
	private static String mailHost = "localhost";
	/**
	 * Mail port
	 */
	private static int mailPort;
	/**
	 * Mail sender of pusher address approval
	 */
	private static String mailSender = "kamshory@gmail.com";
	/**
	 * Mail template of pusher address approval
	 */
	private static String mailTemplate = "";
	/**
	 * Mail subject of pusher address approval
	 */
	private static String mailSubject = "Pusher Address Confirmation";
	/**
	 * Default URL template of pusher address approval when reading template is failed
	 */
	private static String approvalURLTemplate = "http://push.example.com/approve-address/?auth={auth}";
	/**
	 * Use SMTP authentication
	 */
	private static boolean mailUseAuth = false;
	/**
	 * SMPT username
	 */
	private static String mailUsername = "";
	/**
	 * SMTP password
	 */
	private static String mailPassword = "";
	/**
	 * SMTP encrypted password
	 */
	private static String mailPasswordEncrypted = "";
	private static long waitFreeUpPort = 200;
	private static boolean notificationSSLEnabled = false;
	
	private static String databaseType = "mariadb";
	
	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}
	/**
	 * @return the applicationName
	 */
	public static String getApplicationName() {
		return applicationName;
	}
	/**
	 * @return the createConfiguration
	 */
	public static boolean isCreateConfiguration() {
		return createConfiguration;
	}
	/**
	 * @return the developmentMode
	 */
	public static boolean isDevelopmentMode() {
		return developmentMode;
	}
	/**
	 * @return the debugMode
	 */
	public static boolean isDebugMode() {
		return debugMode;
	}
	/**
	 * @return the printStackTrace
	 */
	public static boolean isPrintStackTrace() {
		return printStackTrace;
	}
	/**
	 * @return the readConfigSuccess
	 */
	public static boolean isReadConfigSuccess() {
		return readConfigSuccess;
	}
	/**
	 * @return the apiDocumentRoot
	 */
	public static String getApiDocumentRoot() {
		return apiDocumentRoot;
	}
	/**
	 * @return the pusherContextPusher
	 */
	public static String getPusherContextPusher() {
		return pusherContextPusher;
	}
	/**
	 * @return the pusherContextRemover
	 */
	public static String getPusherContextRemover() {
		return pusherContextRemover;
	}
	/**
	 * @return the pusherContextCreateGroup
	 */
	public static String getPusherContextCreateGroup() {
		return pusherContextCreateGroup;
	}
	/**
	 * @return the pusherContextRegisterDevice
	 */
	public static String getPusherContextRegisterDevice() {
		return pusherContextRegisterDevice;
	}
	/**
	 * @return the pusherContextUnregisterDevice
	 */
	public static String getPusherContextUnregisterDevice() {
		return pusherContextUnregisterDevice;
	}
	/**
	 * @return the version
	 */
	public static String getVersion() {
		return version;
	}
	/**
	 * @return the httpProxyEnabled
	 */
	public static boolean isHttpProxyEnabled() {
		return httpProxyEnabled;
	}
	/**
	 * @return the httpAddressForwarder
	 */
	public static String getHttpAddressForwarder() {
		return httpAddressForwarder;
	}
	/**
	 * @return the connectionPerPush
	 */
	public static boolean isConnectionPerPush() {
		return connectionPerPush;
	}
	/**
	 * @return the databaseConfig1
	 */
	public static DatabaseConfiguration getDatabaseConfig1() {
		return databaseConfig1;
	}
	/**
	 * @return the databaseConfig2
	 */
	public static DatabaseConfiguration getDatabaseConfig2() {
		return databaseConfig2;
	}
	/**
	 * @return the databaseConfig3
	 */
	public static DatabaseConfiguration getDatabaseConfig3() {
		return databaseConfig3;
	}
	/**
	 * @return the properties
	 */
	public static Properties getProperties() {
		return properties;
	}
	/**
	 * @return the pusherPort
	 */
	public static int getPusherPort() {
		return pusherPort;
	}
	/**
	 * @return the pusherPortSSL
	 */
	public static int getPusherPortSSL() {
		return pusherPortSSL;
	}
	/**
	 * @return the notificationPort
	 */
	public static int getNotificationPort() {
		return notificationPort;
	}
	/**
	 * @return the notificationPortSSL
	 */
	public static int getNotificationPortSSL() {
		return notificationPortSSL;
	}
	/**
	 * @return the inspectionInterval
	 */
	public static long getInspectionInterval() {
		return inspectionInterval;
	}
	/**
	 * @return the waitForAnswer
	 */
	public static long getWaitForAnswer() {
		return waitForAnswer;
	}
	/**
	 * @return the filterSource
	 */
	public static boolean isFilterSource() {
		return filterSource;
	}
	/**
	 * @return the groupCreationApproval
	 */
	public static boolean isGroupCreationApproval() {
		return groupCreationApproval;
	}
	/**
	 * @return the limitNotification
	 */
	public static long getLimitNotification() {
		return limitNotification;
	}
	/**
	 * @return the limitTrash
	 */
	public static long getLimitTrash() {
		return limitTrash;
	}
	/**
	 * @return the waitDatabaseReconnect
	 */
	public static long getWaitDatabaseReconnect() {
		return waitDatabaseReconnect;
	}
	/**
	 * @return the tablePrefix
	 */
	public static String getTablePrefix() {
		return tablePrefix;
	}
	/**
	 * @return the cleanUpTime
	 */
	public static String getCleanUpTime() {
		return cleanUpTime;
	}
	/**
	 * @return the keystoreFile
	 */
	public static String getKeystoreFile() {
		return keystoreFile;
	}
	/**
	 * @return the keystorePassword
	 */
	public static String getKeystorePassword() {
		return keystorePassword;
	}
	/**
	 * @return the keystorePasswordEncrypted
	 */
	public static String getKeystorePasswordEncrypted() {
		return keystorePasswordEncrypted;
	}
	/**
	 * @return the pusherSSLEnabled
	 */
	public static boolean isPusherSSLEnabled() {
		return pusherSSLEnabled;
	}
	/**
	 * @return the gcInterval
	 */
	public static long getGcInterval() {
		return gcInterval;
	}
	/**
	 * @return the deleteNotifSent
	 */
	public static long getDeleteNotifSent() {
		return deleteNotifSent;
	}
	/**
	 * @return the deleteNotifNotSent
	 */
	public static long getDeleteNotifNotSent() {
		return deleteNotifNotSent;
	}
	/**
	 * @return the encryptionPassword
	 */
	public static String getEncryptionPassword() {
		return encryptionPassword;
	}
	/**
	 * @return the contentSecure
	 */
	public static boolean isContentSecure() {
		return contentSecure;
	}
	/**
	 * @return the redirectHome
	 */
	public static String getRedirectHome() {
		return redirectHome;
	}
	/**
	 * @return the mailHost
	 */
	public static String getMailHost() {
		return mailHost;
	}
	/**
	 * @return the mailPort
	 */
	public static int getMailPort() {
		return mailPort;
	}
	/**
	 * @return the mailSender
	 */
	public static String getMailSender() {
		return mailSender;
	}
	/**
	 * @return the mailTemplate
	 */
	public static String getMailTemplate() {
		return mailTemplate;
	}
	/**
	 * @return the mailSubject
	 */
	public static String getMailSubject() {
		return mailSubject;
	}
	/**
	 * @return the approvalURLTemplate
	 */
	public static String getApprovalURLTemplate() {
		return approvalURLTemplate;
	}
	/**
	 * @return the mailUseAuth
	 */
	public static boolean isMailUseAuth() {
		return mailUseAuth;
	}
	/**
	 * @return the mailUsername
	 */
	public static String getMailUsername() {
		return mailUsername;
	}
	/**
	 * @return the mailPassword
	 */
	public static String getMailPassword() {
		return mailPassword;
	}
	/**
	 * @return the mailPasswordEncrypted
	 */
	public static String getMailPasswordEncrypted() {
		return mailPasswordEncrypted;
	}
	/**
	 * @return the waitFreeUpPort
	 */
	public static long getWaitFreeUpPort() {
		return waitFreeUpPort;
	}
	/**
	 * @return the notificationSSLEnabled
	 */
	public static boolean isNotificationSSLEnabled() {
		return notificationSSLEnabled;
	}
	/**
	 * @return the databaseType
	 */
	public static String getDatabaseType() {
		return databaseType;
	}
	/**
	 * Constructor
	 */
	private Config()
	{
		
	}
	/**
	 * Load internal 
	 * @param configPath Configuration path file
	 * @return true if success and false if failed
	 * @throws InvalidKeyException if key is invalid
	 * @throws NumberFormatException if any numbering errors
	 * @throws JSONException if any JSON errors
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 * @throws NoSuchPaddingException if padding is invalid
	 * @throws UnsupportedEncodingException if any encoding errors
	 * @throws IllegalBlockSizeException if block size is invalid
	 * @throws BadPaddingException if padding is invalid
	 * @throws IllegalArgumentException if any invalid arguments
	 */
	public static boolean loadInternalConfig(String configPath) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NumberFormatException, IllegalArgumentException, JSONException
	{
		logger.info("loadInternalConfig: {}", configPath);
		Properties properties = Config.getConfiguration(configPath);
		boolean result = false;
		try
		{
			result = Config.init(properties);
		}
		catch(NullPointerException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
		return result;
	}
	public static boolean loadExternalConfig(String configPath) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, NumberFormatException, IllegalArgumentException, JSONException
	{
		logger.info("loadExternalConfig: {}", configPath);
		Properties properties = Config.getConfigurationExternal(configPath);
		boolean result = false;
		try
		{
			result = Config.init(properties);
		}
		catch(NullPointerException e)
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * Load configuration form internal file
	 * @param configPath Configuration file path
	 * @return Properties if success and null if failed
	 */
	private static Properties getConfiguration(String configPath)
	{	
		Properties prop = new Properties();
		InputStream inputStream; 
		inputStream = Application.class.getClassLoader().getResourceAsStream(configPath);
		if(inputStream != null) 
		{
			try 
			{
				prop.load(inputStream);
				logger.info("Load configuration from {}", configPath);
			} 
			catch (IOException e) 
			{
				logger.info("Unable to load configuration from {} because an error occured.", configPath);
				e.printStackTrace();	
				return null;
			}
		} 
		else 
		{
			logger.info("Unable to load configuration from {} because file not found.", configPath);
			return null;
		}	
		return prop;
	}
	/**
	 * Load configuration form external file
	 * @param configPath Configuration file path
	 * @return Properties if success and null if failed
	 * @throws IOException IOException
	 */
	private static Properties getConfigurationExternal(String configPath) throws IOException 
	{
		Properties propertis = new Properties();
	    try(
	    
	    	InputStream input = new FileInputStream(configPath);
	    ) 
	    {
	        propertis.load(input);
	        logger.info("Load configuration from {}", configPath);
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	        logger.info("Unable to load configuration from {}", configPath);
	    }
        return propertis;
    }

	/**
	 * Initialize database configuration
	 * @param properties Properties
	 * @return true if success and false if failed
	 * @throws InvalidKeyException if key is invalid
	 * @throws NumberFormatException if any number format errors
	 * @throws JSONException if any JSON errors
	 * @throws NoSuchAlgorithmException if algorithm is not found
	 * @throws NoSuchPaddingException if padding is invalid
	 * @throws IllegalBlockSizeException if block size is invalid
	 * @throws BadPaddingException if padding is invalid
	 * @throws UnsupportedEncodingException if encoding is not supported
	 * @throws IllegalArgumentException 
	 */
	private static boolean init(Properties properties) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NumberFormatException, IllegalArgumentException, JSONException
	{
		Config.properties = properties;
	
		String database1Type   	     = "";
		String database1HostName     = "";
		String database1PortNumber   = "";
		String database1UserName     = "";
		String database1UserPassword = "";
		String database1Name         = "";
		String database1Used         = "";
		
		String database2Type   	     = "";
		String database2HostName     = "";
		String database2PortNumber   = "";
		String database2UserName     = "";
		String database2UserPassword = "";
		String database2Name         = "";
		String database2Used         = "";
		
		String database3Type   	     = "";
		String database3HostName     = "";
		String database3PortNumber   = "";
		String database3UserName     = "";
		String database3UserPassword = "";
		String database3Name         = "";
		String database3Used         = "";
		


		database1Used 			       = properties.getProperty("DATABASE1_USED", ConstantString.TRUE);
		database2Used 			       = properties.getProperty("DATABASE2_USED", ConstantString.FALSE);
		database3Used 			       = properties.getProperty("DATABASE3_USED", ConstantString.FALSE);

		Config.databaseConfig1.setDatabaseUsed(database1Used.equals(ConstantString.TRUE));
		Config.databaseConfig2.setDatabaseUsed(database2Used.equals(ConstantString.TRUE));
		Config.databaseConfig3.setDatabaseUsed(database3Used.equals(ConstantString.TRUE));
		
		Config.developmentMode = (properties.getProperty("DEVELOPMENT_MODE", ConstantString.FALSE).equals(ConstantString.TRUE));
		Config.createConfiguration = (properties.getProperty("CREATE_CONFIGURATION", ConstantString.FALSE).equals(ConstantString.TRUE));
		Config.developmentMode = true;
		if(Config.developmentMode)
		{		
			database1Type 			= properties.getProperty("DATABASE1_TYPE", "");
			database1HostName 	    = properties.getProperty("DATABASE1_HOST_NAME", "");
			database1PortNumber 	= properties.getProperty("DATABASE1_PORT_NUMBER", "");
			database1UserName 	    = properties.getProperty("DATABASE1_USER_NAME", "");
			database1UserPassword   = properties.getProperty("DATABASE1_USER_PASSWORD", "");
			database1Name 			= properties.getProperty("DATABASE1_NAME", "");
			
			database2Type 			= properties.getProperty("DATABASE2_TYPE", "");
			database2HostName 	    = properties.getProperty("DATABASE2_HOST_NAME", "");
			database2PortNumber 	= properties.getProperty("DATABASE2_PORT_NUMBER", "");
			database2UserName 	    = properties.getProperty("DATABASE2_USER_NAME", "");
			database2UserPassword   = properties.getProperty("DATABASE2_USER_PASSWORD", "");
			database2Name 			= properties.getProperty("DATABASE2_NAME", "");
			
			database3Type 			= properties.getProperty("DATABASE3_TYPE", "");
			database3HostName 	    = properties.getProperty("DATABASE3_HOST_NAME", "");
			database3PortNumber 	= properties.getProperty("DATABASE3_PORT_NUMBER", "");
			database3UserName 	    = properties.getProperty("DATABASE3_USER_NAME", "");
			database3UserPassword   = properties.getProperty("DATABASE3_USER_PASSWORD", "");
			database3Name 			= properties.getProperty("DATABASE3_NAME", "");
			
			Config.keystorePassword = properties.getProperty("KEYSTORE_PASSWORD", "");
			Config.mailPassword     = properties.getOrDefault("MAIL_PASSWORD", "").toString().trim();
			
			if(database1Type.equals("") || database1HostName.equals("") || database1PortNumber.equals("") || database1UserName.equals("") || database1Name.equals(""))
			{
				logger.error("Invalid database configuration");
			}

			if(Config.createConfiguration)
			{
				Encryption en = new Encryption(Config.encryptionPassword);
				Database db = new Database();
				String configuration1 = db.encryptConfiguration(database1Type, database1HostName, Integer.parseInt(database1PortNumber), database1UserName, database1UserPassword, database1Name);
				String configuration2 = db.encryptConfiguration(database2Type, database2HostName, Integer.parseInt(database2PortNumber), database2UserName, database2UserPassword, database2Name);
				String configuration3 = db.encryptConfiguration(database3Type, database3HostName, Integer.parseInt(database3PortNumber), database3UserName, database3UserPassword, database3Name);
				Config.keystorePasswordEncrypted = en.encrypt(Config.keystorePassword, true);
				Config.mailPasswordEncrypted     = en.encrypt(Config.mailPassword, true);
				
				logger.info("");
				logger.info("DATABASE1_CONFIGURATION      = {}", configuration1);
				logger.info("DATABASE2_CONFIGURATION      = {}", configuration2);
				logger.info("DATABASE3_CONFIGURATION      = {}", configuration3);
				logger.info("KEYSTORE_PASSWORD_ENCRYPTED  = {}", Config.keystorePasswordEncrypted);
				logger.info("MAIL_PASSWORD_ENCRYPTED      = {}", Config.mailPasswordEncrypted);
				logger.info("");
				System.exit(0);
			}				
			Config.databaseConfig1.initConfig(database1Type, database1HostName, Integer.parseInt(database1PortNumber), database1UserName, database1UserPassword, database1Name, database1Used.equals(ConstantString.TRUE));
			Config.databaseConfig2.initConfig(database2Type, database2HostName, Integer.parseInt(database2PortNumber), database2UserName, database2UserPassword, database2Name, database2Used.equals(ConstantString.TRUE));
			Config.databaseConfig3.initConfig(database1Type, database3HostName, Integer.parseInt(database3PortNumber), database3UserName, database3UserPassword, database1Name, database3Used.equals(ConstantString.TRUE));
		}
		else
		{
			Config.databaseConfig1 = Config.databaseConfig1.decryptConfigurationNative(properties.getOrDefault("DATABASE1_CONFIGURATION", "{}").toString());
			Config.databaseConfig2 = Config.databaseConfig2.decryptConfigurationNative(properties.getOrDefault("DATABASE2_CONFIGURATION", "{}").toString());
			Config.databaseConfig3 = Config.databaseConfig3.decryptConfigurationNative(properties.getOrDefault("DATABASE3_CONFIGURATION", "{}").toString());
			
			Config.databaseConfig1.setDatabaseUsed(database1Used.equals(ConstantString.TRUE));
			Config.databaseConfig2.setDatabaseUsed(database2Used.equals(ConstantString.TRUE));
			Config.databaseConfig3.setDatabaseUsed(database3Used.equals(ConstantString.TRUE));
			
			Config.keystorePasswordEncrypted    = properties.getProperty("KEYSTORE_PASSWORD_ENCRYPTED", "");
			Config.mailPasswordEncrypted        = properties.getOrDefault("MAIL_PASSWORD_ENCRYPTED", "").toString().trim();

			Encryption en = new Encryption(Config.encryptionPassword);

			Config.keystorePassword             = en.decrypt(Config.keystorePasswordEncrypted, true);			
			Config.mailPassword                 = en.decrypt(Config.mailPasswordEncrypted, true);		
			
		}
		
		Config.apiDocumentRoot           = properties.getOrDefault("SERVER_DOCUMENT_ROOT", "notif").toString().trim();
		Config.cleanUpTime               = properties.getOrDefault("CLEAN_UP_TIME", "0200").toString().trim();
		Config.notificationPort          = Integer.parseInt(properties.getOrDefault("SERVER_PORT_CLIENT", "96").toString().trim());
		Config.notificationPortSSL       = Integer.parseInt(properties.getOrDefault("SERVER_PORT_CLIENT_SSL", "97").toString().trim());
		Config.pusherPort                = Integer.parseInt(properties.getOrDefault("PUSHER_PORT_HTTP", "94").toString().trim());
		Config.pusherPortSSL             = Integer.parseInt(properties.getOrDefault("PUSHER_PORT_HTTPS", "95").toString().trim());
		Config.pusherSSLEnabled          = properties.getOrDefault("PUSHER_HTTPS_ENABLED", ConstantString.FALSE).toString().trim().equalsIgnoreCase(ConstantString.TRUE);
		Config.notificationSSLEnabled    = properties.getOrDefault("NOTIFICATION_HTTPS_ENABLED", ConstantString.FALSE).toString().trim().equalsIgnoreCase(ConstantString.TRUE);
		
		Config.keystoreFile              = properties.getOrDefault("KEYSTORE_PATH", ConstantString.TRUE).toString().trim();
		Config.connectionPerPush         = properties.getOrDefault("DATABASE_CONNECTION_PER_PUSH", ConstantString.TRUE).toString().trim().equalsIgnoreCase(ConstantString.TRUE);
		Config.printStackTrace           = properties.getOrDefault("PRINT_STACK_TRACE", ConstantString.FALSE).toString().trim().equalsIgnoreCase(ConstantString.TRUE);
		Config.debugMode                 = properties.getOrDefault("DEBUG_MODE", ConstantString.FALSE).toString().trim().equalsIgnoreCase(ConstantString.TRUE);
		Config.developmentMode           = properties.getOrDefault("DEVELOPMENT_MODE", ConstantString.FALSE).toString().trim().equalsIgnoreCase(ConstantString.TRUE);
		Config.filterSource              = properties.getOrDefault("FILTER_SOURCE", ConstantString.TRUE).toString().trim().equalsIgnoreCase(ConstantString.TRUE);
		Config.groupCreationApproval     = properties.getOrDefault("GROUP_CREATION_APPROVAL", ConstantString.TRUE).toString().trim().equalsIgnoreCase(ConstantString.TRUE);
		Config.inspectionInterval        = Long.parseLong(properties.getOrDefault("INTERVAL_INSPECTION", "1800000").toString().trim());
		Config.gcInterval                = Integer.parseInt(properties.getOrDefault("INTERVAL_GARBAGE_COLLECTION", "59000").toString().trim());	
		Config.limitNotification         = Long.parseLong(properties.getOrDefault("LIMIT_NOTIFICATION", "50").toString().trim());
		Config.limitTrash                = Long.parseLong(properties.getOrDefault("LIMIT_TRASH", "200").toString().trim());
		Config.pusherContextPusher       = properties.getOrDefault("SERVER_CONTEXT_PUSHER", "pusher").toString().trim();
		Config.pusherContextRemover      = properties.getOrDefault("SERVER_CONTEXT_REMOVER", "remover").toString().trim();
		Config.pusherContextCreateGroup  = properties.getOrDefault("SERVER_CONTEXT_CREATE_GROUP", "create-group").toString().trim();
		Config.waitDatabaseReconnect     = Integer.parseInt(properties.getOrDefault("WAIT_FOR_RECONNECT_DATABASE", "10000").toString().trim());
		Config.waitForAnswer             = Integer.parseInt(properties.getOrDefault("WAIT_FOR_ANSWER", "30000").toString().trim());			
		Config.deleteNotifSent           = Integer.parseInt(properties.getOrDefault("DELETE_NOTIF_SENT", "3").toString().trim());	
		Config.deleteNotifNotSent        = Integer.parseInt(properties.getOrDefault("DELETE_NOTIF_NOT_SENT", "10").toString().trim());	
		Config.contentSecure             = properties.getOrDefault("CONTENT_SECURE", ConstantString.FALSE).toString().trim().equalsIgnoreCase(ConstantString.TRUE);
		Config.redirectHome              = properties.getOrDefault("REDIRECT_HOME", "https://www.planetbiru.com").toString().trim();		
		
		Config.mailHost                  = properties.getOrDefault("MAIL_HOST", "localhost").toString().trim();	
		Config.mailPort                  = Integer.parseInt(properties.getOrDefault("MAIL_PORT", "25").toString().trim());	
		Config.mailUseAuth               = properties.getOrDefault("MAIL_USE_AUTH", ConstantString.FALSE).toString().trim().equalsIgnoreCase(ConstantString.TRUE);
		Config.mailUsername              = properties.getOrDefault("MAIL_USERNAME", "").toString().trim();
		
		Config.mailSubject               = properties.getOrDefault("MAIL_SUBJECT", "Pusher Address Confirmation").toString().trim();
		Config.mailTemplate              = properties.getOrDefault("MAIL_TEMPLATE", "").toString().trim();
		Config.mailSender                = properties.getOrDefault("MAIL_SENDER", "user@example.com").toString().trim();
		
		Config.approvalURLTemplate       = properties.getOrDefault("APPROVAL_URL_TEMPLATE", "http://push.example.com/approve-address/?auth={hash}").toString().trim();
		Config.readConfigSuccess         = true;			
		return Config.readConfigSuccess;		
	}
	public static void setDebugMode(boolean debugMode) {
		Config.debugMode = debugMode;
		
	}
	
}
