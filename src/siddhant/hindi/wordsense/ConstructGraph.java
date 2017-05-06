package siddhant.hindi.wordsense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.graphstream.algorithm.randomWalk.RandomWalk;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
//import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
//import org.graphstream.ui.view.ViewerPipe;

import in.ac.iitb.cfilt.jhwnl.JHWNL;
import in.ac.iitb.cfilt.jhwnl.JHWNLException;
import in.ac.iitb.cfilt.jhwnl.data.POS;
import in.ac.iitb.cfilt.jhwnl.data.Synset;
import in.ac.iitb.cfilt.jhwnl.dictionary.Dictionary;


public class ConstructGraph implements ViewerListener {
	
	//contains senseIDs against each word
	HashMap<String,ArrayList<Long>> wordsSenses;
	
	//to store only the keys i.e. words in the wordsSenses
	Set<String> keys;
	
	//to store values of the respective keys
	
	ArrayList<Long> senses; 
	Graph g; 
	maxDepth mD; 
	
	// to allow dynamic clicks while viewing the graph
	//private boolean loop=true; 
	
	ConstructGraph(HashMap<String,ArrayList<Long>> ws){
		mD = new maxDepth();
		wordsSenses=ws; 
		senses = new ArrayList<Long>();
		keys = new HashSet<String>();
		g=new MultiGraph("Graph");
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
							 System.out.println("final weight is:"+newWeight);
							 //System.out.println("******************************************************************");
							 try
			                    {
			                        Edge e = g.addEdge(Long.toString(senseid)+"_" + Long.toString(senseid2), Long.toString(senseid), Long.toString(senseid2));
			                        e.addAttribute("weight", newWeight);
			                        e.addAttribute("ui.label",newWeight);
			                        
			                    }
			                 catch (Exception e)
			                    {
			                	 	System.err.println("*****ERROR ERROR ERROR*****");
			                	 	System.err.println("Error: Counldn't Create Edge b/w "+senseid + "& "+senseid2+" with weight "+newWeight);
			                	 	System.err.println("***** FINISHED ERROR  *****");
			                        e.printStackTrace();
			                    }
						 }
					 }
				 }
				 
			 }
		 }  
	}
	
	public void displayGraph(){
		
//	 	String styleSheet = "node{size:7.5px;fill-color: black; text-size:10px;text-mode:hidden;} " +
//                "node.marked{fill-color: red;text-size:15px;text-mode:normal;text-color:blue;text-style:bold;size:10px;} " +
//                "edge{fill-color:green;text-size:10px;} " +
//                "edge.marked{fill-color:red;text-size:15px;size:3px;text-color:yellow;text-style:bold;}";
//	 	
//        g.addAttribute("ui.stylesheet",styleSheet);
		
		
		String styleSheet="node{fill-color:red;}"+
		"edge{fill-color:green;}";
		
		g.addAttribute("ui.stylesheet",styleSheet);
		
		g.addAttribute("ui.antialias", true);
		
		//g.addAttribute("ui.stylesheet", "node {fill-color: red; size-mode: dyn-size;} edge {fill-color:grey;}");
		
		g.display();
		
        //Viewer viewer = g.display();
        /*
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(g);

        // to enable clicks in graph 
        while(loop)
        {
            try {
				fromViewer.blockingPump();
			} catch (InterruptedException e) {				
				loop=false;
				e.printStackTrace();
			}; 
        } */	
	}
	
	public Graph rWalk(){
		RandomWalk rwalk = new RandomWalk();

		rwalk.setEntityCount(g.getNodeCount()/2);
		rwalk.init(g);

		for(int i=0; i<500; i++) {
			rwalk.compute();
		}
		rwalk.terminate();

		for(Node node: g.getEachNode()) {
			//System.out.printf("Node %s counts %f%n", node.getId(), rwalk.getPasses(node));
			NodeInfo ni = node.getAttribute("info");
			ni.importance = rwalk.getPasses(node);
		}
		return g; 
	}
	
	public void run(){
		
		displayGraph();
		construction();
		//rWalk();
		
		//return g; 
	}
	
	@Override
	public void buttonPushed(String id) {

		 System.out.println("Button pushed on node "+id);
         Node n=g.getNode(id);
         
         NodeInfo information = n.getAttribute("info");
         System.out.println("Original Word: "+information.originalword+" \nSense: "+information.senseid + "\nRank: "+information.importance);
         n.setAttribute("ui.class","marked");
	}

	@Override
	public void buttonReleased(String arg0) {
				
	}

	@Override
	public void viewClosed(String arg0) {
		//loop=false; 
		
	}

}
