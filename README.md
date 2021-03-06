# PushNotificationServer

## Push Notification

Push Notification is a notification that is forcibly sent by the server to the client so that the notification sent to the client without waiting for the client to request it. In order for the notification to be accepted by the client, the client and server must always be connected through socket communication.

The notification server can be part of the application server and can also be provided by third parties.

Currently, there are many companies that provide push notification services, both free and paid. Some people choose to use their own push notification server for various reasons. If you are among those who want to have their own push notifications that you can install on your online and offline networks, then this application might be for you.

The application server must know the device ID of each user. When the application server sends notifications to users, the application server sends notifications to the notification server that is addressed to the user's device.

When the pusher address filter is applied, the pusher address must be registered in the database in order to send notifications to the destination device. When a new address tries to send a notification, the server records the new address and sends an email to the administrator to approve the address. If the address is approved by the administrator, pusher can send notifications accordingly. This address can be activated and deactivated at any time.

User can modify the mail template. See file *mail-template.html*

## Features

1. Unlimited Notification
2. Notification Group
3. Device Registration
4. Push Source Filter
5. Auto Reconnect Database
6. HTTP and HTTPS Pusher
7. HTTP and HTTPS Remover
8. Production Mode
9. Encrypted Database Configuration
10. Encrypted Keystore Password
11. End-to-End Encryption
12. Send Mail for Pusher Approval

HTTP and HTTPS port is different. So pusher can choose one of them.

## Plan

| Features                         	| Free 	| Premium 	|
|----------------------------------	|:----:	|:-------:	|
| Unlimited Notification           	|   ✔  	|    ✔    	|
| Notification Group               	|   ✔  	|    ✔    	|
| Device Registration              	|   ✔  	|    ✔    	|
| Push Source Filter               	|   ✔  	|    ✔    	|
| Auto Reconnect Database          	|   ✔  	|    ✔    	|
| MariaDB Database Support          |   ✔  	|    ✔    	|
| MySQL Database Support          	|   ✔  	|    ✔    	|
| PostgreSQL Database Support       |   ✔  	|    ✔    	|
| HTTP and HTTPS Pusher            	|   ✔  	|    ✔    	|
| HTTP and HTTPS Remover           	|   ✔  	|    ✔    	|
| Native and SSL Socket           	|   ✔  	|    ✔    	|
| Commercial Use                  	|   ✔  	|    ✔    	|
| Production Mode                  	|   ✔  	|    ✔    	|
| Encrypted Database Configuration 	|   ✘  	|    ✔     |
| Encrypted Keystore Password      	|   ✘  	|    ✔     |
| Send Mail for Pusher Approval     |   ✘   |    ✔     |

## Requirement
1. Operating System

Supported operating system is Windows and Linux

2. Environment

Push Notification Server need Java Runtime Environment (JRE) 1.8

3. Database Management System

Push Notification Server require MySQL database server, MariaDB database server or PostgreSQL database server

## Installation

### Database Preparation
#### For MySQL and MariaDB Database
1. Create MySQL or MariaDB database
2. Import *notification.sql* database into your database
3. Open *createapi.sql*, replace **passclient** with your client password and **passpusher** with your pusher password 
4. Execute *createapi.sql* on your database

#### For PostgreSQL
1. Create PostgreSQL database
2. Import *notification-postgresql.sql* database into your database
3. Open *createapi-postgresql.sql*, replace **passclient** with your client password and **passpusher** with your pusher password 
4. Execute *createapi-postgresql.sql* on your database

## Configuration

### Basic Configuration

To modify configuration, open *config.ini*, modify and save it.

### SMTP Configuration

You can use SMTP with authentication and also without authentication.
For SMTP without authentication, use localhost. In this way, emails will be sent faster. 

Configuring SMTP with authentication on localhost is as follows:
```
MAIL_HOST                    = mail.example.com
MAIL_USE_AUTH                = TRUE
MAIL_USERNAME                = user
MAIL_PASSWORD                = password
```

Configuring SMTP without authentication on localhost is as follows:
```ini
MAIL_HOST                    = localhost
MAIL_USE_AUTH                = FALSE
MAIL_USERNAME                = 
MAIL_PASSWORD                = 
```

