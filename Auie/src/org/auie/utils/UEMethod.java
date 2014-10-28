package org.auie.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

public class UEMethod {

	public static int dp2px(Context context, float dp){
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
	
	/**
	 * Encrypt String by SHA256.
	 * @param String: will be encrypted String.
	 * @return String: encrypted String.
	 */
	public static String EncryptSHA256(String str){
		String ps = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256"); // use SHA256
			md.update(str.getBytes());
			ps = bytes2Hex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			return e.toString();
		}
		return ps;
	}
	
	public static boolean isNumer(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	 } 
	
	/**
	 * Verification String is or not email.
	 * @param String: will be verificated String.
	 * @return boolean: is email; false: isn't email.
	 */
	public static boolean isEmail(String str){
		String pattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		if(m.matches()){
			return true; // is email address
		}else{
			return false;// isn't email address
		}
	}
	
	/**
	 * byte[] to HexString
	 * @param String: will be change byte[].
	 * @return String: changed String.
	 */
	private static String bytes2Hex(byte[] bts) {
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
}
