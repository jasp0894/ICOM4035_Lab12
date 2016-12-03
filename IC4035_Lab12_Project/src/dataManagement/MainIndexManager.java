package dataManagement;

import generalClasses.P3Utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

public class MainIndexManager {
	private static MainIndexManager instance = null; 
	private Map<String, ArrayList<Entry<Integer, Integer>>> map; 
	private File fPath; 
	private RandomAccessFile file; 
	private boolean modified;         // to remember if modifications have been made...
	// needed to writeback to file is needed....
	
	public static MainIndexManager getInstance() throws IOException { 
		if (instance == null) 
			instance = new MainIndexManager(); 
		return instance; 
	}
	
	private MainIndexManager() throws IOException { 
		modified = false; 
		map = new Hashtable<>(); 
		String fName = "main_index.pp3"; 
		fPath = new File(P3Utils.IndexDirectoryPath, fName); 
		if (fPath.exists()) {//in case an exception wasn't thrown due to inexistent path
			file = new RandomAccessFile(fPath, "r"); 
			readMapContentFromFile(); 
			file.close();
			
		}
	}

	private void readMapContentFromFile() throws IOException {
		long fileLength = file.length(); 
		boolean completed = false; 
		while (!completed) {
			try {
				String word = P3Utils.readWord(file);//read name of the word
				ArrayList<Entry<Integer, Integer>> wordDocsList = new ArrayList<>();
				int docID = file.readInt(); //reads integers in the form of 4-bytes blocks
				int wordFreq = file.readInt(); 
				while (docID != -1) { //check if not the end of sequence for mainIndex's current word
					//the idea here is to fill an array list of entries for each word 
					//then we use the list and the name of the word to form a new entry that is to be
					//added to the main data structure. A map whose entries are of
					//type String and and arraylist
					wordDocsList.add(new AbstractMap.SimpleEntry<Integer, Integer>(docID, wordFreq)); 
					docID = file.readInt(); 
					wordFreq = file.readInt(); 
				}
				map.put(word, wordDocsList); //add the entry
			} catch (IOException e) {
				if (file.getFilePointer() == fileLength) //check if we have reached EOF
					completed = true; 
				else
					e.printStackTrace();
			} 
		}
	}

	/**
	 * Adds the data of a new document to the main index. For each word w, 
	 * it will add a pair: (doc id, frequency of w in doc). 
	 * @param word
	 * @param doc
	 * @param frequency
	 */
	public void registerWordInDocument(String word, int docID, int frequency) { 
		ArrayList<Entry<Integer, Integer>> wordDocsList; 
		// the key in each entry is the docID number
		// the value in each entry is the frequency of that word in 
		// the document. There can be only one entry having a particular
		// key value in this list. We can use a Map for this, but since
		// we are not assuming to large number of documents, and because
		// of the operations we do with these type of lists, no need
		// for that at the moment. The only occasions when we need to
		// get one such entry by the id, is when removing; and also
		// perhaps when adding to guarantee that there are no repetitions. 
		// However, the requirement of no repetitions need to be guarantee
		// by the remove operation and the operation that assigned a new
		// id to a new document; it needs to guarantee no two different
		// docs are assigned the same id. 
		
		wordDocsList = map.get(word); 
		Entry<Integer, Integer> newEntry = new AbstractMap.SimpleEntry<>(docID, frequency); 
		
		if (wordDocsList == null)  {
			// ADD MISSING CODE HERE FOR EXERCISE 1 (LAB)
			//the given word has not been registered in the main index.
			
			wordDocsList = new ArrayList<>(); //create a new array list
			wordDocsList.add(newEntry);//add the given entry
			
			map.put(word, wordDocsList); //register it
		}
		else 
			wordDocsList.add(newEntry); //add a new entry to the arraylist of this words' occurrences  
			
		modified = true; 
	}
		
	public Iterable<Entry<Integer, Integer>> getDocsList(String word) { 
		ArrayList<Entry<Integer, Integer>> wordDocsList; 
		wordDocsList = map.get(word); 
		return wordDocsList;          // returns null if the word does not exist in index
	}
	
	/**
	 * Remove the particular instance (if any) of the pair (d, f) for
	 * the particular word; where d is the docID and f is the frequency
	 * of the word in that particular document. 
	 * @param word the word
	 * @param docID the document id
	 * @throws IllegalArgumentException
	 */
	public void removeDocID(String word, int docID) 
	throws IllegalArgumentException {
		ArrayList<Entry<Integer, Integer>> wordDocsList; 
		wordDocsList = map.get(word); 
		System.out.println(word);
		if (wordDocsList == null) 
			throw new IllegalArgumentException("Word " + word + " is not present in system."); 
		Entry<Integer, Integer> searchEntry = new AbstractMap.SimpleEntry<>(docID, null); 
		int docPosIndex = P3Utils.findIndex(wordDocsList, searchEntry);
		if (docPosIndex == -1) 
			throw new IllegalArgumentException("Word " + word + " is not register as part of document " + docID); 
		
		wordDocsList.remove(docPosIndex); 
		if (wordDocsList.isEmpty()) 
			map.remove(word); 
		
		modified = true; 
	}
	
	/**
	 * When the system is about to shutdown, this method needs to be executed to save 
	 * any modifications made to the main index content while in memory.
	 */
	public void close() { 
		// iterate over entries in map and write each one to file
		if (modified) {
			try {
				file = new RandomAccessFile(fPath, "rw");
				file.seek(0);
				// ADD MISSING PART HERE for Exercise 2
				//Now we have to re-write the content of main_index.pp3 after been modified
				//go trough each entry in map
				for(Map.Entry<String, ArrayList<Map.Entry<Integer, Integer>>> e: map.entrySet()){
					//start writing each entry to main_index.pp3
					//first goes the name of the word (key of the entry)
					P3Utils.writeWordToFile(e.getKey(), file);
					
					//then the Doc ID-frequency pairs
				
					
					writeToDocsListToFile(e.getValue());
					/*for(Map.Entry<Integer,Integer> pairs: e.getValue()){
						//write id and frequency in order.
						file.writeInt(pairs.getKey());
						file.writeInt(pairs.getValue());	
					}
					//write end of sequence for this word
					file.writeInt(-1);
					file.writeInt(-1);		
*/				}	
				file.close(); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			}
		}
	}
	private void writeToDocsListToFile(ArrayList<Entry<Integer, Integer>> list) throws IOException {
		for (Entry<Integer, Integer> e : list) { 
			file.writeInt(e.getKey());
			file.writeInt(e.getValue());
		}
		file.writeInt(-1);     // pair (-1, -1) marks the end of the list....
		file.writeInt(-1);
		
	}
	
}
