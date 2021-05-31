package com.learn.blockchain.java_learn_blockchain;

import java.util.ArrayList;
import java.util.Date;

public class Block {
	private String hash;
	private String precedenthash;
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private long timestamp;
	private int nonce;
	
	public Block(String precedenthash) {
		super();
		this.precedenthash = precedenthash;

		this.timestamp = new Date().getTime();
		this.hash = calculateHash();
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPrecedenthash() {
		return precedenthash;
	}

	public void setPrecedenthash(String precedenthash) {
		this.precedenthash = precedenthash;
	}


	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String calculateHash() {
		String calculatedhash = StringUtilitaire.applySha256( 
				 precedenthash+
				Long.toString(timestamp) +
				merkleRoot +
				Integer.toString(nonce)
				);
		return calculatedhash;
	}

	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}
	
	public void nineBlock(int difficulte) {
		merkleRoot = StringUtilitaire.getMerkleRoot(transactions);
		String nombreZero = new String(new char[difficulte]).replace("\0", "0");
		
		while(!this.getHash().substring(0, difficulte).equals(nombreZero)) {
			nonce++;
			this.setHash(calculateHash());
		}
		System.out.println("Nouveau Block miné : " + this.getHash());
	}
	
	//Ajouter une nouvelle transaction au block
	//Add transactions to this block
	public boolean ajouterTransaction(Transaction transaction) {
		//process transaction and check if valid, unless block is genesis block then ignore.
		if(transaction == null) return false;		
		if((this.precedenthash != "0")) {
			if((transaction.TraitementTransac() != true)) {
				System.out.println("Traitement transaction echouée!!!. Rejet.");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction reussie avec succès, ajouté au bloc");
		return true;
	}
	
	}
