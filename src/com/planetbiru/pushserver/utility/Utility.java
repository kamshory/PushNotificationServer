package com.planetbiru.pushserver.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.planetbiru.pushserver.config.Config;

/**
 * Utility
 * @author Kamshory, MT
 *
 */
public class Utility {
	/**
	 * Default constructor
	 */
	private Utility()
	{
		
	}
	/**
	 * Get first header
	 * @param headers Headers
	 * @param key Key
	 * @return Value of the header
	 */
	public static String getFirst(String[] headers, String key) 
	{
		return Utility.getFirst(headers, key, "", ":");
	}
	/**
	 * Get first header with default value and delimiter
	 * @param headers Headers
	 * @param key Key
	 * @param defaultValue Default value
	 * @param delimiter Delimiter
	 * @return Value of the header
	 */
	public static String getFirst(String[] headers, String key, String defaultValue, String delimiter)
	{
		int i;
		String value = defaultValue;
		String line = "";
		String[] arr;
		String key2 = "";
		key = key.trim();
		
		String key3, key4;
		String spliter = delimiter;
		if(spliter.equals(":"))
		{
			spliter = "\\:";
		}
		for(i = 0; i<headers.length; i++)
		{
			line = headers[i].trim();
			if(line.contains(delimiter))
			{
				arr = line.split(spliter, 2);
				key2 = arr[0].trim();
				key3 = key.toLowerCase().trim();
				key4 = key2.toLowerCase().trim();
				if(key3.equals(key4))
				{
					value = arr[1].trim();
					break;
				}
			}
		}
		return value;
	}
	/**
	 * Encode URL
	 * @param input Clear URL
	 * @return Decoded URL
	 */
	public static String urlEncode(String input) 
	{
	   	String result = "";
		try 
		{
			result = java.net.URLEncoder.encode(input, "UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
    	return result;
	}
	/**
	 * Decode URL
	 * @param input Decoded URL
	 * @return Clear URL
	 */
	public static String urlDecode(String input)
    {
    	String result = "";
		try 
		{
			result = java.net.URLDecoder.decode(input, "UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			if(Config.isPrintStackTrace())
			{
				e.printStackTrace();
			}
		}
    	return result;
    }
	/**
	 * Parse query string into map
	 * @param query Query string
	 * @return Map contains query parsed
	 * @throws UnsupportedEncodingException if character encoding is not supported
	 */
	public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException 
	{
	    Map<String, String> queryPairs = new LinkedHashMap<>();
	    String[] pairs = query.split("&");
	    for (String pair : pairs) 
	    {
	        int idx = pair.indexOf("=");
	        queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	    }
	    return queryPairs;
	}
	/**
	 * Parse query string into JSON object
	 * @param query Query string
	 * @return JSONObject contains query parsed
	 * @throws JSONException if any JSON errors
	 */
    public static Map<String, String> parseQuery(String query) throws JSONException
    {
    	Map<String, String> queryString = new HashMap<>();
    	if(query == null)
    	{
    		query = "";
    	}
    	if(query.length() > 0)
    	{
	    	String[] args;
	    	int i;
	    	String arg = "";
	    	String[] arr;
	    	String key = "";
	    	String value = "";
    		if(query.contains("&"))
    		{
    			args = query.split("&");
    		}
    		else
    		{
    			args = new String[1];
    			args[0] = query;
    		}
    		for(i = 0; i<args.length; i++)
    		{
    			arg = args[i];
    			if(arg.contains("="))
    			{
    				arr = arg.split("=", 2);
    				key = arr[0];
    				value = Utility.urlDecode(arr[1]);
    				queryString.put(key, value);
    			}
    		}
    	}
    	return queryString;
    }
    /**
     * Build query string
     * @param query JSONObject contains query information
     * @return Clear query string
     * @throws JSONException if any JSON errors
     */
    public static String buildQuery(JSONObject query) throws JSONException
    {
    	StringBuilder result = new StringBuilder();
    	
    	Iterator<?> keys = query.keys();
    	String key = "";
    	String value = "";
    	int i = 0;
    	while( keys.hasNext() ) 
    	{
    	    key = (String) keys.next();
			if(query.get(key) instanceof JSONObject) 
			{
				value = query.optString(key, "");
				value = Utility.urlEncode(value);
				if(i > 0)
				{
					result.append("&");
				}
				result.append((key+"="+value));
			}
			i++;
    	}
    	return result.toString();
    }
    public static String buildQuery(Map<String, String> query) throws NullPointerException
    {
    	StringBuilder result = new StringBuilder();
    	int i = 0;
    	String key = "";
    	String value = "";
    	for(Map.Entry<String, String> entry : query.entrySet())
    	{
    	    if(i > 0)
    	    {
    	    	result.append("&");
    	    }
    	    key = entry.getKey();
    	    value = entry.getValue();
    	    value = Utility.urlEncode(value);
    	    result.append((key+"="+value));
    	    i++;
    	}
    	return result.toString();
   	
    }
	/**
	 * Strip characters from the beginning of a string
	 * @param input String to be stripped
	 * @param mask Character mask to strip string
	 * @return Stripped string
	 */
	public static String lTrim(String input, String mask)
	{
		int lastLen = input.length();
		int curLen;
		do
		{
			lastLen = input.length();
			input = input.replaceAll("^"+mask, "");
			curLen = input.length();
		}
		while (curLen < lastLen);
		return input;
	}
	/**
	 * Strip characters from the end of a string
	 * @param input String to be stripped
	 * @param mask Character mask to strip string
	 * @return Stripped string
	 */
	public static String rTrim(String input, String mask)
	{
		int lastLen;
		int curLen;
		do
		{
			lastLen = input.length();
			input = input.replaceAll(mask+"$", "");
			curLen = input.length();
		}
		while (curLen < lastLen);
		return input;
	}
	/**
	 * Get N right string
	 * @param input Input string
	 * @param length Expected length
	 * @return N right string
	 */
	public static String right(String input, int length)
	{
		if(length >= input.length())
		{
			return input;
		}
		else
		{
			return input.substring(input.length() - length, input.length());
		}
	}
	/**
	 * Get N left string
	 * @param input Input string
	 * @param length Expected length
	 * @return N left string
	 */
	public static String left(String input, int length)
	{
		if(length >= input.length())
		{
			return input;
		}
		else
		{
			return input.substring(0, length);
		}
	}
	/**
	 * Encode parameter
	 * @param param Parameter to be encoded
	 * @return Encoded parameter
	 */
	public static String escapeParameter(String param)
	{
		String output = param;
		output = output.replace("&", "%26");
		output = output.replace("=", "%3D");
		return output;
	}
	/**
	 * Decode parameter
	 * @param param Parameter to be decoded
	 * @return Decoded parameter
	 */
	public static String deescapeParameter(String param)
	{
		String output = param;
		output = output.replace("%26", "&");
		output = output.replace("%3D", "=");
		return output;
	}
	/**
	 * Escape the XML data
	 * @param input Data to be escaped
	 * @return Escaped string
	 */
	public static String escapeXML(String input)
	{
		String output = input;
		output = output.replace("&", "&amp;");
		output = output.replace("\"", "&quot;");
		output = output.replace("<", "&lt;");
		output = output.replace(">", "&gt;");
		return output;
	}
	/**
	 * Remove escape character of the XML data
	 * @param input Data to be escaped
	 * @return Escaped string
	 */
	public static String deescapeXML(String input)
	{
		String output = input;
		output = output.replace("&lt;", "<");
		output = output.replace("&gt;", ">");
		output = output.replace("&amp;", "&");
		output = output.replace("&quot;", "\"");
		return output;
	}
	/**
	 * Escape string before use it in a SQL command
	 * @param s Input string
	 * @return Escapedstring
	 */
	public static String escapeSQL(String s)
	{
	    s = s.replaceAll("\\00", "\\\\0");
	    s = s.replace("'", "''");
	    return s;
	}
	/**
	 * Remove escape character from a escaped string
	 * @param s Escaped string
	 * @return Clear string
	 */
	public static String deescapeSQL(String s)
	{
	    s = s.replaceAll("\\\\00", "\\0");
	    s = s.replace("''", "'");
	    return s;
	}
	/**
	 * Remove escape character of the JSON data
	 * @param input Data to be escaped
	 * @return Escaped string
	 */
	public static String escapeJSON(String input)
	{
		String output = "";
		if(input != null)
		{
			output = input.replace("\"", "\\\"");
		}
		else
		{		
		}
		return output;
	}
	/**
	 * Remove escape characters of JSON string
	 * @param input JSON string to be clean up
	 * @return Clean JSON string
	 */
	public static String deescapeJSON(String input)
	{
		String output = input;
		output = output.replace("\\\"", "\"");
		return output;
	}
	/**
	 * Escape HTML characters
	 * @param input HTML to be escaped
	 * @return Escaped HTML
	 */
    public static String escapeHTML(String input)
    {
    	String ret = input;
		ret = ret.replace("&", "&amp;");
		ret = ret.replace("\"", "&quot;");
		ret = ret.replace("<", "&lt;");
		ret = ret.replace(">", "&gt;");
    	return ret;
    }
    /**
     * Remove escape characters of HTML
     * @param input HTML to be clean up
     * @return Clean HTML
     */
    public static String deescapeHTML(String input)
    {
    	String ret = input;
 		ret = ret.replace("&lt;", "<");
		ret = ret.replace("&gt;", ">");
		ret = ret.replace("&quot;", "\"");
		ret = ret.replace("&amp;", "&");
     	return ret;
    }
    
    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
    
    public static String prettyFormat(String input, int indent) throws TransformerException 
    {
    	String output = "";
        Source xmlInput = new StreamSource(new StringReader(input));
        StringWriter stringWriter = new StringWriter();
        StreamResult xmlOutput = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", indent);
        Transformer transformer = transformerFactory.newTransformer(); 
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(xmlInput, xmlOutput);
        output = xmlOutput.getWriter().toString();
        output = output.replaceAll("\\?\\>\\<", "?>\r\n<");
        return output;
    }

    
	public static String jsonToXML(JSONObject jsonObject) throws JSONException
	{
		String xml = "";
		xml = XML.toString(jsonObject);
		return xml;
	}
	public static JSONObject xmlToJSON(String xml) throws JSONException
	{
		return XML.toJSONObject(xml);
	}
	/**
	 * Get actual message length received from member
	 * @param message Raw message
	 * @param headerLength Header length
	 * @param headerDirectionRequest Header direction. true = LSB left MSB right, false = MSB left LSB right
	 * @return Actual message length
	 */
    public static long getLength(byte[] message, int headerLength, boolean headerDirectionRequest)
    {
      	int i;
    	long result = 0;
    	int x = 0;
    	if(headerDirectionRequest)
    	{
    	   	// Little endian
    		for(i = headerLength-1; i >= 0; i--)
	    	{
	    		result *= 256;
	    		x = (byte) message[i];
	    		if(x < 0)
	    		{
	    			x = x+256;
	    		}
	    		if(x > 256)
	    		{
	    			x = x-256;
	    		}
	    		result += x;
	    	}  		
    	}
    	else
    	{
    		// Big endian
	    	for(i = 0; i < headerLength; i++)
	    	{
	    		result *= 256;
	    		x = (byte) message[i];
	    		if(x < 0)
	    		{
	    			x = x+256;
	    		}
	    		if(x > 256)
	    		{
	    			x = x-256;
	    		}
	    		result += x;
	    	}
    	}
    	return result;
    }
    /**
	 * Get current time with MMddHHmmss format
	 * @return Current time with MMddHHmmss format
	 */
	public static String date10()
	{
		return now("MMddHHmmss");
	}
	public static String date10(String timezone)
	{
		return now("MMddHHmmss", timezone);
	}
	/**
	 * Get current time with MMdd format
	 * @return Current time with MMdd format
	 */
	public static String date4()
	{
		return now("MMdd");
	}
	/**
	 * Get current time with HHmmss format
	 * @return Current time with HHmmss format
	 */
	public static String time6()
	{
		return now("HHmmss");
	}
	/**
	 * Get current time with HHmm format
	 * @return Current time with HHmm format
	 */
	public static String time4()
	{
		return now("HHmm");
	}
	/**
	 * Convert Date10 to MySQL date format (MMddHHmmss to yyyy-MM-dd HH:mm:ss) 
	 * @param datetime Date10
	 * @return MySQL date format
	 */
	public static String date10ToMySQLDate(String datetime)
	{
		return date10ToFullDate(datetime, "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * Convert Date10 to PgSQL date format (MMddHHmmss to yyyy-MM-dd HH:mm:ss.SSS) 
	 * @param datetime Date10
	 * @return PgSQL date format
	 */
	public static String date10ToPgSQLDate(String datetime)
	{
		return date10ToFullDate(datetime, "yyyy-MM-dd HH:mm:ss.SSS");
	}
	/**
	 * Convert Date10 to full date time
	 * @param datetime Date10
	 * @param format Expected format
	 * @return Full date time format
	 */
	public static String date10ToFullDate(String datetime, String format)
	{
		while(datetime.length() < 10)
		{
			datetime = "0"+datetime;
		}
		String yyyy = now("yyyy");
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String result = "";
	    try 
	    {
	    	// debug year transition
	    	if(datetime.length() > 4)
	    	{
	    		String month1 = datetime.substring(0, 4);
	    		if(month1.equals("1231"))
	    		{
		    		String month2 = now("MMdd");
		    		if(!month2.equals("0101"))
		    		{
		    			int YYYY = Integer.parseInt(yyyy) - 1;
		    			yyyy = YYYY+"";
		    		}
	    		}
	    	}
			Date dateObject = dateFormat.parse(yyyy+datetime);
			dateFormat = new SimpleDateFormat(format);
			result = dateFormat.format(dateObject);
		} 
	    catch (ParseException e) 
	    {
	    	if(Config.isPrintStackTrace())
	    	{
	    		e.printStackTrace();
	    	}
			result = MySQLDate();
		}
	    return result;
	}
	/**
	 * Get MySQL format of current time
	 * @return Current time with MySQL format
	 */
	public static String MySQLDate()
	{
		return now("yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * Get PgSQL format of current time
	 * @return Current time with PgSQL format
	 */
	public static String PgSQLDate()
	{
		return now("yyyy-MM-dd HH:mm:ss.SSS");
	}

	/**
	 * Date time
	 * @param format Date time format
	 * @return String contains current date time
	 */
	public static String date(String format) throws NullPointerException, IllegalArgumentException
	{
		String result = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date dateObject = new Date();
		result = dateFormat.format(dateObject);
		return result;
	}
	/**
	 * Date time
	 * @param format Date time format
	 * @param date Date time
	 * @return String contains current date time
	 */
	public static String date(String format, Date date) throws NullPointerException, IllegalArgumentException
	{
		String result = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		result = dateFormat.format(date);
		return result;
	}
	/**
	 * Date time
	 * @param format Date time format
	 * @param time Unix Timestamp
	 * @return String contains current date time
	 */
	public static String date(String format, long time) throws NullPointerException, IllegalArgumentException
	{
		String result = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date dateObject = new Date(time);
		result = dateFormat.format(dateObject);
		return result;
	}
	/**
	 * Date yesterday
	 * @return Date yesterday
	 */
	public static Date yesterday() 
	{
	    final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
	    return cal.getTime();
	}
	/**
	 * Date tomorrow
	 * @return Date tomorrow
	 */
	public static Date tomorrow()
	{
	    final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, +1);
	    return cal.getTime();		
	}
	/**
	 * Get random integer in a range
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return Random integer
	 */
	public static int random(int min, int max)
	{
		if(min > max)
		{
			int tmp = max;
			max = min;
			min = tmp;
		}
		java.util.Random rand = new java.util.Random();
	    return rand.nextInt((max - min) + 1) + min;
	}
    /**
     * Concate 3 byte array
     * @param firstByte First bytes
     * @param secondByte Second bytes
     * @param thirdByte Third bytes
     * @param fourthByte Fourth byte
     * @return Concatenated bytes
     * @throws IOException IO exception
     */
	public static byte[] byteConcate(byte[] firstByte, byte[] secondByte, byte[] thirdByte, byte[] fourthByte) throws IOException
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(firstByte);
		outputStream.write(secondByte);
		outputStream.write(thirdByte);
		outputStream.write(fourthByte);
		return outputStream.toByteArray();
	}
	/**
	 * Generate SHA-256 hash code from a string
	 * @param input Input string
	 * @return SHA-256 hash code
	 * @throws NoSuchAlgorithmException if algorithm not found
	 */
	public static String sha256(String input) throws NoSuchAlgorithmException, NullPointerException
	{
		String output = "";
		if(input == null)
		{
			input = "";
		}
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
		output = Utility.bytesToHex(encodedhash);
		return output;
	}
	/**
	 * Generate SHA-1 hash code from a string
	 * @param input Input string
	 * @return SHA-1 hash code
	 * @throws NoSuchAlgorithmException if algorithm not found
	 */
	public static String sha1(String input) throws NoSuchAlgorithmException, NullPointerException
	{
		String output = "";
		if(input == null)
		{
			input = "";
		}
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
		output = Utility.bytesToHex(encodedhash);
		return output;
	}
	/**
	 * Generate MD5 hash code from a string
	 * @param input Input string
	 * @return MD5 hash code
	 * @throws NoSuchAlgorithmException if algorithm not found
	 */
	public static String md5(String input) throws NoSuchAlgorithmException, NullPointerException
	{
		String output = "";
		if(input == null)
		{
			input = "";
		}
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
		output = Utility.bytesToHex(encodedhash);
		return output;
	}
	/**
	 * Convert byte to hexadecimal number
	 * @param hash Byte to be converted
	 * @return String containing hexadecimal number
	 */
	public static String bytesToHex(byte[] hash) throws NullPointerException
	{
		if(hash == null)
		{
			throw new NullPointerException("Input is null");
		}
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) 
	    {
		    String hex = Integer.toHexString(0xff & hash[i]);
		    if(hex.length() == 1) 
		    {
		    	hexString.append('0');
		    }
	    	hexString.append(hex);
	    }
	    return hexString.toString();
	}
	public static String now() throws NullPointerException, IllegalArgumentException
	{
		String result = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date dateObject = new Date();
	    result = dateFormat.format(dateObject);
		return result;
	}
	/**
	 * Get current time with specified format
	 * @param precission Decimal precission
	 * @return Current time with format yyyy-MM-dd
	 */
	public static String now(int precission) throws NullPointerException, IllegalArgumentException
	{
		if(precission > 6)
		{
			precission = 6;
		}
		if(precission < 0)
		{
			precission = 0;
		}
		long decimal = 0;
		long nanoSecond = System.nanoTime();
		if(precission == 6)
		{
			decimal = nanoSecond % 1000000;
		}
		else if(precission == 5)
		{
			decimal = nanoSecond % 100000;
		}
		else if(precission == 4)
		{
			decimal = nanoSecond % 10000;
		}
		else if(precission == 3)
		{
			decimal = nanoSecond % 1000;
		}
		else if(precission == 2)
		{
			decimal = nanoSecond % 100;
		}
		else if(precission == 1)
		{
			decimal = nanoSecond % 10;
		}
		String result = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date dateObject = new Date();
	    result = dateFormat.format(dateObject)+"."+decimal;
		return result;
	}
	public static String now(int precission, String timezone) throws NullPointerException, IllegalArgumentException
	{
		if(precission > 6)
		{
			precission = 6;
		}
		if(precission < 0)
		{
			precission = 0;
		}
		long decimal = 0;
		long nanoSecond = System.nanoTime();
		if(precission == 6)
		{
			decimal = nanoSecond % 1000000;
		}
		else if(precission == 5)
		{
			decimal = nanoSecond % 100000;
		}
		else if(precission == 4)
		{
			decimal = nanoSecond % 10000;
		}
		else if(precission == 3)
		{
			decimal = nanoSecond % 1000;
		}
		else if(precission == 2)
		{
			decimal = nanoSecond % 100;
		}
		else if(precission == 1)
		{
			decimal = nanoSecond % 10;
		}
		String result = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
	    Date dateObject = new Date();
	    result = dateFormat.format(dateObject)+"."+decimal;
		return result;
	}
	public static String now3() throws NullPointerException, IllegalArgumentException
	{
		String result = "";
		long miliSecond = System.nanoTime() % 1000;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date dateObject = new Date();
	    result = dateFormat.format(dateObject)+"."+miliSecond;
		return result;
	}
	public static String now6() throws NullPointerException, IllegalArgumentException
	{
		String result = "";
		long microSecond = System.nanoTime() % 1000000;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date dateObject = new Date();
	    result = dateFormat.format(dateObject)+"."+microSecond;
		return result;
	}
	/**
	 * Get current time with specified format
	 * @param format Time format
	 * @return Current time with specified format
	 */
	public static String now(String format) throws NullPointerException, IllegalArgumentException
	{
		String result = "";
		DateFormat dateFormat = new SimpleDateFormat(format);
	    Date dateObject = new Date();
	    result = dateFormat.format(dateObject);
		return result;
	}
	public static String now(String format, String timezone) throws NullPointerException, IllegalArgumentException
	{
		String result = "";
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
	    Date dateObject = new Date();
	    result = dateFormat.format(dateObject);
		return result;
	}
	public static long unixTime()
	{
		return System.currentTimeMillis() / 1000L;
	}
	/**
	 * Convert array byte to string contains hexadecimal number
	 * @param b array byte
	 * @return String contains hexadecimal number
	 */
	public static String byteArrayToHexString(byte[] b) 
	{
		String result = "";
		for (int i=0; i < b.length; i++) 
		{
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	/**
	 * Print text to screen on debug mode
	 * @param input Text to be printed
	 */
	public static void print(String input)
	{
		if(Config.isPrintStackTrace())
		{
			System.out.print(input);
		}
	}
	/**
	 * Print text to screen on debug mode. It will add new line on the end of text 
	 * @param input Text to be printed
	 */
	public static void println(String input)
	{
		if(Config.isPrintStackTrace())
		{
			System.out.println(input+"\r");
		}
	}
	
	public static String number36Encode(long input)
	{
		String key = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String chr = "";
		int val = 0;
		long inp = input;
		String ret = "";
		while(inp > 0)
		{
			val = (int) (inp) % 36;
			chr = key.substring(val, val+1);
			inp = inp/36;
			ret = chr+ret;
		}
		if(ret.substring(0, 1).equals("0"))
		{
			ret = ret.substring(1);
		}
		return ret;
	}
	public static String number36Encode(BigInteger input)
	{
		String key = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String chr = "";
		int val;
		BigInteger inp = input;
		String ret = "";
		BigInteger div = BigInteger.valueOf(36);
		while(inp.longValue() > 0)
		{
			val = inp.mod(div).intValue();
			chr = key.substring(val, val+1);
			inp = inp.divide(div);
			ret = chr+ret;
		}
		if(ret.substring(0, 1).equals("0"))
		{
			ret = ret.substring(1);
		}
		return ret;
	}
	public static long number36Decode(String input)
	{
		String key = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String inp = input;
		String chr = "";
		long val = 0;
		long ret = 0;
		int exponent = 0;
		while(inp.length() > 0)
		{
			chr = inp.substring(inp.length() - 1);
			val = key.indexOf(chr);
			ret += val * Math.pow(36, exponent);
			exponent++;
			inp = inp.substring(0, inp.length() - 1);
		}
		return ret;
	}	
	public static BigInteger number36DecodeBigint(String input)
	{
		String key = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String inp = input;
		String chr = "";
		BigInteger val;
		BigInteger ret = BigInteger.valueOf(0);
		BigInteger div = BigInteger.valueOf(36);
		int exponent = 0;
		while(inp.length() > 0)
		{
			chr = inp.substring(inp.length() - 1);
			val = BigInteger.valueOf(key.indexOf(chr));
			ret = ret.add(val.multiply(div.pow(exponent)));
			exponent++;
			inp = inp.substring(0, inp.length() - 1);
		}
		return ret;
	}
	/**
	 * Encode string with base 64 encoding
	 * @param input String to be encoded
	 * @return Encoded string
	 */
	public static String base64Encode(String input)
	{
		byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes());
		return new String(encodedBytes);
	}
	/**
	 * Decode string with base 64 encoding
	 * @param input String to be decoded
	 * @return Decoded string
	 */
	public static String base64Decode(String input)
	{
		byte[] decodedBytes = Base64.getDecoder().decode(input.getBytes());
		return new String(decodedBytes);
	}
	
}
