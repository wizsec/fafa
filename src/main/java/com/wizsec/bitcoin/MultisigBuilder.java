package com.wizsec.bitcoin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import com.google.gson.Gson;
import com.wizsec.bitcoin.beans.blockrio.UnspentData;
import com.wizsec.bitcoin.beans.blockrio.UnspentOutput;
import com.wizsec.bitcoin.beans.blockrio.UnspentResponse;
import com.wizsec.tools.HexStringHandler;

public class MultisigBuilder {
	
	private final NetworkParameters params;
	
	public MultisigBuilder(NetworkParameters params)
	{
		this.params = params;
	}
	
	public Transaction signFirstTransaction(String receiver, BigDecimal amount, ECKey firstKey, Script redeemScript) throws AddressFormatException, InsufficientMoneyException
	{
		Script script = ScriptBuilder.createP2SHOutputScript(redeemScript);
		Address multisigAddress = Address.fromP2SHScript(params, script);
		
		// Start building the transaction by adding the unspent inputs we want to use
		Transaction spendTx = new Transaction(params);
		Address receiverAddress = new Address(params, receiver);
		UnspentOutput[] unspentOutputs = getUnspentOutputs(multisigAddress.toString());

		addOutputsToTransaction(spendTx, amount, receiverAddress, unspentOutputs, multisigAddress);
		signMultisigTransaction(spendTx, redeemScript, firstKey);
						
		return spendTx;
	}
	
	private static void signMultisigTransaction(Transaction spendTx, Script redeemScript, ECKey key)
	{
		List<TransactionInput> inputList = spendTx.getInputs();
		Iterator<TransactionInput> iterator = inputList.iterator();
		
		int i=0;
		while (iterator.hasNext())
		{
			TransactionInput inputToBeSigned = iterator.next();
			
			// Sign the first part of the transaction using the key from the card
	     	Sha256Hash sighash = spendTx.hashForSignature(i, redeemScript, Transaction.SigHash.ALL, false);
	     	ECKey.ECDSASignature cardSignature = key.sign(sighash);
	     	TransactionSignature transactionSignarture = new TransactionSignature(cardSignature, Transaction.SigHash.ALL, false);

	     	Script inputScript = ScriptBuilder.createP2SHMultiSigInputScript(Arrays.asList(transactionSignarture), redeemScript);

			// Create the script that spends the multi-sig output.
	        inputToBeSigned.setScriptSig(inputScript);
	        i++;
		}
	}
	
	private UnspentOutput[] getUnspentOutputs(String address)
	{
		UnspentResponse unspentResponse = getUnspentOutputsBlockrio(address);
		
		UnspentData unspentData = unspentResponse.getData();
		UnspentOutput[] unspentOutputs = unspentData.getUnspent();
				
		return unspentOutputs;
	}
	
	private UnspentResponse getUnspentOutputsBlockrio(String address)
	{
		UnspentResponse unspentResponse = null;
		StringBuilder sb = new StringBuilder();
		
		try {
		    URL url = new URL("http://tbtc.blockr.io/api/v1/address/unspent/" + address + "?multisigs=1&unconfirmed=1");
		    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		    String line;

		    while ((line = reader.readLine()) != null) {
		    	sb.append(line);
		    }
		    reader.close();

		} catch (MalformedURLException e) {
		    e.getStackTrace();
		} catch (IOException e) {
			e.getStackTrace();
		}
		
		if (!sb.toString().isEmpty())
		{
			Gson gson = new Gson();
			unspentResponse = gson.fromJson(sb.toString(), UnspentResponse.class);
			return unspentResponse;
		} else {
			return null;
		}
	}
	
	public void addOutputsToTransaction(Transaction transaction, BigDecimal amount, Address merchantAddress, UnspentOutput[] unspentOutputs, Address cardAddress) throws InsufficientMoneyException
	{
		BigDecimal availableCoins = new BigDecimal(0);
		BigDecimal miningFee = new BigDecimal("0.0001");
		List<UnspentOutput> outputs = new ArrayList<UnspentOutput>();
		
		for (int i=0; i<unspentOutputs.length; i++)
		{
			ScriptBuilder scriptBuilder = new ScriptBuilder();			
			
			if (unspentOutputs[i].getConfirmations() > 0)
			{
				scriptBuilder.data(new String(unspentOutputs[i].getScript()).getBytes());
				availableCoins = availableCoins.add(unspentOutputs[i].getAmount());
				transaction.addInput(new Sha256Hash(unspentOutputs[i].getTx()), unspentOutputs[i].getN(), scriptBuilder.build());
			}

			if (availableCoins.compareTo(amount.add(miningFee)) > 0)
			{
				BigDecimal paybackCoins = availableCoins.subtract(amount).subtract(miningFee);
				transaction.addOutput(Coin.valueOf(paybackCoins.movePointRight(8).longValueExact()), cardAddress);
				
				break;
			} else if (availableCoins.compareTo(amount.add(miningFee)) == 0)
			{
				throw new org.bitcoinj.core.InsufficientMoneyException(Coin.valueOf(10000), "Need mining fee");
			}
			
		}
		
		if (availableCoins.compareTo(amount.add(miningFee)) < 0)
		{
			throw new org.bitcoinj.core.InsufficientMoneyException(Coin.valueOf((amount.subtract(availableCoins).movePointRight(8).longValue())), "Insufficent inputs");
		} else {
			transaction.addOutput(Coin.valueOf(amount.movePointRight(8).longValueExact()), merchantAddress);
			
			Iterator<UnspentOutput> iterator = outputs.iterator();
			while(iterator.hasNext())
			{
				UnspentOutput output = iterator.next();
				System.out.println(output.getTx() + ":" + output.getN());
			}
		}
	}

}
