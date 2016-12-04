package menuClasses;

import ioManagementClasses.IOComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import dataManagement.MatchingSearchDocument;
import generalClasses.P3Utils;
import systemClasses.SystemController;

public class PerformSearchesAction implements Action {
	private static IOComponent io = IOComponent.getComponent(); 

	@Override
	/**
	 * 
	 */
	public void execute(Object arg) {
		SystemController sc = (SystemController) arg; 
		String answer = "y"; 
		while (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
			io.output("\nSearching Based on Words:\n"); 
			//read the entire input line (words sperated by spaces)
			String words = io.getInput("\nEnter words to search for (separate by spaces): "); 
			Map<Integer, MatchingSearchDocument> matchingDocuments = null;
			try {
				StringTokenizer wordsTokens = new StringTokenizer(words); 
				//convert input list to an array list
				ArrayList<String> wordsList = constructListOfSearchWords(wordsTokens);
				//search for the input list in the index.
				matchingDocuments = sc.search(wordsList);
				if (matchingDocuments.isEmpty()) 
					io.output("No document matches this search.");
				else 
					processMatchingDocuments(matchingDocuments, wordsList.size()); 
			} catch (IOException e) {
				e.printStackTrace();
			}
			answer = io.getInput("\n\n*** Do you want to perform another search: (y/n)? "); 
		}
	}

	/**
	 * 
	 * @param matchingDocuments
	 * @throws IOException
	 */
	private void processMatchingDocuments(Map<Integer, MatchingSearchDocument> matchingDocuments, int listSize) 
			throws IOException 
	{
		ArrayList<MatchingSearchDocument> rankedDocuments = 
				rankMatchingDocuments(matchingDocuments, listSize); 
		displayHeaderLinesMatchingDocuments(rankedDocuments); 
		String answer = "y"; 
		while (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) { 
			int docIndex = io.getInputInteger("\n\n---Please, enter the number of document to see: "); 
			if (docIndex < 1 || docIndex > rankedDocuments.size()) 
				io.output("Invalid index number: " + docIndex);
			else {
				io.output("\n+++++Content of document ranked: " + docIndex + " +++++ \n\n");
				rankedDocuments.get(docIndex-1).displayDocument(0);
			}
			answer = io.getInput("\n\n*** Do you want to display another document: (y/n)? "); 
		}
	}

	/**
	 * 
	 * @param rankedDocuments
	 * @throws IOException 
	 */
	private void displayHeaderLinesMatchingDocuments(
			ArrayList<MatchingSearchDocument> rankedDocuments) throws IOException {
		
		for (int i=0; i<rankedDocuments.size(); i++) { 
			io.output("\n\n****DOCUMENT " + (i+1) + "****\n");
			rankedDocuments.get(i).displayDocument(3);
		}
		
	}

	/**
	 * 
	 * @param matchingDocuments
	 * @return
	 * @throws IOException
	 */
	private ArrayList<MatchingSearchDocument> rankMatchingDocuments(
			Map<Integer, MatchingSearchDocument> matchingDocuments, int listSize) throws IOException {
		
		int searchListSize = listSize;
		
		//create a list to contain the ranked documents
		ArrayList<MatchingSearchDocument> rankedDocuments = new ArrayList<>(); 
		
		//go through all (docID,msd) entries in matchingDocuments. 
		for (Entry<Integer, MatchingSearchDocument> e : matchingDocuments.entrySet()) {
			//compute the rank for this document
			P3Utils.computeRank(e.getValue(), searchListSize);
			rankedDocuments.add(e.getValue()); // just add them for now. 
		}
		
		//now we want to sort the content by rank
		Collections.sort(rankedDocuments, new Comparator<MatchingSearchDocument>() {

			@Override
			public int compare(MatchingSearchDocument arg0, MatchingSearchDocument arg1) {
				// TODO Auto-generated method stub
				return  (int) ((int)arg0.getRank() - arg1.getRank());
			}
		});
		return rankedDocuments; 
	}

	/**
	 *  Construct a list from the given StringTokenizer object.
	 * @param wordsTokens the object.
	 * @return A reference to an list of words.
	 */
	private ArrayList<String> constructListOfSearchWords(
			StringTokenizer wordsTokens) {
		ArrayList<String> uniqueWordsList = new ArrayList<>(); 
		while (wordsTokens.hasMoreTokens()) { 
			String word = wordsTokens.nextToken(); 
			if (!uniqueWordsList.contains(word))
				uniqueWordsList.add(word); 
		} 

		return uniqueWordsList;
	}
}


