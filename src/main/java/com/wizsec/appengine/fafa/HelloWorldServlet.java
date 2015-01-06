package com.wizsec.appengine.fafa;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloWorldServlet extends HttpServlet {
  static final long serialVersionUID = 1;
	
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setContentType("text/html");
      resp.getWriter().println("hello world");
  }
}
