package siddhant.hindi.wordsense;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class PreProcess {
	
	ArrayList<String> inputWords;
	String address; 
	BufferedReader inputWordsFile = null;
	
	PreProcess(){
		address = "./dataset/inputwords.txt";
		inputWords = new ArrayList<String>();
		
	}
	
	void readTextFile(){

		try {
			inputWordsFile = new BufferedReader(new InputStreamReader (new FileInputStream(address), "UTF8"));
		} catch( FileNotFoundException e){
			System.err.println("Error opening input words file.");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			System.err.println("UTF-8 encoding is not supported.");
			System.exit(-1);
		}
		
		String inputline;
		try {
			while ((inputline=inputWordsFile.readLine())!=null){
				String[] words=inputline.split(" ");
				//System.out.println(words.length);
				for(int i=0;i<words.length;i++){
					//System.out.println(words[i]);
					inputWords.add(words[i]);
				}
				
			}
		} catch (IOException e) {
			System.err.println("Error while reading the text file");
			e.printStackTrace();
		}
	}
	
	ArrayList<String> run(){
		
		readTextFile();

		/*
		 for(int i=0;i<inputWords.size();i++){
				System.out.println(inputWords.get(i));
			} */
		
		return inputWords; 
		
	
		
	}
	

	

}
