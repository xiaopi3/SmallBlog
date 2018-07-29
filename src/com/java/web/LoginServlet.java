package com.java.web;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java.dao.UserDao;
import com.java.model.User;
import com.java.util.DbUtil;

public class LoginServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	DbUtil dbUtil=new DbUtil();
	UserDao userDao=new UserDao();
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		req.setCharacterEncoding("utf-8");
		HttpSession session=req.getSession();
		String userName=req.getParameter("userName");
		String password=req.getParameter("password");
		String remember=req.getParameter("remember");
		if(userName==null||password==null) {
			req.setAttribute("error", "用户名或密码为空");
			resp.sendRedirect("login.jsp");
			return;
		}
			
		Connection con=null;
		try {
			con = dbUtil.getCon();
			User user=new User(userName, password);
			User currentUser = userDao.login(con, user);
			if(currentUser==null) {
				req.setAttribute("user", user);
				req.setAttribute("error", "用户名或密码错误");
				//resp.sendRedirect("login.jsp");不能使用客户端跳转，会丢失req和resp
				req.getRequestDispatcher("login.jsp").forward(req, resp);
			}else {
				if("remember-me".equals(remember)) {
					rememberToCookie(userName,password,resp);
				}
				session.setAttribute("currentUser", currentUser);
				req.getRequestDispatcher("main").forward(req, resp);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(con!=null)
				try {
					dbUtil.closeCon(con);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	private void rememberToCookie(String userName, String password, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		Cookie user=new Cookie("user", userName+"-"+password);
		user.setMaxAge(1*60*60*24*7);
		resp.addCookie(user);
	}
	
	
}
