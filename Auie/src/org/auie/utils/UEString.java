package org.auie.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 字符串类(UEString)
 * 
 * 字符串相关拓展，提供验证与加密等
 * (String development, provides authentication and encryption)
 * 
 * @author Soniy7x
 *
 */
public final class UEString {

	/**
	 * SHA256加密方式(SHA256 Encryption)
	 * @param str 需要加密的字符串(Require encrypted String)
	 * @return String 已加密的字符串(Encrypted String)
	 */
	public static String encryptSHA256(String str) {
		String ps = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(str.getBytes());
			ps = transfromHex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			return e.toString();
		}
		return ps;
	}

	/**
	 * MD5加密方式(MD5 Encryption)
	 * @param str 需要加密的字符串(Require encrypted String)
	 * @return String 已加密的字符串(Encrypted String)
	 */
	public static String encryptMD5(String str) {     
        MessageDigest messageDigest = null;     
        try {     
            messageDigest = MessageDigest.getInstance("MD5");     
            messageDigest.reset();     
            messageDigest.update(str.getBytes("UTF-8"));     
        } catch (NoSuchAlgorithmException e) {   
            return null;  
        } catch (UnsupportedEncodingException e) {
            return null;  
        }     
     
        byte[] byteArray = messageDigest.digest();     
        StringBuffer md5StrBuff = new StringBuffer();     
        for (int i = 0; i < byteArray.length; i++) {                 
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)     
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));     
            else     
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));     
        }
        return md5StrBuff.toString();     
    } 
	
	/**
	 * 十进制字节转化为16进制字符串
	 * (Converted decimal byte[] to hexadecimal String)
	 * @param bts 需要转化的字节数组(An array of bytes needed transformation)
	 * @return String 转化后的字符串(Converted String)
	 */
	public static String transfromHex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	/**
	 * 验证字符串是否为数字(Verification String is or not number)
	 * @param str 待验证的字符串(Require Verification String)
	 * @return boolean true/false yes/no
	 */
	public static boolean isNumer(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
	
	/**
	 * 验证字符串是否为邮箱(Verification String is or not Email)
	 * @param str 待验证的字符串(Require Verification String)
	 * @return boolean true/false yes/no
	 */
	public static boolean isEmail(String str){
		String pattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		if(m.matches()){
			return true;
		}else{
			return false;
		}
	}
}
