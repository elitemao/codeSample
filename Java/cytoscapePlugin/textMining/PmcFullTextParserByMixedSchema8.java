package textMining;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import pubMedCentralJaxbMixing2868029_2584013.Abstract;
import pubMedCentralJaxbMixing2868029_2584013.Body;
import pubMedCentralJaxbMixing2868029_2584013.Bold;
import pubMedCentralJaxbMixing2868029_2584013.ExtLink;
import pubMedCentralJaxbMixing2868029_2584013.Fig;
import pubMedCentralJaxbMixing2868029_2584013.NamedContent;
import pubMedCentralJaxbMixing2868029_2584013.P;
import pubMedCentralJaxbMixing2868029_2584013.PmcArticleset;
import pubMedCentralJaxbMixing2868029_2584013.Sec;
import pubMedCentralJaxbMixing2868029_2584013.TableWrap;
import pubMedCentralJaxbMixing2868029_2584013.Title;
import pubMedCentralJaxbMixing2868029_2584013.Xref;

// PmcFullTextParserByMixedSchema----------------
//v0 20110223: use the jaxb generated from pmcMixedSchema2868029_2584013.xsd(package pubMedCentralJaxbMixing2868029_2584013)
//v1 20110223: add parseSec method, so that the recursive parsing of "Sec" is possible
//v2 20110223: add method- getAbstract()
//v3 20110301: add arraylist- processedSection to avoid duplicated processing of the same section(there might be section title, like "Results and Discussion")
//v3 20110301: add "else if(a instanceof Bold){...}" to method parseSec() so that pmid 15571630 can be correctly parsed
//v4 20110302: Extra conditional expression in method "parseSec()" is added due to the pmc article-1995220 
//v5 20110302: create parseP() method to avoid duplicated code
//v6 20110302: create getBodyText() method
//v7 20110413: add one more conditional expression in parseP() method- for the pmcid 2233667
//v7 20110414: deal with the situation in which the structure inside Abstract object is not: Abstract->p->text, like in pmcid 2802977
//v7 20110621: add return in line 95,98,103
//v8 20110704: add getConclusion method

public class PmcFullTextParserByMixedSchema8 {

	private String pmcid;
	private List secList1stLevel;
	private PmcArticleset pmcArticleSet;
	private String articleType;
	private boolean isFullTextAvailable=false;
	private ArrayList processedSection=new ArrayList();
	JAXBContext jc;
	
	public static void main(String[] args){
		String pmcid=args[0];
		EutilityPubMed5 eu3=new EutilityPubMed5();
		String eutilFullText=eu3.getFullTextByPost(pmcid);

		PmcFullTextParserByMixedSchema8 i=new PmcFullTextParserByMixedSchema8(eutilFullText); 
		String abstractString=i.getAbstract();
		String introBackString=i.getIntroductionOrBackground();
		String resultString=i.getResult();
		String discussionString=i.getDiscussion();
		String conclusionString=i.getConclusion();

		System.out.println("AABBBSSSTTTRRAAACCTTT:"+abstractString);
		System.out.println("IIINNTTRROO:"+introBackString);
		System.out.println("RRRRREEEEESSSSEEEULTTTT:"+resultString);
		System.out.println("CCONNNCLUSIONNNNN:"+conclusionString);
		System.out.println("DDIIISSCCCUUSSIION:"+discussionString);

	}
	
	public PmcFullTextParserByMixedSchema8(String pmcFullTextInXml){
		System.out.println(pmcFullTextInXml);
		System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());