On production mode, user **MAIL_PASSWORD_ENCRYPTED** to store the password and let **MAIL_PASSWORD** 

### Encrypt Configuration

To create encrypted database configuration, please do steps as follow:

1. Modify Configuration

```ini
TABLE_PREFIX                 = push_
DEVELOPMENT_MODE             = TRUE
CREATE_CONFIGURATION         = TRUE

#mysql for MySQL and MariaDB database server
#postgresql for PostgeSQL database server
DATABASE1_TYPE               = mysql
DATABASE1_HOST_NAME          = {db-host}
DATABASE1_PORT_NUMBER        = {db-port}
DATABASE1_USER_NAME          = {db-user}
DATABASE1_USER_PASSWORD      = {db-pass}
DATABASE1_NAME               = {db-name}
DATABASE1_USED               = TRUE

#mysql for MySQL and MariaDB database server
#postgresql for PostgeSQL database server
DATABASE2_TYPE               = mysql
DATABASE2_HOST_NAME          = {db-host}
DATABASE2_PORT_NUMBER        = {db-port}
DATABASE2_USER_NAME          = {db-user}
DATABASE2_USER_PASSWORD      = {db-pass}
DATABASE2_NAME               = {db-name}
DATABASE2_USED               = TRUE

#mysql for MySQL and MariaDB database server
#postgresql for PostgeSQL database server
DATABASE3_TYPE               = mysql
DATABASE3_HOST_NAME          = {db-host}
DATABASE3_PORT_NUMBER        = {db-port}
DATABASE3_USER_NAME          = {db-user}
DATABASE3_USER_PASSWORD      = {db-pass}
DATABASE3_NAME               = {db-name}
DATABASE3_USED               = TRUE

KEYSTORE_PASSWORD            = {keystore-pass}
MAIL_PASSWORD                = {mail-pass}
```

TABLE_PREFIX must be appropriate with the table name. For example, the table name listed bellow:

- push_api
- push_api_user
- push_client
- push_client_group
- push_notification
- push_pusher_address
- push_trash
- push_user

So the TABLE_PREFIX  will be **push_**. You can change the TABLE_PREFIX according to your table name. With the TABLE_PREFIX, you can integrate the push notification server database with your own application.

2. Run Service in Console

```bash
java -jar pushserver.jar config=config.ini
```

3. Copy DATABASE1_CONFIGURATION, DATABASE2_CONFIGURATION DATABASE3_CONFIGURATION, KEYSTORE_PASSWORD_ENCRYPTED and MAIL_PASSWORD_ENCRYPTED to file *config.ini*

4. Remove properties bellow

```ini
DATABASE1_TYPE               = mysql
DATABASE1_HOST_NAME          = {db-host}
DATABASE1_PORT_NUMBER        = {db-port}
DATABASE1_USER_NAME          = {db-user}
DATABASE1_USER_PASSWORD      = {db-pass}
DATABASE1_NAME               = {db-name}

DATABASE2_TYPE               = mysql
DATABASE2_HOST_NAME          = {db-host}
DATABASE2_PORT_NUMBER        = {db-port}
DATABASE2_USER_NAME          = {db-user}
DATABASE2_USER_PASSWORD      = {db-pass}
DATABASE2_NAME               = {db-name}

DATABASE3_TYPE               = mysql
DATABASE3_HOST_NAME          = {db-host}
DATABASE3_PORT_NUMBER        = {db-port}
DATABASE3_USER_NAME          = {db-user}
DATABASE3_USER_PASSWORD      = {db-pass}
DATABASE3_NAME               = {db-name}

KEYSTORE_PASSWORD            = {keystore-pass}
MAIL_PASSWORD                = {mail-pass}

```

Please don't remove

```ini
DATABASE1_USED               = TRUE
DATABASE2_USED               = TRUE
DATABASE3_USED               = TRUE
```
5. Modify *config.ini* to
```ini
DEVELOPMENT_MODE             = FALSE
CREATE_CONFIGURATION         = FALSE
```

