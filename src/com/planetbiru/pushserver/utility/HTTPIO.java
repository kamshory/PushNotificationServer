package com.planetbiru.pushserver.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * HTTPIO is class to read and write data over socket with HTTP protocol
 * @author Kamshory, MT
 *
 */
public class HTTPIO 
{
	/**
	 * Default constructor
	 */
	private HTTPIO()
	{
		
	}
	/**
	 * Parse query string
	 * @param query Query string
	 * @return Map contains parsed query
	 * @throws QueryParserException if any errors while parsing
	 */
	public static Map<String, String> parseQuery(String query) throws QueryParserException
    {
		Map<String, String> data = new HashMap<>(); 	
    	if(query == null)
    	{
    		query = "";
    	}
        	
		if(query.contains("="))
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
    				data.put(key, value);
    			}
    		}
		}
		else
		{
			throw new QueryParserException("Query is not contains \"=\"");
		}
	
    	
    	return data;
    }
	/**
	 * Get HTTP request body
	 * @param httpExchange HTTP exchange
	 * @return Request body
	 * @throws IOException if any IO errors
	 */
	public static String getHTTPRequest(HttpExchange httpExchange) throws IOException
	{
		StringBuilder body = new StringBuilder();
		if(httpExchange.getRequestMethod().equalsIgnoreCase("POST") || httpExchange.getRequestMethod().equalsIgnoreCase("PUT")) 
        {
            Headers requestHeaders = httpExchange.getRequestHeaders();
            int contentLength = Integer.parseInt(requestHeaders.getFirst("Content-length"));
            int j;
            for(j = 0; j < contentLength; j++)
            {
            	byte b = (byte) httpExchange.getRequestBody().read();
            	body.append(String.format("%c", b));
            }
        }
        else if(httpExchange.getRequestMethod().equalsIgnoreCase("GET")) 
        {
            String queryString = httpExchange.getRequestURI().getQuery();
            JSONObject getJSON =  new JSONObject(Utility.parseQuery(queryString));
            body.append(getJSON.optString("data", ""));
        }
		return body.toString();
	}
	/**
	 * Get HTTP request body
	 * @param httpExchange HTTP exchange
	 * @return Request body
	 * @throws IOException if any IO errors
	 */
	public static String getHTTPRequestBody(HttpExchange httpExchange) throws IOException
	{
		String body = "";
		InputStream is = httpExchange.getRequestBody();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		int len;
		while ((len = is.read(buffer))>0) 
		{
			bos.write(buffer, 0, len);
		}
		bos.close();
		body = new String(bos.toByteArray(), StandardCharsets.UTF_8);
		return body;
	}
	/**
	 * Send HTTP response without body to client
	 * @param httpExchange HTTP exchange
	 * @param responseHeaders Response headers
	 * @param responseCode Response code
	 * @throws IOException IO exception
	 */
	public static void sendHTTPResponse(HttpExchange httpExchange, Headers responseHeaders, int responseCode) throws IOException
    {
		int size = 0;
        int i = 0;
        String key = "";
        String value = "";    
        for (Map.Entry<String, List<String>> hdr : responseHeaders.entrySet()) 
        {
        	size = hdr.getValue().size();
        	for(i = 0; i < size; i++)
        	{
        		key = hdr.getKey();
        		value = hdr.getValue().get(i);
        		httpExchange.getResponseHeaders().add(key, value);
        	}
        }
		httpExchange.sendResponseHeaders(responseCode, 0);
    }
	/**
	 * Send HTTP response with body to client
	 * @param httpExchange HTTP exchange
	 * @param responseCode Response code
	 * @param responseHeaders Response headers
	 * @param response Response body to the client
	 * @throws IOException if any IO errors
	 */
	public static void sendHTTPResponse(HttpExchange httpExchange, Headers responseHeaders, int responseCode, String response) throws IOException
    {
		int size = 0;
        int i = 0;
        String key = "";
        String value = "";    
        for (Map.Entry<String, List<String>> hdr : responseHeaders.entrySet()) 
        {
        	size = hdr.getValue().size();
        	for(i = 0; i < size; i++)
        	{
        		key = hdr.getKey();
        		value = hdr.getValue().get(i);
        		httpExchange.getResponseHeaders().add(key, value);
        	}
        }	
 		httpExchange.sendResponseHeaders(responseCode, response.length());
		httpExchange.getResponseBody().write(response.getBytes());
    }
}
