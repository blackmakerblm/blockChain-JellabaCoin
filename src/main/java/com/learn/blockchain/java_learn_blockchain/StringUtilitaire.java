package com.learn.blockchain.java_learn_blockchain;

import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

public class StringUtilitaire {
	//Applies Sha256 to a string and returns the result. 
	public static String applySha256(String input){		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        
			//Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));	        
			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//convertie une cle en string
	public static String CleToString(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	
	//Applique la signature electronique à l'aide de la clé privée
	public static byte[] applyRSAsign(PrivateKey privateKey, String input) {
		Signature rsa;
		byte[] output = new byte[0];
		try {
			rsa = Signature.getInstance("SHA256withRSA");
			rsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			rsa.update(strByte);
			byte[] realSig = rsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}
	
	public static boolean verificationRSA(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("SHA256withRSA");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();
		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		for(Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.IdTransaction);
		}
		ArrayList<String> treeLayer = previousTreeLayer;
		while(count > 1) {
			treeLayer = new ArrayList<String>();
			for(int i=1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}
}