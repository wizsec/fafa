package com.wizsec.bitcoin.beans.blockrio;

public class UnspentData {
	
	private String address;
	private boolean with_multisigs;
	private UnspentOutput[] unspent;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public boolean isWith_multisigs() {
		return with_multisigs;
	}
	public void setWith_multisigs(boolean with_multisigs) {
		this.with_multisigs = with_multisigs;
	}
	public UnspentOutput[] getUnspent() {
		return unspent;
	}
	public void setUnspent(UnspentOutput[] unspent) {
		this.unspent = unspent;
	}
	
}
