package candidate_matching;

import java.util.ArrayList;
import java.util.HashSet;

public class Candidate {
	 
	 public Candidate(String idv)
	 {
		 cid = idv;
	 }
	
	 private final String cid; 
	
	 public double[] features; 
	 public double[] rank_weights = {0.2, 0.4, 0.8, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.3};
	 
	 public static String[] fields = 
		 	{
		 		 "CANDIDATEID",
		 		 "FUNCTIONALTITLE",
				 "WILLINGTORELOCATE",
				 "WILLINGTORELOCATEPREFERENCES",
				 "WILLINGTOTRAVEL",
				 "WILLINGTOTRAVELLOCATIONS",
				 "SKILLSANDLANGUAGES",
				 "DEGREETYPEANDPROGRAM",
				 "CERTIFICATIONS",
				 "YEARSOFEXPERIENCE",
				 "PRIMARYROLE",
				 "OFFICESTATE",
				 "CUSTOMATTRIBUTE1",
				 "CUSTOMATTRIBUTE2",
				 "CUSTOMATTRIBUTE3"
		    };
	 
	  private ArrayList<String> raw_data;
	  
	  volatile public double eval = 0.0; 
	  
	  public static Candidate importCandidate(ArrayList<String> lines)
	  {
		  if(lines.size() != fields.length)
			 return null;

		 String id = lines.get(0).trim();		 
				  		  
		 Candidate cand = new Candidate(id);			 
	     cand.parseFields(lines);
		  return cand;		
	  }
	  
	  public void parseFields(ArrayList<String> lines)
	  {
		  features = new double[fields.length-1];	
		  this.raw_data  = lines;
		  
		  features[11] = std2feature(lines.get(12).trim());
		  features[12] = std2feature(lines.get(13).trim());
		  features[13] = std2feature(lines.get(14).trim());
	  }
	  
	  //for each requisition, there's a feature generation process for each candidate
	  public double evalCandidate(Requisition req)
	  {
		  if(features == null)
		  {
			  System.out.println("Candidate not initialized!!! ");
			  return -1.0;
		  }
		  
		  //cut-off business logic comes first: e.g. custom attribute not in range; travel condition can't meet 
		  //in candidate file, only the first two custom atttributes have value
		  for(int i=0; i< 2; i++)
		  {
			  if(features[i+11] < req.min_attr[i] || features[i+11] > req.max_attr[i])
			  {
				  return 0.0;
			  }
		  }
		  
		  if(req.travel - features[3] >= 0.5)
		  {
			  return 0.0;
		  }
		  
		  features[0] = title2feature(raw_data.get(0).trim(), req);
		  features[1] = relo2feature(raw_data.get(1).trim(), req);
		  features[2] = relopref2feature(raw_data.get(2).trim(), req);
		  features[3] = travel2feature(raw_data.get(3).trim(), req);
		  features[4] = travelloc2feature(raw_data.get(4).trim(), req);
		  features[5] = skill2feature(raw_data.get(5).trim(), req);
		  features[6] = degree2feature(raw_data.get(6).trim(), req);
		  features[7] = cert2feature(raw_data.get(7).trim(), req);
		  features[8] = std2feature(raw_data.get(8).trim())/20.0;
		  features[9] = role2feature(raw_data.get(9).trim(), req);
		  features[10] = state2feature(raw_data.get(10).trim(), req);
		  
		  double conf = 0.0;
		  for(int i=0; i< features.length-3; i++)
		  {
			  conf += features[i]*rank_weights[i];
		  }
		  
		  eval = conf;
		  return conf;
	  }
	  
	  public static double title2feature(String instr, Requisition req)
	  {
		  double fv = 0.0;
		  String[] sp = instr.split("[^a-zA-Z]+");
		  
		  for(String elem: sp)
		  {
			  if(elem.isEmpty())
				  continue;
			  elem = elem.toLowerCase();
			  if(req.title.contains(elem))
				  fv += 0.25;
		  }
		  return Math.min(1.0, fv);
	  }
	  
	  public static double relo2feature(String instr, Requisition req)
	  {
		  return 0.25;
	  }
	  
	  public static double relopref2feature(String instr, Requisition req)
	  {
		  return 1.0;
	  }
	  
	  public static double travel2feature(String instr, Requisition req)
	  {
		  if(req.travel <= 1e-6)
			  return 1.0;
		  
		  String[] sp = instr.split("\\s+");
		  
		  double fv = 0.0;
		  
		  for(String elem: sp)
		  {
			  if(elem.isEmpty())
				  continue;
			  try
			  {
				  double tmpd = Double.parseDouble(elem);
				  fv += tmpd/100.0;
				  break;
			  }
			  catch(Exception ex)
			  {
				  
			  }		  
		  }
		  return Math.min(1.0, fv);
	  }
	  
