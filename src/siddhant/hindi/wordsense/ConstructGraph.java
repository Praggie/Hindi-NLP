package siddhant.hindi.wordsense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;


import in.ac.iitb.cfilt.jhwnl.JHWNL;
import in.ac.iitb.cfilt.jhwnl.JHWNLException;
import in.ac.iitb.cfilt.jhwnl.data.POS;
import in.ac.iitb.cfilt.jhwnl.data.Synset;
import in.ac.iitb.cfilt.jhwnl.dictionary.Dictionary;


public class ConstructGraph {
	
	//contains senseIDs against each word
	HashMap<String,ArrayList<Long>> wordsSenses;
	
	//to store only the keys i.e. words in the wordsSenses
	Set<String> keys;
	
	//to store values of the respective keys
	
	ArrayList<Long> senses; 
	Graph g; 
	maxDepth mD; 

	
	ConstructGraph(HashMap<String,ArrayList<Long>> ws){
		mD = new maxDepth();
		wordsSenses=ws; 
		senses = new ArrayList<Long>();
		keys = new HashSet<String>();
		g=new MultiGraph("Graph");
	}
	
	public void run(){
		
		//displayGraph();
		construction();
	}
	
	public void displayGraph(){
	
		String styleSheet="node{fill-color:red;}"+
		"edge{fill-color:green;}";
		
		g.addAttribute("ui.stylesheet",styleSheet);
		g.addAttribute("ui.antialias", true);
		g.display();
	}
		
	
	public void construction(){
		
		 System.out.println("Initializing in construction.class");
		 JHWNL.initialize();
		 System.out.println("");
		 keys=wordsSenses.keySet();
		 Object[] keysArray; 
		 keysArray=keys.toArray();
		 
		 
		 for (int i = 0; i < keysArray.length; i++) {
	           
			 	// gets the value of the respective key here
	            senses=wordsSenses.get(keysArray[i]);
	            
	            for (int k=0;k<senses.size();k++){
	            	
	            	long senseid = senses.get(k);
	            	
	            	// creating each senseID a node
	            	Node n = g.addNode(Long.toString(senseid));
	            	
	            	// NodeInfo custom data structure
	            	NodeInfo details = new NodeInfo(keysArray[i].toString(),senseid);
	            	n.addAttribute("info", details);	
	            	try {
						 Synset a=Dictionary.getInstance().getSynsetAt(POS.NOUN,senseid);
						 n.addAttribute("ui.label",""+a.getWord(1)+"_ID"+Long.toString(senseid));
					} catch (JHWNLException e) {
						e.printStackTrace();
					}
	            	
	            }
	        }
		 
		 /* Each word has multiple senses and I don't want any relation (weight/wordnet distance) between them 
		  * Hence I take a word (key) and then start from next corresponding word (p=l+1, in the code)
		  * Of each word (key) I get the arrayList (key) containing senseIDs  
		  * Now I find the maximum depth in the word net comparing the senseIDs of first key and the senseIDs of the corresponding keys
		  */
		 for (int l=0;l<keysArray.length;l++){
			 
			 ArrayList<Long> s1 = new ArrayList<Long>();
			 s1 = wordsSenses.get(keysArray[l]);
			 
			 for (int p=l+1;p<keysArray.length;p++){
				 
				 ArrayList<Long> s2 = new ArrayList<Long>();
				 s2 = wordsSenses.get(keysArray[p]);
				 
				 for (int m=0;m<s1.size();m++){			
					 
					 long senseid = s1.get(m);
					 
					 for (int q=0;q<s2.size();q++){
						 
						 long senseid2 = s2.get(q);
						 int weight=mD.compute_distance(senseid, senseid2);
						 
						 if(weight>0){
							 
							 float newWeight= 1/(float) weight; 
							 //System.out.println("final weight is:"+newWeight);
							 try
			                    {
			                        Edge e = g.addEdge(Long.toString(senseid)+"_" + Long.toString(senseid2), Long.toString(senseid), Long.toString(senseid2));
			                        e.addAttribute("weight", newWeight);
			                        e.addAttribute("ui.label",newWeight);
			                        
			                    }
			                 catch (Exception e)
			                    {
			                	 	System.err.println("Error: Counldn't Create Edge b/w "+senseid + "& "+senseid2+" with weight "+newWeight);
			                        e.printStackTrace();
			                    }
						 }
					 } 
				 }
				 
			 }
		 }  
	}
	

	public HashMap<Long,Float> weightedDegree(){
		
		HashMap<Long,Float> inDegree = new HashMap<Long,Float>(); 
		
		for (Node n:g){
			NodeInfo info = n.getAttribute("info");
			long id = info.senseid;
			float sum = 0; 
			//System.out.println("At node "+id);
			
			for (Edge e:n.getEachEdge()){
				float weight=e.getAttribute("weight");
				//System.out.println("The Edge b/w"+ e.getNode0()+" "+e.getNode1()+"has weight"+weight);
				sum+=weight; 
			}
			
			inDegree.put(id, sum);
			//System.out.println("Key: "+id+" Sum: "+sum);
		}
		
		return inDegree; 
	}

	
	
}
