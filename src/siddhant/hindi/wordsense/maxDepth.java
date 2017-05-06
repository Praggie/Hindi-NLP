package siddhant.hindi.wordsense;

import in.ac.iitb.cfilt.jhwnl.JHWNL;
import in.ac.iitb.cfilt.jhwnl.data.Pointer;
import in.ac.iitb.cfilt.jhwnl.data.PointerType;
import in.ac.iitb.cfilt.jhwnl.data.Synset;
import in.ac.iitb.cfilt.jhwnl.data.POS;
import in.ac.iitb.cfilt.jhwnl.dictionary.Dictionary;

import java.util.Vector;

class maxDepth
{
  static Vector<Synset> queue ;
  static Vector<Integer> depth ;
  static Vector<Synset> totalList ;
  
  
  maxDepth(){
	  JHWNL.initialize();
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
	      Vector<Synset> bfs_queue = new Vector<Synset>(40000);
	      Vector<Integer> bfs_dist = new Vector<Integer>(40000);
	      Vector<Synset> bfs_all = new Vector<Synset>(40000);
	      bfs_queue.add(s1);
	      bfs_dist.add(new Integer(0));
	      bfs_all.add(s1);
	      
	      Synset temp = new Synset();
	      int temp_dist = 0;
	      Pointer temp_ptr[];
	      
	      while(!bfs_queue.isEmpty())
	      {
		temp = bfs_queue.remove(0);
		temp_dist = bfs_dist.remove(0).intValue();
		
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
		    if(queue.elementAt(h).getPOS().equals(POS.NOUN) && !queue.elementAt(h).equals(temp) && !bfs_all.contains(queue.elementAt(h)))
		    {
		      bfs_queue.add(queue.elementAt(h));
		      bfs_all.add(queue.elementAt(h));
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
    	queue = new Vector<Synset>(40000);
    	depth = new Vector<Integer>(40000);
    	totalList = new Vector<Synset>(40000);
	
    	Synset tmp1,tmp2;
    	tmp1 = Dictionary.getInstance().getSynsetAt(POS.NOUN,a);
    	tmp2 = Dictionary.getInstance().getSynsetAt(POS.NOUN,b);
	
    	System.out.println("\n");
    	System.out.println("The Synsets chosen are:\n"+tmp1+"\n"+tmp2);

    	int dist = Semantic_Distance(tmp1,tmp2);
    	System.out.println("The Distance between the synsets is "+dist);
    	return dist;  
	  }
	  catch(Exception e) 
	  {
		System.out.println(e);
		return 0;
	  }
  }

}