package siddhant.hindi.wordsense;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
		
		long timeStart = System.currentTimeMillis();
		
		String targetWord="";
		ArrayList<String> inputWords = new ArrayList<String>();
		PreProcess pp = new PreProcess();
		targetWord = pp.getTargetWord(); 
		
		System.out.println("Target Word is: "+targetWord);
		inputWords=pp.run(); 
		
		System.out.println(inputWords);
		
		ArrayList<String> contextWindow = new ArrayList<String>();
		int contextWindowSize = 5; 
		 
	
		for (int i=0;i<inputWords.size();i++){
			
			if(inputWords.get(i).equals(targetWord)==true){
		
				int upperLimit = i+contextWindowSize/2;
				int lowerLimit = i-contextWindowSize/2;
				int left = 0;
				int right = 0; 
				
				if (lowerLimit<0){
					left = Math.abs(lowerLimit); 
					lowerLimit=0; 
					
				}
				upperLimit+= left; 
				
				if (upperLimit>=inputWords.size()){
					right = upperLimit-inputWords.size()+1; 
					upperLimit=inputWords.size()-1; 
				}
				lowerLimit-=right; 
				
				if(lowerLimit<0){
					lowerLimit=0; 
				}
				
				for (int j=lowerLimit;j<=upperLimit;j++){
					contextWindow.add(inputWords.get(j));
				}
				

				System.out.println("Context Window Contains: "+contextWindow);
				
				HashMap<String,ArrayList<Long>> wordsSenses = new HashMap<String,ArrayList<Long>>();
				WordSenses ws = new WordSenses(contextWindow);
				wordsSenses=ws.run(); 
				
				ConstructGraph cg = new ConstructGraph(wordsSenses);
				cg.run(); 

				Disambiguation da  = new Disambiguation(cg,wordsSenses);
				da.runRandoms(); 
				
			}
			
		}
		
		long timeFinish = System.currentTimeMillis();
		long timeTook = timeFinish - timeStart; 
		double minutes = timeTook/60000; 
		
		System.out.println("The Program Took "+timeTook+" miliseconds");
		System.out.println("The Program Took "+minutes+" minutes");
		
	  
	}
}
