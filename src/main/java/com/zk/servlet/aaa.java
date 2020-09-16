package com.zk.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class aaa extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private int count=0;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Cookie c = new Cookie("c", "someCookie");
				Cookie c1 = new Cookie("asd", "4513612");
		response.addCookie(c);
		response.addCookie(c1);
		response.getWriter().append("visit:"+(count++));
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
