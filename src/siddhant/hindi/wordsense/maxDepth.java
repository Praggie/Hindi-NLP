package siddhant.hindi.wordsense;

import in.ac.iitb.cfilt.jhwnl.JHWNL;
import in.ac.iitb.cfilt.jhwnl.JHWNLException;
import in.ac.iitb.cfilt.jhwnl.data.IndexWord;
import in.ac.iitb.cfilt.jhwnl.data.IndexWordSet;
import in.ac.iitb.cfilt.jhwnl.data.Pointer;
import in.ac.iitb.cfilt.jhwnl.data.PointerType;
import in.ac.iitb.cfilt.jhwnl.data.Synset;
import in.ac.iitb.cfilt.jhwnl.data.POS;
import in.ac.iitb.cfilt.jhwnl.dictionary.Dictionary;

import java.util.Vector;

class maxDepth
{
  static Vector queue ;//Contains all root nodes of WordNet
  static Vector depth ;
  static Vector totalList ;
  
  
  maxDepth(){
	  JHWNL.initialize();
  }
	
  static void maxDepthComputer()
{
    try
    {
	Synset tmp = new Synset();
	Pointer temp_ptr[];
	int tmp_depth = 0;
	int curr_depth = 0;
	System.out.println("LEVEL 0 STARTED");
	while(!queue.isEmpty())
	{
	  tmp = (Synset)queue.remove(0);
	  tmp_depth = ((Integer)depth.remove(0)).intValue();
	  if(queue == null) System.out.println("HELLO");
	  if(tmp != null)
	  {
	      if(curr_depth != tmp_depth)
	      {
		System.out.println("LEVEL "+tmp_depth+" STARTED");
		curr_depth = tmp_depth;
	      }
	      temp_ptr = tmp.getPointers();
	      if(temp_ptr != null)
	      {
		for(int j=0;j<temp_ptr.length;j++)
		{
		  if(temp_ptr[j].getType().equals(PointerType.HYPONYM) || temp_ptr[j].getType().equals(PointerType.MERO_COMPONENT_OBJECT) || temp_ptr[j].getType().equals(PointerType.MERO_FEATURE_ACTIVITY) ||temp_ptr[j].getType().equals(PointerType.MERO_MEMBER_COLLECTION) || temp_ptr[j].getType().equals(PointerType.MERO_PHASE_STATE) || temp_ptr[j].getType().equals(PointerType.MERO_PLACE_AREA) || temp_ptr[j].getType().equals(PointerType.MERO_PORTION_MASS) || temp_ptr[j].getType().equals(PointerType.MERO_POSITION_AREA) || temp_ptr[j].getType().equals(PointerType.MERO_RESOURCE_PROCESS) || temp_ptr[j].getType().equals(PointerType.MERO_STUFF_OBJECT))
		  {
		    if(temp_ptr[j].getTargetSynset()!=null && (temp_ptr[j].getTargetSynset()).getPOS().equals(POS.NOUN))
		    {
		      if(!totalList.contains(temp_ptr[j].getTargetSynset()))
		      {
			queue.add(temp_ptr[j].getTargetSynset());
			depth.add(new Integer(curr_depth+1));
			totalList.add(temp_ptr[j].getTargetSynset());
		      }
		    }
		  }
		}
	      }
	    }
	}
    }
    catch(Exception e){System.out.println(e);}
}


