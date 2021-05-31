package com.learn.blockchain.java_learn_blockchain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Portefeuille {
	
	public PublicKey clePublique;
	private PrivateKey clePrivee;
	public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //Seul les pieces detenu par ce portefeuille.

	public PrivateKey getClePrivee() {
		return clePrivee;
	}
	public void setClePrivee(PrivateKey clePrivee) {
		this.clePrivee = clePrivee;
	}
	
	//Constructeur 
	public Portefeuille() {
		genererPaireCle();
	}
	//Cette Methode permet de generer une paire de clé (publique et privé) avec un algorithme precis en l'occurence RSA
	public void genererPaireCle() {
		try {
			KeyPairGenerator clegen = KeyPairGenerator.getInstance("RSA");
			clegen.initialize(1024);
			KeyPair PaireCles = clegen.generateKeyPair();
			this.clePrivee = PaireCles.getPrivate();
			this.clePublique = PaireCles.getPublic();
			
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	//retourne les pieces non depenses et detenu par ce portefeuille
	public float getSolde() {
		float total = 0;	
        for (Map.Entry<String, TransactionOutput> item: BlockchainRun.UTXOs.entrySet()){
        	TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(clePublique)) { //if output belongs to me ( if coins belong to me )
            	UTXOs.put(UTXO.id,UTXO); //add it to our list of unspent transactions.
            	total += UTXO.montant ; 
            }
        }  
		return total;
	}
	
	//Genere et retourne une nouvelle transaction à partir de ce portefeuille
	public Transaction sendFunds(PublicKey recepteur,float montant ) {
		if(getSolde() < montant) { //Faire le cumul des pieces et verifier si le solde est inferieur au montant envoyé
			System.out.println("#Vous n'avez pas assez de JellabaCoin, l'opération a echouée.");
			return null;
		}
		
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		float total = 0;
		for (Map.Entry<String, TransactionOutput> element: UTXOs.entrySet()){
			TransactionOutput UTXO = element.getValue();
			total += UTXO.montant;
			inputs.add(new TransactionInput(UTXO.id));
			if(total > montant) break;
		}
		Transaction nouvelleTransaction = new Transaction(this.clePublique, recepteur , montant, inputs);
		nouvelleTransaction.GenererSignature(this.clePrivee);
		
		for(TransactionInput input: inputs){
			UTXOs.remove(input.transactionOutputId);
		}
		return nouvelleTransaction;

}
}
