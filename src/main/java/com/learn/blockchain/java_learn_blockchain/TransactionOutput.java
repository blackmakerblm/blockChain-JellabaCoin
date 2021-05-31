package com.learn.blockchain.java_learn_blockchain;

import java.security.PublicKey;

public class TransactionOutput {

	public String id;
	public PublicKey recepteur;//Nouveau detenteur du coin
	public float montant;
	public String parentTransactionId;//L'id de la transaction à partir de laquelle est faite cette sortie
	public TransactionOutput(PublicKey recepteur, float montant, String parentTransactionId) {
		super();
		this.recepteur = recepteur;
		this.montant = montant;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtilitaire.applySha256(StringUtilitaire.CleToString(recepteur)+Float.toString(montant)+parentTransactionId);
		
	}
	
	//Verifier si le coin m'appartient
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == recepteur);
	}
	
	
	
}
