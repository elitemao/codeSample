##use GetFlankingSeqPeakSummitBatch1.r
##also use GetSeqPeakRegionBatch.r
##v1.2 use another bwa index file
##v1.2 do motif search on the "peak region" seq
##v1.3 use GetFlankingSeqPeakSummitBatch2 and meemChipBatchForOneMacsResult2.These two files handles the situation where flanking length=0 and avoid duplicate the work.
#arguments needed. Only can be run in command line mode.
#integrate homer, beta, ceas
##v2.1.7 uses chipSeqSinglePipeBatch2.sh(calling chipSeqSinglePipe2.2.R which uses macs2)

library("foreach")
library("gdata")

#####!!!!!!!#######!!!!!!!######  modify this parameter   !!!!!!!^^^^^^^^^^^^^^^^^^^^^
#expDesignTable<-"/wrk/hlee/projects/Jussi-Pekka/wrk_16062016_hif3a2/design_wrk_16062016_hif3a2.csv"
#expDesignTable="/wrk/hlee/projects/Jussi-Pekka/wrk_25082016_trimmedHif3a2/design_wrk_25082016_trimmedHif3a2.csv"

args<-commandArgs(trailingOnly=TRUE)
expDesignTable<-args[1]

homerTagToTdfWigSbatchScript<-"/wrk/hlee/code_workspace//r_workspace/common/homerTagToTdfWigSbatch3.sh"
chipSeqSinglePipeBatchScript<-"/wrk/hlee/code_workspace/r_workspace/common/chipSeqSinglePipeBatch2.sh"
#chipSeqSinglePipeScript<-"/wrk/hlee/code_workspace/r_workspace/common/chipSeqSinglePipe0.5.R"

bashScriptRecordPath1<-gsub(".csv","_first.screenOut",expDesignTable)
bashScriptRecordPath2<-gsub(".csv","_second.screenOut",expDesignTable)

sink(bashScriptRecordPath1,append=F,split=TRUE)

date<-as.character(date())
cat(date)
cat("\n")

refGenomeIndexMm9<-"/wrk/hlee/data/mm9bwaindx/mm9bwaindx"
refGenomeIndexMm10<-"/wrk/hlee/data/mouseMm10Index/mm10bwaindx"
refGenomeIndexHs19<-"/wrk/hlee/data/hg19GenomeBwa/hg19bwaidx"

refGenomeBowtie2IndexHs19<-"/wrk/hlee/data/hg19Bowtie2/hg19"
refGenomeBowtie2IndexMm10<-"/wrk/hlee/data/mm10Bowtie2/mm10"
refGenomeBowtie2IndexMm9<-"/wrk/hlee/data/mm9Bowtie2/mm9"

fastqcBatchScript<-"/wrk/hlee/code_workspace/r_workspace/common/fastqcSbatch.sh"

bowtie2Script<-"/wrk/hlee/code_workspace/r_workspace/common/bowtie2Batch.sh"

makeTagScript<-"/wrk/hlee/code_workspace/r_workspace/common/makeTagBatch.sh"

checkDesignTableFormat<-function(designTablePath){
  #designTablePath<-expDesignTable
  
  designTable<-read.csv(designTablePath,sep=",",header=TRUE,comment.char = c("#"),strip.white = T)
  for(i in 1:nrow(designTable)){
    if(designTable[i,"execute"]==0){
      next
    }
    
    for(expType in c("treat","control")){
      #expType<-"treat"
      chipRawDataFileName<-toString(designTable[i,expType])
      if(chipRawDataFileName=="NA"){  #this happens when no control is used(use local signal to normalize)
        next
      }
      unzippedRawDataPath<-paste(toString(designTable$chipSeqRawDataFolder[i]),"/",sub("\\.gz$","",chipRawDataFileName),sep="")
      print(unzippedRawDataPath)
      
      ####Bowtie2 requires the file to end with .fastq or .fq
      
      if(sum(grep(".fq$",unzippedRawDataPath))==0 && sum(grep(".fastq$",unzippedRawDataPath))==0 && 
           sum(grep(".fq.gz$",unzippedRawDataPath))==0&& sum(grep(".fastq.gz$",unzippedRawDataPath))==0 
         && sum(grep("^-",unzippedRawDataPath))==0 ){
        print(unzippedRawDataPath)
        
       return(0)
        
      }
    }
    
  }
  return(1)
}

