package com.planetbiru.pushserver.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;

import com.planetbiru.pushserver.application.Application;
import com.planetbiru.pushserver.database.Database;
import com.planetbiru.pushserver.database.DatabaseConfig;
import com.planetbiru.pushserver.utility.Encryption;
import com.planetbiru.pushserver.utility.Utility;

/**
 * Configuration class
 * @author Kamshor, MT
 *
 */
public class Config {
	private static final String applicationName = "Planet Notif";
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
	private static boolean HTTPProxyEnabled = false;
	private static String HTTPAddressForwarder = "X-Remote-Address-Forwarder";
	/**
	 * Database connection per push
	 */
	private static boolean connectionPerPush = false;
	/**
	 * Primary database configuration
	 */
	private static DatabaseConfig databaseConfig1 = new DatabaseConfig();
	/**
	 * Secondary database configuration
	 */
	private static DatabaseConfig databaseConfig2 = new DatabaseConfig();
	/**
	 * Tertiary database configuration
	 */
	private static DatabaseConfig databaseConfig3 = new DatabaseConfig();
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
	private static int notificationPort = 92;
	/**
	 * Client port
	 */
	private static int notificationPortSSL = 93;
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
	private static boolean filterSource = true;
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
	

	
	public static boolean isCreateConfiguration() {
		return createConfiguration;
	}
	public static void setCreateConfiguration(boolean createConfiguration) {
		Config.createConfiguration = createConfiguration;
	}
	public static boolean isDevelopmentMode() {
		return developmentMode;
	}
	public static void setDevelopmentMode(boolean developmentMode) {
		Config.developmentMode = developmentMode;
	}
	public static boolean isDebugMode() {
		return debugMode;
	}
	public static void setDebugMode(boolean debugMode) {
		Config.debugMode = debugMode;
	}
	public static boolean isPrintStackTrace() {
		return printStackTrace;
	}
	public static void setPrintStackTrace(boolean printStackTrace) {
		Config.printStackTrace = printStackTrace;
	}
	public static boolean isReadConfigSuccess() {
		return readConfigSuccess;
	}
	public static void setReadConfigSuccess(boolean readConfigSuccess) {
		Config.readConfigSuccess = readConfigSuccess;
	}
	public static String getApiDocumentRoot() {
		return apiDocumentRoot;
	}
	public static void setApiDocumentRoot(String apiDocumentRoot) {
		Config.apiDocumentRoot = apiDocumentRoot;
	}
	public static String getPusherContextPusher() {
		return pusherContextPusher;
	}
	public static void setPusherContextPusher(String pusherContextPusher) {
		Config.pusherContextPusher = pusherContextPusher;
	}
	public static String getPusherContextRemover() {
		return pusherContextRemover;
	}
	public static void setPusherContextRemover(String pusherContextRemover) {
		Config.pusherContextRemover = pusherContextRemover;
	}
	public static String getPusherContextCreateGroup() {
		return pusherContextCreateGroup;
	}
	public static void setPusherContextCreateGroup(String pusherContextCreateGroup) {
		Config.pusherContextCreateGroup = pusherContextCreateGroup;
	}
	public static String getPusherContextRegisterDevice() {
		return pusherContextRegisterDevice;
	}
	public static void setPusherContextRegisterDevice(String pusherContextRegisterDevice) {
		Config.pusherContextRegisterDevice = pusherContextRegisterDevice;
	}
	public static String getPusherContextUnregisterDevice() {
		return pusherContextUnregisterDevice;
	}
	public static void setPusherContextUnregisterDevice(String pusherContextUnregisterDevice) {
		Config.pusherContextUnregisterDevice = pusherContextUnregisterDevice;
	}
	public static String getVersion() {
		return version;
	}
	public static void setVersion(String version) {
		Config.version = version;
	}
	public static boolean isHTTPProxyEnabled() {
		return HTTPProxyEnabled;
	}
	public static void setHTTPProxyEnabled(boolean hTTPProxyEnabled) {
		HTTPProxyEnabled = hTTPProxyEnabled;
	}
	public static String getHTTPAddressForwarder() {
		return HTTPAddressForwarder;
	}
	public static void setHTTPAddressForwarder(String hTTPAddressForwarder) {
		HTTPAddressForwarder = hTTPAddressForwarder;
	}
	public static boolean isConnectionPerPush() {
		return connectionPerPush;
	}
	public static void setConnectionPerPush(boolean connectionPerPush) {
		Config.connectionPerPush = connectionPerPush;
	}
	public static DatabaseConfig getDatabaseConfig1() {
		return databaseConfig1;
	}
	public static void setDatabaseConfig1(DatabaseConfig databaseConfig1) {
		Config.databaseConfig1 = databaseConfig1;
	}
	public static DatabaseConfig getDatabaseConfig2() {
		return databaseConfig2;
	}
	public static void setDatabaseConfig2(DatabaseConfig databaseConfig2) {
		Config.databaseConfig2 = databaseConfig2;
	}
	public static DatabaseConfig getDatabaseConfig3() {
		return databaseConfig3;
	}
	public static void setDatabaseConfig3(DatabaseConfig databaseConfig3) {
		Config.databaseConfig3 = databaseConfig3;
	}
	public static Properties getProperties() {
		return properties;
	}
	public static void setProperties(Properties properties) {
		Config.properties = properties;
	}
	public static int getPusherPort() {
		return pusherPort;
	}
	public static void setPusherPort(int pusherPort) {
		Config.pusherPort = pusherPort;
	}
	public static int getPusherPortSSL() {
		return pusherPortSSL;
	}
	public static void setPusherPortSSL(int pusherPortSSL) {
		Config.pusherPortSSL = pusherPortSSL;
	}
	public static int getNotificationPort() {
		return notificationPort;
	}
	public static void setNotificationPort(int notificationPort) {
		Config.notificationPort = notificationPort;
	}
	public static int getNotificationPortSSL() {
		return notificationPortSSL;
	}
	public static void setNotificationPortSSL(int notificationPortSSL) {
		Config.notificationPortSSL = notificationPortSSL;
	}
	public static long getInspectionInterval() {
		return inspectionInterval;
	}
	public static void setInspectionInterval(long inspectionInterval) {
		Config.inspectionInterval = inspectionInterval;
	}
	public static long getWaitForAnswer() {
		return waitForAnswer;
	}
	public static void setWaitForAnswer(long waitForAnswer) {
		Config.waitForAnswer = waitForAnswer;
	}
	public static boolean isFilterSource() {
		return filterSource;
	}
	public static void setFilterSource(boolean filterSource) {
		Config.filterSource = filterSource;
	}
	public static boolean isGroupCreationApproval() {
		return groupCreationApproval;
	}
	public static void setGroupCreationApproval(boolean groupCreationApproval) {
		Config.groupCreationApproval = groupCreationApproval;
	}
	public static long getLimitNotification() {
		return limitNotification;
	}
	public static void setLimitNotification(long limitNotification) {
		Config.limitNotification = limitNotification;
	}
	public static long getLimitTrash() {
		return limitTrash;
	}
	public static void setLimitTrash(long limitTrash) {
		Config.limitTrash = limitTrash;
	}
	public static long getWaitDatabaseReconnect() {
		return waitDatabaseReconnect;
	}
	public static void setWaitDatabaseReconnect(long waitDatabaseReconnect) {
		Config.waitDatabaseReconnect = waitDatabaseReconnect;
	}
	public static String getTablePrefix() {
		return tablePrefix;
	}
	public static void setTablePrefix(String tablePrefix) {
		Config.tablePrefix = tablePrefix;
	}
	public static String getCleanUpTime() {
		return cleanUpTime;
	}
	public static void setCleanUpTime(String cleanUpTime) {
		Config.cleanUpTime = cleanUpTime;
	}
	public static String getKeystoreFile() {
		return keystoreFile;
	}
	public static void setKeystoreFile(String keystoreFile) {
		Config.keystoreFile = keystoreFile;
	}
	public static String getKeystorePassword() {
		return keystorePassword;
	}
	public static void setKeystorePassword(String keystorePassword) {
		Config.keystorePassword = keystorePassword;
	}
	public static String getKeystorePasswordEncrypted() {
		return keystorePasswordEncrypted;
	}
	public static void setKeystorePasswordEncrypted(String keystorePasswordEncrypted) {
		Config.keystorePasswordEncrypted = keystorePasswordEncrypted;
	}
	public static boolean isPusherSSLEnabled() {
		return pusherSSLEnabled;
	}
	public static void setPusherSSLEnabled(boolean pusherSSLEnabled) {
		Config.pusherSSLEnabled = pusherSSLEnabled;
	}
	public static long getGcInterval() {
		return gcInterval;
	}
	public static void setGcInterval(long gcInterval) {
		Config.gcInterval = gcInterval;
	}
	public static long getDeleteNotifSent() {
		return deleteNotifSent;
	}
	public static void setDeleteNotifSent(long deleteNotifSent) {
		Config.deleteNotifSent = deleteNotifSent;
	}
	public static long getDeleteNotifNotSent() {
		return deleteNotifNotSent;
	}
	public static void setDeleteNotifNotSent(long deleteNotifNotSent) {
		Config.deleteNotifNotSent = deleteNotifNotSent;
	}
	public static String getEncryptionPassword() {
		return encryptionPassword;
	}
	public static void setEncryptionPassword(String encryptionPassword) {
		Config.encryptionPassword = encryptionPassword;
	}
	public static boolean isContentSecure() {
		return contentSecure;
	}
	public static void setContentSecure(boolean contentSecure) {
		Config.contentSecure = contentSecure;
	}
	public static String getRedirectHome() {
		return redirectHome;
	}
	public static void setRedirectHome(String redirectHome) {
		Config.redirectHome = redirectHome;
	}
	public static String getMailHost() {
		return mailHost;
	}
	public static void setMailHost(String mailHost) {
		Config.mailHost = mailHost;
	}
	public static int getMailPort() {
		return mailPort;
	}
	public static void setMailPort(int mailPort) {
		Config.mailPort = mailPort;
	}
	public static String getMailSender() {
		return mailSender;
	}
	public static void setMailSender(String mailSender) {
		Config.mailSender = mailSender;
	}
	public static String getMailTemplate() {
		return mailTemplate;
	}
	public static void setMailTemplate(String mailTemplate) {
		Config.mailTemplate = mailTemplate;
	}
	public static String getMailSubject() {
		return mailSubject;
	}
	public static void setMailSubject(String mailSubject) {
		Config.mailSubject = mailSubject;
	}
	public static String getApprovalURLTemplate() {
		return approvalURLTemplate;
	}
	public static void setApprovalURLTemplate(String approvalURLTemplate) {
		Config.approvalURLTemplate = approvalURLTemplate;
	}
	public static boolean isMailUseAuth() {
		return mailUseAuth;
	}
	public static void setMailUseAuth(boolean mailUseAuth) {
		Config.mailUseAuth = mailUseAuth;
	}
	public static String getMailUsername() {
		return mailUsername;
	}
	public static void setMailUsername(String mailUsername) {
		Config.mailUsername = mailUsername;
	}
	public static String getMailPassword() {
		return mailPassword;
	}
	public static void setMailPassword(String mailPassword) {
		Config.mailPassword = mailPassword;
	}
	public static String getMailPasswordEncrypted() {
		return mailPasswordEncrypted;
	}
	public static void setMailPasswordEncrypted(String mailPasswordEncrypted) {
		Config.mailPasswordEncrypted = mailPasswordEncrypted;
	}
	public static long getWaitFreeUpPort() {
		return waitFreeUpPort;
	}
	public static void setWaitFreeUpPort(long waitFreeUpPort) {
		Config.waitFreeUpPort = waitFreeUpPort;
	}
	public static boolean isNotificationSSLEnabled() {
		return notificationSSLEnabled;
	}
	public static void setNotificationSSLEnabled(boolean notificationSSLEnabled) {
		Config.notificationSSLEnabled = notificationSSLEnabled;
	}
	public static String getDatabaseType() {
		return databaseType;
	}
	public static void setDatabaseType(String databaseType) {
		Config.databaseType = databaseType;
	}
	public static String getApplicationname() {
		return applicationName;
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
	 */
	public static boolean loadInternalConfig(String configPath) throws InvalidKeyException, NumberFormatException, JSONException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException
	{
		 Properties properties = Config.getConfiguration(configPath);
		 return Config.init(properties);
	}
	public static boolean loadExternalConfig(String configPath) throws InvalidKeyException, NumberFormatException, JSONException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException
	{
		 Properties properties = Config.getConfigurationExternal(configPath);
		 return Config.init(properties);
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
				System.out.println("Load configuration from "+configPath);
			} 
			catch (IOException e) 
			{
				System.out.println("Unable to load configuration from "+configPath+" because an error occured.");
				e.printStackTrace();	
				return null;
			}
		} 
		else 
		{
			System.out.println("Unable to load configuration from "+configPath+" because file not found.");
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
	    InputStream input = null; 
	    try 
	    {
	        input = new FileInputStream(configPath);
	        propertis.load(input);
	        System.out.println("Load configuration from "+configPath);
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	        System.out.println("Unable to load configuration from "+configPath+".");
	    } 
	    finally 
	    {
	        if(input != null) 
	        {
	            try 
	            {
	                input.close();
	            } 
	            catch (IOException e) 
	            {
	                e.printStackTrace();
	            }
	        }
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
	 */
	private static boolean init(Properties properties) throws InvalidKeyException, IllegalArgumentException, NumberFormatException, JSONException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException
	{
		Config.properties = properties;
	
		String DATABASE1_TYPE   	   = "";
		String DATABASE1_HOST_NAME     = "";
		String DATABASE1_PORT_NUMBER   = "3306";
		String DATABASE1_USER_NAME     = "";
		String DATABASE1_USER_PASSWORD = "";
		String DATABASE1_NAME          = "";
		String DATABASE1_USED          = "";
		
		String DATABASE2_TYPE          = "";
		String DATABASE2_HOST_NAME     = "";
		String DATABASE2_PORT_NUMBER   = "3306";
		String DATABASE2_USER_NAME     = "";
		String DATABASE2_USER_PASSWORD = "";
		String DATABASE2_NAME          = "";
		String DATABASE2_USED          = "";
  		
		String DATABASE3_TYPE          = "";
		String DATABASE3_HOST_NAME     = "";
		String DATABASE3_PORT_NUMBER   = "3306";
		String DATABASE3_USER_NAME     = "";
		String DATABASE3_USER_PASSWORD = "";
		String DATABASE3_NAME          = "";
		String DATABASE3_USED          = "";

		DATABASE1_USED 			       = Config.properties.getProperty("DATABASE1_USED", "TRUE");
		DATABASE2_USED 			       = Config.properties.getProperty("DATABASE2_USED", "FALSE");
		DATABASE3_USED 			       = Config.properties.getProperty("DATABASE3_USED", "FALSE");

		Config.databaseConfig1.setDatabaseUsed(DATABASE1_USED.equals("TRUE"));
		Config.databaseConfig2.setDatabaseUsed(DATABASE2_USED.equals("TRUE"));
		Config.databaseConfig3.setDatabaseUsed(DATABASE3_USED.equals("TRUE"));
		
		Config.developmentMode = (Config.properties.getProperty("DEVELOPMENT_MODE", "FALSE").equals("TRUE"))?true:false;
		Config.createConfiguration = (Config.properties.getProperty("CREATE_CONFIGURATION", "FALSE").equals("TRUE"))?true:false;
		// Free version begin
		Config.developmentMode = true;
		// Free version end
		if(Config.developmentMode)
		{		
			DATABASE1_TYPE 			= Config.properties.getProperty("DATABASE1_TYPE", "");
			DATABASE1_HOST_NAME 	= Config.properties.getProperty("DATABASE1_HOST_NAME", "");
			DATABASE1_PORT_NUMBER 	= Config.properties.getProperty("DATABASE1_PORT_NUMBER", "");
			DATABASE1_USER_NAME 	= Config.properties.getProperty("DATABASE1_USER_NAME", "");
			DATABASE1_USER_PASSWORD = Config.properties.getProperty("DATABASE1_USER_PASSWORD", "");
			DATABASE1_NAME 			= Config.properties.getProperty("DATABASE1_NAME", "");
			
			DATABASE2_TYPE 			= Config.properties.getProperty("DATABASE2_TYPE", "");
			DATABASE2_HOST_NAME 	= Config.properties.getProperty("DATABASE2_HOST_NAME", "");
			DATABASE2_PORT_NUMBER 	= Config.properties.getProperty("DATABASE2_PORT_NUMBER", "");
			DATABASE2_USER_NAME 	= Config.properties.getProperty("DATABASE2_USER_NAME", "");
			DATABASE2_USER_PASSWORD = Config.properties.getProperty("DATABASE2_USER_PASSWORD", "");
			DATABASE2_NAME 			= Config.properties.getProperty("DATABASE2_NAME", "");
			
			DATABASE3_TYPE 			= Config.properties.getProperty("DATABASE3_TYPE", "");
			DATABASE3_HOST_NAME 	= Config.properties.getProperty("DATABASE3_HOST_NAME", "");
			DATABASE3_PORT_NUMBER 	= Config.properties.getProperty("DATABASE3_PORT_NUMBER", "");
			DATABASE3_USER_NAME 	= Config.properties.getProperty("DATABASE3_USER_NAME", "");
			DATABASE3_USER_PASSWORD = Config.properties.getProperty("DATABASE3_USER_PASSWORD", "");
			DATABASE3_NAME 			= Config.properties.getProperty("DATABASE3_NAME", "");
			
			Config.keystorePassword = Config.properties.getProperty("KEYSTORE_PASSWORD", "");
			Config.mailPassword     = Config.properties.getOrDefault("MAIL_PASSWORD", "").toString().trim();
			
			if(DATABASE1_TYPE.equals("") || DATABASE1_HOST_NAME.equals("") || DATABASE1_PORT_NUMBER.equals("") || DATABASE1_USER_NAME.equals("") || DATABASE1_NAME.equals(""))
			{
				System.err.println("Invalid database configuration");
			}

			if(Config.createConfiguration)
			{
				Encryption en = new Encryption(Config.encryptionPassword);
				Database db = new Database();
				String configuration1 = db.encryptConfiguration(DATABASE1_TYPE, DATABASE1_HOST_NAME, Integer.parseInt(DATABASE1_PORT_NUMBER), DATABASE1_USER_NAME, DATABASE1_USER_PASSWORD, DATABASE1_NAME);
				String configuration2 = db.encryptConfiguration(DATABASE2_TYPE, DATABASE2_HOST_NAME, Integer.parseInt(DATABASE2_PORT_NUMBER), DATABASE2_USER_NAME, DATABASE2_USER_PASSWORD, DATABASE2_NAME);
				String configuration3 = db.encryptConfiguration(DATABASE3_TYPE, DATABASE3_HOST_NAME, Integer.parseInt(DATABASE3_PORT_NUMBER), DATABASE3_USER_NAME, DATABASE3_USER_PASSWORD, DATABASE3_NAME);
				Config.keystorePasswordEncrypted = en.encrypt(Config.keystorePassword, true);
				Config.mailPasswordEncrypted     = en.encrypt(Config.mailPassword, true);
				
				System.out.println();
				System.out.println("DATABASE1_CONFIGURATION      = "+configuration1);
				System.out.println("DATABASE2_CONFIGURATION      = "+configuration2);
				System.out.println("DATABASE3_CONFIGURATION      = "+configuration3);
				System.out.println("KEYSTORE_PASSWORD_ENCRYPTED  = "+Config.keystorePasswordEncrypted);
				System.out.println("MAIL_PASSWORD_ENCRYPTED      = "+Config.mailPasswordEncrypted);
				System.out.println();
				System.exit(0);
			}				
			Config.databaseConfig1.initConfig(DATABASE1_TYPE, DATABASE1_HOST_NAME, Integer.parseInt(DATABASE1_PORT_NUMBER), DATABASE1_USER_NAME, DATABASE1_USER_PASSWORD, DATABASE1_NAME, DATABASE1_USED.equals("TRUE"));
			Config.databaseConfig2.initConfig(DATABASE2_TYPE, DATABASE2_HOST_NAME, Integer.parseInt(DATABASE2_PORT_NUMBER), DATABASE2_USER_NAME, DATABASE2_USER_PASSWORD, DATABASE2_NAME, DATABASE2_USED.equals("TRUE"));
			Config.databaseConfig3.initConfig(DATABASE3_TYPE, DATABASE3_HOST_NAME, Integer.parseInt(DATABASE3_PORT_NUMBER), DATABASE3_USER_NAME, DATABASE3_USER_PASSWORD, DATABASE3_NAME, DATABASE3_USED.equals("TRUE"));
		}
		else
		{
			Config.databaseConfig1 = Config.databaseConfig1.decryptConfigurationNative(properties.getOrDefault("DATABASE1_CONFIGURATION", "{}").toString());
			Config.databaseConfig2 = Config.databaseConfig2.decryptConfigurationNative(properties.getOrDefault("DATABASE2_CONFIGURATION", "{}").toString());
			Config.databaseConfig3 = Config.databaseConfig3.decryptConfigurationNative(properties.getOrDefault("DATABASE3_CONFIGURATION", "{}").toString());
			
			Config.databaseConfig1.setDatabaseUsed(DATABASE1_USED.equals("TRUE"));
			Config.databaseConfig2.setDatabaseUsed(DATABASE2_USED.equals("TRUE"));
			Config.databaseConfig3.setDatabaseUsed(DATABASE3_USED.equals("TRUE"));
			
			Config.keystorePasswordEncrypted    = Config.properties.getProperty("KEYSTORE_PASSWORD_ENCRYPTED", "");
			Config.mailPasswordEncrypted        = Config.properties.getOrDefault("MAIL_PASSWORD_ENCRYPTED", "").toString().trim();

			Encryption en = new Encryption(Config.encryptionPassword);

			Config.keystorePassword             = en.decrypt(Config.keystorePasswordEncrypted, true);			
			Config.mailPassword                 = en.decrypt(Config.mailPasswordEncrypted, true);		
			
		}			
		Config.apiDocumentRoot           = Config.properties.getOrDefault("SERVER_DOCUMENT_ROOT", "notif").toString().trim();
		Config.cleanUpTime               = Config.properties.getOrDefault("CLEAN_UP_TIME", "0200").toString().trim();
		Config.notificationPort                = Integer.parseInt(Config.properties.getOrDefault("NOTIFICATION_PORT_HTTP", "92").toString().trim());
		Config.notificationPortSSL             = Integer.parseInt(Config.properties.getOrDefault("NOTIFICATION_PORT_HTTPS", "93").toString().trim());
		Config.pusherPort            = Integer.parseInt(Config.properties.getOrDefault("PUSHER_PORT_HTTP", "94").toString().trim());
		Config.pusherPortSSL           = Integer.parseInt(Config.properties.getOrDefault("PUSHER_PORT_HTTPS", "95").toString().trim());
		Config.pusherSSLEnabled        = Config.properties.getOrDefault("PUSHER_HTTPS_ENABLED", "FALSE").toString().trim().toUpperCase().equals("TRUE");
		Config.notificationSSLEnabled           = Config.properties.getOrDefault("NOTIFICATION_HTTPS_ENABLED", "FALSE").toString().trim().toUpperCase().equals("TRUE");
		
		Config.keystoreFile              = Config.properties.getOrDefault("KEYSTORE_PATH", "TRUE").toString().trim();
		Config.connectionPerPush         = Config.properties.getOrDefault("DATABASE_CONNECTION_PER_PUSH", "TRUE").toString().trim().toUpperCase().equals("TRUE");
		Config.printStackTrace           = Config.properties.getOrDefault("PRINT_STACK_TRACE", "FALSE").toString().trim().toUpperCase().equals("TRUE");
		Config.debugMode                 = Config.properties.getOrDefault("DEBUG_MODE", "FALSE").toString().trim().toUpperCase().equals("TRUE");
		Config.developmentMode           = Config.properties.getOrDefault("DEVELOPMENT_MODE", "FALSE").toString().trim().toUpperCase().equals("TRUE");
		Config.filterSource              = Config.properties.getOrDefault("FILTER_SOURCE", "TRUE").toString().trim().toUpperCase().equals("TRUE");
		Config.groupCreationApproval     = Config.properties.getOrDefault("GROUP_CREATION_APPROVAL", "TRUE").toString().trim().toUpperCase().equals("TRUE");
		Config.inspectionInterval        = Long.parseLong(Config.properties.getOrDefault("INTERVAL_INSPECTION", "1800000").toString().trim());
		Config.gcInterval                = Integer.parseInt(Config.properties.getOrDefault("INTERVAL_GARBAGE_COLLECTION", "59000").toString().trim());	
		Config.limitNotification         = Long.parseLong(Config.properties.getOrDefault("LIMIT_NOTIFICATION", "50").toString().trim());
		Config.limitTrash                = Long.parseLong(Config.properties.getOrDefault("LIMIT_TRASH", "200").toString().trim());
		Config.pusherContextPusher       = Config.properties.getOrDefault("SERVER_CONTEXT_PUSHER", "pusher").toString().trim();
		Config.pusherContextRemover      = Config.properties.getOrDefault("SERVER_CONTEXT_REMOVER", "remover").toString().trim();
		Config.pusherContextCreateGroup  = Config.properties.getOrDefault("SERVER_CONTEXT_CREATE_GROUP", "create-group").toString().trim();
		Config.waitDatabaseReconnect     = Integer.parseInt(Config.properties.getOrDefault("WAIT_FOR_RECONNECT_DATABASE", "10000").toString().trim());
		Config.waitForAnswer             = Integer.parseInt(Config.properties.getOrDefault("WAIT_FOR_ANSWER", "30000").toString().trim());			
		Config.deleteNotifSent           = Integer.parseInt(Config.properties.getOrDefault("DELETE_NOTIF_SENT", "3").toString().trim());	
		Config.deleteNotifNotSent        = Integer.parseInt(Config.properties.getOrDefault("DELETE_NOTIF_NOT_SENT", "10").toString().trim());	
		Config.contentSecure             = Config.properties.getOrDefault("CONTENT_SECURE", "FALSE").toString().trim().toUpperCase().equals("TRUE");
		Config.redirectHome              = Config.properties.getOrDefault("REDIRECT_HOME", "https://www.planetbiru.com").toString().trim();		
		
		Config.mailHost                  = Config.properties.getOrDefault("MAIL_HOST", "localhost").toString().trim();	
		Config.mailPort                  = Integer.parseInt(Config.properties.getOrDefault("MAIL_PORT", "25").toString().trim());	
		Config.mailUseAuth               = Config.properties.getOrDefault("MAIL_USE_AUTH", "FALSE").toString().trim().toUpperCase().equals("TRUE");
		Config.mailUsername              = Config.properties.getOrDefault("MAIL_USERNAME", "").toString().trim();
		
		Config.mailSubject               = Config.properties.getOrDefault("MAIL_SUBJECT", "Pusher Address Confirmation").toString().trim();
		Config.mailTemplate              = Config.properties.getOrDefault("MAIL_TEMPLATE", "").toString().trim();
		Config.mailSender                = Config.properties.getOrDefault("MAIL_SENDER", "user@example.com").toString().trim();
		
		Config.approvalURLTemplate       = Config.properties.getOrDefault("APPROVAL_URL_TEMPLATE", "http://push.example.com/approve-address/?auth={hash}").toString().trim();
		Config.readConfigSuccess         = true;			
		return Config.readConfigSuccess;		
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
