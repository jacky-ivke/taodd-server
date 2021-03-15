package com.esports.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ShaUtil {

	
	public static String sha1Base64(String value) {
		try {
			byte[] b = getSha1(value);
			return Base64Utils.encode(b);
		} catch (Exception e) {
		}
		return "";
	}
	
	public static byte[] getSha1(String str) throws UnsupportedEncodingException {
		byte[] rtv = null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-1 is not supported", e);
		}
		synchronized (md) {
			md.reset(); 
			md.update(str.getBytes(StandardCharsets.UTF_8));
			rtv = md.digest();
		}
		return rtv;
    }
}
