#version 2.2 uses macs2.Changes are:1.-w and -S are abandoned. 2. -mfold xx yy(space to seperate the number). 3. peak file extension is .narrowPeak

library("foreach")

args<-commandArgs(TRUE)
#args<-c("1","1","A","dataAsPosCtl","/wrk/hlee/projects/gonghong/wrk_15052015_weiSong/","/wrk/hlee/projects/gonghong/received/20140912/","/wrk/hlee/projects/gonghong/wrk_15052015_weiSong/","CTCF_RPA_VR_101_26.fastq.gz","IgG_rabbit_rabbit_IgG_VR_95_24.fastq.gz","CTCF_RPA","IgGRabbit_95_24","human","19","factor","10","30","300","1e-05","D","0","1","1000","0","QBPACM","HH")

fastaMakerRScript<-"/wrk/hlee/code_workspace/r_workspace/common/GetFlankingSeqPeakSummitBatch2.R"

fastaMakerForPeakRegionRScript<-"/wrk/hlee/code_workspace/r_workspace/common/GetSeqPeakRegionBatch.R"

memeChipRScript<-"/wrk/hlee/code_workspace/r_workspace/common/memeChipBatchForOneMacsResult2.R"

memeRScript<-"/wrk/hlee/code_workspace/r_workspace/common/memeBatchForOneMacsResult.R"

#homerTagToTdfSbatchScript<-"/wrk/hlee/code_workspace/r_workspace/common/homerTagToTdfSbatch.sh"

#homerTagToTdfWigSbatchScript<-"/wrk/hlee/code_workspace//r_workspace/common/homerTagToTdfWigSbatch.sh"

ceasSbatchScript<-"/wrk/hlee/code_workspace/r_workspace/common/ceasSbatch.sh"

macsPrefixMaker<-function(treatDataAbbre_,controlDataAbbre_,mfoldLow_,mfoldHigh_,bandwidth_,tagSize_,pValue_){
  macsPrefix<-paste(treatDataAbbre_,"Vs",controlDataAbbre_,"_mfold",mfoldLow_,"_",mfoldHigh_,"_bandw",bandwidth_,"_tag",tagSize_,"pV",pValue_,"_macs",sep="")
  return(macsPrefix)
}

homerPrefixMaker<-function(treatDataAbbre_,contrlDataAbbre_,mode_){
  homerPrefix<-paste(treatDataAbbre_,"Vs",contrlDataAbbre_,"_",mode_,sep="")
  return(homerPrefix)
}

createTopFlankBedFilesForSummitFile<-function(macsPeakSummitFolder_,macsPeakBed_,macsSummitBed_,topNumberOptStr_,flankLengOptStr_,folderForNewBeds_){
  
  generatedBedFiles<-c()
  
  topNumberOptions<-unlist(strsplit(toString(topNumberOptStr_),"_"))
  
  flankLengOptions<-unlist(strsplit(toString(flankLengOptStr_),"_"))  #strsplit produces a "list", not directly to vector
  
  peakBedTable<-read.table(paste(macsPeakSummitFolder_,macsPeakBed_,sep="/"))
  
  summitBedTable<-read.table(paste(macsPeakSummitFolder_,macsSummitBed_,sep="/"))
  
  #MACS provides p value in negative log, so sort it by decreasing number
  summitBedTable.ordered<-summitBedTable[order(peakBedTable[,"V5"],decreasing=TRUE),]
  
  foreach(topNumber_=topNumberOptions)%do%{
    newTable<-summitBedTable.ordered[1:topNumber_,]
    foreach(flankLeng_=flankLengOptions) %do%{
      if(flankLeng_!=0){   #since this function is for tailoring MACS peak "summit" file, to create a bed file with length 0 around the summit is meaningless.
        newFileName<-paste(sub(".bed","",macsSummitBed_),"_t",topNumber_,"f",flankLeng_,".bed",sep="")
        
        newTable[,"V2"]<-newTable[,"V2"]-toString(flankLeng_)
        newTable[,"V3"]<-newTable[,"V3"]+toString(flankLeng_)
        
        write.table(newTable,file=paste(folderForNewBeds_,newFileName,sep="/"),col.names=FALSE,row.names=FALSE,sep="\t")
        generatedBedFiles<-append(generatedBedFiles,newFileName)
      }
    }
    
  }
  return(generatedBedFiles)
}

#sort MACS peak file and take the top N entries for different N number
getTopNPeakMacs<-function(macsPeakFolder_,macsPeakFile_,newMacsPeakFolder_,topNumberOptStr_){
 
  tailoredMacsFileName<-c()
  topNumberOptions<-strsplit(toString(topNumberOptStr_),"_")
  peakBedTable<-read.table(paste(macsPeakFolder_,macsPeakFile_,sep="/"))
  peakBedTable.ordered<-peakBedTable[order(peakBedTable[,"V5"],decreasing=TRUE),]
  
  foreach(topNumberOpt=topNumberOptions)%do%{
    newMacsBedTopXFilePath<-paste(newMacsPeakFolder_,"/",sub(".bed","",macsPeakFile_),"_t",topNumberOpt,".bed",sep="")
    write.table(peakBedTable.ordered[1:topNumberOpt,],newMacsBedTopXFilePath,row.names=FALSE,col.names=FALSE,quote=FALSE,sep="\t")
    tailoredMacsFileName<-append(tailoredMacsFileName,paste(sub(".bed","",macsPeakFile_),"_t",topNumberOpt,".bed",sep=""))
  }
  return(tailoredMacsFileName)
}

homerPeakFileToMacsFormat<-function(homerPeakFileFolder_,homerPeakFile_,saveAsFolder_,saveAsFile_){
 
  peakTableNoHeader<-read.table(paste(homerPeakFileFolder_,"/",homerPeakFile_,sep=""),skip=40,comment.char="",sep="\t")
  peakTableNoHeaderModi<-peakTableNoHeader
  peakTableNoHeaderModi[,5]<-peakTableNoHeaderModi[1]
  peakTableNoHeaderModi[,6]<-sapply(peakTableNoHeaderModi[12],function(x){return(-log10(toString(x)))})
  peakTableNoHeaderModi<-peakTableNoHeaderModi[,-1]
  write.table(peakTableNoHeaderModi[,1:5],paste(saveAsFolder_,saveAsFile_,sep="/"),row.names=FALSE,col.names=FALSE,quote=FALSE,sep="\t")
  return(paste(saveAsFolder_,saveAsFile_,sep="/"))
}

pseudoSummit4HomerPeak<-function(fileFolderHomerPeakMacsFormat_,macsFileName4HomerPeak_,pseudoSummitFolder_,pseudoSummitFileName_){
  
  peakTableHomerInMacs<-read.table(paste(fileFolderHomerPeakMacsFormat_,macsFileName4HomerPeak_,sep="/"),sep="\t")
  pseudoSummitTable<-peakTableHomerInMacs
  pseudoSummitLocation<-round((toString(peakTableHomerInMacs[,2])+toString(peakTableHomerInMacs[,3])+1)/2)
  pseudoSummitTable[,2]<-pseudoSummitLocation-1
  pseudoSummitTable[,3]<-pseudoSummitLocation
  write.table(pseudoSummitTable,paste(pseudoSummitFolder_,pseudoSummitFileName_,sep="/"),row.names=FALSE,col.names=FALSE,quote=FALSE,sep="\t")
  return(paste(pseudoSummitFolder_,pseudoSummitFileName_,sep="/"))
}



