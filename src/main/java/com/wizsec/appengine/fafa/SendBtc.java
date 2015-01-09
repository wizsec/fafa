package com.wizsec.appengine.fafa;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;
import com.wizsec.appengine.fafa.beans.SimpleAjaxResponse;
import com.wizsec.bitcoin.MultisigBuilder;
import com.wizsec.tools.HexStringHandler;

/**
 * Servlet implementation class SendBtc
 */
public class SendBtc extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final NetworkParameters params = TestNet3Params.get();
	private Sender sender;
	private final Logger logger = Logger.getLogger(getClass().getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendBtc() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		String key = "AIzaSyBPyv2VexT5MmDY9SUxpvPfuQQ8sslJlBI";
		sender = new Sender(key);
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
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		Gson gson = new Gson();
		SimpleAjaxResponse jsonResponse = new SimpleAjaxResponse();
		
		try {
			
			// Address is 2MzVz9YknU1NGiKgqGVFZrcYiN5rxhhjoYZ
			Script redeemScript = new Script(HexStringHandler.hexStringToByteArray("524104711acc7644d34e493eba76984c81d99f1233f06b3242d90e6cd082b26fd0c1186f65de8d3378a6630f2285bd17972372685378683b604c68343fa1b532196c4d410476d6ef11a42010a889ee0c3d75f9cac3a51a3e245744fb9bf1bc8c196eb0f6982e39aad753514248966f4d545a5439ece8e27e13764c92f6230e0244cae5bee54104a45f0da4e6501fa781b6534e601f410a59328691d86d034d13362138f7e9a2927451280544e36c88279ee00c7face2fb707d0210842017e3937ae4584faacf6753ae"));
			ECKey privateKey = createKey("Super secret key 1");
			String address = request.getParameter("address"); // use mz8UF9wm91WMLFAYYUo3fwiDfWPN1dZZDQ
			BigDecimal amount = new BigDecimal(request.getParameter("amount"));
			MultisigBuilder multisigBuilder = new MultisigBuilder(params);
			String device = request.getParameter("device");
			
			if (amount.compareTo(BigDecimal.valueOf(0)) > 0 && !address.isEmpty() && !device.isEmpty())
			{
				Transaction tx = multisigBuilder.signFirstTransaction(address, amount, privateKey, redeemScript);
				sendSingleMessage(device, tx, jsonResponse);
			} else {
				jsonResponse.setStatus(412);
				jsonResponse.setMessage("Missing parameters");
			}
			
		} catch (AddressFormatException e) {
			jsonResponse.setMessage(e.toString());
			jsonResponse.setStatus(406);
		} catch (InsufficientMoneyException e) {
			jsonResponse.setStatus(204);
			jsonResponse.setMessage(e.toString());
		} catch (NumberFormatException e) {
			jsonResponse.setStatus(400);
			jsonResponse.setMessage(e.toString());
		}
		
		PrintWriter out = response.getWriter();
		out.println(gson.toJson(jsonResponse));
		out.close();
	}
	
	private Message createMessage(Transaction tx) {
	    Message message = new Message.Builder()
	    	.addData("tx", HexStringHandler.byteArrayToHex(tx.bitcoinSerialize()))
	    	.build();
	    return message;
	  }
	
	private void sendSingleMessage(String regId, Transaction tx, SimpleAjaxResponse ajaxResponse) throws IOException {
	    logger.info("Sending message to device " + regId);
	    Message message = createMessage(tx);
	    Result result;
	    try {
	      result = sender.sendNoRetry(message, regId);
	    } catch (IOException e) {
	      logger.log(Level.SEVERE, "Exception posting " + message, e);
	      ajaxResponse.setStatus(500);
	      return;
	    }
	    if (result == null) {
	    	ajaxResponse.setStatus(500);
	      return;
	    }
	    if (result.getMessageId() != null) {
	      logger.info("Succesfully sent message to device " + regId);
	      String canonicalRegId = result.getCanonicalRegistrationId();
	      ajaxResponse.setMessage(result.getMessageId());
	      if (canonicalRegId != null) {
	        // same device has more than on registration id: update it
	        logger.finest("canonicalRegId " + canonicalRegId);
	        Datastore.updateRegistration(regId, canonicalRegId);
	      }
	    } else {
	      String error = result.getErrorCodeName();
	      if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
	        // application has been removed from device - unregister it
	        Datastore.unregister(regId);
	        ajaxResponse.setStatus(404);
	        ajaxResponse.setMessage(result.getErrorCodeName());
	      } else {
	        logger.severe("Error sending message to device " + regId
	            + ": " + error);
	        Datastore.unregister(regId);
	        ajaxResponse.setMessage("Error sending message to device " + regId + ": " + error);
	        ajaxResponse.setStatus(500);
	      }
	    }
	  }
	
	private static ECKey createKey(String secret) {
        byte[] hash = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(secret.getBytes("UTF-8"));
            hash = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ECKey key = new ECKey(hash, (byte[])null);
        return key;
    }
	
	
}
