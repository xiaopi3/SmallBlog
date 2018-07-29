package com.java.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	
	public static String getValue(String key) {
		Properties pro=new Properties();
		InputStream in=new PropertiesUtil().getClass().getResourceAsStream("/blog.properties");
		try {
			pro.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pro.getProperty(key);
	}
}
