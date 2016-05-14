package candidate_matching;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Main {	
	
	public static HashMap<String, Requisition> allreqs = null;
	public static List<Candidate> allcands = null; 	
	
	public static class CompareCandidate implements Comparator<Candidate>
	{
		@Override
		public int compare(Candidate o1, Candidate o2) {
			return new Double(o2.eval).compareTo(o1.eval);
		}		
	}
	
	public static ArrayList<ArrayList<String>> readInputFile(String filename, int fields)
	{
		File f = new File(filename);
		if(!f.isFile())
			return null;
		
		FileReader fr = null;
		BufferedReader br = null;		 
		
		ArrayList<ArrayList<String>> alllines = new ArrayList<ArrayList<String>>();
		
		try
		{
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			
			String line;
			
			line = br.readLine();
			int fieldnum = 0;
	
			ArrayList<String> fieldlines = new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			
			boolean flag = true;
			
			while((line = br.readLine()) != null)
			{
				
				for(int i=0; i< line.length(); i++)
				{
					if(line.charAt(i) == '"')
					{
						flag = !flag;
					}
					
					if(((flag && line.charAt(i) != ',') || !flag) && line.charAt(i) != '"')
					{
						sb.append(line.charAt(i));
					}
					
					if(flag && line.charAt(i) == ',')
					{
						fieldlines.add(sb.toString());
						sb = new StringBuilder();
						fieldnum++;
						
						if(fieldnum == fields-1)
						{
							String tailfield = line.substring(i+1);
							
							if(tailfield.startsWith("\""))
								tailfield = tailfield.substring(1, tailfield.length()-1);
							fieldlines.add(tailfield);
							fieldnum++;
						}
					}
				}
				
				if(flag && fieldnum >= fields)
				{			
					alllines.add(fieldlines);
					sb = new StringBuilder();
					fieldlines = new ArrayList<String>();
					fieldnum = 0;
				}

			}
		}
		catch(Exception ex)
		{
			System.out.println("Error: please check with the input file format. ");
			ex.printStackTrace();
			return null;
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
		
		return alllines;
	}	
	
	public static void main(String[] args)
	{
	
		String talent_filename = "";
		String req_filename = "";
		String qid = "";
		
		int argind = 0;
		
		while(argind < args.length) {
			if(args[argind].equals("-t"))
			{
				allcands = new ArrayList<Candidate>();
				try
				{
					talent_filename = args[++argind];	
					System.out.println("candidate file: "+talent_filename);
					
					ArrayList<ArrayList<String>> candlines = readInputFile(talent_filename, 15);
					
					for(ArrayList<String> elem: candlines)
					{
						Candidate tmpcand = Candidate.importCandidate(elem);
						
						if(tmpcand == null)
						{
							System.out.println("candidate is null!!! "+elem.get(0));
							System.exit(1);
						}
						//System.out.println(tmpcand);
						//Thread.sleep(10000);
						allcands.add(tmpcand);
					}					
				}
				catch(Exception ex)
				{
					System.out.println("Error: Arguments must include a valid talent file.");
				}
			}
			
			if(args[argind].equals("-r"))
			{
				allreqs = new HashMap<String, Requisition>();
				try
				{
					req_filename = args[++argind];	
					
					System.out.println("requisition file: "+req_filename);
					ArrayList<ArrayList<String>> reqlines  = readInputFile(req_filename, 13);
					
					for(ArrayList<String> elem: reqlines)
					{
					    Requisition tepreq = Requisition.importRequisition(elem);					    
					    //System.out.println(tepreq);
					    allreqs.put(elem.get(0), tepreq);
					}					
				}
				catch(Exception ex)
				{
					System.out.println("Error: Arguments must include a valid requisition file.");
					ex.printStackTrace();
					System.exit(1);
				}
			}
			
			if(args[argind].equals("-q"))
			{
				try
				{
					qid = args[++argind].trim();
				}
				catch(Exception ex)
				{
					System.out.println("Error: Arguments must include a requisition id.");
					ex.printStackTrace();
					System.exit(1);
				}
			}
			argind++;
		}
		
		if(!qid.isEmpty())
		{
			if(allreqs == null || !allreqs.containsKey(qid))
			{
				System.out.println("The query ID is not in the input job requisition file!");
				System.exit(1);
			}
			
			Requisition req = allreqs.get(qid);
			for(Candidate cand: allcands)
			{
				System.out.println("Evaluating candidate "+cand.getId()+" : "+cand.evalCandidate(req));
			}
			
			allcands.sort(new CompareCandidate());
			
			System.out.println("rankings:");
			
			for(Candidate cand: allcands)
			{
				System.out.println(cand.getId()+" "+cand.eval);
			}
		}
		
		
	}

}
