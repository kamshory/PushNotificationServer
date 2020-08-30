package com.planetbiru.pushserver.utility;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.planetbiru.pushserver.code.ConstantString;

/**
 * Encryption is class to encrypt and decrypt data.
 * @author Kamshory, MT
 *
 */
public class Encryption 
{
	/**
	 * Encryptor
	 */
    private Cipher ecipher;
    /**
     * Decryptor
     */
    private Cipher dcipher;
    /**
     * Constructor with key as String
     * @param key Key
     * @throws NoSuchPaddingException if padding is invalid
     * @throws NoSuchAlgorithmException if algorithm is not found
     * @throws InvalidKeyException if key is invalid
     */
    public Encryption(String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalArgumentException 
    {
    	 this.init(key);
    }
    /**
     * Constructor with key as SecretKey
     * @param key String key to encrypt or decrypt data
     * @throws NoSuchPaddingException if padding is invalid
     * @throws NoSuchAlgorithmException if algorithm is not found
     * @throws InvalidKeyException if keys is invalid
     */
    public Encryption(SecretKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalArgumentException  
    {
    	this.init(key);
    }
    /**
     * Init key
     * @param key SecretKey to encrypt or decrypt data
     * @return true if success and false if failed
     * @throws NoSuchPaddingException if padding is invalid
     * @throws NoSuchAlgorithmException if algorith is not found
     * @throws InvalidKeyException if key is invalid
     */
    public boolean init(SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        this.ecipher = Cipher.getInstance(ConstantString.AES_GCM_NO_PADDING);
        this.dcipher = Cipher.getInstance(ConstantString.AES_GCM_NO_PADDING);
        this.ecipher.init(Cipher.ENCRYPT_MODE, key);
        this.dcipher.init(Cipher.DECRYPT_MODE, key);
        return true;
    }
    /**
     * Init key
     * @param key Key
     * @return true if success and false if failed
     * @throws NoSuchPaddingException if padding is invalid
     * @throws NoSuchAlgorithmException if algorithm is not found
     * @throws InvalidKeyException if key is invalid
     */
    public boolean init(String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalArgumentException 
    {
    	StringBuilder bld = new StringBuilder();
    	while( key.length() < 16)
    	{
    		bld.append("&");
    		key = bld.toString();
    	}  	
    	byte[] bkey = key.getBytes();
    	bkey = Arrays.copyOf(bkey, 16); // use only first 128 bit
    	SecretKeySpec skey2 = new SecretKeySpec(bkey, ConstantString.AES);
        this.ecipher = Cipher.getInstance(ConstantString.AES_GCM_NO_PADDING);
        this.dcipher = Cipher.getInstance(ConstantString.AES_GCM_NO_PADDING);
        this.ecipher.init(Cipher.ENCRYPT_MODE, skey2);
        this.dcipher.init(Cipher.DECRYPT_MODE, skey2); 
        return true;   	
    }
    /**
     * Encrypt plain text into cipher text
     * @param input Plain text to be encrypted
     * @return String containing cipher text
     * @throws UnsupportedEncodingException if encoding is not supported
     * @throws BadPaddingException if padding is invalid
     * @throws IllegalBlockSizeException if block size is invalid
     */
    public String encrypt(byte[] input) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException 
    {
        // Encrypt
        byte[] enc = this.ecipher.doFinal(input);  
        // Encode bytes to base64 to get a string
        return new String(Base64.getEncoder().encode(enc));            
    }
   /**
     * Encrypt plain text into cipher text
     * @param input Plain text to be encrypted
     * @param encode Base 64 encode or not
     * @return String containing cipher text
     * @throws UnsupportedEncodingException if encoding is not supported
     * @throws BadPaddingException if padding is invalid
     * @throws IllegalBlockSizeException if block size is invalid
     */
    public String encrypt(String input, boolean encode) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException 
    {
        // Encode the string into bytes using utf-8
        byte[] utf8 = input.getBytes(StandardCharsets.UTF_8);  
        // Encrypt
        byte[] enc = this.ecipher.doFinal(utf8);  
        // Encode bytes to base64 to get a string
        if(encode)
        {
            return new String(Base64.getEncoder().encode(enc));                   	
        }
        else
        {
        	return new String(enc);
        }
    }
    /**
     * Decrypt cipher text into plain text
     * @param input Cipher text
     * @param decode Base 64 decode or not
     * @return Plain text
     * @throws BadPaddingException if padding is invalid
     * @throws IllegalBlockSizeException if block size is invalid
     * @throws UnsupportedEncodingException if encoding is not supported
     */
    public String decrypt(String input, boolean decode) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException 
    {
    	byte[] dec;
    	byte[] utf8;
    	if(decode)
    	{
	        // Decode base64 to get bytes
	        dec = Base64.getDecoder().decode(input); 
    	}
    	else
    	{
    		dec = input.getBytes();
    	}
        // Decrypt
        utf8 = this.dcipher.doFinal(dec);
        // Decode using utf-8
        return new String(utf8);
    }
    /**
     * Base64 encoding
     * @param input String to be encoded
     * @return Encoded string
     */
    public String base64Encode(String input) throws NullPointerException
    {
		byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes());
    	return new String(encodedBytes, StandardCharsets.UTF_8);
    }
    /**
     * Base64 decoding
     * @param input String to be decoded
     * @return Decoded string
     */
    public String base64Decode(String input) throws NullPointerException, IllegalArgumentException
    {
        byte[] decodedBytes = Base64.getDecoder().decode(input);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}