#sort Homer peaks file and take the top N rows
#what returned are the file names of tailored peak file
sortCropHomerPeakFile<-function(homerPeakFile_,topNumberOptStr_,homerPeakFilePath_,homerPeakTailoredFolderPath_,mode_,hasControl_){
  
  topNumberOptions<-strsplit(toString(topNumberOptStr_),"_")
 
  #now topNumberOptions is a list
  
  #unlist topNumberOptions
  topNumberOptions<-unlist(topNumberOptions)
  print(paste("line134",topNumberOptions))
  
  allDiffTopNoPeakFile<-c()
  
  #try to get the header lines, order the data table and put the header lines back.
  
  peakTableNoHeader<-read.table(paste(homerPeakFilePath_,"/",homerPeakFile_,sep=""),comment.char="#",sep="\t")
  
  #get header lines. The number 60 is not exactly the number of headlines. These 60 lines will be filtered later.
  headerLines<-readLines(file(homerPeakFile_,"r"),n=60)
  
  #get colunm name
  #colNames<-as.data.frame.array(read.table(homerPeakFile_,skip=39,nrows=1,comment.char="",sep="\t")) #colNames here is a data frame
  
  #colNames[1]<-gsub("#","",colNames[1])
  
  #names(peakTable)<-colNames
  
  #order the data matrix 
  #the homer peak p-value is in the form of 3.24e-100, not negative log!!!!
  if(mode_=="super"){
    #in the super-enhancer homer peak output, there is no p-value provided. Use "Total Tags(normalized to Control Exepriment)" as substitute
    peakTableNoHeader.ordered<-peakTableNoHeader[order(peakTableNoHeader[,"V9"]),]
  }else if(mode_=="factor"){
    
    peakTableNoHeader.ordered<-peakTableNoHeader[order(peakTableNoHeader[,"V12"]),]
  }else if(mode_=="histone"){
    if(hasControl_==F){
      #"V8" should be "findPeaks Score"
      peakTableNoHeader.ordered<-peakTableNoHeader[order(peakTableNoHeader[,"V8"]),]
    }else{
      peakTableNoHeader.ordered<-peakTableNoHeader[order(peakTableNoHeader[,"V12"]),]
    }
    
  }
  
  #create files for different top number
  for(i in 1:length(topNumberOptions)){
    #some homer peak file don't have enough data entry(not as many as the topNumber)
    peakTable.ordered.partial<-peakTableNoHeader.ordered[1:min(topNumberOptions[i],nrow(peakTableNoHeader.ordered)),]
    
    peak.ordered.partial.fileName<-paste(homerPeakFile_,"T",topNumberOptions[i],sep="")
    
    sink(paste(homerPeakTailoredFolderPath_,"/",peak.ordered.partial.fileName,sep=""))
    
    for(q in 1:length(headerLines)){
      if(grepl("^#",headerLines[q])==T){
        cat(headerLines[q])
        cat("\n")
      }
    }
    
    for(s in 1:min(topNumberOptions[i],nrow(peakTableNoHeader.ordered))){
      #when a matrix being writen to a text file, the data from "each grid" needs to be handled "individually" to transform to string(use toString()).
      for(o in 1:ncol(peakTableNoHeader.ordered)){
        cat(toString(peakTableNoHeader.ordered[s,o]))
        if(o!=15){
          cat("\t")
        }
      }
      
      #can't do the string transformation by the whole row
      #cat(paste(toString(peakTableNoHeader.ordered[s,]),sep="\t"))
      
      cat("\n")
    }
    
    sink()
    
    #modi_table<-rbind(peakTableHeader,peakTable.ordered.partial)
    
    #write.table(peakTable.ordered.partial,peak.ordered.partial.fileName,col.names=F,row.names=F)
    
    allDiffTopNoPeakFile<-append(allDiffTopNoPeakFile,peak.ordered.partial.fileName)
  }
  
  return(allDiffTopNoPeakFile)
}



checkBedFormatCompliance<-function(bedFilePath){
  #check if the chr column of the peak .bed file is something like "chr1"
  bedFileTop3Row<-read.table(bedFilePath,nrows=3,sep="\t")
  bedFileTop3Chr<-bedFileTop3Row[,1]
  
  matchResult<-grep("^chr",bedFileTop3Chr,ignore.case=TRUE,perl=TRUE)
  
  #if the chr column in the bed file is not something like "chrXX", then add "chr" in front of the digit
  if(sum(matchResult)==0){
    bedFileTable<-read.table(bedFilePath,sep="\t")
    bedFileTable[,1]<-sapply(bedFileTable[,1],function(j){ if(sum(grep("^\\d+$",toString(j),perl=TRUE))==1){return(paste("chr",toString(j),sep=""))}else if(sum(grep("^[XY]{1}$",toString(j),perl=TRUE))==1){return(paste("chr",toString(j),sep=""))}else{return(toString(j))}})
  }
  
  write.table(bedFileTable,bedFilePath,row.names=FALSE,col.names=FALSE,sep="\t")
}

designMatrix<-data.frame(execute=args[1],designId=args[2],macsSettingNo=args[3],expName=args[4],workingDir=args[5],chipSeqRawDataFolder=args[6],whereSamResult=args[7],
                         treat=args[8],control=args[9],treatDataAbbre=args[10],controlDataAbbre=args[11],organism=args[12],genomeBuildVersion=args[13],
                         target=args[14],mFold_low=args[15],mFold_high=args[16],bandwidth=args[17],pValue=args[18],tagSize=args[19],
                         genomeSize=args[20],memChipModeCode=args[21],topNumberOptStr=args[22],flankLengOptStr=args[23],analysisDepth=args[24],
                         programPairing=args[25])

i=1

if(!file.exists(paste(gsub("/$","",designMatrix[i,"workingDir"]),"logSingleChipSeqSbatch",sep="/"))){
  system(paste("mkdir",paste(gsub("/$","",designMatrix[i,"workingDir"]),"logSingleChipSeqSbatch",sep="/"),sep=" "))
}
   
logFileName<-paste(gsub("/$","",designMatrix[i,"workingDir"]),"logSingleChipSeqSbatch",paste(designMatrix[i,"designId"],".txt",sep=""),sep="/")

print(designMatrix)
designMatrix<-data.frame(designMatrix,stringsAsFactors=F)

bashScriptBookPath<-paste(designMatrix[i,"workingDir"],paste(designMatrix[i,c(8:23)],sep="_"),sep="/")

date<-as.character(date())
system(paste("echo",date,">>",logFileName,sep=" "))
cat(date)

cat("\n")
cat("\n")
if(designMatrix[i,"organism"]=="mouse"){
  
  genomeAbbre<-"mm"
  
  if(designMatrix[i,"genomeBuildVersion"]=="9"){
    refSeqFileForCeas<-"/wrk/hlee/data/mm9.refGene"
    genomeAbbreBeta<-"mm9"
  }else if(designMatrix[i,"genomeBuildVersion"]!="9"){
    print("Erik's Ceas and BETA only support mm9 for annotation.Now using mm9.")
    break
    #refSeqFileForCeas<-"/wrk/hlee/data/mm9.refGene"
    #genomeAbbreBeta<-"mm9"
  }
}else if(designMatrix[i,"organism"]=="human"){
  
  genomeAbbre<-"hs"
  
  if(designMatrix[i,"genomeBuildVersion"]=="19"){
    refSeqFileForCeas<-"/wrk/hlee/data/hg19.refGene"
    genomeAbbreBeta<-"hg19"
  }else if(designMatrix[i,"genomeBuildVersion"]!="19"){
    print("Erik's Ceas and BETA only support hg19 for annotation.Now using hg19.")
    break
    #refSeqFileForCeas<-"/wrk/hlee/data/hg19.refGene"
    #genomeAbbreBeta<-"hg19"
  }
}

# do MACS for the experimental design
print(toString(designMatrix$analysisDepth[i]))