checkSbatchJob<-function(jobName,allJobId){
  if(length(allJobId)!=0){
    Sys.sleep(120)
    
    for(jobId in allJobId){
      print(paste("now checking the state of ",jobName," job ",jobId,sep=""))
      jobFinish<-F
      while(jobFinish==F){
        comd<-paste("sacct","-j",jobId,"-o State",sep=" ")
        statusReply<-system(comd,intern=T)
        cat(comd)
        cat("\n")
        print(paste(jobId," is ",statusReply[3],sep=""))
        
        if(gsub(" ","",statusReply[3])%in%c("PENDING","RUNNING","NA")){   #"COMPLETED" is another option
          print(paste("unfinished ",jobName," process id:",jobId,sep=""))
          Sys.sleep(3) 
          jobFinish<-F
        }else{
          jobFinish<-T
        }
      }
    }
    
  }
  
}

returnValue<-checkDesignTableFormat(expDesignTable)
print(returnValue)
if(returnValue==0){
  #stop("At least one of the input raw data ends with wrong file extension.Should be fastq or fq or fq.gz or fastq.gz")
}


designMatrix<-read.csv(expDesignTable,sep=",",header=TRUE,strip.white = T,comment.char = c("#"))

#designMatrix=trim(designMatrix)

#the unzipped files need to be removed in the end
unzippedFqFiles<-c()

#do FastQC for every fastq file
#FastQC result is under the same folder as sam file
allJobIdFromFastqcSbatch<-c()

fastqcComingFromBatch<-c()

for(i in 1:nrow(designMatrix)){
  #i<-1
  if(designMatrix[i,"execute"]==0){
    next
  }
  
  if(sum(grep("Q",designMatrix$analysisDepth[i]))!=1){    
    next
  } 
  
  workingDir<-toString(designMatrix[i,"workingDir"])
  
  #this is where samResult is located(not including /samResult)
  whereSamResult<-toString(designMatrix[i,"whereSamResult"])
  
  setwd(workingDir)
  
  if(!file.exists(paste(whereSamResult,"fastqc",sep="/"))){
    comd<-paste("mkdir",paste(whereSamResult,"fastqc",sep="/"),sep=" ")
    system(comd)
    cat(comd)
    cat("\n")
  }
  
  
  #system(paste("mkdir","macsResult",sep=" "))
  
  
  
  chipSeqBatchRawDataFolder<-toString(designMatrix$chipSeqRawDataFolder[i])
  
  
  for(expType in c("treat","control")){
    #expType<-"treat"
    chipRawDataFileName<-designMatrix[i,expType]
    
    #check if the raw data file name contains ".fq" or ".fastq".
    #Due to the control file name could be "NA".Skip it.
    if((sum(grep(".fq",chipRawDataFileName,ignore.case=TRUE))==0 && sum(grep(".fastq",chipRawDataFileName,ignore.case=TRUE))==0) | chipRawDataFileName=="NA" ){
      next
    }
    
    fastqcFolder<-paste(whereSamResult,"/fastqc/",designMatrix[i,paste(expType,"DataAbbre",sep="")],sep="")
    
    if(!file.exists(fastqcFolder)){
      #check if the reads file is the unzipped fastq
      unzippedRawDataPath<-paste(chipSeqBatchRawDataFolder,"/",sub("\\.gz$","",chipRawDataFileName),sep="")
            
      if(!file.exists(unzippedRawDataPath)){
        comd<-paste("gunzip -c ",chipSeqBatchRawDataFolder,"/",chipRawDataFileName," > ",unzippedRawDataPath,sep="")
        system(comd)
        unzippedFqFiles<-append(unzippedFqFiles,unzippedRawDataPath)
        cat(comd)
        cat("\n")
        
      }
      
            
      ##avoid to do QC on the same reads file    
      if(!fastqcFolder%in%fastqcComingFromBatch){
        system(paste("mkdir",fastqcFolder,sep=" "))
        
        comd<-paste("sbatch",fastqcBatchScript,unzippedRawDataPath,fastqcFolder,sep=" ")
        consoleReplyFastqc<-system(comd,intern=TRUE)
        cat(comd)
        cat("\n")
        jobIdFastqc<-sub("Submitted batch job ","",consoleReplyFastqc)
        print(paste("fastqcJobId:",jobIdFastqc,sep=""))
        allJobIdFromFastqcSbatch<-append(allJobIdFromFastqcSbatch,jobIdFastqc)
        fastqcComingFromBatch<-append(fastqcComingFromBatch,fastqcFolder)
      }
    }
  }
  
}

#checkSbatchJob("fastqc",allJobIdFromFastqcSbatch)


#align reads to refrence genome by Bowtie2. Using Bowtie2 to generate sam file,
allJobIdFromBowtieSbatch<-c()

