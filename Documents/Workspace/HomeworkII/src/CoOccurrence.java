import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;


public class CoOccurrence {

	private HashMap<String,Integer> unigrams, bigrams;
	private HashMap<String, HashMap<String, Integer>> unigramCo, bigramCo;

	private final  String FILENAME = "src/UKWAC-1.xml";
	private HashSet<String> stopwords;

	public CoOccurrence(HashMap<String, Integer> unigrams2, HashMap<String, Integer> bigrams2) throws IOException{
		this.unigrams = unigrams2;
		this.bigrams = bigrams2;
		this.unigramCo = null;
		this.bigramCo = null;
		stopwords = new HashSet<String>();

		BufferedReader stopwordsReader = new BufferedReader(new FileReader("src/stopwords.txt"));
		String line = "";

		while( (line = stopwordsReader.readLine()) != null){
			stopwords.add(line);
		}
		stopwordsReader.close();
	}

	/*
	 * @parola - HashSet containing all the words that I need check for co-occurrence 
	 * @cooccorrenti/@dueParoleCooccorrenti - Set containing the list of co-occurrences with the String tag, representing the 
	 * 					current word taking into account for that HashMap of co-occurrences
	 */
	public void countOccurrences(HashSet<String> parola, HashSet<String> dueParole) throws IOException{

		HashMap<String, HashMap<String, Integer>> parolaCooccorrenti = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, HashMap<String, Integer>> dueParoleCooccorrenti = new HashMap<String, HashMap<String,Integer>>();
		//
		for(String s: parola){
			parolaCooccorrenti.put(s, new HashMap<String, Integer>());
		}
		for(String s: dueParole){
			dueParoleCooccorrenti.put(s, new HashMap<String, Integer>());
		}

		HashSet<String> sentence = new HashSet<String>();
		HashSet<String> sentenceBigrams = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(FILENAME));

		String line = "", bigram = "", firstPart = "", secondPart = "";
		boolean readingSentence = false, bigramFirstPart = true;

		while( (line = br.readLine()) != null){
			if(line.contains("<s>")){
				readingSentence = true;
				continue;
			}else if(line.contains("</s>")){
				readingSentence = false;

				for(String word: sentence){
					if(parolaCooccorrenti.containsKey(word)){
						HashSet<String> correctSentence = sentence;
//						correctSentence.remove(word);
						fillMap(parolaCooccorrenti, correctSentence, word);
					}
				}
				
				for(String bigr : sentenceBigrams){
					if(dueParoleCooccorrenti.containsKey(bigr)){
						String[] singleWords = bigr.split("\\s+");
						HashSet<String> correctSentence = sentence;
//						correctSentence.remove(singleWords[0]);
//						correctSentence.remove(singleWords[1]);
						fillMap(dueParoleCooccorrenti, correctSentence, bigr);
					}
				}
				
				sentence.clear();
				sentenceBigrams.clear();
				continue;
			}
			if(readingSentence){

				String[] lineWords = line.split("\\s+");

				if(lineWords.length < 3)
					continue;

				String lemma = lineWords[2].replaceAll("[^a-zA-Z\\s+]", "").toLowerCase();

				boolean adding = !(lineWords[1].equals("CD")) && !(lemma.equals("")); 

				if(adding){
					if(!stopwords.contains(lemma))
						sentence.add(lemma);
					
					if(bigramFirstPart){
						firstPart = lemma;
						bigram = secondPart + " " + firstPart;
						bigramFirstPart = false;
					}else{
						secondPart = lemma;
						bigram = firstPart + " " + secondPart;
						bigramFirstPart = true;
					}
					
					boolean addingBigram = !firstPart.equals("") && !secondPart.equals("") &&
							!stopwords.contains(firstPart.toLowerCase()) && !stopwords.contains(secondPart.toLowerCase());
					
					if(addingBigram){
						sentenceBigrams.add(bigram);
					}
				}
			}
		}


		br.close();

		unigramCo = parolaCooccorrenti;
		bigramCo = dueParoleCooccorrenti;

	}

	
	public HashMap<String, HashMap<String, Integer>> getUnigramCo() {
		return unigramCo;
	}

	public HashMap<String, HashMap<String, Integer>> getBigramCo() {
		return bigramCo;
	}

	/*
	 * Used to fill the map that is a 'value' (with key->String which is the tag) 
	 * in the HashMap<String, HashMap> at the tag given by word
	 */
	private void fillMap(HashMap<String, HashMap<String, Integer>> cooccorrenti, HashSet<String> sentence, String tag) {
		HashMap<String, Integer> tempMap = cooccorrenti.get(tag);
		for(String s: sentence){
			String[] tagArray = tag.split("\\s+");
			if((tagArray.length > 1)){
				if(s.equals(tagArray[0]) || s.equals(tagArray[1]))
					continue;
			}else if(s.equals(tag))
				continue;
			int value = 1;
			if(tempMap.containsKey(s))
				value += tempMap.get(s);
			tempMap.put(s, value);
		}
		cooccorrenti.put(tag, tempMap);
	}

	public HashMap<String, Double> jaccard(String word, HashMap<String, Integer> cooccorrenti) throws IOException{

		HashMap<String, Double> jaccardMap = new HashMap<String, Double>(cooccorrenti.size());
		double frequencyIntersect = 0.0, frequencyUnion = 0.0;
		double jaccard = 0.0;
		int frequency = 0;
		

		for(String s: cooccorrenti.keySet()){
			frequencyIntersect = cooccorrenti.get(s);
			frequency = unigrams.get(s);
			if((word.split("\\s+").length > 1)){
				frequencyUnion = frequency + bigrams.get(word) - frequencyIntersect;
			}
			else{
				frequencyUnion = frequency + unigrams.get(word) - frequencyIntersect;
			}
			jaccard = frequencyIntersect/frequencyUnion;

			jaccardMap.put(s, jaccard);

		}

		return jaccardMap;
	}

}
