package siddhant.hindi.wordsense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;


import in.ac.iitb.cfilt.jhwnl.JHWNL;
import in.ac.iitb.cfilt.jhwnl.JHWNLException;
import in.ac.iitb.cfilt.jhwnl.data.POS;
import in.ac.iitb.cfilt.jhwnl.data.Synset;
import in.ac.iitb.cfilt.jhwnl.dictionary.Dictionary;


public class ConstructGraphMultiThreaded {
	
	//contains senseIDs against each word
	HashMap<String,ArrayList<Long>> wordsSenses;
	
	//to store only the keys i.e. words in the wordsSenses
	Set<String> keys;
	
	//to store values of the respective keys
	
	ArrayList<Long> senses; 
	Graph g; 
	maxDepth mD; 
	
	ConstructGraphMultiThreaded(HashMap<String,ArrayList<Long>> ws){
		mD = new maxDepth();
		wordsSenses=ws; 
		senses = new ArrayList<Long>();
		keys = new HashSet<String>();
		g=new MultiGraph("Graph");
	}
	
	public void run(){
		
		displayGraph();
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
		
		 JHWNL.initialize();
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
		 
		 /* Each word has multiple senses and I don't want any relation (weight/wordnet distance) between senses of a same parent word.  
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
					 
					 
					 /*  This Code Does Multitasking */
					 	 
					 long senseid = s1.get(m);
					 
					 try {
						List<ThreadsOutput> out = processInputs(senseid,s2,new maxDepth());
						for (ThreadsOutput xyz:out){							 
							 if(xyz.weight>0){
								 
								 float newWeight= 1/(float) xyz.weight; 
								 //System.out.println("final weight is:"+newWeight);
								 try
				                    {
				                        Edge e = g.addEdge(Long.toString(xyz.senseid)+"_" + Long.toString(xyz.senseid2), Long.toString(xyz.senseid), Long.toString(xyz.senseid2));
				                        e.addAttribute("weight", newWeight);
				                        e.addAttribute("ui.label",newWeight);
				                        
				                    }
				                 catch (Exception e)
				                    {
				                        e.printStackTrace();
				                    }
							 } 
						}
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					} 
				 }
				 
			 }
		 }  
	}
	
	/* For MultiThreading: Getting Weight  */
	public List<ThreadsOutput> processInputs(Long senseid,ArrayList<Long> s2,maxDepth obj)
	        throws InterruptedException, ExecutionException {

	    int threads = Runtime.getRuntime().availableProcessors();
	    ExecutorService service = Executors.newFixedThreadPool(threads);

	    List<Future<ThreadsOutput>> futures = new ArrayList<Future<ThreadsOutput>>();
	    
	    List<Integer> iterate = new ArrayList<Integer>();
	    
	    for (int i=0;i<s2.size();i++){
	    	iterate.add(i);
	    }
	    
	    
	    for (final Integer i:iterate) {
	        Callable<ThreadsOutput> callable = new Callable<ThreadsOutput>() {
	            public ThreadsOutput call() throws Exception {
					 long senseid2 = s2.get(i);
					 int weight=obj.compute_distance(senseid, senseid2);
					 ThreadsOutput output = new ThreadsOutput(senseid,senseid2,weight);
					 return output;
	            }
	        };
	       futures.add(service.submit(callable));
	    }

	    service.shutdown();
	    
	    List<ThreadsOutput> outputs = new ArrayList<ThreadsOutput>();
	    for (Future<ThreadsOutput> future : futures) {
	        outputs.add(future.get());
	    }
	    return outputs; 
	}
	
	
    /* Graph Centrality Algorithm */
	
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
