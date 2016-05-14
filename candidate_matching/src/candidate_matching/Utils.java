package candidate_matching;

import java.util.HashSet;
import java.io.FileReader;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;

public class Utils {
	
	public static final String stopwords_filename = "stopwords.txt";
	public static final String majorwords_filename = "majorwords.txt";
	public static final String commonwords_filename = "commonwords.txt";
	public static final String synonyms_filename = "synonyms.txt";
	
	private static HashSet<String> stopwords = null;
	private static HashSet<String> majorwords = null;
	private static HashSet<String> commonwords = null;
	private static HashMap<String, HashSet<String>> synonyms = null;
	
	private static Utils instance = null;
	
	private Utils()
	{
		stopwords = initSpecialWords(stopwords_filename);
		majorwords = initSpecialWords(majorwords_filename);
		commonwords = initSpecialWords(commonwords_filename);
		synonyms = initSynonyms(synonyms_filename);
	}
	
	public static Utils getInstance()
	{
		if(instance == null)
			instance = new Utils();
		return instance;
	}
	
	public HashMap<String, HashSet<String>> getSynonyms()
	{
		return synonyms;
	}
	
	public HashSet<String> getStopWords()
	{
		return stopwords;
	}
	
	public HashSet<String> getMajorWords()
	{
		return majorwords;
	}
	
	public HashSet<String> getCommonWords()
	{
		return commonwords;
	}
	
	public HashSet<String> initSpecialWords(String fn)
	{
		HashSet<String> target = new HashSet<String>();
		FileReader fr = null;
		BufferedReader br = null;		 
		
		try
		{
			fr = new FileReader(new File(fn));
			br = new BufferedReader(fr);
			
			String line = null;
			while((line = br.readLine()) != null)
			{
				target.add(line.trim().toLowerCase());
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(br != null)
			{
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fr != null)
			{
				try {
					fr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return target;
	}
	
	public HashMap<String, HashSet<String>> initSynonyms(String fn)
	{
		HashMap<String, HashSet<String>> target = new HashMap<String, HashSet<String>>();
		FileReader fr = null;
		BufferedReader br = null;		 
		
		try
		{
			fr = new FileReader(new File(fn));
			br = new BufferedReader(fr);
			
			String line = null;
			while((line = br.readLine()) != null)
			{
				String[] sp = line.trim().toLowerCase().split(",");
				for(int i=0; i< sp.length; i++)
				{
					if(!target.containsKey(sp[i]))
						target.put(sp[i], new HashSet<String>());
					for(int j=0; j < sp.length; j++)
					{
						if(i ==j) continue;
						target.get(sp[i]).add(sp[j]);
					}
				}
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(br != null)
			{
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fr != null)
			{
				try {
					fr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return target;
	}

}
