# PushNotificationServer
Push notification server for mobile application.
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

HTTP and HTTPS port is different. So pusher can choose one of them.

If you won't to use port 80 and 443 for any reason, you can user proxy to redirect the push. Source code to create proxy can be found at 
https://github.com/kamshory/PHPProxyServer

To get notification sender, please visit https://github.com/kamshory/PushNotificationSender

To get notification client, please visit https://github.com/kamshory/PushNotificationClient

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
| Commercial Use                  	|   ✘  	|    ✔    	|
| Production Mode                  	|   ✘  	|    ✔    	|
| Encrypted Database Configuration 	|   ✘  	|    ✔    	|
| Encrypted Keystore Password      	|   ✘  	|    ✔    	|

## Requirement
1. Operating System
   Supported operating system is Windows and Linux
2. Environtment
   Push Notification Server need Java Runtime Environtment (JRE) 1.8
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
1. Create MySQL or MariaDB database
2. Import *notification-postgresql.sql* database into your database
3. Open *createapi-postgresql.sql*, replace **passclient** with your client password and **passpusher** with your pusher password 
4. Execute *createapi-postgresql.sql* on your database

## Configuration

### Basic Configuration

To modify configuration, open *config.ini*, modify and save it.

### Encrypt Database Configuration

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
```

TABLE_PREFIX must be appropriate with the table name. For example, the table name listed bellow:

- push_api
- push_client
- push_client_group
- push_notification
- push_pusher_address
- push_trash

So the TABLE_PREFIX  will be **push_**. You can change the TABLE_PREFIX according to your table name. With the TABLE_PREFIX, you can integrate the push notification server database with your own application.

2. Run Service in Console

```bash
java -jar pushserver.jar config=config.ini
```

3. Copy DATABASE1_CONFIGURATION, DATABASE2_CONFIGURATION DATABASE3_CONFIGURATION, and KEYSTORE_PASSWORD_ENCRYPTED to file config.ini

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

```

Please don't remove

```ini
DATABASE1_USED               = TRUE
DATABASE2_USED               = TRUE
DATABASE3_USED               = TRUE
```
5. Modify config.ini to
```ini
DEVELOPMENT_MODE             = FALSE
CREATE_CONFIGURATION         = FALSE
```

Now your database configuration is safe.

## Usage

### Start Service

```bash
java -jar pushserver.jar config=config.ini
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



