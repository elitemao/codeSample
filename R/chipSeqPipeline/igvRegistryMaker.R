library(foreach)
library(XML)

args<-commandArgs(TRUE)

registryFilePathToSave<-args[1]
#registryFilePathToSave<-"/wrk/hlee/projects/gonghong/wrk_10022015-1_reRun20141212ForWigTdfCeas/registryIgv.xml"

chipExpDesignFilePath<-args[2]
#chipExpDesignFilePath<-"/wrk/hlee/projects/gonghong/wrk_10022015-1_reRun20141212ForWigTdfCeas/design_wrk10022015-1.csv"

globalName<-sub(".*/","",chipExpDesignFilePath,perl=TRUE)
globalName<-sub("\\..*$","",globalName,perl=TRUE)

designTable<-read.csv(chipExpDesignFilePath,sep=",",header=TRUE,comment.char = c("#"))

infoLinkStringSuffix<-sub("/wrk/hlee/projects/","",registryFilePathToSave,perl=T)
infoLinkStringSuffix<-sub("/registryIgv.xml","",infoLinkStringSuffix)

print(infoLinkStringSuffix)

igv=newXMLNode("Global",attrs=c(infolink=eval(paste("http://86.50.168.202:8080/igvDataServer/",infoLinkStringSuffix,sep="")),version="1"))
addAttributes(igv,name=eval(globalName))

#to avoid duplicated entry in the xml file
samplePool<-c()

for(i in 1:nrow(designTable)){
  parentNode<-eval(igv)
  #wrkDir_partial<-sub("/wrk/hlee/projects/gonghong/","",designTable[i,"workingDir"])
#  wrkDir_partial<-"kjrewkrwl/wrk_xxxxx/"
  treat_abbre<-toString(designTable[i,"treatDataAbbre"])
#  treat_abbre<-"wip65"
  control_abbre<-toString(designTable[i,"controlDataAbbre"])
#  control_abbre<-"wip7"
 
  #folderHierarchy<-unlist(strsplit(wrkDir_partial,"/"))
  
  #foreach(folderName=folderHierarchy) %do% {
  #  assign(folderName,newXMLNode("Category",attrs=c(name=eval(folderName))))
    
  #  addChildren(parentNode,get(folderName),append=T)
    
  #parentNode<-get(folderName)
  #}
  
  if(!treat_abbre %in% samplePool){
    tdfSourcePath_treat<-paste("http://86.50.168.202:8080/igvDataServer",infoLinkStringSuffix,paste(treat_abbre,"_homerTag.tdf",sep=""),sep="/")
    
    assign(treat_abbre,newXMLNode("Resource",attrs=c(name=eval(paste(treat_abbre,"_homerTag.tdf",sep="")),path=eval(tdfSourcePath_treat))))
    
    addChildren(parentNode,get(treat_abbre),append=T)
    
    samplePool<-append(samplePool,treat_abbre)
  }
  
  if(!control_abbre=="localSignal"){
    if(!control_abbre %in% samplePool){
      tdfSourcePath_control<-paste("http://86.50.168.202:8080/igvDataServer",infoLinkStringSuffix,paste(control_abbre,"_homerTag.tdf",sep=""),sep="/")
      
      assign(control_abbre,newXMLNode("Resource",attrs=c(name=eval(paste(control_abbre,"_homerTag.tdf",sep="")),path=eval(tdfSourcePath_control))))
      
      addChildren(parentNode,get(control_abbre),append=T)
      
      samplePool<-append(samplePool,control_abbre)
    }
  }
  
  
}
saveXML(igv,file=registryFilePathToSave,indent=T,prefix='<?xml version="1.0" encoding="UTF-8"?>\n')
