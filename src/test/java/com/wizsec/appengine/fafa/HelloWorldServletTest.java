package com.wizsec.appengine.fafa;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloWorldServletTest {

  private HelloWorldServlet helloWorldServlet;

  @Before
  public void setupGuestBookServlet() {
    helloWorldServlet = new HelloWorldServlet();
  }

  @Test
  public void testDoGet() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    StringWriter stringWriter = new StringWriter();

    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

    helloWorldServlet.doGet(request, response);

    assertEquals("hello world\n", stringWriter.toString());
  }

}
