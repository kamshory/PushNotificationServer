# PushNotificationServer
Push notification server for mobile application.

## Features
1. Socket communication
2. HTTP communication for pusher and remover
3. HTTPS communication for pusher and remover

HTTP and HTTPS port is different. So pusher can choose one of them.

Usage

### Start Service

```bash
java -jar pushserver.jar config=config.ini
```

### Stop Service

```bash
java -jar pushserver.jar action=stop
```

### Modify Configuration

To modify configuration, open **config.ini**, modify and save it.

### Encrypt Database Configuration

To create encrypted database configuration, please do steps as follow:

1. Modify Configuration

```
DEVELOPMENT_MODE             = TRUE
CREATE_CONFIGURATION         = TRUE

DATABASE1_TYPE               = mysql
DATABASE1_HOST_NAME          = {db-host}
DATABASE1_PORT_NUMBER        = {db-port}
DATABASE1_USER_NAME          = {db-user}
DATABASE1_USER_PASSWORD      = {db-pass}
DATABASE1_NAME               = {db-name}
DATABASE1_USED               = TRUE

DATABASE2_TYPE               = mysql
DATABASE2_HOST_NAME          = {db-host}
DATABASE2_PORT_NUMBER        = {db-port}
DATABASE2_USER_NAME          = {db-user}
DATABASE2_USER_PASSWORD      = {db-pass}
DATABASE2_NAME               = {db-name}
DATABASE2_USED               = TRUE

DATABASE3_TYPE               = mysql
DATABASE3_HOST_NAME          = {db-host}
DATABASE3_PORT_NUMBER        = {db-port}
DATABASE3_USER_NAME          = {db-user}
DATABASE3_USER_PASSWORD      = {db-pass}
DATABASE3_NAME               = {db-name}
DATABASE3_USED               = FALSE
```

2. Run Service in Console

```bash
java -jar pushserver.jar config=config.ini
```

3. Copy DATABASE1_CONFIGURATION, DATABASE2_CONFIGURATION and DATABASE3_CONFIGURATION to file config.ini

4. Remove properties bellow

```
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
```

Please don't remove

```
DATABASE1_USED               = TRUE
DATABASE2_USED               = TRUE
DATABASE3_USED               = TRUE
```
5. Modify config.ini to
```
DEVELOPMENT_MODE             = FALSE
CREATE_CONFIGURATION         = FALSE
```

Now your database configuration is safe.

### Create SSL Centificate

1. Go to your JAVA home
2. Create key file
3. Create certificate file
4. Copy key file and certificate file to your destination directory
5. Edit **KEYSTORE_PATH** on config.ini to point to the key file

To create key file and certificate file, do commands as follow:
```
C:
cd C:\Program Files\Java\jre1.8.0_191\bin
keytool -genkey -alias push.example.com -keyalg RSA -keystore d:\keystore.jks -keysize 2048
keytool -certreq -alias push.example.com -keystore d:\keystore.jks -file d:\example.com.csr
```