samComingFromBatch<-c()

for(i in 1:nrow(designMatrix)){
  #i<-1
  if(designMatrix[i,"execute"]==0){
    next
  }
  
  if(sum(grep("B",designMatrix$analysisDepth[i]))!=1){    
    next
  } 
  
  workingDir<-toString(designMatrix[i,"workingDir"])
  
  #this is where samResult is located(not including /samResult)
  whereSamResult<-toString(designMatrix[i,"whereSamResult"])
  
  setwd(workingDir)
  
  if(!file.exists(paste(whereSamResult,"samResult",sep="/"))){
    comd<-paste("mkdir",paste(whereSamResult,"samResult",sep="/"),sep=" ")
    system(comd)
    cat(comd)
    cat("\n")
  }
  
  
  #system(paste("mkdir","macsResult",sep=" "))
  
  
  
  chipSeqBatchRawDataFolder<-toString(designMatrix$chipSeqRawDataFolder[i])
  
  if(designMatrix[i,"organism"]=="mouse"){
    if(designMatrix[i,"genomeBuildVersion"]==10){
      refGenomeIndexBowtie2<-refGenomeBowtie2IndexMm10
    }else if(designMatrix[i,"genomeBuildVersion"]==9){
      refGenomeIndexBowtie2<-refGenomeBowtie2IndexMm9
    }
  }else if(designMatrix[i,"organism"]=="human"){
    if(designMatrix[i,"genomeBuildVersion"]==19){
      refGenomeIndexBowtie2<-refGenomeBowtie2IndexHs19
    }
    
  }
  
  for(expType in c("treat","control")){
    #expType<-"treat"
    chipRawDataFileName<-designMatrix[i,expType]
    
    #due to the control file name could be "NA".Skip it.
    if((sum(grep(".fq",chipRawDataFileName,ignore.case=TRUE))==0 && sum(grep(".fastq",chipRawDataFileName,ignore.case=TRUE))==0) | chipRawDataFileName=="NA" ){
      next
    }
      
    samPath<-paste(whereSamResult,"/samResult/",sub("\\.gz$","",chipRawDataFileName),".sam",sep="")
    
    if(!file.exists(samPath)){
      unzippedRawDataPath<-paste(chipSeqBatchRawDataFolder,"/",sub("\\.gz$","",chipRawDataFileName),sep="")
      
      
      if(!file.exists(unzippedRawDataPath)){
        comd<-paste("gunzip -c ",chipSeqBatchRawDataFolder,"/",chipRawDataFileName," > ",unzippedRawDataPath,sep="")
        system(comd)
        unzippedFqFiles<-append(unzippedFqFiles,unzippedRawDataPath)
        cat(comd)
        cat("\n")
        
      }
      
      #bwaPath<-paste(workingDir,"/bwaResult/",gsub("\\.gz$",".bwa",chipRawDataFileName),sep="")
      
      
      
      #create the same folder structure in samResult folder as in the raw data folder
      folderHeirarchy<-unlist(strsplit(toString(chipRawDataFileName),"/"))
      
      folderHeirarchy<-folderHeirarchy[-length(folderHeirarchy)]
      lineageAfterSamResult<-""
      
      for(folderName in folderHeirarchy){
        
        lineageAfterSamResult<-paste(lineageAfterSamResult,folderName,sep="/")
        if(!file.exists(paste(whereSamResult,"samResult",lineageAfterSamResult,sep="/"))){
          comd<-paste("mkdir",paste(whereSamResult,"samResult",lineageAfterSamResult,sep="/"),sep=" ")
          system(comd)
          cat(comd)
          cat("\n")
        }    
      }
      
      
      ##avoid replicate the mapping of the same reads file    
      if(!samPath%in%samComingFromBatch){
        comd<-paste("sbatch",bowtie2Script,refGenomeIndexBowtie2,unzippedRawDataPath,samPath,sep=" ")
        consoleReplyBowtie<-system(comd,intern=TRUE)
        cat(comd)
        cat("\n")
        jobIdBowtie<-sub("Submitted batch job ","",consoleReplyBowtie)
        print(paste("bowtieJobId:",jobIdBowtie,sep=""))
        allJobIdFromBowtieSbatch<-append(allJobIdFromBowtieSbatch,jobIdBowtie)
        samComingFromBatch<-append(samComingFromBatch,samPath)
      }
    }
  }
  
}

checkSbatchJob("Bowtie2",allJobIdFromBowtieSbatch)

#remove the unzipped fastq files to save space
lapply(unique(unzippedFqFiles),function(x){file.remove(x)})

