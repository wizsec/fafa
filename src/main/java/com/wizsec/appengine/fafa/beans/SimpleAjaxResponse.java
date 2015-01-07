package com.wizsec.appengine.fafa.beans;

public class SimpleAjaxResponse {
	
	private int status;
	private String message;
	
	public SimpleAjaxResponse() {
		status = 200;
		message = "";
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
