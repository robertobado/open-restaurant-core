package net.openrally.restaurant.core.util;

import java.io.UnsupportedEncodingException;

public class HashCalculator {
	private static final String MD5 = "MD5";
	private static final String UTF_8 = "UTF-8";

	public static String generateMD5Hash(String md5) {
		   try {
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance(MD5);
		        byte[] array = md.digest(md5.getBytes(UTF_8));
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    	e.printStackTrace();
		    } catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		    return null;
		}
}