if(sum(grep("P",toString(designMatrix$analysisDepth[i])))==1 && sum(grep("^X",c(toString(designMatrix$programPairing[i])),perl=TRUE))!=0){   #for one treatment and control experiment
  
  
  
  
  workingDir<-toString(designMatrix[i,"workingDir"])
  
  setwd(workingDir)
  
 
  
  whereSamResult<-toString(designMatrix[i,"whereSamResult"])
  mfoldLow<-toString(designMatrix[i,"mFold_low"])
  mfoldHigh<-toString(designMatrix[i,"mFold_high"])
  pValue<-toString(designMatrix[i,"pValue"])
  bandwidth<-toString(designMatrix[i,"bandwidth"])
  tagSize<-toString(designMatrix[i,"tagSize"])
  genomeSize<-toString(designMatrix[i,"genomeSize"])
  
  #run MACS to get the peak
  controlSamPath<-paste(whereSamResult,"/samResult/",gsub("\\.gz$","",designMatrix[i,"control"]),".sam",sep="")
  treatSamPath<-paste(whereSamResult,"/samResult/",gsub("\\.gz$","",designMatrix[i,"treat"]),".sam",sep="")
  
  macsPrefix<-macsPrefixMaker(designMatrix[i,"treatDataAbbre"],designMatrix[i,"controlDataAbbre"],mfoldLow,mfoldHigh,bandwidth,tagSize,pValue)
  
  macsComing<-paste(macsPrefix,"_peaks.narrowPeak",sep="") #narrowPeak is the output in macs2
  
  if(!file.exists(macsComing)){ #avoid replicate the work
    if(genomeSize=="0"){
      if(tagSize!="D"){
        #system(paste("macs14 -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -s ",tagSize," -m ",mfoldLow,",",mfoldHigh," -p ",pValue," -S -B -g ",genomeAbbre,sep=""))  ##genome size has to be put in the result file name##-B and -w option don't go along with each other
        #comd<-paste("macs14 -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -s ",tagSize," -m ",mfoldLow,",",mfoldHigh," -p ",pValue," -S -w -g ",genomeAbbre,sep="")
        comd<-paste("macs2 callpeak -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -s ",tagSize," -m ",mfoldLow," ",mfoldHigh," -p ",pValue," -g ",genomeAbbre,sep="")
        system(comd)  ##genome size has to be put in the result file name##if wig output is wanted, use this line.
        
        system(paste("echo",comd,">>",logFileName,sep=" "))
        cat(comd)
        cat("\n")
        cat("\n")
        
      }else if(tagSize=="D"){
        #system(paste("macs14 -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -m ",mfoldLow,",",mfoldHigh," -p ",pValue," -S -B -g ",genomeAbbre,sep=""))  ##genome size has to be put in the result file name##-B and -w option don't go along with each other
        
        #comd<-paste("macs14 -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -m ",mfoldLow,",",mfoldHigh," -p ",pValue," -S -w -g ",genomeAbbre,sep="")
        
        comd<-paste("macs2 callpeak -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -m ",mfoldLow," ",mfoldHigh," -p ",pValue," -g ",genomeAbbre,sep="")
        system(comd)  ##genome size has to be put in the result file name##if wig output is wanted, use this line.
        system(paste("echo",comd,">>",logFileName,sep=" "))
        cat(comd)
        cat("\n")
        cat("\n")
      }
    }else{
      macsPrefix<-paste(macsPrefix,"_gsCustom",sep="")
      if(tagSize!="D"){
        #system(paste("macs14 -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -s ",tagSize," -m ",mfoldLow,",",mfoldHigh," -p ",pValue," -S -B -g ",genomeSize,sep=""))   ##genome size has to be put in the file name
        #comd<-paste("macs14 -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -s ",tagSize," -m ",mfoldLow,",",mfoldHigh," -p ",pValue," -S -w -g ",genomeSize,sep="")
        
        comd<-paste("macs2 callpeak -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -s ",tagSize," -m ",mfoldLow," ",mfoldHigh," -p ",pValue," -g ",genomeSize,sep="")
        
        system(comd)  ##genome size has to be put in the result file name##if wig output is wanted, use this line.
        system(paste("echo",comd,">>",logFileName,sep=" "))
        cat(comd)
        cat("\n")
        cat("\n")
        
      }else if(tagSize=="D"){
        #system(paste("macs14 -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -m ",mfoldLow,",",mfoldHigh," -p ",pValue," -S -B -g ",genomeSize,sep=""))   ##genome size has to be put in the file name
        #comd<-paste("macs14 -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -m ",mfoldLow,",",mfoldHigh," -p ",pValue," -S -w -g ",genomeSize,sep="")
        
        comd<-paste("macs2 callpeak -t ",treatSamPath," -c ",controlSamPath," -n ",macsPrefix," --bw=",bandwidth," -m ",mfoldLow," ",mfoldHigh," -p ",pValue," -g ",genomeSize,sep="")
        
        system(comd)
        system(paste("echo",comd,">>",logFileName,sep=" "))
        cat(comd)
        cat("\n")
        cat("\n")
      }
      
    }
  }
  
  #checkBedFormatCompliance(paste(workingDir,macsComing,sep="/"),paste(workingDir,"/",macsComing,"_"))
  
 
  #annotate macs peak file by beta
  betaOutput<-paste("BetaOutput_",macsPrefix,sep="")
  if(!file.exists(betaOutput)){  #avoid duplicate the work   
    comd<-paste("BETA minus -p",macsComing,"--bl -g",genomeAbbreBeta,"-n",betaOutput,"-o",betaOutput,sep=" ")
    system(comd)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    cat(comd)
    cat("\n")
    cat("\n")
  }
  
}

#do ceas for MACS-generated peak
if(sum(grep("C",toString(designMatrix$analysisDepth[i])))==1 && sum(grep("^X",c(toString(designMatrix$programPairing[i])),perl=TRUE))!=0){   #for one treatment and control experiment
  
  workingDir<-toString(designMatrix[i,"workingDir"])
  setwd(workingDir)
  ceasWrkDir<-paste(workingDir,"ceasResult",sep="/")
  mfoldLow<-toString(designMatrix[i,"mFold_low"])
  mfoldHigh<-toString(designMatrix[i,"mFold_high"])
  pValue<-toString(designMatrix[i,"pValue"])
  bandwidth<-toString(designMatrix[i,"bandwidth"])
  tagSize<-toString(designMatrix[i,"tagSize"])
  
  macsPrefix<-macsPrefixMaker(designMatrix[i,"treatDataAbbre"],designMatrix[i,"controlDataAbbre"],mfoldLow,mfoldHigh,bandwidth,tagSize,pValue)
  
  if(!file.exists(ceasWrkDir)){
    system("mkdir ceasResult")
  }
  
  
  #annotate the macs peak file by ceas
  ceasPrefix<-paste(macsPrefix,"_ceas",sep="")
  ceasComing<-paste(ceasWrkDir,"/",ceasPrefix,".pdf",sep="")
  
  if(!file.exists(ceasComing)){  #avoid duplicate the work
    #unzip wig file
    #macsResultPath<-paste(workingDir,"/",macsPrefix,"_peaks.bed",sep="")
    
    macsResultPath<-paste(workingDir,"/",macsPrefix,"_peaks.narrowPeak",sep="")#narrowPeak is the output in macs2
    
    zippedWigFileLocation<-paste(workingDir,"/",macsPrefix,"_MACS_wiggle/treat/",macsPrefix,"_treat_afterfiting_all.wig.gz",sep="")
    unzippedWigFileLocation<-paste(workingDir,"/",macsPrefix,"_MACS_wiggle/treat/",macsPrefix,"_treat_afterfiting_all.wig",sep="")
    
    #if wig is in zip form, unzip it.
    if(!file.exists(unzippedWigFileLocation)){
      comd<-paste("gunzip ",zippedWigFileLocation,sep="")
      system(comd)
      system(paste("echo",comd,">>",logFileName,sep=" "))
      cat(comd)
      cat("\n")
      cat("\n")
    }
    
    comd<-paste("sbatch",ceasSbatchScript,ceasWrkDir,ceasPrefix,refSeqFileForCeas,macsResultPath,unzippedWigFileLocation,sep=" ")
    system(paste("echo",comd,">>",logFileName,sep=" "))
    retur<-system(comd,intern=T)
    system(paste("echo",retur,">>",logFileName,sep=" "))
    cat(retur)
    cat("\n")
    cat("\n")
  }
}

