package siddhant.hindi.wordsense;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map; 

public class Disambiguation {
	
	ConstructGraph cg; 
	HashMap<String,ArrayList<Long>> wordsSenses; 
	ArrayList<Long> values; 
	HashMap<String,Long> disambiguated; 
	HashMap<Long,Double> sumImportance;
	
	Disambiguation(ConstructGraph g,HashMap<String,ArrayList<Long>> ws){
		cg=g; 
		wordsSenses=ws;
		values = new ArrayList<Long>();
		disambiguated = new HashMap<String,Long>();
		sumImportance = new HashMap<Long,Double>(); 
	}
	
	public HashMap<String,Long> runRandoms(){
		
		for (int r=0;r<5;r++){
			
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
		display(disambiguated);
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
