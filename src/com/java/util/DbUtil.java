package com.java.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbUtil {
	
	public Connection getCon() throws Exception {
		Class.forName(PropertiesUtil.getValue("driver"));
		Connection con = DriverManager.getConnection(PropertiesUtil.getValue("url"), PropertiesUtil.getValue("userName"), PropertiesUtil.getValue("userPassword"));
		return con;
	}
	
	public void closeCon(Connection con) throws Exception{
		if(con!=null)
			con.close();
	}
	
	public static void main(String[] args) {
		DbUtil dbUtil=new DbUtil();
		try {
			dbUtil.getCon();
			System.out.println("ok");
		} catch (Exception e) {
			System.out.println("fail");
			e.printStackTrace();
		}
	}
}
