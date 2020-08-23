package com.planetbiru.pushserver.utility;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class SocketIO 
{
	/**
	 * Client socket
	 */
	private Socket socket;
	/**
	 * Header
	 */
	private String header = "";
	/**
	 * Body
	 */
	private String body = "";
	/**
	 * Content length
	 */
	private long contentLength = 0;
	/**
	 * Response header
	 */
	private Map<String, String> responseHeader = new HashMap<String, String>();
	/**
	 * Request header
	 */
	private Map<String, String> requestHeader = new HashMap<>();
	/**
	 * Raw request header
	 */
	private String rawRequestHeader = "";
	
	
	
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public long getContentLength() {
		return contentLength;
	}
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	public String getRawRequestHeader() {
		return rawRequestHeader;
	}
	public void setRawRequestHeader(String rawRequestHeader) {
		this.rawRequestHeader = rawRequestHeader;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public void setResponseHeader(Map<String, String> responseHeader) {
		this.responseHeader = responseHeader;
	}
	public void setRequestHeader(Map<String, String> requestHeader) {
		this.requestHeader = requestHeader;
	}
	/**
	 * Default constructor
	 */
	public SocketIO()
	{
		
	}
	/**
	 * Constructor with initialization
	 * @param socket Client socket
	 */
	public SocketIO(Socket socket)
	{
		this.socket = socket;
	}
	/**
	 * Reset request header
	 */
	public void resetResponseHeader()
	{
		this.responseHeader = new HashMap<String, String>();
	}
	/**
	 * Add request header
	 * @param key Key
	 * @param value Value
	 */
	public void addResponseHeader(String key, String value)
	{
		this.responseHeader.put(key, value);
	}
	/**
	 * Get response header
	 * @return Response header
	 */
	public Map<String, String> getResponseHeader()
	{
		return this.responseHeader;
	}
	/**
	 * Get response header value
	 * @param key Key
	 * @return Response header value
	 */
	public String getResponseHeader(String key)
	{
		return this.responseHeader.getOrDefault(key, "");
	}
	/**
	 * Get response header value with default value
	 * @param key Key
	 * @param defaultValue Default value
	 * @return Response header value
	 */
	public String getResponseHeader(String key, String defaultValue)
	{
		return this.responseHeader.getOrDefault(key, defaultValue);
	}
	/**
	 * Reset request header
	 */
	public void resetRequestHeader()
	{
		this.requestHeader = new HashMap<String, String>();
	}
	/**
	 * Add request header
	 * @param key Key
	 * @param value Value
	 */
	public void addRequestHeader(String key, int value)
	{
		this.addRequestHeader(key, value+"");
	}
	/**
	 * Add request header
	 * @param key Key
	 * @param value Value
	 */
	public void addRequestHeader(String key, String value)
	{
		this.requestHeader.put(key, value);
	}
	/**
	 * Get request header
	 * @return Request header
	 */
	public Map<String, String> getRequestHeader()
	{
		return this.requestHeader;
	}
	/**
	 * Get request header value
	 * @param key Key
	 * @return Request header value
	 */
	public String getRequestHeader(String key)
	{
		return this.requestHeader.getOrDefault(key, "");
	}
	/**
	 * Get request header value with default value
	 * @param key Key
	 * @param defaultValue Default value
	 * @return Request header value
	 */
	public String getRequestHeader(String key, String defaultValue)
	{
		return this.requestHeader.getOrDefault(key, defaultValue);
	}
	/**
	 * Get last substring
	 * @param data Data
	 * @param length length
	 * @return Last substring
	 */
	public String last(String data, int length)
	{
		if(data.length() < length)
		{
			return "";
		}
		byte[] b = data.getBytes();
		int dataLength = b.length;
		int offset = dataLength - length;
		String result = "";
		int i;
		for(i = offset; i<dataLength; i++ )
		{
			result += String.format("%c", b[i]);
		}
		return result;
	}
	/**
	 * Read response header
	 * @return Response header
	 * @throws IOException if any IO errors
	 */
	public String readResponseHeader() throws IOException
	{
		int buf;
		String data = "";
		do
		{
			buf = this.socket.getInputStream().read();
			if(buf >= 0)
			{
				data += String.format("%c", buf);
			}
		}
		while(!this.last(data, 4).equals("\r\n\r\n") && buf > -1);
		data = data.trim();
		this.rawRequestHeader = data;
		this.header = data;
		return data;
	}
	/**
	 * Get first value of headers
	 * @param headers Headers
	 * @param key Key
	 * @return Value
	 */
	public String getFirst(String[] headers, String key)
	{
		return Utility.getFirst(headers, key);
	}
	/**
	 * Read request header
	 * @return Request header
	 * @throws IOException if any IO errors
	 */
	public Map<String, String> readRequestHeader() throws IOException
	{
		String data = this.readResponseHeader();
	    String[] lines = data.split("\\r?\\n"); // split on new lines
	    Map<String, String> header = new HashMap<String, String>();
	    int i;
	    String line = "";
	    String[] arr;
	    for(i = 0; i < lines.length; i++)
	    {
	    	line = lines[i].trim();
	    	if(line.contains(":"))
	    	{
	    		arr = line.split("\\:", 2);
	    		header.put(arr[0].trim(), arr[1].trim());
	    	}
	    }
	    this.requestHeader = header;
		return header;
	}
	/**
	 * Get request data length
	 * @return Request data length
	 */
	public long getRequestDataLength()
	{
		String header = this.rawRequestHeader;
		long length = 0;
		if(header.length() > 1)
		{
			String[] lines = header.split("\\r\\n");
			int i;
			String line;
			String x;
			for(i = 0; i<lines.length; i++)
			{
				line = lines[i];
				if(line.toLowerCase().contains("content-length") && line.toLowerCase().contains(":"))
				{
					x = line.trim();
					x = x.toLowerCase();
					x = x.replace("content-length", "").replace(":", "").trim();
					length = Long.parseLong(x.replaceAll("\\\r", "").replaceAll("\\\n", "").trim());
				}
			}
			this.contentLength = length;
		}
		else
		{
			length = -1;
		}
		return length;
	}
	/**
	 * Get request body
	 * @param length Data length
	 * @return Request body
	 * @throws IOException if any IO errors
	 */
	private String getBody(long length) throws IOException
	{
		int buf;
		long i;
		String data = "";
		if(length > 0)
		{
			for(i = 0; i < length; i++)
			{
				buf = this.socket.getInputStream().read();
				data += String.format("%c", buf);
			}
			this.contentLength = length;
		}
		else
		{
			this.contentLength = length;
		}
		return data;
	}
	/**
	 * Get headers
	 * @return Headers
	 */
	public String[] getHeaders()
	{
		String[] lines = this.header.split("\\r\\n");
		return lines;
	}
	/**
	 * Get request body
	 * @return Request body
	 */
	public String getBody()
	{
		return this.body;
	}
	/**
	 * Read socket
	 * @return true if success and false if failed
	 * @throws IOException if any IO errors
	 */
	public boolean read() throws IOException
	{
		return this.read(this.socket);
	}
	/**
	 * Read socket
	 * @param socket Client socket
	 * @return true if success and false if failed
	 * @throws IOException if any IO errors
	 */
	public boolean read(Socket socket) throws IOException
	{
		this.socket = socket;
		this.readResponseHeader();
		long length = this.getRequestDataLength();
		if(length > -1)
		{
			this.body = this.getBody(length);
		}
		else
		{
			this.body = "";
			this.contentLength = 0;
			return false;
		}
		return true;
	}
	/**
	 * Create content length
	 * @param length Content length
	 * @return String contains content length
	 */
	public String createContentLength(long length)
	{
		return "Content-Length: "+length+"\r\n\r\n";
	}
	/**
	 * Write data to socket
	 * @param data Data
	 * @return true if success and false if failed
	 * @throws IOException if any IO errors
	 */
	public boolean write(String data) throws IOException, SocketException
	{
		return this.write(this.socket, data);
	}
	/**
	 * Write data to socket
	 * @param socket Client socket
	 * @param data Data
	 * @return true if success and false if failed
	 * @throws IOException if any IO errors
	 */
	public boolean write(Socket socket, String data) throws IOException, SocketException
	{
		if(socket.isConnected() && !socket.isClosed())
		{
			String hdr = "";
			this.addRequestHeader("Content-Length", data.length());
			String data2sent = "";
			if(!this.requestHeader.isEmpty())
			{
				 Set<Map.Entry<String, String>> entrySet = this.requestHeader.entrySet();
				 for (Entry<String, String> entry : entrySet) 
				 {
				    hdr = entry.getKey().toString().trim()+": "+entry.getValue().toString().trim()+"\r\n";
				    data2sent += hdr;
				 }
			}
			data2sent += "\r\n";
			data2sent += data;
			socket.getOutputStream().write(data2sent.getBytes());
			return true;
		}
		else
		{
			return false;
		}
	}
}
