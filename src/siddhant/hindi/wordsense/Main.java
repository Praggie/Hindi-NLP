package siddhant.hindi.wordsense;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
//import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import in.ac.iitb.cfilt.jhwnl.data.Synset;

public class Main {

	static String address = "/Users/siddhant/Projects/Hindi-NLP/dataset/DataSet - Graph WSD/अंग/";
	// file containing the paragraphs 
	static String inputfilename="inputwords.txt";
	
	// this is actually the default filename in the data set
	static String targetWordFile="targetword.txt";
	// correct sense of the file
	static String sensefilename="sense.txt";
	
	static String stopwordfile="hindistopwords.txt";
	
	static String targetWord="";
	static ArrayList<String> inputWords;
	static ArrayList<String> contextWindow;
	static ArrayList<ArrayList<String>> allContextWindow; 
	static ArrayList<Synset> answers; 
	static Long correct; 
	
	// senses of the target word
	static String[] sensesTargetWord = null; 
	
	
	public static void main(String[] args) {
		

		if (args.length==1){
			address = args[0];
		}
		
		long timeStart = System.currentTimeMillis();
		processDirectoryandExecute();
		
		long timeFinish = System.currentTimeMillis();
		long timeTook = timeFinish - timeStart; 
		float minutes = (float) timeTook/60000; 
		
		System.out.println("The Program Took "+minutes+" minutes");
	}
	
	
	public static void processDirectoryandExecute(){
				
				// this is a csv file
				String numberSensesFile = address + "No_of_Senses.txt";
				BufferedReader br = null;
			    String csvSplitBy = ",";
			   
				
			    try {

		            br = new BufferedReader(new FileReader(numberSensesFile));
		            // there is only one line in the csv file
		            sensesTargetWord = br.readLine().split(csvSplitBy);
		            
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            if (br != null) {
		                try {
		                    br.close();
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            }
		        }
			    
			    // to get the number of files in the directory 
			    if (sensesTargetWord!=null){
			    	int nFiles = sensesTargetWord.length; 
			    	System.out.println(nFiles);
			    	for (int i=1;i<nFiles+1;i++){
			    		inputfilename = "ContextSenses00"+i+".txt"; 
			    		sensefilename = "Senses00"+i+".txt";
			    		System.out.println(sensefilename);
			    		System.out.println(inputfilename);
			    		// this is going to  take crazy time. 
			    		executeLogic(); 
			    	}
			    }
			    		
	}
	
	public static int executeLogic(){
		
		PreProcess pp = new PreProcess(address,inputfilename,targetWordFile,stopwordfile,sensefilename);
		correct = pp.readSense(); 
		targetWord = pp.getTargetWord(); 
		inputWords=pp.run(); 
		
		
		System.out.println("Correct Sense ID is: "+correct);
		System.out.println("Target Word is: "+targetWord);
		System.out.println(inputWords);
		
		generateContextWindowsAndDisambiguate();
		generateOutput(); 
		return 1; 
	}
	
	
	public static void generateContextWindowsAndDisambiguate(){
		
		int contextWindowSize = 5; 
		contextWindow = new ArrayList<String>();
		allContextWindow = new ArrayList<ArrayList<String>>();
		answers = new ArrayList<Synset>();
		
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
				
				WordSenses ws = new WordSenses(contextWindow,targetWord,sensesTargetWord);
				HashMap<String,ArrayList<Long>> wordsSenses=ws.run(); 
				
				
				ConstructGraph cg = new ConstructGraph(wordsSenses);
				cg.run();
				
				/*
				ConstructGraphMultiThreaded cg = new ConstructGraphMultiThreaded(wordsSenses);
				cg.run(); */
				

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
	}
	
	public static void generateOutput(){
		
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
				
				System.out.println((xy+1)+" "+targetWord+" -> "+answers.get(xy));
				
				Long id=answers.get(xy).getOffset();
				
				outputDetailed.write(allContextWindow.get(xy).toString()+","+answers.get(xy));
				outputDetailed.newLine();
				output.write(allContextWindow.get(xy).toString()+","+id);
				output.newLine();
				
				System.out.println("id: "+id.getClass().getName()+"correct: "+correct.getClass().getName());
				
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
	}
	
	
	
}
