package textMining;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BioTextParser {
	ArrayList uidList=new ArrayList();
	public BioTextParser(String htmlString){
		Pattern ptn1=Pattern.compile("(.*?)\n");
		Pattern ptn2=Pattern.compile(".*VIEW FULL ARTICLE:.*");
		Pattern ptn3=Pattern.compile(".*href=\"http:\\/\\/www\\.pubmedcentral\\.nih\\.gov\\/articlerender\\.fcgi\\?tool=pubmed&pubmedid=(\\d+)\" target.*");
//		Pattern ptn3=Pattern.compile(".*articlerender\\.fcgi\\?tool=pubmed&pubmedid=(\\d+).*");

		Matcher mtch1=ptn1.matcher(htmlString);
		Boolean willMeetAnotherUid=false;
		while(mtch1.find()){
			String oneLineString=mtch1.group(1).trim();
			Matcher mtch2=ptn2.matcher(oneLineString);
			Matcher mtch3=ptn3.matcher(oneLineString);
			if(mtch2.matches()){
				willMeetAnotherUid=true;
//				System.out.println("see view full article");
				continue;
			}
//			if(willMeetAnotherUid){
//				System.out.println(oneLineString);
//			}
			if(mtch3.matches() && willMeetAnotherUid){
//				System.out.println("mtch3 matches");
				String uid=mtch3.group(1);
				uidList.add(uid);
				willMeetAnotherUid=false;
			}
		}
	}
	
	public ArrayList getUids(){
		return uidList;
	}
	
	public int getDocCount(){
		return uidList.size();
	}
	
}