#use homer to call peaks. Assum the bam and homer tag are from the same folder(set by whereSamResult). 
# do peak calling for one experimental designs
#Then do peak annotation by Hommer annotatePeaks.pl
if(sum(grep("P",toString(designMatrix$analysisDepth[i])))==1 && sum(grep("^H",c(toString(designMatrix$programPairing[i])),perl=TRUE))!=0){
  
  
  
  workingDir<-toString(designMatrix[i,"workingDir"])
  
  setwd(workingDir)
  
  whereSamResult<-toString(designMatrix[i,"whereSamResult"])
  
  mode<-toString(designMatrix[i,"target"])
  
  treatDataAbbre<-toString(designMatrix[i,"treatDataAbbre"])
    
  controlDataAbbre<-toString(designMatrix[i,"controlDataAbbre"])
  
  if(designMatrix[i,"organism"]=="mouse"){
    
    if(designMatrix[i,"genomeBuildVersion"]=="9"){
      
      genomeBuildAbbre<-"mm9"
    }else if(designMatrix[i,"genomeBuildVersion"]!="9"){
      print("Homer annotation only supports mm9.Now using mm9.")
      
      genomeBuildAbbre<-"mm9"
    }
  }else if(designMatrix[i,"organism"]=="human"){
    
    
    if(designMatrix[i,"genomeBuildVersion"]=="19"){
      
      genomeBuildAbbre<-"hg19"
    }else if(designMatrix[i,"genomeBuildVersion"]!="19"){
      print("Homer annotation only supports hg19.Now using hg19.")
      
      genomeBuildAbbre<-"hg19"
    }
  }
  
  treatTagDir<-paste(whereSamResult,"/homerTag/",treatDataAbbre,"_homerTag",sep="")
  controlTagDir<-paste(whereSamResult,"/homerTag/",controlDataAbbre,"_homerTag",sep="")
  
  homerPrefix<-homerPrefixMaker(treatDataAbbre,controlDataAbbre,mode)
  homerPeakOutput<-paste(homerPrefix,"_homerPeak",sep="")
  
  #these variables will be used if mode="super"
  homerPrefixTypicalE<-homerPrefixMaker(treatDataAbbre,controlDataAbbre,"typical")
  homerPrefixYoungStyle<-homerPrefixMaker(treatDataAbbre,controlDataAbbre,"youngStyle")
  homerPeakOutputTypicalE<-paste(homerPrefixTypicalE,"_homerPeak",sep="")
  homerPeakOutputForYoungPlotting<-paste(homerPrefixYoungStyle,"_homerPeak",sep="")
  #macsComing<-paste(macsPrefix,"_peaks.bed",sep="")
  
  #do peak calling by Homer 
  if(!file.exists(paste(workingDir,homerPeakOutput,sep="/"))){ #avoid replicate the work
    if(controlDataAbbre=="localSignal"){  #use local signal as the control signal
      if(mode=="super"){
                
        comdForYoungPlot<-paste("findPeaks",treatTagDir,"-style",mode,"-superSlope -1000","-o",homerPeakOutputForYoungPlotting,sep=" ")
        cat(comdForYoungPlot)
        cat("\n")
        cat("\n")
        system(comdForYoungPlot)
        system(paste("echo",comdForYoungPlot,">>",logFileName,sep=" "))
        comd<-paste("findPeaks",treatTagDir,"-style",mode,"-typical",homerPeakOutputTypicalE,"-o",homerPeakOutput,sep=" ")
      }else{
        comd<-paste("findPeaks",treatTagDir,"-style",mode,"-o",homerPeakOutput,sep=" ")
      }
          
      
    }else{
      if(mode=="super"){
                
        comdForYoungPlot<-paste("findPeaks",treatTagDir,"-i",controlTagDir,"-style",mode,"-superSlope -1000","-o",homerPeakOutputForYoungPlotting,sep=" ")
        cat(comdForYoungPlot)
        cat("\n")
        cat("\n")
        system(comdForYoungPlot)
        system(paste("echo",comdForYoungPlot,">>",logFileName,sep=" "))
        comd<-paste("findPeaks",treatTagDir,"-i",controlTagDir,"-style",mode,"-typical",homerPeakOutputTypicalE,"-o",homerPeakOutput,sep=" ")
      }else{
        #find peaks by homer
        comd<-paste("findPeaks",treatTagDir,"-style",mode,"-o",homerPeakOutput,"-i",controlTagDir,sep=" ")
        
        
      }
    }
    system(comd)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    cat(comd)
    cat("\n")
    cat("\n")
    
    
  }
  
  #homer peak file to bed format
  homerPeakInBed<-paste(homerPeakOutput,".bed",sep="")
  
  if(!file.exists(paste(workingDir,homerPeakInBed,sep="/"))){
    #create bed peak file from homer peak file
    
    comd<-paste("/wrk/hlee/code_workspace/r_workspace/common/pos2bedWrapper.sh",homerPeakOutput,homerPeakInBed,sep=" ")
    #comd<-paste("perl /wrk/hlee/programs/homer/bin/pos2bed.pl",homerPeakOutput,">",homerPeakInBed,sep=" ")
    
    print(comd)
    system(comd)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    cat(comd)
    cat("\n")
    cat("\n")
    
    #if the mode is "super", homer peak file for typical enhancer and young-style graph were also generated. Transform this to bed too.
    if(mode=="super"){
      comd<-paste("pos2bed.pl",homerPeakOutputTypicalE,">",paste(homerPeakOutputTypicalE,".bed",sep=""),sep=" ")
      system(comd)
      system(paste("echo",comd,">>",logFileName,sep=" "))
      cat(comd)
      cat("\n")
      cat("\n")
      comd<-paste("pos2bed.pl",homerPeakOutputForYoungPlotting,">",paste(homerPeakOutputForYoungPlotting,".bed",sep=""),sep=" ")
      system(comd)
      system(paste("echo",comd,">>",logFileName,sep=" "))
      cat(comd)
      cat("\n")
      cat("\n")
    }
  }
  
  
  # do the "basic" annotation (not using tag directory, -hist, -size option)
  #this is also for the "super-enhancer" peaks
  if(!file.exists(paste(workingDir,"homerAnnot",sep="/"))){
    system("mkdir homerAnnot")
  }
  
  homerAnnotFileName<-paste(homerPrefix,"_homerAnnot",sep="")
  
  if(!file.exists(paste(workingDir,"homerAnnot",homerAnnotFileName,sep="/"))){
    comd<-paste("/wrk/hlee/code_workspace//r_workspace/common/homerAnnotatePeaksWrapper.sh",homerPeakOutput,genomeBuildAbbre,paste("homerAnnot",homerAnnotFileName,sep="/"),sep=" ")
    system(comd)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    cat(comd)
    cat("\n")
    cat("\n")
  }
  
  #also annotate "typical" enhancer and the peak file used to plot young-style plot
  if(mode=="super"){
    #annotate typical enhancer
    homerAnnotTypicalEFileName<-paste(homerPrefixTypicalE,"_homerAnnot",sep="")
    if(!file.exists(paste(workingDir,"homerAnnot",homerAnnotTypicalEFileName,sep="/"))){
      comd<-paste("annotatePeaks.pl",homerPeakOutputTypicalE,genomeBuildAbbre,">",paste("homerAnnot",homerAnnotTypicalEFileName,sep="/"),sep=" ")
      system(comd)
      system(paste("echo",comd,">>",logFileName,sep=" "))
      cat(comd)
      cat("\n")
      cat("\n")
    }
    #annotate the peak file which is used to plot young-style plot
    annotatedPeakFileForYoungPlot<-paste(homerPrefixYoungStyle,"_homerAnnot",sep="")
    if(!file.exists(paste(workingDir,"homerAnnot",annotatedPeakFileForYoungPlot,sep="/"))){
      comd<-paste("annotatePeaks.pl",homerPeakOutputForYoungPlotting,genomeBuildAbbre,">",paste("homerAnnot",annotatedPeakFileForYoungPlot,sep="/"),sep=" ")
      system(comd)
      system(paste("echo",comd,">>",logFileName,sep=" "))
      cat(comd)
      cat("\n")
      cat("\n")
    }
    
  }
  
  
  
    
  #########################################################################################
  #the following code is for generating heat map, histogram, and visualization(with -size,-hist parameters). They should be moved to a seperate code.#
  #########################################################################################
  #peakNeighborTagAnnot<-paste(homerPrefix,"_homerHeatmap",sep="")
  #peakNeighborTagAnnotPath<-paste(workingDir,"/",peakNeighborTagAnnot,sep="")
  
 
  #if(!file.exists(peakNeighborTagAnnotPath)){
  #  comd<-paste("annotatePeaks.pl",homerPeakOutput,genomeBuildAbbre,"-size given -hist 25 -d",treatTagDir,controlTagDir,">",peakNeighborTagAnnotPath,sep=" ")
  #  system(comd)
  #  cat(comd)
  #  cat("\n")
  #}
  
  #if(!file.exists(paste(workingDir,"/",peakNeighborTagAnnot,"_cdt.cdt",sep=""))){ 
    #cluster the peak heatmap data
  #  clusterMethodCode<-2 #0: no clustering;1:uncentered correlation;2:pearson correlation;7:euclidean distance
  #  comd<-paste("cluster -f",peakNeighborTagAnnotPath,"-g",clusterMethodCode,"-u",paste(peakNeighborTagAnnot,"_cdt",sep=""),sep=" ")
  #  system(comd)
  #  cat(comd)
  #  cat("\n")
    
    #generate heatmap
    #system(paste("Rscript","/wrk/hlee/code_workspace//r_workspace/gonghong//heatMapHomer.R",paste(workingDir,"/",peakNeighborTagAnnot,"_cdt.cdt",sep="")))
  #}
}

