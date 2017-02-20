package com.bossy.util;

public class Utils {
	
	//字节数组转换成十六进制,以空格分隔
    public static String BytesHexString(byte[] b) { 
	   StringBuffer ret = new StringBuffer(""); 
	   for (int i = 0; i < b.length; i++) { 
	     String hex = Integer.toHexString(b[i] & 0xFF); 
	     if (hex.length() == 1) {
	       hex = '0' + hex; 
	     }
	     if(i>0){
	    	 ret.append(" ");
	     }
	     ret.append(hex.toUpperCase()); 
	   } 
	   return ret.toString(); 
	}
}
