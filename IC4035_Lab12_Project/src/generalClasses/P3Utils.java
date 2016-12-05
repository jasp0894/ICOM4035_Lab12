package generalClasses;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map.Entry;

import dataManagement.MatchingSearchDocument;

public class P3Utils {
	public static final int MAXFILENAMELENGTH = 20; 
	public static final File IndexDirectoryPath = new File("p340354020data", "index"); 
	public static final File DocsDirectoryPath = new File("p340354020data", "docs"); 
	
	
	
	private static boolean validName(String name) { 
		
		if (name == null || name.length()==0)
			return false; 
		
		char cc = name.charAt(0); 
		
		if (!Character.isLetter(cc) && cc != '_')
			return false; 
		
		boolean validSoFar = true; 

		for (int i=1; validSoFar && i < name.length(); i++) { 
			cc = name.charAt(i); 
			validSoFar = Character.isLetter(cc) || cc == '_' || 
					Character.isDigit(cc); 
		}
		return validSoFar; 
	}
	
	private static boolean validFileName(String name) { 
		if (name.length() > MAXFILENAMELENGTH)
			return false; 
		return validName(name); 
	}
	
	/**
	 * Validates the name given for a document. If such name is valid, and also
	 * the file exists in the docs directory, then the corresponding File object
	 * is returned. If not, an exception is thrown. 
	 * @param fName the name of the document
	 * @return the File object that corresponds to the document's content. 
	 * @throws IllegalArgumentException if name is not valid or if file
	 * does not exist. 
	 */
	public static File validateDocumentFile(String fName) 
	throws IllegalArgumentException { 
		if (!validFileName(fName)) 
			throw new IllegalArgumentException("Invalid file name:" + fName); 
		File fPath = new File(DocsDirectoryPath, fName);
		if (!fPath.exists())
			throw new IllegalArgumentException("No such file" + fPath.getAbsolutePath()); 

		return fPath; 
	}
	
	@SuppressWarnings("unchecked")
	public static <E> int findIndex(ArrayList<E> list, E e) { 
		ArrayList<Entry<Integer,Integer>> l = (ArrayList<Entry<Integer,Integer>>) list;
		Entry<Integer,Integer> e2 = (Entry<Integer,Integer>) e;
		Integer docID = e2.getKey();
		for(int i=0; i<l.size(); i++){
			if (l.get(i).getKey().equals(docID))
				return i; 
		}
		
		return -1; 
	}
	
	public static void writeWordToFile(String word, RandomAccessFile file) 
			throws IOException 
	{
		for (int i=0; i<word.length(); i++)
			file.writeByte((byte) word.charAt(i)); 
		file.writeByte((byte) ' ');
	}

	/**
	 * Read next word from current file pointer in file. 
	 * @param file the random access file corresponding to the main index
	 * @return the next word
	 * @throws IOException
	 */
	public static String readWord(RandomAccessFile file) throws IOException {
		String word = ""; 
		char ch = (char) file.readByte(); 
		for (int i=1; ch != ' '; i++) { 
			word += ch; 
			ch = (char) file.readByte(); 
		}
		return word; 
	}

	/**
	 * To compute the rank of the given matching search document. The idea is to rank the document, such that it
	 * can be compared to other documents' ranks
	 * @param msd document.
	 * @param listSize size of the list of words to search entered by the user. 
	 * @return The rank of the document. It is a number [0:2]
	 */
	public static float computeRank(MatchingSearchDocument msd, int listSize){
	
		float r = msd.getMatchingWords().size();
		float n= listSize;
		float pd = r/n;
		float rd = 0;
		
		for (String word : msd.getMatchingWords()) 
			//divide the result by the total number of words in original doc
			rd += (r*(float)msd.getMatchingWordOccurrences(word))/((float)msd.getOriginalDocLenght());
					
		float rank = pd+rd;
		msd.setRank(rank);
		
		return rank;
	}

}