#do CEAS on the bed format of homer peaks.
if(sum(grep("C",toString(designMatrix$analysisDepth[i])))==1 && sum(grep("^H",c(toString(designMatrix$programPairing[i])),perl=TRUE))!=0){
  
  workingDir<-toString(designMatrix[i,"workingDir"])
  treatDataAbbre<-toString(designMatrix[i,"treatDataAbbre"])
  controlDataAbbre<-toString(designMatrix[i,"controlDataAbbre"])
  mode<-toString(designMatrix[i,"target"])
  homerPrefix<-homerPrefixMaker(treatDataAbbre,controlDataAbbre,mode)
  homerPeakOutput<-paste(homerPrefix,"_homerPeak",sep="")
  
  wigOutput_treat<-paste(workingDir,"homerWigTdf",paste(treatDataAbbre,"_homerTag.wig",sep=""),sep="/")
  
  homerPeakInBedPath<-paste(workingDir,paste(homerPeakOutput,".bed",sep=""),sep="/")
  
  ceasWrkDir<-paste(workingDir,"ceasResult",sep="/")
  
  #these variables will be used if mode="super"
  homerPrefixTypicalE<-homerPrefixMaker(treatDataAbbre,controlDataAbbre,"typical")
  homerPrefixYoungStyle<-homerPrefixMaker(treatDataAbbre,controlDataAbbre,"youngStyle")
  homerPeakOutputTypicalE<-paste(homerPrefixTypicalE,"_homerPeak",sep="")
  homerPeakOutputForYoungPlotting<-paste(homerPrefixYoungStyle,"_homerPeak",sep="")
  
  if(!file.exists(ceasWrkDir)){
    system("mkdir ceasResult")
  }
  
  ceasPrefix<-paste(homerPeakOutput,"_ceas",sep="")
  
  if(!file.exists(paste(ceasWrkDir,"/",ceasPrefix,".pdf",sep=""))){
    
    comd<-paste("sbatch",ceasSbatchScript,ceasWrkDir,ceasPrefix,refSeqFileForCeas,homerPeakInBedPath,wigOutput_treat,sep=" ")
    
    cat("chipSeqSinglepipe2.2.R line 706")
    cat("\n")
    cat(comd)
    cat("\n")
    ret<-system(comd,intern=T)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    system(paste("echo",ret,">>",logFileName,sep=" "))
    cat(ret)
  }
  
  #if the mode is "super", the typical enhancer peak also need to do CEAS
  if(mode=="super"){
    #do CEAS for typical enhancer
    
    ceasPrefixTypicalEFileName<-paste(homerPrefixTypicalE,"_ceas",sep="")
    homerPeakOutputTypicalEInBed<-paste(homerPrefixTypicalE,"_homerPeak.bed",sep="")
    
    
    if(!file.exists(paste(ceasWrkDir,paste(ceasPrefixTypicalEFileName,".pdf",sep=""),sep="/"))){
      comd<-paste("sbatch",ceasSbatchScript,ceasWrkDir,ceasPrefixTypicalEFileName,refSeqFileForCeas,homerPeakOutputTypicalEInBed,wigOutput_treat,sep=" ")
      system(comd)
      system(paste("echo",comd,">>",logFileName,sep=" "))
      cat(comd)
      cat("\n")
      cat("\n")
    }
    
    #do ceas on the peak file which is used to plot young-style plot
    ceasPrefixForYoungStylePeak<-paste(homerPrefixYoungStyle,"_ceas",sep="")
    homerPeakYoungStyleInBed<-paste(homerPrefixYoungStyle,"_homerPeak.bed",sep="")
    ceasFileInPdfForYoungPlot<-paste(ceasPrefixForYoungStylePeak,".pdf",sep="")
    if(!file.exists(paste(ceasWrkDir,ceasFileInPdfForYoungPlot,sep="/"))){
      comd<-paste("sbatch",ceasSbatchScript,ceasWrkDir,ceasPrefixForYoungStylePeak,refSeqFileForCeas,homerPeakYoungStyleInBed,wigOutput_treat,sep=" ")
      system(comd)
      system(paste("echo",comd,">>",logFileName,sep=" "))
      cat(comd)
      cat("\n")
      cat("\n")
    }
    
  }
}

#find motif by homer for the peaks genearated by Homer #abbreviate HH
if(sum(grep("M",toString(designMatrix$analysisDepth[i])))==1 && toString(designMatrix$programPairing[i])== "HH"){   
  
  
  #set working directory to the place where homer peak is
  workingDir<-toString(designMatrix[i,"workingDir"]) 
  
  setwd(workingDir)
  
  if(!file.exists("homerMotif")){
    system("mkdir homerMotif")
  }
  
  topNumberOptStr<-toString(designMatrix[i,"topNumberOptStr"])
  #print(paste("line526",as.character(designMatrix[i,"topNumberOptStr"]),sep=" "))
  flankLengthOptStr<-as.character(designMatrix[i,"flankLengOptStr"])
  #print(paste("line 528",toString(designMatrix[i,"flankLengOptStr"]))
  
  mode<-toString(designMatrix[i,"target"])
  
  controlPara<-toString(designMatrix[i,"control"])
  
  if(controlPara=="NA"){
    hasControl<-F
  }else{
    hasControl<-T
  }
  
  #if only apply strsplit, it will return a list, which can't be used in iteration
  flankLengthOptions<-unlist(strsplit(toString(flankLengthOptStr),"_"))
  print(paste("line 779",flankLengthOptStr))
  
  homerPrefix<-homerPrefixMaker(designMatrix[i,"treatDataAbbre"],designMatrix[i,"controlDataAbbre"],designMatrix[i,"target"])
  
  #get the homer peak file
  homerPeakFile<-paste(homerPrefix,"_homerPeak",sep="")
  #homerPeakFile<-"exp2HomerPeaks"
  
  #create homer motif folder
  homerMotifFolder<-paste(toString(designMatrix[i,"workingDir"]),"/homerMotif/homerMotif_",homerPrefix,sep="")
  
  if(!file.exists(homerMotifFolder)){
    comd<-paste("mkdir ",homerMotifFolder,sep="")
    system(comd)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    cat(comd)
    cat("\n")
    cat("\n")
  }  
  
  
  #tailor the homer peak file according to different top number
  homer.peak.file.diff.top.no<-sortCropHomerPeakFile(homerPeakFile,topNumberOptStr,workingDir,homerMotifFolder,mode,hasControl)
  
  
  #move into homer motif folder.This is where the tailored peak files are.
  setwd(homerMotifFolder)  
  
  if(designMatrix[i,"organism"]=="mouse"){
    if(designMatrix[i,"genomeBuildVersion"]=="10"){
      print("mm10 is so far not supported in Erik's homer-peak-finding script.Use mm9 instead")
      genomeAbbr<-"mm9"
    }else if(designMatrix[i,"genomeBuildVersion"]=="9"){
      genomeAbbr<-"mm9"
    }
  }else if(designMatrix[i,"organism"]=="human"){
    if(designMatrix[i,"genomeBuildVersion"]=="19"){
      genomeAbbr<-"hg19"
    }
    
  }
  print("line 820")
  #for each top number, do motif search according to different flanking length
  #for each top number, also do motif search according to purely the peak region
  foreach(oneTopPeakFile=homer.peak.file.diff.top.no) %do% {
    #oneTopPeakFile<-"lane2_NoIndex_L002_R1_001.fastq.gz.GCCT.fq.gzVslane2_NoIndex_L002_R1_001.fastq.gz.ATTT.fq.gzHomerPeakT200"
    #oneTopPeakFile<-"HOXB13_RPA_VR_3_1.fastq.gzVsIgG_rabbit_rabbit_IgG_VR_11_3.fastq.gz_homerPeakT1000"
    print(paste("line577",oneTopPeakFile))
    foreach(flankLeng=flankLengthOptions)%do% {
      #flankLeng<-"75"
      print(paste("line 581",flankLeng))
      if(flankLeng!=0){ #if flankLeng ==0,it means to do motif search on the "peak region", then it is taken cared by the following code.
        next   #when do motif search for "homer" peak, the flanking length option is meaningless due to the lack of summit info of the peak
        
        #motifOutFolderForTopXFlankY<-paste("homerMotif_",oneTopPeakFile,"_f",flankLeng,sep="")
        
        #if(!file.exists(paste(homerMotifFolder,motifOutFolderForTopXFlankY,sep="/"))){ #avoid duplicate the work
        #  comd<-paste("findMotifsGenome.pl",oneTopPeakFile,genomeAbbr, motifOutFolderForTopXFlankY,"-size",flankLeng,"-p 8",sep=" ")
        
        #  print(comd)
        #  system(comd)
        #  cat(comd)
        #  cat("\n")
        
        
        
      }else if(flankLeng==0){
        #do motif discovery based on purely the peak region 
        motifOutFolderForTopXPeakRegion<-paste("homerMotif_",oneTopPeakFile,"_peakRegion",sep="")
        
        if(!file.exists(paste(homerMotifFolder,motifOutFolderForTopXPeakRegion,sep="/"))){  #avoid replicate the work
          comd<-paste("findMotifsGenome.pl",oneTopPeakFile,genomeAbbr, motifOutFolderForTopXPeakRegion,"-size given","-p 8",sep=" ")
          print(comd)
          system(comd)
          system(paste("echo",comd,">>",logFileName,sep=" "))
          cat(comd)
          cat("\n")
          cat("\n")
          
          #do spamo
          spamoOutFolder<-paste(motifOutFolderForTopXPeakRegion,"spamo_out",sep="/")
          homerMotifResultFile<-paste(motifOutFolderForTopXPeakRegion,"homerMotifs.all.motifs",sep="/")
          margin<-"150"
          comdSpamo<-paste("Rscript /wrk/hlee/code_workspace/r_workspace/common/spamo.R",spamoOutFolder,oneTopPeakFile,homerMotifResultFile,"tf",designMatrix[i,"organism"],designMatrix[i,"genomeBuildVersion"],margin)
          print(comdSpamo)
          system(comdSpamo)
          system(paste("echo",comdSpamo,">>",logFileName,sep=" "))
          cat(comdSpamo)
          cat("\n")
          cat("\n")
        }
      }
    }    
  }
  
}

