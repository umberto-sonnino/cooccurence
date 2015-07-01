import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class HomeworkTaggedParser {

	private final String FILENAME = "src/UKWAC-1.xml";

	private HashMap<String, Integer> unigrams, bigrams;
	private HashSet<String> stopwords;

	public HomeworkTaggedParser() {
		unigrams = new HashMap<String, Integer>(100000);
		bigrams = new HashMap<String, Integer>(1000000);
		stopwords = new HashSet<String>();

		try {
			BufferedReader stopwordsReader = new BufferedReader(new FileReader("src/stopwords.txt"));
			String line = "";

			while( (line = stopwordsReader.readLine()) != null){
				stopwords.add(line);
			}
			stopwordsReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void fillUnigramsBigrams() throws IOException{

		BufferedReader br = new BufferedReader(new FileReader(FILENAME));
		
		long time = System.currentTimeMillis();
		String line = "", bigram = "", firstPart = "", secondPart = "";
		HashSet<String> singleUnigrams = new HashSet<String>();
		HashSet<String> singleBigrams = new HashSet<String>();
		boolean readingSentence = false;
		boolean bigramFirstPart = true;
		
		while( (line = br.readLine())!= null ){
			if(line.contains("<s>")){
				readingSentence = true;
				continue;
			}else if(line.contains("</s>")){
				readingSentence = false;
				firstPart = secondPart = bigram = "";
				int value = 0;
				for(String s: singleUnigrams){
					value = 1;
					if(unigrams.containsKey(s))
						value += unigrams.get(s);
					unigrams.put(s, value);
				}
				
				for(String s: singleBigrams){
					value = 1;
					if(bigrams.containsKey(s))
						value = bigrams.get(s) + 1;
					
					bigrams.put(s, value);
				}
				
				singleBigrams.clear();
				singleUnigrams.clear();
				
				continue;
			}
			if(readingSentence){
				
				String[] lineWords = line.split("\\s+");
				if(lineWords.length < 3)
					continue;
				
				String currentLemma = lineWords[2].replaceAll("[^a-zA-Z\\s+]", "").toLowerCase();
				
				boolean legal = !(lineWords[1].equals("CD")) && //there are no weird numbers
						!(currentLemma.equals("")); //there are no strange characters
				
				if(legal){ 
					//We can add unigram if it's not a stopword
					if( !stopwords.contains(currentLemma) ){
						singleUnigrams.add(currentLemma);
					}
					
					if(bigramFirstPart){
						firstPart = currentLemma;
						bigram = secondPart + " " + firstPart;
						bigramFirstPart = false;
					}else{
						secondPart = currentLemma;
						bigram = firstPart + " " + secondPart;
						bigramFirstPart = true;
					}
					
					boolean addingBigram = !firstPart.equals("") && !secondPart.equals("") &&
							!stopwords.contains(firstPart.toLowerCase()) && !stopwords.contains(secondPart.toLowerCase());
					
					if(addingBigram){
						singleBigrams.add(bigram);
					}
				}
			}
		}

		br.close();
		long elapsed = System.currentTimeMillis() - time;
//		System.out.println("HOMEWORK TAGGED PARSER: " + elapsed);
	}

	public HashMap<String, Integer> getUnigrams() {
		return unigrams;
	}

	public HashMap<String, Integer> getBigrams() {
		return bigrams;
	}

}
