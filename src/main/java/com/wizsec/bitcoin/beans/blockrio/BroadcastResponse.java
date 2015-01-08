package com.wizsec.bitcoin.beans.blockrio;

public class BroadcastResponse {
	
	// {"status":"success","data":"b1b63e64ec54c30a5239877d598c332afcf6d1b520501c4de301aaf47c35fbf6","code":200,"message":""} 
	private String status;
	private String data;
	private int code;
	private String message;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
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
