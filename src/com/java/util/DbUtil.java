package com.java.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbUtil {
	
	private String url="jdbc:mysql://localhost:3306/db_blog?useSSL=false";
	private String driver="com.mysql.jdbc.Driver";
	private String userName="root";
	private String userPassword="123456";
	
	public Connection getCon() throws Exception {
		Class.forName(driver);
		Connection con = DriverManager.getConnection(url, userName, userPassword);
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