#for generating fasta file for macs result
if(sum(grep("M",toString(designMatrix$analysisDepth[i])))==1 && sum(grep("^X",c(toString(designMatrix$programPairing[i])),perl=TRUE))!=0){   #for one treatment and control experiment
  
  
  workingDir<-toString(designMatrix[i,"workingDir"]) 
  
  setwd(workingDir) ##working space should be set where peak, summit files are located.
  
  settingNo<-designMatrix[i,"macsSettingNo"]
  
  folderToPutFasta<-paste(toString(designMatrix[i,"workingDir"]),"/memeSuiteMacsSetting",toString(settingNo),sep="")
  
  if(!file.exists(folderToPutFasta)){
    comd<-paste("mkdir ",folderToPutFasta,sep="")
    system(comd)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    cat(comd)
    cat("\n")
    cat("\n")
  }   
  
  mfoldLow<-toString(designMatrix[i,"mFold_low"])
  mfoldHigh<-toString(designMatrix[i,"mFold_high"])
  
  organism<-toString(designMatrix[i,"organism"])
  topNumberOptStr<-toString(designMatrix[i,"topNumberOptStr"])
  flankLengthOptStr<-toString(designMatrix[i,"flankLengthOptStr"])
  genomeBuildVersion<-toString(designMatrix[i,"genomeBuildVersion"])
  pValue<-toString(designMatrix[i,"pValue"])
  bandwidth<-toString(designMatrix[i,"bandwidth"])
  tagSize<-toString(designMatrix[i,"tagSize"])
  genomeSize<-toString(designMatrix[i,"genomeSize"])
  
  macsPrefix<-macsPrefixMaker(designMatrix[i,"treatDataAbbre"],designMatrix[i,"controlDataAbbre"],mfoldLow,mfoldHigh,bandwidth,tagSize,pValue)
  
  #get the flanking seq for different top entries and flanking seq and create the fasta for each option and do meme-chip 
  #macsPeakBed<-paste(macsPrefix,"_peaks.bed",sep="") #for macs14
  
  macsPeakBed<-paste(macsPrefix,"_peaks.narrowPeak",sep="")  #for macs2
  
  macsSummitBed<-paste(macsPrefix,"_summits.bed",sep="")
  macsPeakSummitBedLocation<-workingDir
  #folderToPutFasta<-workingDir
  
  #get the seq for different top entries and flanking length and create the fasta for each option
  comd<-paste("Rscript",fastaMakerRScript,macsPeakBed,macsSummitBed,macsPeakSummitBedLocation,folderToPutFasta,organism,topNumberOptStr,flankLengthOptStr,genomeBuildVersion,sep=" ")
  system(comd)
  system(paste("echo",comd,">>",logFileName,sep=" "))
  cat(comd)
  cat("\n")
  cat("\n")
  
  #get the peak region seq for different top number for one macs result
  comd<-paste("Rscript",fastaMakerForPeakRegionRScript,macsPeakBed,macsPeakSummitBedLocation,folderToPutFasta,organism,topNumberOptStr,genomeBuildVersion,sep=" ")
  system(comd)
  system(paste("echo",comd,">>",logFileName,sep=" "))
  cat(comd)
  cat("\n")
  cat("\n")
}

#do "meme-chip" for MACS peaks of different top number and flanking seq. #abbreviate:XMc
#do "meme-chip" for MACS "peak region" of differnt top number
if(sum(grep("M",toString(designMatrix$analysisDepth[i])))==1 && toString(designMatrix$programPairing[i]) == "XMc"){   #for one treatment and control experiment
  
  
 
  macsSettingNo<-designMatrix[i,"macsSettingNo"]
  
  fastaFileFolder<-paste(toString(designMatrix[i,"workingDir"]),"/memeSuiteMacsSetting",macsSettingNo,sep="")
  
  if(!file.exists(fastaFileFolder)){
    comd<-paste("mkdir ",fastaFileFolder,sep="")
    system(comd)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    cat(comd)
    cat("\n")
    cat("\n")
  } 
  
  setwd(fastaFileFolder)      
  #system(paste("mkdir ",memeWorkingDir,sep=""))
  
  
  
  mfoldLow<-designMatrix[i,"mFold_low"]
  mfoldHigh<-designMatrix[i,"mFold_high"]
  
  topNumberOptStr<-designMatrix[i,"topNumberOptStr"]
  flankLengthOptStr<-designMatrix[i,"flankLengthOptStr"]
  memeChipModeCode<-designMatrix[i,"memeChipModeCode"]  #memeChipModeCode  1:one occurrence per seq  2:zero or one occurrence per seq  3:any number of repetitions
  genomeBuildVersion<-designMatrix[i,"genomeBuildVersion"]
  bandwidth<-designMatrix[i,"bandwidth"]
  tagSize<-designMatrix[i,"tagSize"]
  pValue<-designMatrix[i,"pValue"]
  
  macsPrefix<-macsPrefixMaker(designMatrix[i,"treatDataAbbre"],designMatrix[i,"controlDataAbbre"],mfoldLow,mfoldHigh,bandwidth,tagSize,pValue)
  
  #get the flanking seq for different top entries and flanking seq and create the fasta for each option and do meme-chip 
  #macsPeakBed<-paste(macsPrefix,"_peaks.bed",sep="") #for macs14
  
  macsPeakBed<-paste(macsPrefix,"_peaks.narrowPeak",sep="") #for macs2
  
  #macsSummitBed<-paste(macsPrefix,"_summits.bed",sep="")
  #macsPeakSummitBedLocation<-workingDir
  #folderToPutFasta<-workingDir
  
  #do meme-chip for different number of top entries and flanking seq length  
  #do meme-chip also on the exact peak region
  comd<-paste("sbatch /wrk/hlee/code_workspace/r_workspace/common/memeChipForMacsSbatch.sh",macsPeakBed,fastaFileFolder,memeChipModeCode,topNumberOptStr,flankLengthOptStr,sep=" ")
  cat(comd)
  returnMesg<-system(comd,intern=T)
  cat(returnMesg)
  cat("\n")
  cat("\n")
  system(paste("echo",comd,">>",logFileName,sep=" "))
  #cat(comd)
  #cat("\n")  
}


