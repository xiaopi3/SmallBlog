package com.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.java.model.User;

public class UserDao {
	
	public User login(Connection con,User user) throws Exception {
		User resultUser=null;
		String sql="select * from t_user where userName=? and password=?;";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, user.getName());
		pstmt.setString(2, user.getPassword());
		ResultSet r = pstmt.executeQuery();
		if(r.next()) {
			resultUser=new User();
			resultUser.setId(r.getInt("userId"));
			resultUser.setName(r.getString("userName"));
			resultUser.setPassword(r.getString("password"));
		}
		return resultUser;
	}
}
