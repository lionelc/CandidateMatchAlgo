package candidate_matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Requisition {
	
	 public Requisition(String id)
	 {
		 rid = id;
	 }
	
	 private String rid;
	 
	 public static String[] fields = 
		 {	
			"POSITIONID",
			"TITLE",
			"POSITIONDESC",
			"REMOTEDELIVERY",
			"BASICQUALIFICATIONS",
			"PRIMARYROLE",
			"OFFICESTATE",
			"CUSTOMATTRIBUTE1",
			"CUSTOMATTRIBUTE2",
			"CUSTOMATTRIBUTE3",
			"CUSTOMATTRIBUTE1TOLERANCE",
			"CUSTOMATTRIBUTE2TOLERANCE",
			"CUSTOMATTRIBUTE3TOLERANCE" 
		 };
	 
	 public String getId()
	 {
		 return rid;
	 }
	 
	 public ArrayList<String> title;
	 public HashMap<String, Double> qualif;
	 public HashSet<String> desc; 
	 
	 public HashSet<String> role;
	 
	 public String state;
	 
	 public double travel = 0.0;
	 
	 public double[] min_attr = null;
	 public double[] max_attr = null;
	 
	 public static Requisition importRequisition(ArrayList<String> lines)
	 {
		 if(lines.size() != fields.length)
			 return null;

		 String id = lines.get(0).trim();
		 
		 Requisition req = new Requisition(id);		 
		 req.parseFields(lines);
		 return req;
	 }
	 
	 public void parseFields(ArrayList<String> lines)
	 {
		 if(lines.size() != fields.length)
			 return;

		 title = parseTitle(lines.get(1).trim());
		 desc = parseDesc(lines.get(2).trim());
		 
		 travel = parseTravel(lines.get(3).trim());
		 qualif = parseQualif(lines.get(4).trim());
		 		
		 role = parseRole(lines.get(5).trim());
		 state = lines.get(6).trim().toUpperCase();
		 
		 min_attr = new double[3];
		 max_attr = new double[3];
		 
		if(!this.parseCustomAttribute(lines))
		{
			System.out.println("Custom attributes info incompelte.");
		}
	 }
	 
	 public static ArrayList<String> parseTitle(String line)
	 {
		 String[] sp = line.split("\\s+");
		 
		 ArrayList<String> tmptitle = new ArrayList<String>();
		 
		 for(String elem: sp)
		 {
			 elem = elem.toLowerCase();
			 if(!elem.isEmpty())
				 tmptitle.add(elem);
		 }
		 return tmptitle;
	 }
	 
	 //basic qualifications full weight
	 //additional qualifications weight divided by 2
	 public static HashMap<String, Double> parseQualif(String line)
	 {
		 HashMap<String, Double> m = new HashMap<String, Double>();
		 
		 String basstr = "";
		 String addstr = "";
		 
		 m.put("clearance", 0.0);
		 
		 if(line.contains("Basic Qualification"))
		 {
			 basstr = line.substring(line.indexOf("Basic Qualification")+19+3);
		 }
		 else
		 {
			 basstr = line;
		 }
		 
		 if(basstr.contains("Additional Qualification"))
		 {
			 int tmpind = basstr.indexOf("Additional Qualification");
			 addstr = basstr.substring(tmpind+24+3);
			 basstr = basstr.substring(0, tmpind);
		 }
		 
		 if(addstr.contains("Clearance"))
		 {
			 addstr = addstr.substring(0,  addstr.indexOf("Clearance"));
			 m.put("clearance", 1.0);
		 }
		 
		 m.put("degree", 0.0);
		 
		 for(String subreq: basstr.split("-"))
		 {
			 if(subreq.contains("degree"))
			 {
				 String[] subdq = subreq.split("[^A-Za-z]+");
		
				 double degw = 0.2;
				 boolean majorreq = false;
		
				 for(String elem: subdq)
				 {
					 elem = elem.toLowerCase();
					 if(!Utils.getInstance().getStopWords().contains(elem))
					 {
						 if(elem.equals("school") || elem.equals("hs"))
						 {
							 degw = 0.0;
						 }
						 else if(elem.equals("ba") || elem.equals("bs"))
						 {
							 degw = Math.max(degw, 0.5);
						 }
						 else if(elem.equals("ma") || elem.equals("ms"))
						 {
							 degw += 0.2;
						 }
						 
						 if(Utils.getInstance().getMajorWords().contains(elem))
						 {
							 majorreq = true;
						 }
					 }
				 }
				 
				 if(majorreq)
					 degw += 0.15;
				 
				 m.put("degree", degw);
				 continue;
			 }
			 
			 int subind = 0;
			 String[] subsq = subreq.split("[^A-Za-z0-9]+");
			 double wt = 1.0;
			 while(subind < subsq.length)
			 {
				 subsq[subind] = subsq[subind].toLowerCase();
				 try
				 {
					 double tmpw = Double.parseDouble(subsq[subind]);
					 wt *= tmpw;
				 }
				 catch(Exception ex)
				 {
					 if(!Utils.getInstance().getStopWords().contains(subsq[subind]) && !Utils.getInstance().getCommonWords().contains(subsq[subind]))
					 {
						 m.put(subsq[subind], wt);
					 }
				 }				 
				 subind++;
			 }
		 }
		 
		 return m;
	 }
	 
	 public static HashSet<String> parseDesc(String line)
	 {
		 line = line.substring(line.indexOf(":")+1);		 
		 String[] sp = line.split("[^A-Za-z]+");
		 
		 HashSet<String> res = new HashSet<String>();
		 
		 for(String elem: sp)
		 {
			if(elem.length() > 1 && !Utils.getInstance().getStopWords().contains(elem) && !Utils.getInstance().getCommonWords().contains(elem))
				res.add(elem.toLowerCase());
		 }
		 
		 return res;
	 }
	 
	 public static HashSet<String> parseRole(String line)
	 {
		 line = line.substring(line.indexOf(":")+1);		 
		 String[] sp = line.split("[[^A-Za-z]+]");
		 
		 HashSet<String> res = new HashSet<String>();
		 
		 for(String elem: sp)
		 {
		    if(elem.length() > 1 && !Utils.getInstance().getStopWords().contains(elem) && !Utils.getInstance().getCommonWords().contains(elem))
			    res.add(elem.toLowerCase());
		 }
		 
		 return res;
	 }
	 
	 public static double parseTravel(String line)
	 {
		 try
		 {
			 return Double.parseDouble(line)/100.0;
		 }
		 catch(Exception ex)
		 {
			 return 0.0;
		 }
	 }
	 
	 public boolean parseCustomAttribute(ArrayList<String> lines)
	 {
		 //it would be hard condition from the last few attributes 
		 try
		 {
			 double[] base = new double[3];
			 
			 for(int i=7; i<=9; i++)
				 base[i-7] = Double.parseDouble(lines.get(7));
			 
			 for(int i=0; i< 3; i++)
			 {
				 String[] sp = lines.get(i+10).split("\\s+");
				 int ind =0;
				 double tmpplus = 0.0;
				 double tmpminus = 0.0;				 
				 while(ind < sp.length)
				 {
					 if(sp[ind].toLowerCase().equals("plus"))
					 {
						 if(sp[ind+1].toLowerCase().equals("or")) //percentage
						 {
							 double perc = Double.parseDouble(sp[ind+3].substring(0, sp[ind+3].length()-1));
							 tmpplus = perc/100.0*base[i];
							 tmpminus = tmpplus;
						 }
						 else
						 {
							 tmpplus = Double.parseDouble(sp[ind+1]);
							 tmpminus = Double.parseDouble(sp[ind+4]);
						 }
						 break;
					 }
					 ind++;
				 }
				 
				 if(Math.abs(tmpplus) <= 0.01 || Math.abs(tmpminus) <= 0.01)
					 System.out.println("No custom attribute tolerance provided: "+this.rid);
				 
				 min_attr[i] = base[i]-tmpminus;
				 max_attr[i] = base[i]+tmpplus;
			 }
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 return false;
		 }
		 return true;
	 }
	 
	 @Override
	 public String toString()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append("title: "+title+"\n");
		 sb.append("desc: "+desc+"\n");
		 sb.append("travel: "+travel+"\n");
		 sb.append("qualif: "+qualif+"\n");
		 sb.append("role: "+role+"\n");
		 sb.append("state: "+state+"\n");
		 
		 if(min_attr != null && max_attr != null)
		 {
			 sb.append("min_attr: ");
			 for(int i=0; i< 3; i++)
				 sb.append(min_attr[i]+" ");
			 sb.append("\nmax_attr: ");
			 for(int i=0; i< 3; i++)
				 sb.append(max_attr[i]+" ");
		 }	 
		 return sb.toString();
	 }
	
}
