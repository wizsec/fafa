package com.wizsec.bitcoin.beans.blockrio;

import java.math.BigDecimal;

public class UnspentOutput {
	
	private String tx;
	private BigDecimal amount;
	private int n;
	private int confirmations;
	private String script;
	
	public String getTx() {
		return tx;
	}
	public void setTx(String tx) {
		this.tx = tx;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}
	public int getConfirmations() {
		return confirmations;
	}
	public void setConfirmations(int confirmations) {
		this.confirmations = confirmations;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}

}
