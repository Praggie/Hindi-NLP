package siddhant.hindi.wordsense;

public class NodeInfo {
	
	public String originalword; 
	public long senseid; 
	public double importance; 
	
	NodeInfo(){
		
	}
	
	NodeInfo(String ow,long sid){
		originalword = ow;
		senseid =sid; 
		importance =0.0;
	}
}