#do "meme" for MACS peaks of different top number and flanking seq. #Abbreviate XM
#do "meme" for MACS "peak region" of differnt top number
if(sum(grep("M",toString(designMatrix$analysisDepth[i])))==1 && toString(designMatrix$programPairing[i]) == "XM"){  
  
  macsSettingNo<-designMatrix[i,"macsSettingNo"]
  
  fastaFileFolder<-paste(toString(designMatrix[i,"workingDir"]),"/memeSuiteMacsSetting",macsSettingNo,sep="")
  
  if(!file.exists(fastaFileFolder)){
    comd<-paste("mkdir ",fastaFileFolder,sep="")
    system(comd)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    cat(comd)
    cat("\n")
    cat("\n")
  }   
  
  setwd(fastaFileFolder)      
  #system(paste("mkdir ",memeWorkingDir,sep=""))
  
  
  
  mfoldLow<-designMatrix[i,"mFold_low"]
  mfoldHigh<-designMatrix[i,"mFold_high"]
  
  topNumberOptStr<-designMatrix[i,"topNumberOptStr"]
  flankLengthOptStr<-designMatrix[i,"flankLengthOptStr"]
  memeChipModeCode<-designMatrix[i,"memeChipModeCode"]  #memeChipModeCode  1:one occurrence per seq  2:zero or one occurrence per seq  3:any number of repetitions
  #genomeBuildVersion<-designMatrix[i,"genomeBuildVersion"]
  bandwidth<-designMatrix[i,"bandwidth"]
  tagSize<-designMatrix[i,"tagSize"]
  pValue<-designMatrix[i,"pValue"]
  
  macsPrefix<-macsPrefixMaker(designMatrix[i,"treatDataAbbre"],designMatrix[i,"controlDataAbbre"],mfoldLow,mfoldHigh,bandwidth,tagSize,pValue)
  
  #get the flanking seq for different top entries and flanking seq and create the fasta for each option and do meme-chip 
  #macsPeakBed<-paste(macsPrefix,"_peaks.bed",sep="") #for macs14
  
  macsPeakBed<-paste(macsPrefix,"_peaks.narrowPeak",sep="") #for macs2
  
  #macsSummitBed<-paste(macsPrefix,"_summits.bed",sep="")
  #macsPeakSummitBedLocation<-workingDir
  #folderToPutFasta<-workingDir
  
  #do meme for different number of top entries and flanking seq length  
  #do meme also on the exact peak region
  comd<-paste("Rscript",memeRScript,macsPeakBed,fastaFileFolder,memeChipModeCode,topNumberOptStr,flankLengthOptStr,sep=" ")
  system(comd)
  system(paste("echo",comd,">>",logFileName,sep=" "))
  cat(comd)
  cat("\n")
  cat("\n")
}




#do homer-motif for MACS result#abbreviate:XH
if(sum(grep("M",toString(designMatrix$analysisDepth[i])))==1 && toString(designMatrix$programPairing[i])== "XH"){  
  
  #macsSettingNo<-designMatrix[i,"macsSettingNo"]
  
  macsResultFolder<-toString(designMatrix[i,"workingDir"])
  
  #setwd(fastaFileFolder)      
  #system(paste("mkdir ",memeWorkingDir,sep=""))
  
  if(designMatrix[i,"organism"]=="mouse"){
    if(designMatrix[i,"genomeBuildVersion"]=="10"){
      print("mm10 is so far not supported in Erik's homer-peak-finding script.Use mm9 instead")
      genomeAbbr<-"mm9"
    }else if(designMatrix[i,"genomeBuildVersion"]=="9"){
      genomeAbbr<-"mm9"
    }
  }else if(designMatrix[i,"organism"]=="human"){
    if(designMatrix[i,"genomeBuildVersion"]=="19"){
      genomeAbbr<-"hg19"
    }
    
  }
  
  mfoldLow<-designMatrix[i,"mFold_low"]
  mfoldHigh<-designMatrix[i,"mFold_high"]
  
  topNumberOptStr<-toString(designMatrix[i,"topNumberOptStr"])
  flankLengthOptStr<-toString(designMatrix[i,"flankLengthOptStr"])
  flankLengthOpt<-unlist(strsplit(flankLengthOptStr,"_"))
  bandwidth<-toString(designMatrix[i,"bandwidth"])
  tagSize<-toString(designMatrix[i,"tagSize"])
  pValue<-toString(designMatrix[i,"pValue"])
  
  macsPrefix<-macsPrefixMaker(designMatrix[i,"treatDataAbbre"],designMatrix[i,"controlDataAbbre"],mfoldLow,mfoldHigh,bandwidth,tagSize,pValue)
  
  #get the flanking seq for different top entries and flanking seq and create the fasta for each option and do meme-chip 
  macsPeakBed<-paste(macsPrefix,"_peaks.narrowPeak",sep="")
  macsSummitBed<-paste(macsPrefix,"_summits.bed",sep="")
  
  
  
  folderForTailoredBed<-paste(toString(designMatrix[i,"workingDir"]),"/homerMotifMacs",sep="")
  
  if(!file.exists(folderForTailoredBed)){
    comd<-paste("mkdir",folderForTailoredBed,sep=" ")
    system(comd)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    cat(comd)
    cat("\n")
    cat("\n")
  }
  
  setwd(folderForTailoredBed)
  
  #if flanking length option is 0, which means purely the peak region and no flanking sequence is added, do homer motif search 
  if(0 %in% flankLengthOpt){
    #create bed file for the peak region with diff top number 
    macs.files.diff.topN<-getTopNPeakMacs(macsResultFolder,macsPeakBed,folderForTailoredBed,topNumberOptStr)
    
    #do homer-motif on peak region bed of diff top number
    foreach(bedFile=macs.files.diff.topN)%do%{
      homerMotifOutFolderForMacsTopX<-paste("homerMotif_",sub(".bed","",bedFile),sep="")
      if(!file.exists(homerMotifOutFolderForMacsTopX)){  #avoid replicate the work
        comd<-paste("findMotifsGenome.pl",bedFile,genomeAbbr, homerMotifOutFolderForMacsTopX,"-size given","-p 8",sep=" ")
        system(comd)
        system(paste("echo",comd,">>",logFileName,sep=" "))
        cat(comd)
        cat("\n")
        cat("\n")
      }
    }
  }
  
  #create bed file for different flanking length(except 0) and top number of peak summit  
  bedFileNameDiffTopFlank<-createTopFlankBedFilesForSummitFile(macsResultFolder,macsPeakBed,macsSummitBed,topNumberOptStr,flankLengthOptStr,folderForTailoredBed)
  
  #do homer-motif on diff.flanking leng.(except 0) and top number bed  
  foreach(bedFile=bedFileNameDiffTopFlank) %do%{
    homerMotifOutFolderForMacsTopXFlankY<-paste("homerMotif_",sub(".bed","",bedFile),sep="")
    if(!file.exists(paste(folderForTailoredBed,homerMotifOutFolderForMacsTopXFlankY,sep="/"))){ #avoid duplicate the work
      comd<-paste("findMotifsGenome.pl",bedFile,genomeAbbr, homerMotifOutFolderForMacsTopXFlankY,"-size given","-p 8",sep=" ")
      system(comd)
      system(paste("echo",comd,">>",logFileName,sep=" "))
      cat(comd)
      cat("\n")
      cat("\n")
    }
    
  }
  
  #macsPeakSummitBedLocation<-workingDir
  #folderToPutFasta<-workingDir
  ############################################################
  #############################################################
  ##################################################################
  ##something need to be modified from here
  
  #do meme-chip for different number of top entries and flanking seq length  
  #do meme-chip also on the exact peak region
  #system(paste("Rscript",memeChipRScript,macsPeakBed,fastaFileFolder,memeChipModeCode,topNumberOptStr,flankLengthOptStr,sep=" "))
  
}







