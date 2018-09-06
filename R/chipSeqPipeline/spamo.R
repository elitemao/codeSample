args<-commandArgs(TRUE)

memeMotifDb<-"/wrk/hlee/data/motif_databases_12_2"

motifName<-args[4]
#motifName<-"AR"

organism<-args[5]
#organism<-"human"

genomeBuildVersion<-args[6]
#genomeBuildVersion<-"19"

margin<-args[7]
#margin<-"150"

spamoOutFolder<-args[1]
#spamoOutFolder<-"/wrk/hlee/projects//gonghong/wrk_01042015_testSpamo/spamoOutVEAR_vs_VEIgG"


homerPeakFileForHomerMotifDisco<-args[2]
#homerPeakFileForHomerMotifDisco<-"/wrk/hlee/projects/gonghong/wrk_01042015_testSpamo/VEAR_s_2VsVEIgG_s_7_factor_homerPeak" 

#after running homerPeakToFasta.R, a "_m150" suffix will be added to the output file name
fastaToCome<-paste(homerPeakFileForHomerMotifDisco,"_m",margin,".fasta",sep="")
#fastaFolder<-args[2]
#fastaFolder<-"/wrk/hlee/projects//gonghong/wrk_10022015_sheet2/homerMotif/homerMotif_BGIP1VsBGIP3_factor/"

#homer peak file to fasta format
if(!file.exists(fastaToCome)){
  comd<-paste("Rscript /wrk/hlee/code_workspace/r_workspace/common/homerPeakToFasta.R",homerPeakFileForHomerMotifDisco,organism,genomeBuildVersion,margin,sep=" ")
  print(comd)
  system(comd,intern=T)
}


homerMotifFile<-args[3]
#homerMotifFile<-"/wrk/hlee/projects//gonghong/wrk_01042015_testSpamo/homerMotif_VEAR_s_2VsVEIgG_s_7_factor/homerMotif_VEAR_s_2VsVEIgG_s_7_factor_homerPeakT1000_peakRegion/homerMotifs.all.motifs"

#homerMotifFile<-"/wrk/hlee/projects//gonghong/wrk_01042015_testSpamo/homerMotif_VEAR_s_2VsVEIgG_s_7_factor/homerMotif_VEAR_s_2VsVEIgG_s_7_factor_homerPeakT1000_peakRegion/homerResults/motif1.motif"

#homer motif file format(only take 1 ranked motif) to Taipale file format
comd<-paste(c("Rscript /wrk/hlee/code_workspace/r_workspace/common/homerMotif2Taipale.R",homerMotifFile,motifName),collapse = " ")
print(comd)
system(comd,intern=T)
#????????????????????????????????????????????????????????????????????????
#Taipale motif format to MEME file format
comd=paste(c("/wrk/hlee/programs/meme_4.10.0_2/bin/taipale2meme",sub(".motif[s]?$",".taipale",homerMotifFile),">",sub(".motif[s]?$",".meme",homerMotifFile)),sep=" ")
print(comd)
system(comd,intern=T)

comd=paste(c("spamo",fastaToCome,sub(".motif[s]?$",".meme",homerMotifFile),"/wrk/hlee/data/motif_databases_12_2/JASPAR_CORE_2014_vertebrates.meme","-oc",spamoOutFolder,"-margin",margin),sep=" ")
print(comd)
system(comd,intern=T)




chenFilePath<-paste(homerMotifFile,".chen",sep="")

#read in each line to a list
x<-scan(homerMotifFile,what="list",sep="\n")


#ordering homer motif by pValue
homerMotifObjList<-list()

logPVector<-c()

for(i in 1:length(x)){
  if(sum(grep("^>",toString(x[i])))==1){
    if(i!=1){
      homerMotifObjList<-c(homerMotifObjList,list(newListObj))
    }
    logPVector<-append(logPVector,strsplit(toString(x[i]),"\t")[[1]][4])
    newListObj<-list()
    newListObj<-c(newListObj,toString(x[i]))
    cat(x[i])
    cat("\n")
  }else{
    y<-strsplit(toString(x[i]),"\t")
    y<-1000*as.numeric(unlist(y))
    newListObj<-c(newListObj,list(y))
    cat(paste(y,sep="\t"))
    cat("\n")
    
  }
}
homerMotifObjList<-c(homerMotifObjList,list(newListObj))

homerMotifObjListSorted<-homerMotifObjList[order(as.numeric(logPVector))]

#print the first 5 significant motifs to file
sink(chenFilePath)
#for(j in 1:length(homerMotifObjListSorted)){
for(j in 1:5){
#j<-1 
  #homerMotifObjListSorted[[j]][[1]]) contains motif title
  cat(gsub("\t"," ",homerMotifObjListSorted[[j]][[1]]))
  cat("\n")
  
  #the next few lines output the frequency count of nucleoic acid of each position
  for(k in 2:length(homerMotifObjListSorted[[j]])){
    cat(paste(homerMotifObjListSorted[[j]][[k]],sep="\t"))
    cat("\n")
  }
}
sink()

memeFormatPath<-paste(chenFilePath,".meme",sep="")

comd<-paste("/wrk/hlee/programs/meme_4.10.0_2/bin/chen2meme",chenFilePath,">",memeFormatPath,sep=" ")
print(comd)
system(comd)

comdSpamo<-paste("spamo","-oc",spamoOutFolder,"-margin",margin,fastaToCome,memeFormatPath,memeMotifDb,sep=" ")
print(comdSpamo)

system(comdSpamo)
