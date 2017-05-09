package siddhant.hindi.wordsense;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import in.ac.iitb.cfilt.jhwnl.data.Synset;

public class Main {

	public static void main(String[] args) {
		
		String address = "./dataset/";
		long timeStart = System.currentTimeMillis();
		
		String targetWord="";
		Long correct=(long) 0;
		
		ArrayList<String> inputWords = new ArrayList<String>();
		PreProcess pp = new PreProcess();
		correct = pp.readSense(); 
		targetWord = pp.getTargetWord(); 
		
		System.out.println("Correct Sense ID is: "+correct);
		System.out.println("Target Word is: "+targetWord);
		inputWords=pp.run(); 
		System.out.println(inputWords);
		
		
		ArrayList<String> contextWindow = new ArrayList<String>();
		ArrayList<ArrayList<String>> allContextWindow = new ArrayList<ArrayList<String>>();
		ArrayList<Synset> answers = new ArrayList<Synset>();
		int contextWindowSize = 5; 
		
		/* Getting context window elements and then running the entire program on the window */
		
		for (int i=0;i<inputWords.size();i++){
			
			if(inputWords.get(i).equals(targetWord)==true){
				
				/*  Getting the context window range 
				 *  Left & Right are used to accommodate for if contextWindowSize couldn't be distributed
				 *  evenly across left and right of target word 
				 *  */
				
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
				

				System.out.println("\n\nContext Window Contains: "+contextWindow);
				
				/*  WordSense would contain word from context window and it's corresponding list of senses  */
				
				HashMap<String,ArrayList<Long>> wordsSenses = new HashMap<String,ArrayList<Long>>();
				WordSenses ws = new WordSenses(contextWindow);
				wordsSenses=ws.run(); 
				
				ConstructGraph cg = new ConstructGraph(wordsSenses);
				cg.run(); 

				/* ans contains disambiguated sense of the target word
				 * answers contains a list of ans */
				
				Disambiguation da  = new Disambiguation(cg,wordsSenses,targetWord);
				Synset ans = da.run();
				if (ans!=null){
					answers.add(ans);
				}
				allContextWindow.add(contextWindow);
				contextWindow = new ArrayList<String>();
				
			}
			
		}
		
		/* to calculate the accuracy */
		int correctDisambiguated=0; 
		
		/* to calculate the one sense of the entire document */
		HashMap<Long,Integer> voting = new HashMap<Long,Integer>();
		
		/* creating two output files 
		 * 1. outputDetailed: it contains the context window and the complete synset
		 * 2. output: it contains the context window and only the sense id
		 */
		
		try {
			BufferedWriter outputDetailed,output = null;
			outputDetailed = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(address+"outputDetailed.txt"), "unicode"));            
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(address+"output.txt"), "unicode"));		
			
			System.out.println(" ");
			for (int xy=0;xy<answers.size();xy++){
				
				System.out.println(xy+" "+targetWord+" -> "+answers.get(xy));
				
				Long id=answers.get(xy).getOffset();
				
				outputDetailed.write(allContextWindow.get(xy).toString()+","+answers.get(xy));
				outputDetailed.newLine();
				output.write(allContextWindow.get(xy).toString()+","+id);
				output.newLine();
				
				/* Calculating correctly disambiguated senses  */
				//System.out.println("id: "+id.getClass().getName()+"correct: "+correct.getClass().getName());
				
				if (id.toString().equals(correct.toString())){
					correctDisambiguated+=1; 
				}
				
				/* Getting the most occurrence of a sense id by counting each sense id 
				 * for document voting
				 */
				if (voting.containsKey(id)==true){
					int value = voting.get(id);
					voting.put(id, value+1);
				}
				else{
					voting.put(id, 1);
				}
			}
			outputDetailed.close();
			output.close();
		} catch (IOException e){
			System.err.println("Can't write to file outputDetailed.txt");
		}
		
		
		/* Getting the key with maximum counts for document voting */
		
		Long elected = Collections.max(voting.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
		System.out.println("\nThe complete document disambiguated is:"+elected);
		

		/* Calculating Accuracy */
		try {
			float accuracy = correctDisambiguated/answers.size(); 
			System.out.println("\n\nThe Accuracy of the System is: "+accuracy*100+"%");
		} catch (Exception e){
			System.err.println("Can't Divide");
			e.printStackTrace();
		}
		
		long timeFinish = System.currentTimeMillis();
		long timeTook = timeFinish - timeStart; 
		float minutes = (float) timeTook/60000; 
		
		System.out.println("The Program Took "+minutes+" minutes");
	}
}
