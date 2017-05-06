package siddhant.hindi.wordsense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ac.iitb.cfilt.jhwnl.JHWNL;
import in.ac.iitb.cfilt.jhwnl.JHWNLException;
import in.ac.iitb.cfilt.jhwnl.data.IndexWord;
import in.ac.iitb.cfilt.jhwnl.data.IndexWordSet;
import in.ac.iitb.cfilt.jhwnl.data.POS;
import in.ac.iitb.cfilt.jhwnl.data.Synset;
import in.ac.iitb.cfilt.jhwnl.dictionary.Dictionary;

public class WordSenses {
	
	ArrayList<String> inputWords;
	
	// to store senseIDs of each word once 
	ArrayList<Long> senseIDs; 
	
	// store senseIDs against each word
	HashMap<String,ArrayList<Long>> wordsSenses;

	
	
	WordSenses(ArrayList<String> words){
		inputWords = words; 
		//senseIDs = new ArrayList<Long>(); 
		wordsSenses = new HashMap<String,ArrayList<Long>>();  
	}
	
	void getSenseIDs(){
		
		for (int index=0;index<inputWords.size();index++){
			senseIDs=new ArrayList<Long>(); 
			try {
				// would return almost everything about the word here
				//IndexWordSet IWSet = Dictionary.getInstance().lookupAllIndexWords(inputWords.get(index));
				IndexWordSet IWSet = Dictionary.getInstance().lookupMorphedIndexWords(POS.NOUN,inputWords.get(index));
				//IndexWord = Dictionary.getInstance().lookupIndexWord(POS.NOUN, inputWords.get(index));
				// store multiple possible synsets as individuals 
				
				IndexWord[]  IndexWord = new IndexWord[IWSet.size()];
				IndexWord  = IWSet.getIndexWordArray();
				
				for ( int i = 0;i < IndexWord.length;i++ ){
					
					int size = IndexWord[i].getSenseCount();
					
					//System.out.println("Sense Count is " + size);
					Synset[] synsetArray = IndexWord[i].getSenses(); 
					
					//System.out.println("The Word Is: "+IndexWord[i]);
					
					for ( int k = 0;k < size;k++ ){
						
						long synsetid = synsetArray[k].getOffset(); 
						
						//System.out.println("Synset ID: " + synsetid);
						senseIDs.add(synsetid);
					}
					
					//System.out.println(" ");
					
				}	
				wordsSenses.put(inputWords.get(index).toString(), senseIDs);
			} catch (JHWNLException e) {
				System.err.println("Internal Error raised from API.");
				e.printStackTrace();
			}
			
			
			
			
		}
		
	}

	
	void DisplayHashMap(){
		
		 System.out.println("\n\n******* WORDS -> SENSES ***********");
		 for(Map.Entry<String,ArrayList<Long>> m:wordsSenses.entrySet()){  
			   System.out.println(m.getKey()+" = "+m.getValue());  
			  } 		
		 System.out.println("************************************* \n");
	}
	
	
	HashMap<String,ArrayList<Long>> run(){
		JHWNL.initialize();
		getSenseIDs(); 
		DisplayHashMap();
		return wordsSenses; 
		
	}

}
