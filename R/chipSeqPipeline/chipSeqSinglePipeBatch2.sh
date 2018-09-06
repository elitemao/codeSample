#!/bin/bash -l
#version 2 uses chipSeqSinglePipe2.2.R and also use r-app module

#SBATCH -J singleChipSeqPipe
#SBATCH -o output_%j.txt
#SBATCH -e errors_%j.txt
#SBATCH -t 24:00:00
#SBATCH -n 1
#SBATCH --nodes=1
#SBATCH --cpus-per-task=8
#SBATCH --mem-per-cpu=2000

echo "Starting chipSeqSinglePipeBatch2.sh at" date

execute=$1
paraSetName=$2
macsSettingNo=$3
expName=$4
workingDir=$5
chipSeqRawDataFolder=$6
whereSamResult=$7
treat=$8
control=$9
treatDataAbbre=${10}
controlDataAbbre=${11}
organism=${12}
genomeBuildVersion=${13}
target=${14}
mFold_low=${15}
mFold_high=${16}
bandwidth=${17}
pValue=${18}
tagSize=${19}
genomeSize=${20}
memeChipModeCode=${21}
topNumberOptStr=${22}
flankLengOptStr=${23}
analysisDepth=${24}
programPairing=${25}


#loading these three modules according to the CSC's instruction of using macs2 
module load biokit
module load openblas/0.2.6
module load python
module load r-app ##can't load r-env and biokit at the same time! They are mutually exclusive.


cd $workingDir

echo "Rscript /wrk/hlee/code_workspace/r_workspace/common/chipSeqSinglePipe2.2.R $execute $paraSetName $macsSettingNo $expName $workingDir $chipSeqRawDataFolder $whereSamResult $treat $control $treatDataAbbre $controlDataAbbre $organism $genomeBuildVersion $target $mFold_low $mFold_high $bandwidth $pValue $tagSize $genomeSize $memeChipModeCode $topNumberOptStr $flankLengOptStr $analysisDepth $programPairing"

echo "starting one singlePipe"
date

Rscript /wrk/hlee/code_workspace/r_workspace/common/chipSeqSinglePipe2.2.R $execute $paraSetName $macsSettingNo $expName $workingDir $chipSeqRawDataFolder $whereSamResult $treat $control $treatDataAbbre $controlDataAbbre $organism $genomeBuildVersion $target $mFold_low $mFold_high $bandwidth $pValue $tagSize $genomeSize $memeChipModeCode $topNumberOptStr $flankLengOptStr $analysisDepth $programPairing

echo "one singlePipe finishes"

echo "chipSeqSinglePipeBatch2.sh finished with exit code $? at: $(date)"