		//	String fullTextString=eu3.getFullTextByGet(pmcid);
		try{
			//		InputStream is= new FileInputStream(new File("h:\\ForWork\\growNetwork\\jkjkjkj.txt"));
			System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
			jc=JAXBContext.newInstance("pubMedCentralJaxbMixing2868029_2584013");
			System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
			Unmarshaller u=jc.createUnmarshaller();
			System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
			
			pmcArticleSet=(PmcArticleset)u.unmarshal(new ByteArrayInputStream(pmcFullTextInXml.getBytes("UTF-8")));
			System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
			articleType=pmcArticleSet.getArticle().getArticleType();
			try{
				Body fullTextBody=(Body)pmcArticleSet.getArticle().getBody();
				isFullTextAvailable=true;
				System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
				secList1stLevel=fullTextBody.getSec();
				for(Object x:secList1stLevel){
					List secOrTitleList=((Sec)x).getSecOrTitle();
					for(Object m:secOrTitleList){
						if(m instanceof Title){
							List secTitleContent=((Title)m).getContent();
							for(Object s:secTitleContent){
								System.out.println((String)s);
							}
							System.out.println("ww");
						}
					}
				}
			}catch(Exception ex){
				isFullTextAvailable=false;
				System.out.println("there is no passage body in this reference.It might not be downloaded in XML format.But you can still get the abstract.");

			}




		} catch (JAXBException e) {
			e.printStackTrace();
			System.out.println("xxx");
			return;
		}catch(IOException ioex){
			ioex.printStackTrace();
			System.out.println("ppx");
			return;
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("ooo");
			return;
		}


	}
	
	public boolean isFullTextAvailable(){
		return isFullTextAvailable;
	}
	
	public String getArticleType(){
		return articleType;	
	}
	
	
	public String getBodyText(){  // this method is for review article
		String bodyTextString="";
		
		
		for(Object x:secList1stLevel){

			
			List secOrTitleList2ndLevel=((Sec)x).getSecOrTitle();
			if(!secOrTitleList2ndLevel.isEmpty()){
				for(Object secOrTitleIn2ndLevel:secOrTitleList2ndLevel){
					if(secOrTitleIn2ndLevel instanceof Sec){
						bodyTextString=parseSec((Sec)secOrTitleIn2ndLevel,bodyTextString);
					}
//					else if(secOrTitleIn2ndLevel instanceof Title){
//						List titleContentIn2ndLevel=((Title)secOrTitleIn2ndLevel).getContent();
//						for(Object s:titleContentIn2ndLevel){
////							resultString=resultString+"\n"+(String)s;
//							System.out.println("title of one sec"+(String)s);
//			
//						}
//					}
				}
			}
			
			List pOrFigOrTableResultSec=((Sec)x).getPOrFigOrTableWrap();
			if(!pOrFigOrTableResultSec.isEmpty()){
				for(Object w:pOrFigOrTableResultSec){
					if(w instanceof P){
						bodyTextString=parseP((P)w,bodyTextString);
					}else if(w instanceof Fig){

					}else if(w instanceof TableWrap){

					}
				}
			}
		
		}
		
		return bodyTextString;
	}
	
	public String getAbstract(){
		String abstractString="";
		boolean nonTrivialStructure=false;
		Abstract aabstract=pmcArticleSet.getArticle().getFront().getArticleMeta().getAbstract();
		try{  //some pmc article don't fulfill the structure abstract->p->textContent, like pmcid 2802977.Use try..catch in case the non-trivial situation happens 
			List paragraphContentList=aabstract.getP().getContent();  
			for(Object a:paragraphContentList){
				//			if(p instanceof P){
				//				List oneParagraphOfText=((P)p).getContent();
				//				for(Object a:oneParagraphOfText){

				if(a instanceof String){
					abstractString=abstractString+(String)a;
					//						System.out.print((String)a);
				}else if(a instanceof Xref){
					//								System.out.println(((Xref)a).getContent().get(0));
				}else if(((JAXBElement)a).getName().toString().equalsIgnoreCase("italic")){
					String uu=(String)((JAXBElement)a).getValue();
					abstractString=abstractString+uu;
					//						System.out.println(uu);
				}else if(((JAXBElement)a).getName().toString().equalsIgnoreCase("underline")){
					String uu=(String)((JAXBElement)a).getValue();
					abstractString=abstractString+uu;
					//						System.out.println(uu);
				}else if(((JAXBElement)a).getName().toString().equalsIgnoreCase("sub")){
					String uu=(String)((JAXBElement)a).getValue();
					abstractString=abstractString+uu;
					//						System.out.println(uu);
				}else if(((JAXBElement)a).getName().toString().equalsIgnoreCase("suup")){
					String uu=(String)((JAXBElement)a).getValue();
					abstractString=abstractString+uu;
					//						System.out.println(uu);
				}else {
					System.out.println(a.getClass().getName());
					System.out.println(((JAXBElement)a).getName().toString());
				}
				//				}
				//					abstractString=abstractString+"\n";
				//				System.out.println("");
				//			}
			}
		
		
		}catch(Exception ex){
			nonTrivialStructure=true;
			ex.printStackTrace();
		}
//		StringWriter sw=new StringWriter();
//		ByteArrayOutputStream byteOs=new ByteArrayOutputStream();
//		// if the structure in the Abstract object is not trivial, then get the text content in Abstract object directly.
//		if(nonTrivialStructure==true){
//			try{
//				Marshaller marshaller=jc.createMarshaller();
//				marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
//				marshaller.marshal(aabstract, sw);
//				abstractString=sw.toString();
//				System.out.println("non-trivial abstractString is:"+abstractString);
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
//		}
		return abstractString;
	}
	
	public String getIntroductionOrBackground(){
		Sec introBackSec=null;
		String introBackString="";
		Pattern ptn=Pattern.compile(".*introduction.*",Pattern.CASE_INSENSITIVE);
		Pattern ptn1=Pattern.compile(".*background.*",Pattern.CASE_INSENSITIVE);
		
		boolean introBackSecFound=false;
		
		for(Object x:secList1stLevel){
			List secOrTitleList2ndLevel=((Sec)x).getSecOrTitle();
			for(Object o:secOrTitleList2ndLevel){
				if(o instanceof Title){
					List secTitleContent=((Title)o).getContent();
					for(Object s:secTitleContent){
						
						Matcher mtch=ptn.matcher((String)s);
						Matcher mtch1=ptn1.matcher((String)s);
						if(mtch.matches()|| mtch1.matches()){
							System.out.println("intro or background sec is found");
							for(Object z:processedSection){
								System.out.println("so far the processed section is:"+(String)z);
								if(((String)z).equalsIgnoreCase((String)s)){
									System.out.println((String)s);
									System.out.println("this section has been processed before");
									return "this section has been processed before";
								}
							}
							introBackSecFound=true;
							introBackSec=(Sec)x;
							processedSection.add((String)s);
							System.out.println((String)s+" is added to processedSection");
							break;
						}
					}
				}
			}
		}
		
		if(introBackSecFound==true){
			
			List secOrTitleList2ndLevel=introBackSec.getSecOrTitle();
			if(!secOrTitleList2ndLevel.isEmpty()){
				for(Object secOrTitleIn2ndLevel:secOrTitleList2ndLevel){
					if(secOrTitleIn2ndLevel instanceof Sec){
						introBackString=parseSec((Sec)secOrTitleIn2ndLevel,introBackString);
					}
//					else if(secOrTitleIn2ndLevel instanceof Title){
//						List titleContentIn2ndLevel=((Title)secOrTitleIn2ndLevel).getContent();
//						for(Object s:titleContentIn2ndLevel){
////							introBackString=introBackString+"\n"+(String)s;
//							System.out.println("title of one sec"+(String)s);
//			
//						}
//					}
				}
			}
			
			List pOrFigOrTableResultSec=introBackSec.getPOrFigOrTableWrap();
			if(!pOrFigOrTableResultSec.isEmpty()){
				for(Object w:pOrFigOrTableResultSec){
					if(w instanceof P){
						introBackString=parseP((P)w,introBackString);
					}else if(w instanceof Fig){

					}else if(w instanceof TableWrap){

					}
				}
			}
		}
		return introBackString+"\n";
	}
	
	
	
	public String getResult(){
		Sec resultSec=null;
		String resultString="";
		Pattern ptn=Pattern.compile(".*Result.*",Pattern.CASE_INSENSITIVE);
		boolean resultSecFound=false;
		
		for(Object x:secList1stLevel){
			List secOrTitleList2ndLevel=((Sec)x).getSecOrTitle();
			for(Object o:secOrTitleList2ndLevel){
				if(o instanceof Title){
					List secTitleContent=((Title)o).getContent();
					for(Object s:secTitleContent){
						
						Matcher mtch=ptn.matcher((String)s);
						if(mtch.matches()){
							System.out.println("result sec is found");
							for(Object z:processedSection){
								System.out.println("so far the processed section is:"+(String)z);
								if(((String)z).equalsIgnoreCase((String)s)){
									System.out.println((String)s);
									System.out.println("this section has been processed before");
									return "this section has been processed before";
								}
							}
							resultSecFound=true;
							resultSec=(Sec)x;
							processedSection.add((String)s);
							System.out.println((String)s+" is added to processedSection");
							break;
						}
					}
				}
			}
		}
		
		if(resultSecFound==true){
			
			List secOrTitleList2ndLevel=resultSec.getSecOrTitle();
			if(!secOrTitleList2ndLevel.isEmpty()){
				for(Object secOrTitleIn2ndLevel:secOrTitleList2ndLevel){
					if(secOrTitleIn2ndLevel instanceof Sec){
						resultString=parseSec((Sec)secOrTitleIn2ndLevel,resultString);
					}
//					else if(secOrTitleIn2ndLevel instanceof Title){
//						List titleContentIn2ndLevel=((Title)secOrTitleIn2ndLevel).getContent();
//						for(Object s:titleContentIn2ndLevel){
////							resultString=resultString+"\n"+(String)s;
//							System.out.println("title of one sec"+(String)s);
//			
//						}
//					}
				}
			}
			
			List pOrFigOrTableResultSec=resultSec.getPOrFigOrTableWrap();
			if(!pOrFigOrTableResultSec.isEmpty()){
				for(Object w:pOrFigOrTableResultSec){
					if(w instanceof P){
						resultString=parseP((P)w,resultString);
					}else if(w instanceof Fig){

					}else if(w instanceof TableWrap){

					}
				}
			}
		}
		return resultString+"\n";
	}
	
	public String getConclusion(){
		Sec conclusionSec=null;
		String conclusionString="";
		Pattern ptn=Pattern.compile(".*Conclusion.*",Pattern.CASE_INSENSITIVE);
		boolean conclusionSecFound=false;
		
		for(Object x:secList1stLevel){
			List secOrTitleList2ndLevel=((Sec)x).getSecOrTitle();
			for(Object o:secOrTitleList2ndLevel){
				if(o instanceof Title){
					List secTitleContent=((Title)o).getContent();
					for(Object s:secTitleContent){
						
						Matcher mtch=ptn.matcher((String)s);
						if(mtch.matches()){
							System.out.println("conclusion sec is found");
							for(Object z:processedSection){
								System.out.println("so far the processed section is:"+(String)z);
								if(((String)z).equalsIgnoreCase((String)s)){
									System.out.println((String)s);
									System.out.println("this section has been processed before");
									return "this section has been processed before";
								}
							}
							conclusionSecFound=true;
							conclusionSec=(Sec)x;
							processedSection.add((String)s);
							System.out.println((String)s+" is added to processedSection");
							break;
						}
					}
				}
			}
		}
		
		if(conclusionSecFound==true){
			
			List secOrTitleList2ndLevel=conclusionSec.getSecOrTitle();
			if(!secOrTitleList2ndLevel.isEmpty()){
				for(Object secOrTitleIn2ndLevel:secOrTitleList2ndLevel){
					if(secOrTitleIn2ndLevel instanceof Sec){
						conclusionString=parseSec((Sec)secOrTitleIn2ndLevel,conclusionString);
					}
//					else if(secOrTitleIn2ndLevel instanceof Title){
//						List titleContentIn2ndLevel=((Title)secOrTitleIn2ndLevel).getContent();
//						for(Object s:titleContentIn2ndLevel){
////							resultString=resultString+"\n"+(String)s;
//							System.out.println("title of one sec"+(String)s);
//			
//						}
//					}
				}
			}
			
			List pOrFigOrTableResultSec=conclusionSec.getPOrFigOrTableWrap();
			if(!pOrFigOrTableResultSec.isEmpty()){
				for(Object w:pOrFigOrTableResultSec){
					if(w instanceof P){
						conclusionString=parseP((P)w,conclusionString);
					}else if(w instanceof Fig){

					}else if(w instanceof TableWrap){

					}
				}
			}
		}
		return conclusionString+"\n";
	}
	
	public String getDiscussion(){
		Sec discussionSec=null;
		String discussionString="";
		Pattern ptn=Pattern.compile(".*Discussion.*",Pattern.CASE_INSENSITIVE);
		boolean discussionSecFound=false;
		
		for(Object x:secList1stLevel){
			List secOrTitleList2ndLevel=((Sec)x).getSecOrTitle();
			for(Object o:secOrTitleList2ndLevel){
				if(o instanceof Title){
					List secTitleContent=((Title)o).getContent();
					for(Object s:secTitleContent){
						
						Matcher mtch=ptn.matcher((String)s);
						if(mtch.matches()){
							for(Object z:processedSection){
								System.out.println("so far the processed section is:"+(String)z);
								if(((String)z).equalsIgnoreCase((String)s)){
									
									System.out.println((String)s);
									System.out.println("this section has been processed before");
									return "this section has been processed before";
								}
							}

							System.out.println("discussion sec is found");
							discussionSecFound=true;
							discussionSec=(Sec)x;
							processedSection.add((String)s);
							System.out.println((String)s+" is added to processedSection");
							break;
						}
					}
				}
			}
		}
		
		if(discussionSecFound==true){
			
			List secOrTitleList2ndLevel=discussionSec.getSecOrTitle();
			if(!secOrTitleList2ndLevel.isEmpty()){
				for(Object secOrTitleIn2ndLevel:secOrTitleList2ndLevel){
					if(secOrTitleIn2ndLevel instanceof Sec){
						discussionString=parseSec((Sec)secOrTitleIn2ndLevel,discussionString);
					}
//					else if(secOrTitleIn2ndLevel instanceof Title){
//						List titleContentIn2ndLevel=((Title)secOrTitleIn2ndLevel).getContent();
//						for(Object s:titleContentIn2ndLevel){
////							discussionString=discussionString+"\n"+(String)s;
//							System.out.println("title of one sec"+(String)s);
//			
//						}
//					}
				}
			}
			
			List pOrFigOrTableDiscussionSec=discussionSec.getPOrFigOrTableWrap();
			if(!pOrFigOrTableDiscussionSec.isEmpty()){
				for(Object w:pOrFigOrTableDiscussionSec){
					if(w instanceof P){
						discussionString=parseP((P)w,discussionString);
					}else if(w instanceof Fig){

					}else if(w instanceof TableWrap){

					}
				}
			}
		}
		return discussionString+"\n";
	}
	
	
	public String parseSec(Sec sec, String rawString){
		String returnString=rawString;
		
		List secOrTitleListNextLevel=sec.getSecOrTitle();
		if(!secOrTitleListNextLevel.isEmpty()){
			for(Object secOrTitleInNextLevel:secOrTitleListNextLevel){
				if(secOrTitleInNextLevel instanceof Sec){
					returnString=parseSec((Sec)secOrTitleInNextLevel,returnString);
					List pOrFigOrTableOfNextLevelSec=((Sec)secOrTitleInNextLevel).getPOrFigOrTableWrap();
					for(Object w:pOrFigOrTableOfNextLevelSec){
						if(w instanceof P){
							returnString=parseP((P)w,returnString);
						}else if(w instanceof Fig){
							
						}else if(w instanceof TableWrap){
							
						}
					}
				}else if(secOrTitleInNextLevel instanceof Title){
					List secTitleContentInNextLevel=((Title)secOrTitleInNextLevel).getContent();
					for(Object s:secTitleContentInNextLevel){
						if(s instanceof String){
							returnString=returnString+"\n"+(String)s+"\n"; //add the title to a new line
							System.out.println("title of one sec"+(String)s);
						}else if(((JAXBElement)s).getName().toString().equalsIgnoreCase("italic")){
							String vv=(String)((JAXBElement)s).getValue();
							returnString=returnString+vv;
							System.out.println(vv);
						}else if(((JAXBElement)s).getName().toString().equalsIgnoreCase("underline")){
							String mm=(String)((JAXBElement)s).getValue();
							returnString=returnString+mm;
							System.out.println(mm);
						}else {
							System.out.println(s.getClass().getName());
							System.out.println(((JAXBElement)s).getName().toString());
						}
					}
				}
			}
		}
		
		List pOrFigOrTableResultSec=sec.getPOrFigOrTableWrap();
		if(!pOrFigOrTableResultSec.isEmpty()){
			for(Object w:pOrFigOrTableResultSec){
				if(w instanceof P){
					returnString=parseP((P)w,returnString);
				}else if(w instanceof Fig){

				}else if(w instanceof TableWrap){

				}
			}
		}
		
		
		
		return returnString;
	}
	
	public String parseP(P p, String inputString){
		String returnString=inputString;
		List oneParagraphOfText=((P)p).getContent();
		for(Object a:oneParagraphOfText){

			if(a instanceof String){
				returnString=returnString+(String)a;
				//									System.out.print((String)a);
			}else if(a instanceof Bold){
				List ee=((Bold)a).getContent();
				for(Object e:ee){
//					String oo=(String)((JAXBElement)e).getValue();
					System.out.println(returnString+" e:"+e+"/"+this.getClass().getName());
					if(e instanceof String){  // There are other tags enclosed in <bold> tag, such as <sub></sub>,<sup></sup> in pmc356925
						returnString=returnString+(String)e;
					}
				}
				
			}else if(a instanceof Xref){
				//								System.out.println(((Xref)a).getContent().get(0));
			}else if(a instanceof ExtLink){

			}else if(a instanceof NamedContent){   //this condition is for pmcid 2233667
				List uu=((NamedContent)a).getContent();
				for(Object u:uu){
					returnString=returnString+(String)u;
				}
			}else if(((JAXBElement)a).getName().toString().equalsIgnoreCase("italic")){
				String uu=(String)((JAXBElement)a).getValue();
				returnString=returnString+uu;
				System.out.println(uu);
			}else if(((JAXBElement)a).getName().toString().equalsIgnoreCase("underline")){
				String uu=(String)((JAXBElement)a).getValue();
				returnString=returnString+uu;
				System.out.println(uu);
			}else if(((JAXBElement)a).getName().toString().equalsIgnoreCase("list")){
				Object uu=(Object)((JAXBElement)a).getValue();
				System.out.println("the object under list is:"+uu.getClass().getName());
				returnString=returnString+(String)uu;
				System.out.println(uu);
			}else {
				System.out.println(a.getClass().getName());
				System.out.println(((JAXBElement)a).getName().toString());
			}
		}
		//							System.out.println("");

		return returnString;
		
	}
	
	
	
	
}
