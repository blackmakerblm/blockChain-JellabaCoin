package com.learn.blockchain.java_learn_blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
	public String IdTransaction;
	public PublicKey expediteur;
	public PublicKey recepteur;
	public float montant;
	public byte[] signatureElectronique;
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence =0;

	public Transaction(PublicKey expediteur, PublicKey recepteur, float montant, ArrayList<TransactionInput> inputs) {
		super();
		this.expediteur = expediteur;
		this.recepteur = recepteur;
		this.montant = montant;
		this.inputs = inputs;
	}
	
	private String calculeHash() {
		sequence++;
		return StringUtilitaire.applySha256(
				StringUtilitaire.CleToString(this.expediteur)+
				StringUtilitaire.CleToString(this.recepteur)+
				Float.toString(montant)+
				sequence
				);
	}
	
	public void GenererSignature(PrivateKey cleprivee) {
		String donnee = StringUtilitaire.CleToString(expediteur)+StringUtilitaire.CleToString(recepteur)+Float.toString(montant);
		this.signatureElectronique = StringUtilitaire.applyRSAsign(cleprivee, donnee);
		
	}
	
	public boolean VerifierSignature() {
		String donnee = StringUtilitaire.CleToString(expediteur)+StringUtilitaire.CleToString(recepteur)+Float.toString(montant);
		return StringUtilitaire.verificationRSA(expediteur, donnee, this.signatureElectronique);
		
	}
	
	public boolean TraitementTransac() {
		if(VerifierSignature()==false) {
			System.out.println("La signature de la transaction est incorrecte");
			return false;
		}
		//Rassembler les transactions entrantes et s'assurer qu'elles ne sont pas depensees
		for(TransactionInput i:inputs) {
			i.UTXO=BlockchainRun.UTXOs.get(i.transactionOutputId);
		}
		
		//Verifier si la transaction est valide
		if(getInputsValue()<BlockchainRun.minimumTransac) {
			System.out.println("#Transaction Inputs to small: " + getInputsValue());
			return false;
		}
		
		//Generer sortie transaction
		float restant =getInputsValue() - montant;
		IdTransaction = calculeHash();
		outputs.add(new TransactionOutput(this.recepteur,montant,IdTransaction));
		outputs.add(new TransactionOutput(this.expediteur,restant,IdTransaction));
		
		//Ajouter toutes les sorties aux sorties non depensees
		for(TransactionOutput o:outputs) {
			BlockchainRun.UTXOs.put(o.id, o);
		}
		
		//Supprimer toutes les transactions entrantes des restants une fois depensees
		for(TransactionInput i:inputs) {
			BlockchainRun.UTXOs.remove(i.UTXO.id);
		}
		return true;
	}
		
		
		//returns sum of inputs(UTXOs) values
		public float getInputsValue() {
			float total = 0;
			for(TransactionInput i : inputs) {
				if(i.UTXO == null) continue; //if Transaction can't be found skip it 
				total += i.UTXO.montant;
			}
			return total;
		}

	//returns sum of outputs:
		public float getOutputsValue() {
			float total = 0;
			for(TransactionOutput o : outputs) {
				total += o.montant;
			}
			return total;
		
	}
}

