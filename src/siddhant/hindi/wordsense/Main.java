package siddhant.hindi.wordsense;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
		
		long timeStart = System.currentTimeMillis();
		
		
		ArrayList<String> inputWords = new ArrayList<String>();
		PreProcess pp = new PreProcess();
		inputWords=pp.run(); 
		System.out.println(inputWords);
			
		HashMap<String,ArrayList<Long>> wordsSenses = new HashMap<String,ArrayList<Long>>();
		WordSenses ws = new WordSenses(inputWords);
		wordsSenses=ws.run(); 
		
		
		ConstructGraph cg = new ConstructGraph(wordsSenses);
		cg.run(); 

		Disambiguation da  = new Disambiguation(cg,wordsSenses);
		da.runRandoms(); 
		
		long timeFinish = System.currentTimeMillis();
		long timeTook = timeFinish - timeStart; 
		long minutes = timeTook/60000; 
		
		System.out.println("The Program Took "+timeTook+" miliseconds");
		System.out.println("The Program Took "+minutes+" minutes");
		
	  
	}
}
