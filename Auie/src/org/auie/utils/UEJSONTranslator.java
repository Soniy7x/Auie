package org.auie.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.auie.utils.UEException.UEJSONTranslateException;

public class UEJSONTranslator {

	public static String translateListToJsonForKey(List<?> list, String key) throws UEJSONTranslateException{
		return "{\"" + key + "\":" + translateListToJson(list) + "}";
	}
	
	public static String translateListToJson(List<?> list) throws UEJSONTranslateException{
		int size = list.size();
		if (size < 1) {
			return "{}";
		}
		String type = list.get(0).getClass().toString();
		StringBuffer jsonString = new StringBuffer("{");
		if (type.equals("class java.lang.String")) {
			for (int i = 0; i < size; i++) {
				if (!jsonString.equals("{")) {
					jsonString.append(",");
				}
				jsonString.append("\"" + list.get(i) + "\"");
			}
		}else if (type.equals("class java.lang.Integer") || type.equals("class java.lang.Boolean") ||
				type.equals("class java.lang.Float") ||  type.equals("class java.lang.Double") || 
				type.equals("class java.lang.Short") || type.equals("class java.lang.Long")){
			jsonString = new StringBuffer(list.toString().replace("[", "{").replace("]", "}"));
			return jsonString.toString();
		}else {
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					jsonString.append(",");
				}
				jsonString.append(translateObjectToJson(list.get(i)));
			}
		}
		jsonString.append("}");
		return jsonString.toString();
	}
	
	public static String translateObjectToJson(Object object) throws UEJSONTranslateException{
		try{
			StringBuffer jsonString = new StringBuffer("{");
			Class<?> clazz = object.getClass();
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				String name = field.getName();
				if (name.equals("this$0")) {
					continue;
				}
				if (jsonString.length() > 1) {
					jsonString.append(",");
				}
				String type = field.getGenericType().toString();
				System.out.println(type);
				String methodName = "get" + name.substring(0, 1).toUpperCase(Locale.getDefault()) + name.substring(1);
				jsonString.append("\"" + name + "\":");
				if (type.equals("class java.lang.String")) {
					Method method = clazz.getMethod(methodName);
					String value = (String) method.invoke(object);
					if (value == null) {
						jsonString.append("null");
					}else {
						jsonString.append("\"" + value + "\"");
					}
				}else if (type.equals("boolean")){
					Method method = clazz.getMethod(methodName.replace("get", "is"));
					if (method == null) {
						method = clazz.getMethod(methodName);
					}
					boolean value = (boolean) method.invoke(object);
					jsonString.append(value);
				}else if (type.equals("int")){
					Method method = clazz.getMethod(methodName);
					int value = (int) method.invoke(object);
					jsonString.append(value);
				}else if (type.equals("short")){
					Method method = clazz.getMethod(methodName);
					short value = (short) method.invoke(object);
					jsonString.append(value);
				}else if (type.equals("long")){
					Method method = clazz.getMethod(methodName);
					long value = (long) method.invoke(object);
					jsonString.append(value);
				}else if (type.equals("double")){
					Method method = clazz.getMethod(methodName);
					double value = (double) method.invoke(object);
					jsonString.append(value);
				}else if (type.equals("float")){
					Method method = clazz.getMethod(methodName);
					float value = (float) method.invoke(object);
					jsonString.append(value);
				}else if (type.equals("class java.util.ArrayList")) {
					Method method = clazz.getMethod(methodName.replace("get", "is"));
					if (method == null) {
						method = clazz.getMethod(methodName);
					}
					ArrayList<?> value = (ArrayList<?>) method.invoke(object);
					if (value == null) {
						jsonString.append("null");
					}else {
						jsonString.append(translateListToJson(value));
					}
				}else if (type.equals("class java.lang.Boolean")){
					Method method = clazz.getMethod(methodName.replace("get", "is"));
					if (method == null) {
						method = clazz.getMethod(methodName);
					}
					Boolean value = (Boolean) method.invoke(object);
					if (value == null) {
						jsonString.append("null");
					}else {
						jsonString.append(value);
					}
				}else if (type.equals("class java.lang.Integer")){
					Method method = clazz.getMethod(methodName);
					Integer value = (Integer) method.invoke(object);
					if (value == null) {
						jsonString.append("null");
					}else {
						jsonString.append(value);
					}
				}else if (type.equals("class java.lang.Short")){
					Method method = clazz.getMethod(methodName);
					Short value = (Short) method.invoke(object);
					if (value == null) {
						jsonString.append("null");
					}else {
						jsonString.append(value);
					}
				}else if (type.equals("class java.lang.Long")){
					Method method = clazz.getMethod(methodName);
					Long value = (Long) method.invoke(object);
					if (value == null) {
						jsonString.append("null");
					}else {
						jsonString.append(value);
					}
				}else if (type.equals("class java.lang.Double")){
					Method method = clazz.getMethod(methodName);
					Double value = (Double) method.invoke(object);
					if (value == null) {
						jsonString.append("null");
					}else {
						jsonString.append(value);
					}
				}else if (type.equals("class java.lang.Float")){
					Method method = clazz.getMethod(methodName);
					Float value = (Float) method.invoke(object);
					if (value == null) {
						jsonString.append("null");
					}else {
						jsonString.append(value);
					}
				}else if (type.equals("class java.util.Date")){
					Method method = clazz.getMethod(methodName);
					Date value = (Date) method.invoke(object);
					if (value == null) {
						jsonString.append("null");
					}else {
						jsonString.append(value);
					}
				}else {
					Method method = clazz.getMethod(methodName);
					Object value = (Object) method.invoke(object);
					if (value == null) {
						jsonString.append("null");
					}else {
						jsonString.append(UEJSONTranslator.translateObjectToJson(value));
					}
				}
			}
			jsonString.append("}");
			System.out.println(jsonString);
			return jsonString.toString();
			
		}catch(Exception e){
			throw new UEJSONTranslateException(e);
		}
	}
	
}