#if Homer is chosen for peak calling, generate tag directory
tagComingFromBatch<-c()
allJobIdFromMakeTagSbatch<-c()

for(i in 1:nrow(designMatrix)){
  if(designMatrix[i,"execute"]==0){
    next
  }
  
  #create tag directory for each chip-seq exp
  if(sum(grep("P",designMatrix$analysisDepth[i]))==1 && sum(grep("^H",c(toString(designMatrix$programPairing[i])),perl=TRUE))!=0){   #for one treatment and control experiment
    #i<-1
   
    workingDir<-toString(designMatrix[i,"workingDir"])
    
    setwd(workingDir)
    
    if(!file.exists("homerTag")){
      system("mkdir homerTag")
    }    
    
    whereSamResult<-toString(designMatrix[i,"whereSamResult"])
    
    
    controlSamPath<-paste(whereSamResult,"/samResult/",gsub("\\.gz$","",designMatrix[i,"control"]),".sam",sep="")
    treatSamPath<-paste(whereSamResult,"/samResult/",gsub("\\.gz$","",designMatrix[i,"treat"]),".sam",sep="")
    
    #make tag directory
    treatTagDir<-paste(whereSamResult,"/homerTag/",designMatrix[i,"treatDataAbbre"],"_homerTag",sep="")
    controlTagDir<-paste(whereSamResult,"/homerTag/",designMatrix[i,"controlDataAbbre"],"_homerTag",sep="")
    
    if(!file.exists(treatTagDir) & !treatTagDir %in% tagComingFromBatch){
      #Input is sam file 
      comd<-paste("sbatch",makeTagScript,treatTagDir,treatSamPath,sep=" ")
      
      consoleReplyMakeTag<-system(comd,intern=TRUE)
      cat(comd)
      cat("\n")
      jobIdMakeTag<-sub("Submitted batch job ","",consoleReplyMakeTag)
      print(paste("makeHomerTagJobId:",jobIdMakeTag,sep=""))
      allJobIdFromMakeTagSbatch<-append(allJobIdFromMakeTagSbatch,jobIdMakeTag)
      tagComingFromBatch<-append(tagComingFromBatch,treatTagDir)
    }
    
    if(designMatrix[i,"controlDataAbbre"]!="localSignal"){ #if using local signal as control, no control tag directory is needed
      if(!file.exists(controlTagDir) & !controlTagDir %in% tagComingFromBatch){
        #Input is sam file 
        comd<-paste("sbatch",makeTagScript,controlTagDir,controlSamPath,sep=" ")
       
        consoleReplyMakeTag<-system(comd,intern=TRUE)
        cat(comd)
        cat("\n")
        jobIdMakeTag<-sub("Submitted batch job ","",consoleReplyMakeTag)
        print(paste("makeHomerTagJobId:",jobIdMakeTag,sep=""))
        allJobIdFromMakeTagSbatch<-append(allJobIdFromMakeTagSbatch,jobIdMakeTag)
        tagComingFromBatch<-append(tagComingFromBatch,controlTagDir)
      }
    }
    
    
  }
}

checkSbatchJob("Homer Tag",allJobIdFromMakeTagSbatch)

#homerTag to bigWig,bedGraph,Wig,Tdf

wigComingFromBatch<-c()
allJobIdFromMakeBigWigBedGraphWigTdfSbatch<-c()

