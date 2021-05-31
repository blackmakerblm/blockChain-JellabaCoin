package com.learn.blockchain.java_learn_blockchain;

public class TransactionInput {
	
	public String transactionOutputId; //Fait reference à une transactionOutput -> transactionId(peut etre utilisé pour voir si reelement vous avez recu)
	public TransactionOutput UTXO; //Contient transactionOutput Non depense
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

}