	  public static double travelloc2feature(String instr, Requisition req)
	  {
		  if(req.travel <= 1e-6)
			  return 1.0;
		  else
		      return 0.5;
	  }
	  
	  public static double skill2feature(String instr, Requisition req)
	  {
		  double fv = 0.0;
		  
		  String[] sp = instr.split(";");		
		  
		  for(String elem: sp)
		  {
			  if(elem.isEmpty())
				  continue;
			  double skillw = 0.0;
			  String[] tmpsp = elem.split(" ");
			  
			  try
			  {
				  
				  skillw = Double.parseDouble(tmpsp[tmpsp.length-2]);
			  }
			  catch(Exception e)
			  {
				  
			  }
			  
			  for(int i=0; i< tmpsp.length-3; i++)
			  {
				  String subelem = tmpsp[i].trim().toLowerCase();
				  boolean skillflag = false;
				  
				  if(req.qualif.containsKey(subelem))
					  skillflag = true;
				  else if(Utils.getInstance().getSynonyms().containsKey(subelem))
				  {
					  HashSet<String> tmpsynonym = Utils.getInstance().getSynonyms().get(subelem);
					  for(String tmpword: tmpsynonym)
					  {
						  if(req.qualif.containsKey(tmpword))
						  {
							  skillflag = true;
							  subelem = tmpword;
							  break;
						  }
					  }
				  }
				  
				  if(skillflag)
					  fv += (skillw+1.0)/req.qualif.get(subelem)/10.0;
			  }
		  }			  
	      return Math.min(1.0, fv);
	  }
	  
	  public static double degree2feature(String instr, Requisition req)
	  {
		  double candw = 0.2;
		  
		  if(instr.contains("master"))
		      candw = 0.8;
		  else if(instr.contains("Bachelor") || instr.contains("BS") || instr.contains("BA"))
			  candw = 0.5;
		  
		  return Math.min(1.0, 2*(candw-req.qualif.get("degree")));
	  }
	  
	  public static double cert2feature(String instr, Requisition req)
	  {
		  double certf = 0.0;
		  String[] sp = instr.split("[^a-zA-Z]");
		  
		  for(String elem: sp)
		  {
			  if(!elem.isEmpty())
				  continue;
			  elem = elem.toLowerCase();
			  
			  if(req.qualif.containsKey(elem))
			  {
				  certf += req.qualif.get("elem")/8.0;
			  }
		  }
		  return Math.min(1.0, certf);
	  }
	  
	  public static double role2feature(String instr, Requisition req)
	  {
		  double fv = 0.0;
		  String[] sp = instr.split("[^a-zA-Z]+");
		  
		  for(String elem: sp)
		  {
			  if(elem.isEmpty())
				  continue;
			  elem = elem.toLowerCase();
			  if(req.title.contains(elem))
				  fv += 0.25;
		  }
		  
		  return Math.min(1.0, fv);
	  }
	  
	  public static double state2feature(String instr, Requisition req)
	  {
		  if(instr.equalsIgnoreCase(req.state))
			  return 1.0;
		  else
			  return 0.0;
	  }
	  
	  public static double std2feature(String instr)
	  {
		  try
		  {
			  return Double.parseDouble(instr);
		  }
		  catch(Exception e)
		  {
			  System.out.println("Warning: the input candidate string "+instr+" can't be converted to a standard feature! "+instr);
			  return 0.0;
		  }
	  }
	  
	  public String getId()
	  {
		  return cid;
	  }
	  
	  @Override
	  public String toString()
	  {
		  if(features == null)
		  {
			  return "null";
		  }
		  
		  StringBuilder sb = new StringBuilder();
		  for(int i=0; i< features.length-1; i++)
		  {
			  sb.append(features[i]+",");
		  }
		  sb.append(features[features.length-1]);
		  
		  return sb.toString();
	  }
	  
	  //used for learning-to-rank
	  public String toFeatureString(Requisition req)
	  {
		  StringBuilder sb = new StringBuilder();
		  
		  sb.append(this.evalCandidate(req)+" ");
		  for(int i=0; i< features.length-3; i++)
		  {
			  sb.append(i+":"+rank_weights[i]*features[i]);
			  if(i < features.length-1)
				  sb.append(" ");
		  }
		  return sb.toString();
	  }

}
