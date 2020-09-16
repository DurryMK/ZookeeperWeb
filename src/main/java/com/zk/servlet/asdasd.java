package com.zk.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class asdasd
 */
public class asdasd extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private  int count = 0;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Cookie[] cs = request.getCookies();
		
		String back = "Cookies["+cs.length+"]:";
		
		if(cs.length>0){
			
			for(Cookie c :cs){
				back+=c.getValue();
			}
			
		}
		
		response.getWriter().append(back);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
