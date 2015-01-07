package com.wizsec.appengine.fafa.beans;

import java.util.List;

public class DeviceListBean {
	
	private int status;
	private String[] devices;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String[] getDevices() {
		return devices;
	}
	public void setDevices(List<String> devices) {
		this.devices = devices.toArray(new String[devices.size()]);
	}

}