Now your database configuration is safe.

## Usage

Service is CLI application. User only can control application by using the command line when start it. However, user can stop the service by using the command line too.

| Argument             	| Meaning                   	|
|----------------------	|---------------------------	|
| -h                   	| Show Help                 	|
| --help               	| Show Help                 	|
| action=stop          	| Stop Service              	|
| action=start         	| Start Service             	|
| config={config-path} 	| Set Configuration Path    	|
| debug-mode=true      	| Run Service in Debug Mode 	|

### Start Service

**Normal Mode**

```bash
java -jar pushserver.jar config=/etc/pushnotif/config.ini
```

**Debug Mode**

```bash
java -jar pushserver.jar config=/etc/pushnotif/config.ini debug-mode=true
```


### Stop Service

```bash
java -jar pushserver.jar action=stop
```

## Create SSL Centificate

1. Go to your JAVA home
2. Create key file
3. Create certificate file
4. Copy key file and certificate file to your destination directory
5. Edit **KEYSTORE_PATH** on config.ini to point to the key file

The command to generate a Java keystore and keypair:

```
keytool -genkey -alias example.com -keyalg RSA -keystore keystore.jks -keysize 2048
```

The command to generate a certificate signing request (CSR) for an existing Java keystore:

```
keytool -certreq -alias example.com -keystore keystore.jks -file example.com.csr
```

The command for importing a root or intermediate certificate to an existing Java keystore:

```
keytool -import -trustcacerts -alias root -file Thawte.crt -keystore keystore.jks
```

The command for importing a signed primary certificate to an existing Java keystore:

```
keytool -import -trustcacerts -alias example.com -file example.com.crt -keystore keystore.jks
```

The command to generate a keystore and a self-signed certificate:

```
keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048
```

The list of above commands will assist in generating a keypair and certificate signing request for a certificate.

We also gathered the list of Java Keystore commands to validate the generation process for certificates and CSRs.

The command for checking a standalone certificate:

```
keytool -printcert -v -file example.com.crt
```

The command for checking which certificates are in a Java keystore:

```
keytool -list -v -keystore keystore.jks
```

The command for checking a particular keystore entry using an alias:

```
keytool -list -v -keystore keystore.jks -alias example.com
```

Additionally, there are few crucial processes where you need Java Keytool commands. Let’s have those commands for further validation.

The command for deleting a certificate from a Java Keytool keystore:

```
keytool -delete -alias example.com -keystore keystore.jks
```

The command for changing a Java keystore password:

```
keytool -storepasswd -new new_storepass -keystore keystore.jks
```

The command for exporting a certificate from a keystore:

```
keytool -export -alias example.com -file example.com.crt -keystore keystore.jks
```

The command to view a list of trusted CA certs:

```
keytool -list -v -keystore $JAVA_HOME/jre/lib/security/cacerts
```

The command for importing new CAs into your trusted certs:

```
keytool -import -trustcacerts -file /path/to/ca/ca.pem -alias CA_ALIAS -keystore $JAVA_HOME/jre/lib/security/cacerts
```

We are sure that this list of commands will definitely save developers' time while implementing a certificate for an existing application or a website.

For more information, visit https://dzone.com/articles/understand-java-keytool-keystore-commands 

## KeyStore Application

Another way to create keystore is by using KeyStore Explorer. KeyStore Explorer is an open source GUI replacement for the Java command-line utilities keytool and jarsigner. KeyStore Explorer presents their functionality, and more, via an intuitive graphical user interface. 

Download KeyStore Explorer from https://keystore-explorer.org/

# API Documentation

Assume that:

- Domain : yourdomain.tld
- Pusher Port (Accessed by Your Application Server) : 8080
- Notification Port (Accessed by Mobile Devices) : 9090

## Authorization

