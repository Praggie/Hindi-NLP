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
import java.util.HashMap;

import in.ac.iitb.cfilt.jhwnl.data.Synset;

public class Main {

	// this will actually be the command line input for the program
	static String address = "/Users/siddhant/Projects/Hindi-NLP/dataset/अंग/";
	// file containing the paragraphs 
	static String inputfilename="inputwords.txt";
	
	// this is actually the default filename in the data set
	static String targetWordFile="targetword.txt";
	// correct sense of the file
	static String sensefilename="sense.txt";
	
	static String stopwordfile="/dataset/hindistopwords.txt";
	
	static String targetWord="";
	static ArrayList<String> inputWords;
	static ArrayList<String> contextWindow;
	static ArrayList<ArrayList<String>> allContextWindow; 
	static ArrayList<Synset> answers; 
	static Long correct; 
	
	// correct/processed
	static ArrayList<Double> precisions = null;
	// correct/all-instances
	static ArrayList<Double> recalls = null; 
	// number of instances of the target word in a file
	static ArrayList<Integer> instances = null; 
	
	// senses of the target word
	static String[] sensesTargetWord = null; 
	static int instance; 

	
	public static void main(String[] args) {
		
	    System.out.println("Working Directory = " +
	               System.getProperty("user.dir"));
	       
	    stopwordfile =  System.getProperty("user.dir")+"/dataset/hindistopwords.txt";
		
		precisions = new ArrayList<Double>();
		recalls = new ArrayList<Double>();
		instances = new ArrayList<Integer>(); 

		if (args.length==1){
			address = args[0];
		}
		else {
			System.out.println("Usage Error: Command Line Argument Required");
			System.out.println("-AddressToTheDirectoyOfAWord");
			System.out.println("Exiting!");
			System.exit(1);
		}
		
		long timeStart = System.currentTimeMillis();
		processDirectoryandExecute();
		finalResult();
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
			    		String instancesfilename = "Instances"+i+".txt"; 
			    		
			    		try {
							BufferedReader instancefile = new BufferedReader(new FileReader(address+instancesfilename));
							instance = Integer.parseInt(instancefile.readLine().trim());
							instances.add(instance);
							instancefile.close();
						} catch  (NumberFormatException | IOException e) {
							System.out.println("Couldn't Open Instances"+i+".txt File ");
							e.printStackTrace();
							System.exit(0);
						}
			    		
			    		
			    		System.out.println(sensefilename);
			    		System.out.println(inputfilename);
			    		// this is going to  take crazy time. 
			    		executeLogic(); 
			    		generateOutput(i); 
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
				
				
				ConstructGraph cg = new ConstructGraph(wordsSenses,targetWord);
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
	
	public static void generateOutput(int filenumber){
		
		int correctDisambiguated=0; 
		
		try {
			BufferedWriter output = null;
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(address+"detailedOutput"+filenumber+".txt"), "unicode"));		
			
			System.out.println(" ");
			for (int xy=0;xy<answers.size();xy++){
				
				System.out.println((xy+1)+" "+targetWord+" -> "+answers.get(xy));
				
				Long id=answers.get(xy).getOffset();
				
				output.write(allContextWindow.get(xy).toString()+","+id);
				output.newLine();
				
				System.out.println("id: "+id.getClass().getName()+"correct: "+correct.getClass().getName());
				
				if (id.toString().equals(correct.toString())){
					correctDisambiguated+=1; 
				}
				
			}
	
			output.close();
		} catch (IOException e){
			System.err.println("Can't write to file detailedOutput.txt");
		}
		
		/* Calculating Accuracy */
		try {
			double precision = (double) correctDisambiguated/answers.size(); 
			precisions.add(precision);
			double recall = (double) correctDisambiguated/instance;
			recalls.add(recall);
			System.out.println("\n\nThe Precision of the System is: "+precision*100+"%");
			System.out.println("\n\nThe Recall of the System is: "+recall*100+"%");
			BufferedWriter result = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(address+"result"+filenumber+".txt")));
			result.write("TotalInstances,"+instance);
			result.newLine();
			result.write("TotalProcessed,"+answers.size());
			result.newLine(); 
			result.write("CorrectDisambiguated,"+correctDisambiguated);
			result.newLine();
			result.write("Precision,"+precision);
			result.newLine(); 
			result.write("Recall,"+recall);
			result.close();
		} catch (Exception e){
			System.err.println("Can't Divide");
			e.printStackTrace();
		}
	}
	
	public static void finalResult(){
		
		double pTotal = 0; 
		double rTotal = 0; 
		int size = precisions.size();
		for (int i=0;i<size;i++){
			pTotal += precisions.get(i);
			rTotal += recalls.get(i);
		}
		
		double fprecision = (float) pTotal/size; 
		double frecall = (float) rTotal/size; 
		
		System.out.println("Final Precison: "+fprecision);
		System.out.println("Final Recall: "+frecall);
		
		try {
			
			BufferedWriter fresult = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(address+"final_result.txt")));
			fresult.write("Precision,"+fprecision+",Recall,"+frecall);
			fresult.close();
		}
		catch (Exception e){
			System.out.println("Final Output Error in Writing File");
		}
		
	}
	
}
