package com.wizsec.bitcoin.beans.blockrio;

public class UnspentResponse {
	
	private String status;
	private UnspentData data;
	private int code;
	private String message;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public UnspentData getData() {
		return data;
	}
	public void setData(UnspentData data) {
		this.data = data;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
