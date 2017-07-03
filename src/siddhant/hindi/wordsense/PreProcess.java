package siddhant.hindi.wordsense;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class PreProcess {
	
	String address; 
	String inputfilename; 
	String stopwordfile; 
	String targetWordFile; 
	String sensefilename; 
	BufferedReader inputWordsFile = null;
	
	PreProcess(String addr,String inpFN,String targWF,String SWF,String senseFN){
		address = addr;
		inputfilename=inpFN;
		targetWordFile=targWF;
		stopwordfile=SWF;
		sensefilename=senseFN; 
	}
	
	ArrayList<String> run(){
		
		ArrayList<String> cleanWords; 
		cleanWords=readAndFilter(address+inputfilename); 
		
		ArrayList<String> stopWordsRemoved; 
		stopWordsRemoved=stopWordRemoval(cleanWords);
		
		//return cleanWords; 
		return stopWordsRemoved; 
	}
	
	
	public Long readSense(){
		
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(address+sensefilename)));
			String ans = input.readLine().trim();
			Long sen = Long.valueOf(ans);
			input.close();
			return sen; 
			
		} catch ( FileNotFoundException e) {
			System.err.println("sense.txt not found");
			e.printStackTrace();
			System.exit(-1);
			return null; 
		} catch (UnsupportedEncodingException e){
			System.err.println("sense.txt : unicode not supported");
			e.printStackTrace();
			System.exit(-1);
			return null; 
		} catch (IOException e){
			System.err.println("sense.txt : can't read");
			e.printStackTrace();
			System.exit(-1);
			return null; 
		}
		
	}
	
	
    /* Gets target word from a file */
	public String getTargetWord()
	{
		File inFiletargetword = new File(address+targetWordFile);
		String targetword1 = "";
    
		try
		{
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(inFiletargetword), "unicode"));
    			while (true) 
        			{
            			String TTline1 = input.readLine();  
           					if (TTline1 == null) 
           					{
           						break;
           					}	
           					targetword1 = targetword1 +TTline1+"\n";         
        			}
    			input.close();
		}
		catch (IOException except)
		{
                 except.printStackTrace();
		}
		String targetword = targetword1.trim();
		return(targetword);
	} 
	
	/* Reads the input file, removes characters like ,/;' etc. and then writes to a output file
	 * and puts all the words into an array list. 
	 * This method calls another clean method in the class as well
	 */
	public ArrayList<String> readAndFilter(String inp)
	{
		File inFile = new File(inp);
		File outFile = new File(inp+"filterprocess.txt");
		String text  = "";
		
		BufferedReader input=null; 

		try 
		{
			input = new BufferedReader(new InputStreamReader (new FileInputStream(inFile), "unicode"));
			while (true) 
			{
				String TTline = input.readLine();  
           			if (TTline == null) 
           			{
           				break;
           			}	
           			text+=TTline+"\n";          
			}
			
			
			String text1 = text.replace(",","  ");
			String text2 = text1.replace("-","  ");
			String text3 = text2.replace(".","  ");
			String text4 = text3.replace("?","  ");
			String text5 = text4.replace("[", "  ");
			String text6 = text5.replace("]", "  ");
			String text7 = text6.replace("|","  ");
			String text8 = text7.replace(";","  ");
			String text9 = text8.replace("\"", "  ");
			String text10 = text9.replaceAll("REABaBoGKKaKoMMaMlNSTTe", "  ");
			String text11 = text10.replace("।", "  ");
			String text12 = text11.replace("'","  ");
			String text13 = text12.replace("/", "  ");
			String text14 = text13.replace("(", "  ");
			String text15 = text14.replace(")", "  ");
			String text16 = text15.replace(".", "  ");
			String text17 = text16.replace("*", "  ");
			String text18 = text17.replace("`", "  ");
			String text19 = text18.replace("?", "  ");
			String text20 = text19.replace("=", "  ");
			String text21 = text20.replace("+", "  ");
			String text22 = text21.replace("‘", "  ");
			String text23 = text22.replace("’", "  ");
			String text24 = text23.replace("!", "  ");
			String text25 = text24.replace("“", "  ");
			String text26 = text25.replace("”", "  ");
			String text27 = text26.replace("।"," ");
			String text28 = text27.replace("–", " ");
			String text29 = text28.replace(":", " ");
			String text30 = text29.replace("<"," ");
			String text31 = text30.replace(">"," ");
			String text32 = text31.replace("I"," ");
			String text33 = text32.replace("0"," ");
			String text34 = text33.replace("1"," ");
			String text35 = text34.replace("2"," ");
			String text36 = text35.replace("3"," ");
			String text37 = text36.replace("4"," ");
			String text38 = text37.replace("5"," ");
			String text39 = text38.replace("6"," ");
			String text40 = text39.replace("7"," ");
			String text41 = text40.replace("8"," ");
			String text42 = text41.replace("9"," ");
			String text43 = text42.replace("०"," ");
			String text44 = text43.replace("१"," ");
			String text45 = text44.replace("२"," ");
			String text46 = text45.replace("३"," ");
			String text47 = text46.replace("४"," ");
			String text48 = text47.replace("५"," ");
			String text49 = text48.replace("६"," ");
			String text50 = text49.replace("७"," ");
			String text51 = text50.replace("८"," ");
			String text52 = text51.replace("९"," ");
			String text53 = text52.replace("%"," ");
			String text54 = text53.trim();
			
			String word; 
			
			ArrayList<String> inputWords = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(text54);
        
			while(st.hasMoreTokens()){
				word = st.nextToken();
				String word1 = clean(word); 
				inputWords.add(word1.trim());
			}
			input.close();
			Writer output = null;
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "unicode"));            
			output.write(inputWords.toString());
			output.close();
			return(inputWords); 
		}
		catch( FileNotFoundException e){
			System.err.println("Error opening input words file.");
			System.exit(-1);
			return null;
		} 
		catch (UnsupportedEncodingException e){
			System.err.println("Unicode encoding is not supported: Can't Open File");
			System.exit(-1);
			return null;
		}
		catch (IOException except){
			except.printStackTrace();
			return null;
		}
	}
    
	/* Further removal of useless characters
	 * Gets called by CreateVector */
    public String clean (String x){
        String word;
        word = x;
        
        if ((word.startsWith(" , "))|| (word.endsWith(" , ")))
                word.replace(" , ", " ");
        if ((word.startsWith(" - "))|| (word.endsWith(" - ")))
                word.replace(" - ", " ");
        if ((word.startsWith(" . "))|| (word.endsWith(" . ")))
                word.replace(" . ", " ");
        if ((word.startsWith(" ? "))|| (word.endsWith(" ?  ")))
                word.replace(" ? ", " ");
        if ((word.startsWith(" [ "))|| (word.endsWith(" [ ")))
                word.replace(" [ ", " ");
        if ((word.startsWith(" ] "))|| (word.endsWith(" ] ")))
                word.replace(" ]  ", " ");
        if ((word.startsWith(" | "))|| (word.endsWith(" | ")))
                word.replace(" | ", " ");
        if ((word.startsWith(" ; "))|| (word.endsWith(" ; ")))
                word.replace(" ; ", " ");
        if ((word.startsWith(" \" "))|| (word.endsWith(" \" ")))
                word.replace(" \" ", " ");
        if ((word.startsWith(" : "))|| (word.endsWith(" : ")))
                word.replace(" : ", " ");
        if ((word.startsWith(" – "))|| (word.endsWith(" – ")))
                word.replace(" – ", " ");
        if ((word.startsWith(" । "))|| (word.endsWith(" । ")))
                word.replace(" । ", " ");
        if ((word.startsWith(" ” "))|| (word.endsWith(" ” ")))
                word.replace(" ” ", " ");
        if ((word.startsWith(" “ "))|| (word.endsWith(" “ ")))
                word.replace(" “ ", " ");
        if ((word.startsWith(" ! "))|| (word.endsWith(" ! ")))
                word.replace(" ! ", " ");
        if ((word.startsWith(" ’ "))|| (word.endsWith(" ’ ")))
                word.replace(" ’ ", " ");
        if ((word.startsWith(" ‘ "))|| (word.endsWith(" ‘ ")))
                word.replace(" ‘ ", " ");
        if ((word.startsWith(" + "))|| (word.endsWith(" + ")))
                word.replace(" + ", " ");
        if ((word.startsWith(" = "))|| (word.endsWith(" =  ")))
                word.replace(" = ", " ");
        if ((word.startsWith(" ? "))|| (word.endsWith(" ? ")))
                word.replace(" ? ", " ");
        if ((word.startsWith(" ` "))|| (word.endsWith(" ` ")))
                word.replace(" ` ", " ");
        if ((word.startsWith(" * "))|| (word.endsWith("  * ")))
                word.replace(" * ", " ");
        if ((word.startsWith(" ) "))|| (word.endsWith(" ) ")))
                word.replace(" )  ", " ");
        if ((word.startsWith(" ( "))|| (word.endsWith(" ( ")))
                word.replace(" ( ", " ");
        if ((word.startsWith(" ' "))|| (word.endsWith(" ' ")))
                word.replace(" ' ", " ");
        if ((word.startsWith(" । "))|| (word.endsWith(" । ")))
                word.replace(" । ", " ");
        word.trim();
        return word;
    }
    
    /* Reads stop words from a file and removes them */
    
    public ArrayList<String> stopWordRemoval(ArrayList<String> x ){
    	    	
    	ArrayList<String> stopword = new ArrayList<String>();
    	ArrayList<String> fil = x;
    	
    	stopword = readAndFilter(stopwordfile);
    	
    	String[] arraystopword = new String [stopword.size()];
    	String[] astopword = stopword.toArray(arraystopword);
    	
    	String[] arrayfil = new String [fil.size()];
    	String[] file = fil.toArray(arrayfil);
    	
    	ArrayList<String> stopWordsRemoved = new ArrayList<String>();
    	
    	for(int i =0; i < file.length; i++){
    		for(int j = 0 ; j < astopword.length; j++){
    			if (file[i].equalsIgnoreCase(astopword[j])==false) {
    				stopWordsRemoved.add(file[i]);
    			}
    		}
    	}
    	
    	return(stopWordsRemoved);
    } 
}
