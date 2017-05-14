package siddhant.hindi.wordsense;

import in.ac.iitb.cfilt.jhwnl.JHWNL;
import in.ac.iitb.cfilt.jhwnl.JHWNLException;
import in.ac.iitb.cfilt.jhwnl.data.POS;
import in.ac.iitb.cfilt.jhwnl.data.Synset;
import in.ac.iitb.cfilt.jhwnl.dictionary.Dictionary;

import java.util.ArrayList;
import java.util.HashMap;

public class Disambiguation {
	
	ConstructGraph cg; 
	HashMap<String,ArrayList<Long>> wordsSenses; 
	ArrayList<Long> values; 
	HashMap<String,Long> disambiguated; 
	HashMap<Long,Double> sumImportance;
	String targetWord; 
	
	Disambiguation(ConstructGraph g,HashMap<String,ArrayList<Long>> ws,String tW){
		cg=g; 
		wordsSenses=ws;
		targetWord = tW; 
		values = new ArrayList<Long>();
		disambiguated = new HashMap<String,Long>();
		sumImportance = new HashMap<Long,Double>(); 
	}
	
	public Synset run(){
		
		long correctSense = getMax(); 
		JHWNL.initialize();
		try {
			Synset answer = Dictionary.getInstance().getSynsetAt(POS.NOUN, correctSense);
			System.out.println("\n"+answer);
			return answer; 
		} catch (JHWNLException e) {
			e.printStackTrace();
			return null;
		} 
		
	}
	
	public Long getMax(){
		
		HashMap<Long,Float> indegree = cg.weightedDegree();
		
		ArrayList<Long> targetSenses = wordsSenses.get(targetWord);
		
		Long ans = targetSenses.get(0);
		float max = indegree.get(ans);
		
		for (int i=0;i<targetSenses.size();i++){
			Long key = targetSenses.get(i);
			Float val = indegree.get(key);
			if (val>max){
				max = val;
				ans = key; 
			}
		}
		
		return ans; 
	}
}
