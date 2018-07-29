package com.java.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain filter)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest req=(HttpServletRequest)sreq;
		HttpServletResponse resp=(HttpServletResponse)sresp;
		HttpSession session=req.getSession();
		Object obj = session.getAttribute("currentUser");
		String path=req.getServletPath();
		System.out.println(path);
		if(obj==null&&path.indexOf("login")<0) {//若此时请求mainTemp.jsp会出错
			resp.sendRedirect("login.jsp");
		}else {
			filter.doFilter(sreq, sresp);
		}
	}

}
