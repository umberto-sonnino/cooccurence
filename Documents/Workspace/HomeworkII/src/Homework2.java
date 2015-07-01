import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;


public class Homework2 {

	private static final int TOP_RANKING = 500000;
	private static final int MOST_FREQUENT= 5000;
	private static final int SAMPLE = 50;

	private static List<Entry<String, Integer>> topUnigrams;
	private static List<Entry<String, Integer>> topBigrams;
	private static PrintWriter writer;
	private static HashMap<String,Double> jaccardMap;
	private static HashMap<String,Integer> unigrams, bigrams;


	public static void main(String[] args) throws IOException {
		HomeworkTaggedParser parser = new HomeworkTaggedParser();

		Random random = new Random();
		long range = MOST_FREQUENT - 3000 + 1;
		long fraction = (long) (range * random.nextDouble());
		int randomNumber = (int)(fraction + 3000);

		long time = System.currentTimeMillis();


		parser.fillUnigramsBigrams();

		//get all the frequency counts, sort them by count and write them to file
		unigrams = parser.getUnigrams();
		bigrams = parser.getBigrams();

		topUnigrams = findGreatest(unigrams, TOP_RANKING);
		topBigrams = findGreatest(bigrams, TOP_RANKING);

		writeToFileUnigramsBigrams();

		//get only the top 5000 unigrams, so that we can check those for co-occurrence
		topUnigrams = topUnigrams.subList(topUnigrams.size() - MOST_FREQUENT, topUnigrams.size());
		topBigrams = topBigrams.subList(topBigrams.size() - MOST_FREQUENT, topBigrams.size());

		HashSet<String> chosenWords = new HashSet<String>(), chosenBigrams = new HashSet<String>();
		for(int i = 0; i < SAMPLE; ++i){
			String currentWord = topUnigrams.get(randomNumber + i).getKey();
			String currentBigram = topBigrams.get(randomNumber + i).getKey();
			chosenWords.add(currentWord);
			chosenBigrams.add(currentBigram);
		}
		writeToFileUnigramJaccard(chosenWords, chosenBigrams);
		
//		System.out.println("Done in " + (System.currentTimeMillis() - time)+"ms");


	}


	private static void writeToFileUnigramJaccard(HashSet<String> jaccardWords, HashSet<String> jaccardDoubleWords) throws IOException {

		CoOccurrence counter = new CoOccurrence(unigrams, bigrams);
		counter.countOccurrences(jaccardWords, jaccardDoubleWords);
		
		HashMap<String, HashMap<String, Integer>> allUnigramCoOccurrences = counter.getUnigramCo();
		HashMap<String, HashMap<String, Integer>> allBigramCoOccurrences = counter.getBigramCo();
		

		Iterator<String> iterator = allUnigramCoOccurrences.keySet().iterator();
		double freqValue = 0, jaccardDouble = 0;
		String currentWord = "", coocurringWord = "";
		HashMap<String, Integer> currentMap = null;

		while(iterator.hasNext()){
			currentWord = iterator.next();
			currentMap = allUnigramCoOccurrences.get(currentWord);

			writer = new PrintWriter(currentWord + "_1.txt");

			List<Entry<String, Integer>> coocList = findGreatest(currentMap, currentMap.size());
			jaccardMap = counter.jaccard(currentWord, currentMap);
			writer.write("WORD" + "\t" + "FREQ" + "\t" + "JACCARD SIMILARITY" + "\n");
			freqValue = 0;

			for(Entry<String, Integer> e: coocList){
				coocurringWord = e.getKey();
				freqValue = e.getValue();
				jaccardDouble = jaccardMap.get(coocurringWord);

				String lineToWrite = coocurringWord + "\t" + freqValue + "\t" + jaccardDouble + "\n";

				writer.write(lineToWrite);
			}

			writer.close();

		}

		//Ora devo iterare su tutti i bigram tag
		iterator = allBigramCoOccurrences.keySet().iterator();
		
		while(iterator.hasNext()){
			currentWord = iterator.next();
			currentMap = allBigramCoOccurrences.get(currentWord);
			
			String [] splitBigram = currentWord.split("\\s+");
			
			writer = new PrintWriter(splitBigram[0] + "_" + splitBigram[1] + "_2.txt"); 
			
			List<Entry<String, Integer>> coocList = findGreatest(currentMap, currentMap.size());
			jaccardMap = counter.jaccard(currentWord, currentMap);
			writer.write("WORD" + "\t" + "FREQ" + "\t" + "JACCARD SIMILARITY" + "\n");
			
			freqValue = 0;
			
			for(Entry<String, Integer> e: coocList){
				coocurringWord = e.getKey();
				freqValue = e.getValue();
				jaccardDouble = jaccardMap.get(coocurringWord);
				
				String lineToWrite = coocurringWord + "\t" + freqValue + "\t" + jaccardDouble + "\n";

				writer.write(lineToWrite);
				
			}
			writer.close();
		}
		
	}


	private static void writeToFileUnigramsBigrams() throws FileNotFoundException {
		int i;
		writer = new PrintWriter("unigrams.txt");
		for(i = 0; i < topUnigrams.size(); i++){
			Entry<String, Integer> e = topUnigrams.get(i);
			String line = e.getKey() + "\t" + e.getValue() + "\n";
			writer.write(line);
		}
		writer.close();

		writer = new PrintWriter("bigrams.txt");
		for(i = 0; i < topBigrams.size(); ++i){
			Entry<String, Integer> e = topBigrams.get(i);
			String line = e.getKey() + "\t" + e.getValue() + "\n";
			writer.write(line);
		}
		writer.close();
	}


	private static List<Entry<String, Integer>> findGreatest(Map<String, Integer> map, int n){

		Comparator<? super Entry<String, Integer>> comparator = 
				new Comparator<Entry<String, Integer>>(){
			@Override
			public int compare(Entry<String, Integer> e0, Entry<String, Integer> e1)
			{
				int a = e0.getValue();
				int b = e1.getValue();
				if (a > b)
					return 1;
				else if(a < b)
					return -1;
				else 
					return 0;
			}
		};

		PriorityQueue<Entry<String, Integer>> highest = new PriorityQueue<Entry<String, Integer>>(n, comparator);

		for (Entry<String, Integer> entry : map.entrySet())
		{
			highest.offer(entry);
			while (highest.size() > n)
			{
				highest.poll();
			}
		}

		List<Entry<String, Integer>> result = new ArrayList<Map.Entry<String, Integer>>();
		while (highest.size() > 0)
		{
			Entry<String, Integer> e = highest.poll();
			result.add(e);
		}

		return result;
	}
}
