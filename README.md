# PushNotificationServer
Push notification server for mobile application.

## Features
1. Socket communication
2. HTTP communication for pusher and remover
3. HTTPS communication for pusher and remover

HTTP and HTTPS port is different. So pusher can choose one of them.

If yo won't to use port 80 and 443 for any reason, you can user proxy to redirect the push. Source code to create proxy can be found at 
https://github.com/kamshory/PHPProxyServer


## Plan

| Features                    | Free | Premium |
|-----------------------------|------|---------|
| Unlimited Notification      |   ✔  |    ✔    |
| HTTP and HTTPS Pusher       |   ✔  |    ✔    |
| HTTP and HTTPS Remover      |   ✔  |    ✔    |
| Encrypted DB Config         |   ✘  |    ✔    |
| Encrypted Keystore Password |   ✘  |    ✔    |


## Usage

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