#do meme-chip on Homer peaks  #abbreviate: HMc
if(sum(grep("M",toString(designMatrix$analysisDepth[i])))==1 && designMatrix$programPairing[i]== "HMc"){   #for one treatment and control experiment
  #generate fasta file

  workingDir<-toString(designMatrix[i,"workingDir"]) 
  
  setwd(workingDir) ##working space should be set where peak, summit files are located.
  
  settingNo<-designMatrix[i,"macsSettingNo"]
  
  folderToPutFasta<-paste(toString(designMatrix[i,"workingDir"]),"/memeSuiteHomerPeak",sep="")
  
  if(!file.exists(folderToPutFasta)){
    comd<-paste("mkdir ",folderToPutFasta,sep="")
    system(comd)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    cat(comd)
    cat("\n")
    cat("\n")
  }   
  
  organism<-toString(designMatrix[i,"organism"])
  topNumberOptStr<-toString(designMatrix[i,"topNumberOptStr"])
  flankLengthOptStr<-toString(designMatrix[i,"flankLengthOptStr"])
  genomeBuildVersion<-toString(designMatrix[i,"genomeBuildVersion"])
  
  
  homerPrefix<-homerPrefixMaker(designMatrix[i,"treatDataAbbre"],designMatrix[i,"controlDataAbbre"],toString(designMatrix[i,"target"]))
  
  #get the flanking seq for different top entries and flanking seq and create the fasta for each option and do meme-chip 
  homerPeakBed<-paste(homerPrefix,"_homerPeak",sep="")
  #transform homer peak file to MACS format
  macsFileName4HomerPeak<-paste(sub(".bed","",homerPeakBed),"_macsFormat.bed",sep="")
  filePathHomerPeakMacsFormat<-homerPeakFileToMacsFormat(workingDir,homerPeakBed,workingDir,macsFileName4HomerPeak)
  
  #create pseudo macs summit file
  pseudoSummitFileName<-paste(sub(".bed","",homerPeakBed),"_summit_macsFormat.bed",sep="")
  pseudoSummit4HomerPeak(workingDir,macsFileName4HomerPeak,workingDir,pseudoSummitFileName)
  
  macsPseudoPeakSummitBedLocation<-workingDir
  #folderToPutFasta<-workingDir
  
  #get the seq for different top entries and flanking length and create the fasta for each option
  comd<-paste("Rscript",fastaMakerRScript,macsFileName4HomerPeak,pseudoSummitFileName,macsPseudoPeakSummitBedLocation,folderToPutFasta,organism,topNumberOptStr,flankLengthOptStr,genomeBuildVersion,sep=" ")
  system(comd)
  cat(comd)
  cat("\n")
  cat("\n")
  #get the peak region seq for different top number for one macs result  
  comd<-paste("Rscript",fastaMakerForPeakRegionRScript,macsFileName4HomerPeak,macsPseudoPeakSummitBedLocation,folderToPutFasta,organism,topNumberOptStr,genomeBuildVersion,sep=" ")
  system(comd)
  system(paste("echo",comd,">>",logFileName,sep=" "))
  cat(comd)
  cat("\n")
  cat("\n")
  
  
  #do meme-chip
  
  fastaFileFolder<-paste(toString(designMatrix[i,"workingDir"]),"/memeSuiteHomerPeak",sep="")
  
  setwd(fastaFileFolder)      
  #system(paste("mkdir ",memeWorkingDir,sep=""))
  
  memeChipModeCode<-designMatrix[i,"memeChipModeCode"]  #memeChipModeCode  1:one occurrence per seq  2:zero or one occurrence per seq  3:any number of repetitions
  
  homerPeakFile<-paste(homerPrefix,"_homerPeak",sep="")
  
  homerPeakFileToMacsFormat(toString(designMatrix[i,"workingDir"]),homerPeakFile,toString(designMatrix[i,"workingDir"]),paste(homerPeakFile,"_macsFormat.bed",sep=""))
  
  
  #get the flanking seq for different top entries and flanking seq and create the fasta for each option and do meme-chip 
  #macsPeakBed<-paste(macsPrefix,"_peaks.bed",sep="")
  #macsSummitBed<-paste(macsPrefix,"_summits.bed",sep="")
  #macsPeakSummitBedLocation<-workingDir
  #folderToPutFasta<-workingDir
  
  #do meme-chip for different number of top entries and flanking seq length  
  #do meme-chip also on the exact peak region
  comd<-paste("Rscript",memeChipRScript,macsFileName4HomerPeak,fastaFileFolder,memeChipModeCode,topNumberOptStr,flankLengthOptStr,sep=" ")
  system(comd)
  system(paste("echo",comd,">>",logFileName,sep=" "))
  cat(comd)
  cat("\n")
  cat("\n")
  
}

#do "meme" on Homer peaks  #abbreviate: HM
#the result folders(prefixed with "memeout_xxxxx")will be saved in "memeSuiteHomerPeak/meme" folder
if(sum(grep("M",toString(designMatrix$analysisDepth[i])))==1 && designMatrix$programPairing[i]== "HM"){   #for one treatment and control experiment
  #generate fasta file
  
  
  workingDir<-toString(designMatrix[i,"workingDir"]) 
  
  setwd(workingDir) ##working space should be set where peak, summit files are located.
  
  settingNo<-designMatrix[i,"macsSettingNo"]
  
  folderToPutFasta<-paste(toString(designMatrix[i,"workingDir"]),"/memeSuiteHomerPeak",sep="")
  
  if(!file.exists(folderToPutFasta)){
    comd<-paste("mkdir ",folderToPutFasta,sep="")
    system(comd)
    system(paste("echo",comd,">>",logFileName,sep=" "))
    cat(comd)
    cat("\n")
    cat("\n")
  }   
  
  organism<-toString(designMatrix[i,"organism"])
  topNumberOptStr<-toString(designMatrix[i,"topNumberOptStr"])
  flankLengthOptStr<-toString(designMatrix[i,"flankLengthOptStr"])
  genomeBuildVersion<-toString(designMatrix[i,"genomeBuildVersion"])
  homerPrefix<-homerPrefixMaker(designMatrix[i,"treatDataAbbre"],designMatrix[i,"controlDataAbbre"],toString(designMatrix[i,"target"]))
  
  #get the flanking seq for different top entries and flanking seq and create the fasta for each option and do meme-chip 
  homerPeakFile<-paste(homerPrefix,"_homerPeak",sep="")
  #transform homer peak file to MACS format
  macsFileName4HomerPeak<-paste(sub(".bed","",homerPeakFile),"_macsFormat.bed",sep="")
  filePathHomerPeakMacsFormat<-homerPeakFileToMacsFormat(workingDir,homerPeakFile,workingDir,macsFileName4HomerPeak)
  
  #create pseudo macs summit file
  pseudoSummitFileName<-paste(sub(".bed","",homerPeakFile),"_summit_macsFormat.bed",sep="")
  pseudoSummit4HomerPeak(workingDir,macsFileName4HomerPeak,workingDir,pseudoSummitFileName)
  
  macsPseudoPeakSummitBedLocation<-workingDir
  #folderToPutFasta<-workingDir
  
  #get the seq for different top entries and flanking length and create the fasta for each option
  comd<-paste("Rscript",fastaMakerRScript,macsFileName4HomerPeak,pseudoSummitFileName,macsPseudoPeakSummitBedLocation,folderToPutFasta,organism,topNumberOptStr,flankLengthOptStr,genomeBuildVersion,sep=" ")
  system(comd)
  system(paste("echo",comd,">>",logFileName,sep=" "))
  cat(comd)
  cat("\n")
  cat("\n")
  #get the peak region seq for different top number for one macs result
  comd<-paste("Rscript",fastaMakerForPeakRegionRScript,macsFileName4HomerPeak,macsPseudoPeakSummitBedLocation,folderToPutFasta,organism,topNumberOptStr,genomeBuildVersion,sep=" ")
  system(comd)
  system(paste("echo",comd,">>",logFileName,sep=" "))
  cat(comd)
  cat("\n")
  cat("\n")
  
  
  #do "meme"
  fastaFileFolder<-paste(toString(designMatrix[i,"workingDir"]),"/memeSuiteHomerPeak",sep="")
  
  #now move to memeSuitHomerPeak folder
  setwd(fastaFileFolder)      
  #system(paste("mkdir ",memeWorkingDir,sep=""))
  
  memeChipModeCode<-designMatrix[i,"memeChipModeCode"]  #memeChipModeCode  1:one occurrence per seq  2:zero or one occurrence per seq  3:any number of repetitions
  
  #homerPeakFile<-paste(designMatrix[i,"treatDataAbbre"],"Vs",designMatrix[i,"controlDataAbbre"],"_homerPeak",sep="")
  
  #homerPeakFileToMacsFormat(toString(designMatrix[i,"workingDir"]),homerPeakFile,toString(designMatrix[i,"workingDir"]),paste(homerPeakFile,"_macsFormat.bed",sep=""))
  
  
  #get the flanking seq for different top entries and flanking seq and create the fasta for each option and do meme-chip 
  #macsPeakBed<-paste(macsPrefix,"_peaks.bed",sep="")
  #macsSummitBed<-paste(macsPrefix,"_summits.bed",sep="")
  #macsPeakSummitBedLocation<-workingDir
  #folderToPutFasta<-workingDir
  
  #do meme-chip for different number of top entries and flanking seq length  
  #do meme-chip also on the exact peak region
  comd<-paste("Rscript",memeRScript,macsFileName4HomerPeak,fastaFileFolder,memeChipModeCode,topNumberOptStr,flankLengthOptStr,sep=" ")
  system(comd)
  system(paste("echo",comd,">>",logFileName,sep=" "))
  cat(comd)
  cat("\n")
  cat("\n")
}
#sink()
