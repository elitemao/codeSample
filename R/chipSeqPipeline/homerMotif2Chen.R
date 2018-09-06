options(stringsAsFactors = F)
args=commandArgs(T)

homerMotifFile=args[1]


#homerMotifFile="/wrk/hlee/projects/gonghong/wrk_15022017_STATchipGEO/homerMotif/IFNa6h_STAT2vsK562_Input_peaks_t1000/homerResults/motif1.motif"

chenMotifToSave=args[2]
#chenMotifToSave="~/Desktop/test.chen"

allLines=readLines(homerMotifFile)

sink(chenMotifToSave)

for(g in 1:length(allLines)){
  if(g==1){
    cat(allLines[g])
    cat("\n")
    next
  }
  lineArray=unlist(strsplit(allLines[g],"\t"))
  lineArray=1000*as.numeric(lineArray)
  
  cat(lineArray,sep="\t")
  
  cat("\n")
  
  
}

sink()