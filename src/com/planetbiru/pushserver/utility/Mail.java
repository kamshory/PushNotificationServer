package com.planetbiru.pushserver.utility;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.internet.*;

import com.planetbiru.pushserver.config.Config;

public class Mail 
{
	/**
	 * FROM address
	 */
	private String from = "";
	/**
	 * TO address
	 */
	private String to = "";
	/**
	 * CC address
	 */
	@SuppressWarnings("unused")
	private String cc = "";
	/**
	 * BCC address
	 */
	@SuppressWarnings("unused")
	private String bcc = "";
	/**
	 * Email properties
	 */
	private Properties properties;
	/**
	 * Session
	 */
	private Session session;
	/**
	 * Message type
	 */
	private MimeMessage message;	
	/**
	 * Mail authenticator
	 */
	private Authenticator auth;
	/**
	 * SMTP host
	 */
	private String host = "localhost";
	/**
	 * Flag to use authentication or not
	 */
	private boolean useAuth = false;
	/**
	 * SMTP Username
	 */
	private String username = "";
	/**
	 * SMTP Password
	 */
	private String password = "";
	/**
	 * Main server port
	 */
	private int port = 25;
	
	
	
	/**
	 * Default constructor
	 */
	public Mail()
	{
		this.host = Config.getMailHost();
		this.useAuth = Config.isMailUseAuth();
		this.username = Config.getMailUsername();
		this.password = Config.getMailPassword();
		this.port = Config.getMailPort();
		
		this.properties = System.getProperties();
		this.properties.put("mail.smtp.host", this.host);
		this.properties.put("mail.smtp.port", this.port );
		this.auth = new SMTPAuthenticator(this.username, this.password);
		if(this.useAuth )
		{
			this.properties.put("mail.transport.protocol", "smtp");
			this.properties.put("mail.smtp.auth", "true");
			this.session = Session.getDefaultInstance(this.properties, this.auth);   
		}
		else
		{
			this.session = Session.getDefaultInstance(this.properties);   
		}
		this.message = new MimeMessage(this.session);
	}
	/**
	 * Set FROM address
	 * @param address Email address
	 * @throws MessagingException if any errors occurred while send message
	 */
	public void setFrom(String address) throws MessagingException
	{
		this.from = address;
	}
	/**
	 * Set TO address
	 * @param address Email address
	 * @throws MessagingException if any errors occurred while send message
	 */
	public void setTo(String address) throws MessagingException
	{
		this.to = address;
	}
	/**
	 * Set CC address
	 * @param address Email address
	 * @throws AddressException if any invalid address
	 * @throws MessagingException if any errors occurred while send message
	 */
	public void setCC(String address) throws MessagingException
	{
		this.cc = address;
		this.message.setRecipient(Message.RecipientType.CC, new InternetAddress(address));
	}
	/**
	 * Set BCC address
	 * @param address Email address
	 * @throws MessagingException if any errors occurred while send message
	 */
	public void setBCC(String address) throws MessagingException
	{
		this.bcc  = address;
		this.message.setRecipient(Message.RecipientType.BCC, new InternetAddress(address));
	}
	/**
	 * Send mail
	 * @param subject Subject
	 * @param text Message
	 * @param type Mime type
	 * @return true if success and false if failed
	 * @throws MessagingException if any errors occurred while send message
	 */
	public boolean send(String subject, String text, String type) throws MessagingException
	{
		this.message.setFrom(new InternetAddress(this.from));
		this.message.addRecipient(Message.RecipientType.TO, new InternetAddress(this.to));
		this.message.setSubject(subject);
		this.message.setContent(text, type);
		Transport.send(this.message);
		return true;
	}
	/**
	 * Check whether mail address is valid or not
	 * @param emailAddress Email address to be checked
	 * @return true if valid and false if invalid
	 */
	public boolean isValidMailAddress(String emailAddress)
	{
		Pattern regexPattern;
	    Matcher regMatcher;
        regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
        regMatcher = regexPattern.matcher(emailAddress);
        return regMatcher.matches();
	}
	private class SMTPAuthenticator extends javax.mail.Authenticator 
    {
        private String username = "";
        private String password = "";
    	public SMTPAuthenticator(String username, String password)
    	{
    		this.username = username;
    		this.password = password;
    	}
    	
    	@Override
        public PasswordAuthentication getPasswordAuthentication() 
        {
           return new PasswordAuthentication(this.username, this.password);
        }
    }
}
