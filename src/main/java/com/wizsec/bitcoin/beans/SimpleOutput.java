package com.wizsec.bitcoin.beans;

import java.math.BigDecimal;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

public class SimpleOutput {
	
	Coin charge;
	Script outputScript;
	
	public SimpleOutput(BigDecimal amount, Address address)
	{
		Coin charge = Coin.valueOf(amount.movePointRight(8).longValueExact());
		Script outputScript = ScriptBuilder.createOutputScript(address);
	}
	
	public Coin getCharge()
	{
		return charge;
	}
	
	public Script getOutputScript()
	{
		return outputScript;
	}

}
