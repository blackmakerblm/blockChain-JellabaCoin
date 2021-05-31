package com.learn.blockchain.java_learn_blockchain;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class BlockchainRun {
	public static ArrayList<Block> blockchain = new ArrayList<Block>(); 
	public static int difficulte = 3;
	public static float minimumTransac = 0.1f;
	public static Portefeuille portefeuille1;
	public static Portefeuille portefeuille2;
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //liste des transactions non depensees.
	public static Transaction genesisTransaction;

	
	public static void main(String[] args) {
		/*
		
		blockchain.add(new Block("0","premiere transac"));
		System.out.println("Minage du Block 1");
		blockchain.get(0).nineBlock(difficulte);
		blockchain.add(new Block(blockchain.get(blockchain.size()-1).getHash(),"2 transac"));
		System.out.println("Minage du Block 2");
		blockchain.get(1).nineBlock(difficulte);
		blockchain.add(new Block(blockchain.get(blockchain.size()-1).getHash(),"3 transac"));
		System.out.println("Minage du Block 3");
		blockchain.get(2).nineBlock(difficulte);
		blockchain.add(new Block(blockchain.get(blockchain.size()-1).getHash(),"4 transac"));
		System.out.println("Minage du Block 4");
		blockchain.get(3).nineBlock(difficulte);
		blockchain.add(new Block(blockchain.get(blockchain.size()-1).getHash(),"5 transac"));
		System.out.println("Minage du Block 5");
		blockchain.get(4).nineBlock(difficulte);
		
		//Verifier la validité de la blockchain
		String validite = (BlockchainRun.isChainValid())?"oui":"non";
		System.out.println("La blockchain est valide: "+validite);
		//converion en json
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);	
		//visualisation
		System.out.println(blockchainJson);*/
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		portefeuille1 = new Portefeuille();
		portefeuille2 = new Portefeuille();
		Portefeuille coinbase = new Portefeuille();
		
		genesisTransaction = new Transaction(coinbase.clePublique, portefeuille1.clePublique, 100f, null);
		genesisTransaction.GenererSignature(coinbase.getClePrivee());	 //manually sign the genesis transaction	
		genesisTransaction.IdTransaction = "0"; //manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recepteur, genesisTransaction.montant, genesisTransaction.IdTransaction)); //manually add the Transactions Output
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.ajouterTransaction(genesisTransaction);
		ajouterBlock(genesis);
		
		//testing
				Block block1 = new Block(genesis.getHash());
				System.out.println("\nPortefeuille1 solde: " + portefeuille1.getSolde());
				System.out.println("\nPortefeuille1 tente d'envoyer(40) au portefeuille2...");
				block1.ajouterTransaction(portefeuille1.sendFunds(portefeuille2.clePublique, 40f));
				ajouterBlock(block1);
				System.out.println("\nPortefeuille1 solde: " + portefeuille1.getSolde());
				System.out.println("Portefeuille2 solde: " + portefeuille2.getSolde());
				
				Block block2 = new Block(block1.getHash());
				System.out.println("\nPortefeuille1 tente d'envoye (1000) plus qu'il n'en a...");
				block2.ajouterTransaction(portefeuille1.sendFunds(portefeuille2.clePublique, 1000f));
				ajouterBlock(block2);
				System.out.println("\nPortefeuille1 solde: " + portefeuille1.getSolde());
				System.out.println("Portefeuille2 solde: " + portefeuille2.getSolde());
				
				Block block3 = new Block(block2.getHash());
				System.out.println("\nPortefeuille2 tente d'envoyer (20) au portefeuille1...");
				block3.ajouterTransaction(portefeuille2.sendFunds( portefeuille1.clePublique, 20));
				System.out.println("\nPortefeuille1 solde: " + portefeuille1.getSolde());
				System.out.println("\nPortefeuille2 solde: " + portefeuille2.getSolde());
		/*		
		//Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(StringUtilitaire.CleToString(portefeuille1.getClePrivee()));
		System.out.println(StringUtilitaire.CleToString(portefeuille1.clePublique));
		Transaction tr1 = new Transaction(portefeuille1.clePublique,portefeuille2.clePublique,5000,null);
		tr1.GenererSignature(portefeuille1.getClePrivee());
		//Verification de la signature
		System.out.println("La signature est elle verifiée");
		System.out.println(tr1.VerifierSignature());*/

	}
	
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulte]).replace('\0', '0');
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.getHash().equals(currentBlock.calculateHash()) ){
				System.out.println("#Le hash actuel n'est pas egal");
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.getHash().equals(currentBlock.getPrecedenthash()) ) {
				System.out.println("#Le hash precedent n'est pas egal");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.getHash().substring( 0, difficulte).equals(hashTarget)) {
				System.out.println("#Ce block n'a pas été minné");
				return false;
			}
			
			//loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);
				
				if(!currentTransaction.VerifierSignature()) {
					System.out.println("#Signature de la transaction(" + t + ") n'est pas valide");
					return false; 
				}
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Les transactions entrantes sont differentes des sortantes(" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						System.out.println("#La reference d'entree manque à la transaction(" + t + ")");
						return false;
					}
					
					if(input.UTXO.montant != tempOutput.montant) {
						System.out.println("#La transaction entrante referencé(" + t + ") a une valeur Invalide");
						return false;
					}
					
					tempUTXOs.remove(input.transactionOutputId);
				}
				
				for(TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if( currentTransaction.outputs.get(0).recepteur != currentTransaction.recepteur) {
					System.out.println("#La transaction(" + t + ") sortante a un mauvais recepteur");
					return false;
				}
				if( currentTransaction.outputs.get(1).recepteur != currentTransaction.expediteur) {
					System.out.println("#La transaction(" + t + ") sortante 'change' n'est pas l'expediteur.");
					return false;
				}
				
			}
			
		}
		System.out.println("Blockchain valide");
		return true;
	}
	
	public static void ajouterBlock(Block newBlock) {
		newBlock.nineBlock(difficulte);
		blockchain.add(newBlock);
	}

}
