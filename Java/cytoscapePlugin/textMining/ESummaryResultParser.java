package textMining;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import eSummaryResultJaxb.DocSum;
import eSummaryResultJaxb.ESummaryResult;
import eSummaryResultJaxb.Item;

public class ESummaryResultParser {
	ESummaryResult eSummaryResult;
	public ESummaryResultParser(String eSummaryResultInXml){
		try{
			System.out.println("the xml received in EutilityResultParser is:\n"+eSummaryResultInXml);
			InputStream is=new ByteArrayInputStream(eSummaryResultInXml.getBytes());

			JAXBContext jc=JAXBContext.newInstance("eSummaryResultJaxb");

			Unmarshaller u=jc.createUnmarshaller();

			eSummaryResult=(ESummaryResult)(u.unmarshal(is));
			
		}catch(JAXBException jaxbex){
			jaxbex.printStackTrace();
		}
	}

	// the returned HashMap contains:"11917006"=>"2002 Apr 1" , "17967175"=>"2007 Oct 29",........
	public HashMap getEPubDate(){
		HashMap ePubDate=new HashMap();
		List docSumList=eSummaryResult.getDocSum();
		
		for(Object eachDocSum:docSumList){
			ArrayList ePubDateList=new ArrayList();
			ArrayList pubDateList=new ArrayList();
			String pmid=((DocSum)eachDocSum).getId().toString();
			List itemList=((DocSum)eachDocSum).getItem();
			for(Object eachItem:itemList){
				String itemName=((Item)eachItem).getName();
				if(itemName.equalsIgnoreCase("EPubDate")){
					ePubDateList=(ArrayList)((Item)eachItem).getContent();
				}
				if(itemName.equalsIgnoreCase("PubDate")){
					pubDateList=(ArrayList)((Item)eachItem).getContent();
				}

			}

			if(!ePubDateList.isEmpty()){
				ePubDate.put(pmid, ePubDateList.get(0));
			}else if(!pubDateList.isEmpty()){
				ePubDate.put(pmid, pubDateList.get(0));
			}
		}
		
		
		return ePubDate;
	}
	
}
