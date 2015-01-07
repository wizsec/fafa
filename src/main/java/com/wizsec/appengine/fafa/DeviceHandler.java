package com.wizsec.appengine.fafa;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.wizsec.appengine.fafa.Datastore;
import com.wizsec.appengine.fafa.beans.DeviceListBean;

/**
 * Servlet implementation class DeviceHandler
 */
public class DeviceHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeviceHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
String path = request.getRequestURI().substring(request.getContextPath().length()).toLowerCase();
		
		switch (path) {
        case "/api/listdevices":
        		getJsonList(request, response);
                return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length()).toLowerCase();
		
		switch (path) {
        case "/api/registerdevice":
        	registerDevice(request, response);
                return;
        case "/api/unregisterdevice":
        		unregisterDevice(request, response);
                return;
		}
	}
	
	private void registerDevice(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String regId = request.getParameter("regid");
		if (regId != null)
		{
			Datastore.register(regId);
		} else {
			response.sendError(500);
		}
	}
	
	private void unregisterDevice(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String regId = request.getParameter("regid");
		if (regId != null)
		{
			Datastore.unregister(regId);
		} else {
			response.sendError(500);
		}
	}
	
	private void getJsonList(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		DeviceListBean deviceList = new DeviceListBean();
		deviceList.setStatus(200);
		deviceList.setDevices(Datastore.getDevices());
		
		Gson gson = new Gson();
		out.println(gson.toJson(deviceList));
		
		out.close();
	}

}
