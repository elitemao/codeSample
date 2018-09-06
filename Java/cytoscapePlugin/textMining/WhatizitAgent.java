package textMining;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import whatizitws.client.SelectItem;
import whatizitws.client.Whatizit;
import whatizitws.client.WhatizitException_Exception;
import whatizitws.client.Whatizit_Service;

import com.sun.xml.ws.developer.JAXWSProperties;

public class WhatizitAgent {
	Whatizit whatizit;
	String unTaggedString;
	
	public static void main(String[] args){
		String modifiedString=args[0].replaceAll("\"", "%22");
		System.out.println(modifiedString);
		WhatizitAgent a=new WhatizitAgent(modifiedString);
		
		String annotateByUkPmcChemicals=a.annotationWithUkPmcChemicals();
		System.out.println(annotateByUkPmcChemicals);
	}
	
	public WhatizitAgent(String unTaggedString){
		this.unTaggedString=unTaggedString;
		
		try{
			String computername=InetAddress.getLocalHost().getHostName();
			System.out.println(computername);
			
			if(computername.equalsIgnoreCase("trevally")){
				System.setProperty("http.proxyHost", "www-proxy");
			  	System.setProperty("http.proxyPort", "80");
			}


			// Get the WHATIZIT service end point (always like this) 
			Whatizit_Service service = new Whatizit_Service();    
			whatizit = service.getPipeline();              

			// Mtom and session maintain flag
			BindingProvider bindingProvider = (BindingProvider)whatizit;   
			((SOAPBinding)bindingProvider.getBinding ()).setMTOMEnabled(true);
			Map <String,Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(JAXWSProperties.MTOM_THRESHOLOD_VALUE, new Integer(0));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	    
	    
		
	}
	
	public String annotateWithSwissprot(){
		String taggedString="";
		String pipelineName = "whatizitSwissprot"; 
    	
    	boolean convertToHtml = true; // vs. leave it as plain XML
    	int inputStringLengthForWhatizit=unTaggedString.length();
    	System.out.println("the string length sent to whatizit is:"+inputStringLengthForWhatizit);
    	System.out.println("string sent to whatizit:"+unTaggedString);
    	
    	try{
    		List<SelectItem> pipelineList=whatizit.getPipelinesStatus();
    		for(SelectItem k:pipelineList){
    			System.out.println("pipeline that is alive:"+k.getLabel());
    		}
    		
    		taggedString = whatizit.contact(pipelineName, unTaggedString, convertToHtml);
    	
    	}catch(WhatizitException_Exception wexex){
    		wexex.printStackTrace();
    	}
    	System.out.println(taggedString); 
		
    	return taggedString;
	}

	public String annotationWithUkPmcAll(){
		String taggedString="";
		String pipelineName = "whatizitUkPmcAll"; 
    	
    	boolean convertToHtml = true; // vs. leave it as plain XML
    	
    	try{
    		taggedString = whatizit.contact(pipelineName, unTaggedString, convertToHtml);
    	
    	}catch(WhatizitException_Exception wexex){
    		wexex.printStackTrace();
    	}
    	
    	System.out.println(taggedString); 
		
    	return taggedString;
	}
	
	public String annotationWithChemicals(){
		String taggedString="";
		String pipelineName = "whatizitChemicals"; 
    	
    	boolean convertToHtml = true; // vs. leave it as plain XML
    	
    	try{
    		taggedString = whatizit.contact(pipelineName, unTaggedString, convertToHtml);
    	
    	}catch(WhatizitException_Exception wexex){
    		wexex.printStackTrace();
    	}
    	
    	System.out.println(taggedString); 
		
    	return taggedString;
	}
	
	public String annotationWithUkPmcChemicals(){
		System.out.println("untaggedString:"+unTaggedString+"/"+this.getClass().getName()+" "+new Exception().getStackTrace()[0].getLineNumber());
		String taggedString="";
		String pipelineName = "whatizitUkPmcChemicals"; 
    	System.out.println(this.getClass().getName()+" "+new Exception().getStackTrace()[0].getLineNumber());
    	boolean convertToHtml = true; // vs. leave it as plain XML
    	
    	try{
    		taggedString = whatizit.contact(pipelineName, unTaggedString, convertToHtml);
    	
    	}catch(WhatizitException_Exception wexex){
    		wexex.printStackTrace();
    		return "error in whatizit server";
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return "error in whatizit server";
    	}
    	
    	System.out.println(taggedString); 
		
    	return taggedString;
	}
	
	
	public String annotationWithEbiMed(){
		String taggedString="";
		String pipelineName = "whatizitEbiMed"; 
    	
    	boolean convertToHtml = true; // vs. leave it as plain XML
    	
    	try{
    		taggedString = whatizit.contact(pipelineName, unTaggedString, convertToHtml);
    	
    	}catch(WhatizitException_Exception wexex){
    		wexex.printStackTrace();
    	}
    	
    	System.out.println(taggedString); 
		
    	return taggedString;
	}
	
}
