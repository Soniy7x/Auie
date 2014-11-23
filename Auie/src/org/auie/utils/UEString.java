package org.auie.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
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
public final class UEString{

	private UEString(){}
	
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
			ps = transformHex(md.digest());
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
	public static String transformHex(byte[] bts) {
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

	public static boolean isBankCardCode(String str){
		if (str == null || str.trim().length() == 0 || !str.matches("\\d+")) {
			return false;
		}
		char[] chs = str.trim().substring(0, str.length() - 1).toCharArray();
        int sum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
        	int k = chs[i] - '0';
        	if (j % 2 == 0) {
        		k *= 2;
        		k = k / 10 + k % 10;
        	}
        	sum += k;
        }
        return str.charAt(str.length() - 1) == ((sum % 10 == 0) ? '0' : (char) ((10 - sum % 10) + '0'));
	}
	
	public static boolean isChinaIDCardCode(String str){
		if (str == null || str.trim().length() != 18 || !isNumber(str.substring(0, 17))) {
			return false;
		}
		if (!isDateOfBirth(str.substring(6, 14))) {
			return false;
		}
		int sum = Integer.parseInt(str.substring(0, 1)) * 7 + Integer.parseInt(str.substring(1, 2)) * 9 
				+ Integer.parseInt(str.substring(2, 3)) * 10 + Integer.parseInt(str.substring(3, 4)) * 5
				+ Integer.parseInt(str.substring(4, 5)) * 8 + Integer.parseInt(str.substring(5, 6)) * 4
				+ Integer.parseInt(str.substring(6, 7)) * 2 + Integer.parseInt(str.substring(7, 8)) * 1
				+ Integer.parseInt(str.substring(8, 9)) * 6 + Integer.parseInt(str.substring(9, 10)) * 3
				+ Integer.parseInt(str.substring(10, 11)) * 7 + Integer.parseInt(str.substring(11, 12)) * 9
				+ Integer.parseInt(str.substring(13, 14)) * 5 + Integer.parseInt(str.substring(14, 15)) * 8
				+ Integer.parseInt(str.substring(15, 16)) * 4 + Integer.parseInt(str.substring(16, 17)) * 2;
		int over = sum % 11;
		String bit;
		if (over == 2) {
			bit = str.substring(17).toUpperCase(Locale.CHINESE);
		}else {
			bit = str.substring(17);
		}
		switch (over) {
		case 0:
			if (!bit.equals("1")) {
				return false;
			}
			break;
		case 1:
			if (!bit.equals("0")) {
				return false;
			}
			break;
		case 2:
			if (!bit.equals("X")) {
				return false;
			}
			break;
		case 3:
			if (!bit.equals("9")) {
				return false;
			}
			break;
		case 4:
			if (!bit.equals("8")) {
				return false;
			}
			break;
		case 5:
			if (!bit.equals("7")) {
				return false;
			}
			break;
		case 6:
			if (!bit.equals("6")) {
				return false;
			}
			break;
		case 8:
			if (!bit.equals("4")) {
				return false;
			}
			break;
		case 9:
			if (!bit.equals("3")) {
				return false;
			}
			break;
		case 10:
			if (!bit.equals("2")) {
				return false;
			}
			break;
		}
		return true;
	}
	
	public static boolean isDateOfBirth(String date){
		if (date == null || date.trim().length() != 8 || !isNumber(date)) {
			return false;
		}
		date = date.trim();
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4, 6));
		int day = Integer.parseInt(date.substring(6));
		if (year > 2200 || year < 1900 || month > 12 || month < 1 || day < 1) {
			return false;
		}
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
			if (day > 31) {
				return false;
			}
		}else if (month == 2) {
			if (year % 4 == 0) {
				if (day > 29) {
					return false;
				}
			}else {
				if (day > 28) {
					return false;
				}
			}
		}else {
			if (day > 30) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 验证字符串是否为数字(Verification String is or not number)
	 * @param str 待验证的字符串(Require Verification String)
	 * @return boolean true/false yes/no
	 */
	public static boolean isNumber(String str) {
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