for(i in 1:nrow(designMatrix)){
  if(designMatrix[i,"execute"]==0){
    next
  }
  
  if(sum(grep("C",designMatrix$analysisDepth[i]))==1 && sum(grep("^H",c(toString(designMatrix$programPairing[i])),perl=TRUE))!=0){   #for one treatment and control experiment
    #i<-1
    
    workingDir<-toString(designMatrix[i,"workingDir"])
    
    setwd(workingDir)
    
    if(!file.exists("homerWigTdf")){
      system("mkdir homerWigTdf")
    }    
    
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
    
    whereSamResult<-toString(designMatrix[i,"whereSamResult"])
    treatDataAbbre<-toString(designMatrix[i,"treatDataAbbre"])
    controlDataAbbre<-toString(designMatrix[i,"controlDataAbbre"]) 
    
    #get tag directory
    treatTagDir<-paste(whereSamResult,"/homerTag/",treatDataAbbre,"_homerTag",sep="")
    controlTagDir<-paste(whereSamResult,"/homerTag/",controlDataAbbre,"_homerTag",sep="")
    
    #the default output position would be the same one as homerTag
    bigWigOutput_treat<-paste(whereSamResult,"homerWigTdf",paste(treatDataAbbre,"_homerTag.bw",sep=""),sep="/")
    bigWigOutput_control<-paste(whereSamResult,"homerWigTdf",paste(controlDataAbbre,"_homerTag.bw",sep=""),sep="/")
    trackInfoOutput_treat<-paste(whereSamResult,"homerWigTdf",paste(treatDataAbbre,"_trackInfo",sep=""),sep="/")
    trackInfoOutput_control<-paste(whereSamResult,"homerWigTdf",paste(controlDataAbbre,"_trackInfo",sep=""),sep="/")
    wigOutput_treat<-paste(whereSamResult,"homerWigTdf",paste(treatDataAbbre,"_homerTag.wig",sep=""),sep="/")
    wigOutput_control<-paste(whereSamResult,"homerWigTdf",paste(controlDataAbbre,"_homerTag.wig",sep=""),sep="/")
    tdfOutput_treat<-paste(whereSamResult,"homerWigTdf",paste(treatDataAbbre,"_homerTag.tdf",sep=""),sep="/")
    tdfOutput_control<-paste(whereSamResult,"homerWigTdf",paste(controlDataAbbre,"_homerTag.tdf",sep=""),sep="/")
    
    if(!file.exists(wigOutput_treat) & !wigOutput_treat %in% wigComingFromBatch){
      comd<-paste("sbatch",homerTagToTdfWigSbatchScript,treatTagDir,genomeBuildAbbre,bigWigOutput_treat,trackInfoOutput_treat,wigOutput_treat,tdfOutput_treat,sep=" ")
      consoleReplyMakeWig<-system(comd,intern=TRUE)    
      cat(comd)
      cat("\n")
      jobIdMakingWig<-sub("Submitted batch job ","",consoleReplyMakeWig)
      print(paste("makeWigJobId:",jobIdMakingWig,sep=""))
      cat("\n\n")
      allJobIdFromMakeBigWigBedGraphWigTdfSbatch<-append(allJobIdFromMakeBigWigBedGraphWigTdfSbatch,jobIdMakingWig)
      wigComingFromBatch<-append(wigComingFromBatch,wigOutput_treat)
      
      
    }
    if(!file.exists(wigOutput_control) & !wigOutput_control %in% wigComingFromBatch & designMatrix[i,"controlDataAbbre"]!="localSignal"){
      comd<-paste("sbatch",homerTagToTdfWigSbatchScript,controlTagDir,genomeBuildAbbre,bigWigOutput_control,trackInfoOutput_control,wigOutput_control,tdfOutput_control,sep=" ")
      consoleReplyMakeWig<-system(comd,intern=TRUE)    
      cat(comd)
      cat("\n")
      jobIdMakingWig<-sub("Submitted batch job ","",consoleReplyMakeWig)
      print(paste("makeWigJobId:",jobIdMakingWig,sep=""))
      cat("\n\n")
      allJobIdFromMakeBigWigBedGraphWigTdfSbatch<-append(allJobIdFromMakeBigWigBedGraphWigTdfSbatch,jobIdMakingWig)
      wigComingFromBatch<-append(wigComingFromBatch,wigOutput_control)
      
      
    }
        
  }
}

checkSbatchJob("homerTagToTdfWig",allJobIdFromMakeBigWigBedGraphWigTdfSbatch)

sink()

#do peak, motif finding and annotation
#allJobIdSinglePipeSbatch<-c()
sink(bashScriptRecordPath2,append=T,split=TRUE)

date<-as.character(date())
cat(date)
cat("\n\n")

for(i in 1:nrow(designMatrix)){
  if(designMatrix[i,"execute"]==0){
    next
  }
  
  parameters<-as.vector(as.matrix(designMatrix[i,]))
  
  #print(parameters)
  
  if(designMatrix[i,"execute"]==0){
    next
  }
  
  comd<-paste("sbatch",chipSeqSinglePipeBatchScript,paste(parameters,collapse=" "),sep=" ")
  #comd<-paste("Rscript",chipSeqSinglePipeScript,paste(parameters,collapse=" "),sep=" ")
  
    
  print(comd)
  
  sbatchReply<-system(comd,intern=T)
  
  cat(comd)
  
  cat("\n")
  
  cat(paste(sbatchReply,"\t",date(),sep=""))
  
  cat("\n\n")
  #jobIdSinglePipeSbatch<-sub("Submitted batch job ","",consoleReplySinglePipeSbatch)
  #allJobIdSinglePipeSbatch<-append(allJobIdSinglePipeSbatch,jobIdSinglePipeSbatch)
}


sink()