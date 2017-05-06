package siddhant.hindi.wordsense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.graphstream.graph.Graph;

public class Main {

	public static void main(String[] args) {
		
		ArrayList<String> inputWords = new ArrayList<String>();
		PreProcess pp = new PreProcess();
		inputWords=pp.run(); 
		
		HashMap<String,ArrayList<Long>> wordsSenses = new HashMap<String,ArrayList<Long>>();
		WordSenses ws = new WordSenses(inputWords);
		wordsSenses=ws.run(); 
		
		ConstructGraph cg = new ConstructGraph(wordsSenses);
		cg.run(); 
		
		HashMap<String,Long> semiDisambiguated;
		HashMap<String,ArrayList<Long>> Disambiguated=new HashMap<String,ArrayList<Long>>();
		ArrayList<Long> senses=new ArrayList<Long>(); 
		
		for(int i=0;i<10;i++){
			
			Graph graph = cg.rWalk();
			Disambiguation da = new Disambiguation(graph,wordsSenses);
			semiDisambiguated=da.run();
			
			for (Map.Entry<String, Long> item: semiDisambiguated.entrySet()){
				String key = item.getKey();
				Long value = item.getValue(); 
				if (Disambiguated.containsKey(key)==true){
					senses = Disambiguated.get(key);
					senses.add(value);
				}
				else{
					senses.add(value);
				}
				Disambiguated.put(key, senses);
				senses.clear();
			}
		}
		
		
		HashMap<String,Long> completeDisambiguated = new HashMap<String,Long>();
		HashMap<Long,Integer> count = new HashMap<Long,Integer>(); 
		
		for (Map.Entry<String, ArrayList<Long>> item : Disambiguated.entrySet()) {
			  String key = item.getKey();
			  ArrayList<Long> value = item.getValue();
			  
			  if (value.size()<=0){
				  continue; 
			  }
			  
			  for (int i=0;i<value.size();i++){
				  
				  Long tmpKey = value.get(i);
				  if (count.containsKey(tmpKey)==true){
					  Integer c = count.get(tmpKey);
					  c+=1;
					  count.put(tmpKey, c);
				  }
				  else{
					  count.put(tmpKey, 1);
				  }
				  
			  }
			  
			  int max=0; 
			  long ans=0; 
			  
			  for (Map.Entry<Long, Integer> m : count.entrySet()){
				  
				  Long k=m.getKey();
				  Integer c = m.getValue();
				  
				  if (c>max){
					  max=c; 
					  ans=k; 
				  }
			  }
			  
			  completeDisambiguated.put(key,ans); 
			  System.out.println("Correct Meaning of: "+key+" is at:"+ans);
			  
			}
	  
		

	}

}