  static int Semantic_Distance(Synset s1, Synset s2)
  {
    try
    {
	    if(s1.equals(s2))
	    {
	      return 0;
	    }
	    else
	    {
	      Vector bfs_queue = new Vector(40000);
	      Vector bfs_dist = new Vector(40000);
	      Vector bfs_all = new Vector(40000);
	      bfs_queue.add(s1);
	      bfs_dist.add(new Integer(0));
	      bfs_all.add(s1);
	      
	      Synset temp = new Synset();
	      int temp_dist = 0;
	      Pointer temp_ptr[];
	      
	      while(!bfs_queue.isEmpty())
	      {
		temp = (Synset)bfs_queue.remove(0);
		temp_dist = ((Integer)bfs_dist.remove(0)).intValue();
		
		if(temp.equals(s2)) return temp_dist;
		
		temp_ptr = temp.getPointers();
		if(temp_ptr!=null)
		{
		  for(int j=0;j<temp_ptr.length;j++)
		  {
		    if(temp_ptr[j].getType().equals(PointerType.HYPERNYM) || temp_ptr[j].getType().equals(PointerType.HOLO_COMPONENT_OBJECT) || temp_ptr[j].getType().equals(PointerType.HOLO_FEATURE_ACTIVITY) || temp_ptr[j].getType().equals(PointerType.HOLO_MEMBER_COLLECTION) || temp_ptr[j].getType().equals(PointerType.HOLO_PHASE_STATE) || temp_ptr[j].getType().equals(PointerType.HOLO_PLACE_AREA) || temp_ptr[j].getType().equals(PointerType.HOLO_PORTION_MASS) || temp_ptr[j].getType().equals(PointerType.HOLO_POSITION_AREA) || temp_ptr[j].getType().equals(PointerType.HOLO_RESOURCE_PROCESS) || temp_ptr[j].getType().equals(PointerType.HOLO_STUFF_OBJECT) || temp_ptr[j].getType().equals(PointerType.HYPONYM) || temp_ptr[j].getType().equals(PointerType.MERO_COMPONENT_OBJECT) || temp_ptr[j].getType().equals(PointerType.MERO_FEATURE_ACTIVITY) ||temp_ptr[j].getType().equals(PointerType.MERO_MEMBER_COLLECTION) || temp_ptr[j].getType().equals(PointerType.MERO_PHASE_STATE) || temp_ptr[j].getType().equals(PointerType.MERO_PLACE_AREA) || temp_ptr[j].getType().equals(PointerType.MERO_PORTION_MASS) || temp_ptr[j].getType().equals(PointerType.MERO_POSITION_AREA) || temp_ptr[j].getType().equals(PointerType.MERO_RESOURCE_PROCESS) || temp_ptr[j].getType().equals(PointerType.MERO_STUFF_OBJECT) )
		    {
		      if(temp_ptr[j].getTargetSynset()!=null && (temp_ptr[j].getTargetSynset()).getPOS().equals(POS.NOUN) && !bfs_all.contains(temp_ptr[j].getTargetSynset()))
		      {
			bfs_queue.add(temp_ptr[j].getTargetSynset());
			bfs_all.add(temp_ptr[j].getTargetSynset());
			bfs_dist.add(new Integer(temp_dist+1));
		      }
		    }
		  }
		}
		if(queue.contains(temp))
		{
		  for(int h=0;h<queue.size();h++)
		  {
		    if(((Synset)queue.elementAt(h)).getPOS().equals(POS.NOUN) && !((Synset)queue.elementAt(h)).equals(temp) && !bfs_all.contains((Synset)queue.elementAt(h)))
		    {
		      bfs_queue.add((Synset)queue.elementAt(h));
		      bfs_all.add((Synset)queue.elementAt(h));
		      bfs_dist.add(new Integer(temp_dist+1));
		    }
		  }
		}
	      }
	      return -1;
	    }
    }
    catch(Exception e){System.out.println(e);}
    return -1;
  }

  public int compute_distance (long a, long b)
  {
    try
    {
	
	queue = new Vector(40000);
	depth = new Vector(40000);
	totalList = new Vector(40000);
	
	Synset tmp = new Synset();
	Pointer temp_ptr[];
	
	// what is this queue?
	
//	for(int i=1;i<=30977;i++)
//	{
//	  tmp = Dictionary.getInstance().getSynsetAt(POS.NOUN,i);
//	  System.out.println(""+tmp);
//	  if(tmp!=null)
//	  {
//	    temp_ptr = tmp.getPointers();
//	    int num = 0;
//	    if(temp_ptr!=null)
//	    {
//	    for(int j=0;j<temp_ptr.length;j++)
//	    {
//	      if(temp_ptr[j].getType().equals(PointerType.HYPERNYM) || temp_ptr[j].getType().equals(PointerType.HOLO_COMPONENT_OBJECT) || temp_ptr[j].getType().equals(PointerType.HOLO_FEATURE_ACTIVITY) || temp_ptr[j].getType().equals(PointerType.HOLO_MEMBER_COLLECTION) || temp_ptr[j].getType().equals(PointerType.HOLO_PHASE_STATE) || temp_ptr[j].getType().equals(PointerType.HOLO_PLACE_AREA) || temp_ptr[j].getType().equals(PointerType.HOLO_PORTION_MASS) || temp_ptr[j].getType().equals(PointerType.HOLO_POSITION_AREA) || temp_ptr[j].getType().equals(PointerType.HOLO_RESOURCE_PROCESS) || temp_ptr[j].getType().equals(PointerType.HOLO_STUFF_OBJECT)) 		num++;
//	    }
//	    if(num == 0) {queue.add(tmp);depth.add(new Integer(0));totalList.add(tmp);}
//	    }
//	  }
//	}
	
	//System.out.println("Queue has been generated\n");
	
	Synset tmp1,tmp2;
	//do
	//{
	  tmp1 = Dictionary.getInstance().getInstance().getSynsetAt(POS.NOUN,a);//(int)(30977*Math.random()));
	  //System.out.println(tmp1.getWord(1));
	//}while(tmp1==null);
	//do
	//{
	  tmp2 = Dictionary.getInstance().getInstance().getSynsetAt(POS.NOUN,b);//(int)(30977*Math.random()));
	  //System.out.println(tmp2.getWord(1));
	//}while(tmp2==null);
	
	System.out.println("\n");
	System.out.println("The Synsets chosen are:\n"+tmp1+"\n"+tmp2);
	//System.out.println("Starting Distance Computation");
	int dist = Semantic_Distance(tmp1,tmp2);
	//System.out.println("Distance Computation Ended");
	System.out.println("The Distance between the synsets is "+dist);
	return dist;
    }
    catch(Exception e) {System.out.println(e);
    return 0;}
  }
  
}