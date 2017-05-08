package siddhant.hindi.wordsense;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import in.ac.iitb.cfilt.jhwnl.JHWNL;
import in.ac.iitb.cfilt.jhwnl.JHWNLException;
import in.ac.iitb.cfilt.jhwnl.data.POS;
import in.ac.iitb.cfilt.jhwnl.data.Synset;
import in.ac.iitb.cfilt.jhwnl.dictionary.Dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map; 

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
		
		runRandoms();
		long correctSense = disambiguated.get(targetWord);
		JHWNL.initialize();
		try {
			Synset answer = Dictionary.getInstance().getSynsetAt(POS.NOUN, correctSense);
			System.out.println(answer);
			return answer; 
		} catch (JHWNLException e) {
			e.printStackTrace();
			return null;
		} 
		
	}
	
	public HashMap<String,Long> runRandoms(){
		
		for (int r=0;r<15;r++){
			
			Graph g = cg.rWalk();
		
			for(Map.Entry<String,ArrayList<Long>> m:wordsSenses.entrySet()){ 
				
				String key = m.getKey().toString();
				values = wordsSenses.get(key);
			   
				if (values.size()<=0){
						continue; 
					}
					
				for (Long i:values){
				   
					Node n = g.getNode(i.toString());
					NodeInfo details = n.getAttribute("info");
				   
					double imp = details.importance;
				   
				   	if (sumImportance.containsKey(i)==true){
				   		double x=sumImportance.get(i);
				   		x+=imp;
				   		sumImportance.put(i, x);
				   	}
				   	else {
				   		sumImportance.put(i,imp); 
				   	} 
				}
			}
		}
		
		for (Map.Entry<String,ArrayList<Long>> m:wordsSenses.entrySet()){
			String key = m.getKey().toString();
			values = wordsSenses.get(key);
		   
			if (values.size()<=0){
					continue; 
				}

			Long ansSenseID=values.get(0);
			double max = sumImportance.get(ansSenseID); 
			
			for (Long i:values){
				
				double x = sumImportance.get(i);
				if(x>max){
					max=x;
					ansSenseID=i;
				}
				//System.out.println("ID:"+i+"SUM:"+x);
			}
			
			disambiguated.put(key, ansSenseID); 
		}
		//display(disambiguated);
		return disambiguated; 
	}
	

	public void display(HashMap<String,Long> ds){
		
		for(Map.Entry<String,Long> i:ds.entrySet()){
			String key=i.getKey(); 
			Long senseid= i.getValue();
			System.out.println("Word: "+key+" SenseID "+senseid);
		}
		
	}

}
