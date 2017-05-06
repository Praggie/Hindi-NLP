package siddhant.hindi.wordsense;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map; 

public class Disambiguation {
	
	Graph graph;
	HashMap<String,ArrayList<Long>> wordsSenses; 
	ArrayList<Long> values; 
	HashMap<String,Long> disambiguated; 
	
	Disambiguation(Graph g,HashMap<String,ArrayList<Long>> ws){
		graph=g; 
		wordsSenses=ws;
		values = new ArrayList<Long>();
		disambiguated = new HashMap<String,Long>();
	}
	
	public void findBest(){
		
		for(Map.Entry m:wordsSenses.entrySet()){  
			   //System.out.println(m.getKey()+" = "+m.getValue());  
			   String key = m.getKey().toString();
			   values = wordsSenses.get(key);
			   
			   if (values.size()<=0){
				   continue; 
			   }
			   
			   Long start = values.get(0);
			   
			   Node n = graph.getNode(start.toString()); 
			   NodeInfo answer = n.getAttribute("info");
			   
			   double max = answer.importance;
			   
			   for (Long i:values){
				   
				   n = graph.getNode(i.toString());
				   NodeInfo details = n.getAttribute("info");
				   
				   double imp = details.importance;
				   
				   if (imp>max){
					   max=imp;
					   answer=details; 
				   }
			   }
			   
			   disambiguated.put(key,answer.senseid);
			   //System.out.println("Correct Meaning of: "+key+" is at:"+answer.senseid);
			  } 		
	}

	public HashMap<String,Long> run(){
		findBest();
		return disambiguated; 
	}

}