```php
$YourAPIKey = "PLANETBIRU";
$YourAPIPusherPassword = "PASSWORD123";
$CanonicalRequestBody = str_replace(array("\r", "\n", "\t", " "), "", $RequestBody);

$UnixTimestamp = time(0);
$YourToken = sha1($UnixTimestamp . $YourAPIKey);
$YourSignature = sha1(sha1($YourAPIPusherPassword)."-".$YourToken."-".$YourAPIKey); 
$DataIntegrity = sha1(sha1($YourAPIPusherPassword)."-".$YourToken."-".$CanonicalRequestBody); 

$YourAPIKey = urlencode($YourAPIKey);
$YourGroup = urlencode($YourGroup);
```

## Create Group

Before application send notification, user must create a notification user group on push notification server. Notification user group usefull if application has more than one level user that receive push notification. Aplication must send device ID and notification user group when it send notification.

```http
POST /create-group HTTP/1.1
Host: yourdomain.tld:8080
Autorization: Bearer key=YourAPIKey&token=YourToken&hash=YourSignature&time=UnixTimestamp&group=YourGroup
X-Integrity: DataIntegrity
X-Application-Name: Your Application Name
X-Application-Version: Your Application Version
Content-Type: application/json

{
    "command": "create-group",
    "data": {
        "groupKey": "GR1",
        "groupName": "Group 1",
        "groupDescription": "This is the description of the group 1"
    }
}

```

## Register Device

Before application send notification, user must register user device. Application must save the user's device on its database. When aplication send notification for any user, aplication must send it to all user devices. Aplication must send device ID and notification user group when it send notification. If device ID not registered on the group, push notification server will ignore the notification without push it to the device or save it into the database.


```http
POST /register-device HTTP/1.1
Host: yourdomain.tld:8080
Autorization: Bearer key=YourAPIKey&token=YourToken&hash=YourSignature&time=UnixTimestamp&group=YourGroup
X-Integrity: DataIntegrity
X-Application-Name: Your Application Name
X-Application-Version: Your Application Version
Content-Type: application/json

{
    "command": "register-device",
    "data": {
        "deviceID": "1345632163"
    }
}

```

## Unregister Device

When the user has ended using the application and no longer wants to receive notifications, the user must unregister the device. This action will remove the device on user notification group. Application must also remove the pair or user ID, group and device ID on its database.

```http
POST /unregister-device HTTP/1.1
Host: yourdomain.tld:8080
Autorization: Bearer key=YourAPIKey&token=YourToken&hash=YourSignature&time=UnixTimestamp&group=YourGroup
X-Integrity: DataIntegrity
X-Application-Name: Your Application Name
X-Application-Version: Your Application Version
Content-Type: application/json

{
    "command": "unregister-device",
    "data": {
        "deviceID": "1345632163"
    }
}

```

## Send Notification From Application Server

```http
POST /pusher HTTP/1.1
Host: yourdomain.tld:8080
Autorization: Bearer key=YourAPIKey&token=YourToken&hash=YourSignature&time=UnixTimestamp&group=YourGroup
X-Integrity: DataIntegrity
X-Application-Name: Your Application Name
X-Application-Version: Your Application Version
Content-Type: application/json

{
    "command":"push-notification",
    "data": {
        "deviceIDs": ["DeviceID1", "DeviceID2", "DeviceID3"],
        "data": {
                "message": "Notification message",
                "title": "Title",
                "subtitle": "Subtitle",
                "tickerText": "Ticker text",
                "uri": "http://yourdomain.tld/your-path?args1=val1&args2=val2",
                "clickAction": "open-url",
                "type": "info",
                "miscData": {},
                "color": "#FF5599",
                "vibrate": [200, 0, 200, 400, 0],
                "sound": "sound1.wav",
                "badge": "badge.png",
                "largeIcon": "large_icon.png",
                "smallIcon": "small_icon.png"
        }
    }
}

```

## Delete Sent Notification From Application Server

```http
POST /remover HTTP/1.1
Host: yourdomain.tld:8080
Autorization: Bearer key=YourAPIKey&token=YourToken&hash=YourSignature&time=UnixTimestamp&group=YourGroup
X-Integrity: DataIntegrity
X-Application-Name: Your Application Name
X-Application-Version: Your Application Version
Content-Type: application/json

{
    "command": "remove-notification",
    "data": {
        "id": [1, 2, 3, 4]
    }
}

```