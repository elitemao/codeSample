#!usr/bin/perl -w
####after the mass(+0) or mass(+4) is found, scan for the other peaks(-1,+2,+0/+4) directly. Then print out the peak pairs-- 
##--if all three peaks are prsent, and it passed spectrum interfering filtration.
####Neutral for intensity or area!!!!!
####input warp file,btdx file, SNAP file,APEX file, start alpha, offset, allowed mass deviation, tolerance(in "well"),collection duration,variable used to calculate O ratio(1,2), flow type(1,-1),rank threshold,intermediate file, comment,SN threshold,(-1/0)threshold at the command line.
###mark out left and right range filtration
###set allowed mass deviation between SNAP and APEX as 0.05
##for each $pep_calc_mr, if there is a SNAP mass differing from $pep_calc_mr in $mass_devi, then find the corresponding APEX mass and print out the data
##do mass match and peak pairing at the same time(to fit the need of code for summing peak signal)
###process warplc file as well as esi file
###search the well allocation with strongest SNAP signal. Then using this well as center to find the span of well.
###add "if(($$line[0]-$pep_calc_mr-1)>1){last;}" in several blocks. Add "last;" in several blocks.
###each query will generate a peptide ratio.
#############################################################################
use XML::Simple;
use Data::Dumper;
use Spreadsheet::BasicRead;

($warp_file,$esi_file,$btdx_file,$snapFileName,$apexFileName,$start_alpha,$offset,$mass_devi,$well_toler_max_search,$colle_sec,$well_toler,$seed,$flow_type,$rank_thresh,$quantPerFraction,$comment,$sn_thresh,$minus_threshold,$pep_score_thresh)=@ARGV;   #user defined
$ipc="ipc_result.txt";
open(OUT,">$quantPerFraction");

print OUT "WARP:$warp_file\tESI:$esi_file\tBTDX:$btdx_file\tMALDI:$apexFileName\tMASS_DEVI:$mass_devi\tTOLERENCE:$well_toler\n\n";

if($seed==1){
    $parameter="intensity";
    $index=1;
}elsif($seed==2){
    $parameter="area";
    $index=5;
}

$direction=1;
for ($start_alpha..Z){
    my $alpha=$_;
    my @digit_order=(1..24);
    
    if($direction==(-1)){
	@digit_order=reverse(@digit_order);
    }
    foreach(@digit_order){
	push(@well_order,"$alpha"."$_");
    }
    if($flow_type==-1){
	$direction=$direction*(-1);
    }
}


$ss=new Spreadsheet::BasicRead($snapFileName)|| die "couldn't open '$snapFileName':$!";
$numSheetSnap=$ss->numSheets();

$aa=new Spreadsheet::BasicRead($apexFileName)|| die "couldn't open '$apexFileName':$!";
$numSheetApex=$aa->numSheets();

for(my $i=0;$i<=$numSheetSnap;$i++){
    $ss->setCurrentSheetNum($i);
    $row=1;   
    
    while (my $data=$ss->getNextRow()){   
	if($row==1){                      
	    $well_info=$data->[0];        
	    $well_info =~ s/Spectrum:.*\\0_(\w\d+)\\.*\(.*\)/$1/;
	    
	} 
	unless($row<4){
	    push(@$well_info,$data); 
	}    
	$row++;    
    }
}


for(my($j)=0;$j<=$numSheetApex;$j++){
    $aa->setCurrentSheetNum($j);
    my $row=1;   
    my $well_info;
    my $well_name;
    while (my $data=$aa->getNextRow()){   
	if($row==1){                      
	    $well_info=$data->[0];        
	    $well_info =~ s/Spectrum:.*\\0_(\w\d+)\\.*\(.*\)/$1/;
	    $well_name="$well_info"."apex";
     	} 
	unless($row<4){                
	    push(@$well_name,$data);  	       
	}    
	$row++;    
    }   
}    



if(-e $btdx_file){
    $btdx=XMLin($btdx_file);
    
    $cmpd_tof=$btdx->{BioTools}->{compounds}->[1]->{cmpd};
    foreach $cmpd(@$cmpd_tof){
	my $target_position=$cmpd->{precursor}->{TargetPosition};
	my $title=$cmpd->{title};
	$well_store{$title}=$target_position; 
	
    }
    
}

if(-e $warp_file){
$xs=XMLin($warp_file);
$hit_array=$xs->{hits}->{hit};

if(ref($hit_array) eq "HASH"){
    my $fragment=$hit_array->{protein};
    
    my $description=$fragment->{prot_desc};   
    my $accession=$fragment->{accession};  
    my $prot_score=$fragment->{prot_score};
    my $prot_mass=$fragment->{prot_mass};
	
    my $peptides=$fragment->{peptide};
    if(ref($peptides) eq "ARRAY"){
	foreach my $peptide(@$peptides){
	    my $query=$peptide->{query};
	    

		my $control=0;  
		my @wells2compare;
		my @wells2compare_max_search;
		my $esi;
		my $retention;
		my $pep_calc_mr=$peptide->{pep_calc_mr};
		my $pep_exp_mz=$peptide->{pep_exp_mz}; 
		my $pep_exp_z=$peptide->{pep_exp_z};
		my $pep_delta=$peptide->{pep_delta};
		my $pep_expect=$peptide->{pep_expect};
		my $pep_start=$peptide->{pep_start};
		my $pep_end=$peptide->{pep_end};  
		my $pep_var_mod=$peptide->{pep_var_mod};		
		my $pep_exp_mr=$peptide->{pep_exp_mr};
		my $pep_score=$peptide->{pep_score};
		my $pep_rank=$peptide->{rank};
		my $seq=$pep_seq=$peptide->{pep_seq};
		my $remark;
		my $corr_string=$peptide->{pep_scan_title};
		my $pep_homol=$peptide->{pep_homol};
		my $pep_ident=$peptide->{pep_ident};
		my $summitWell;
		my @envolopeValues;
		my $comment_prt;
		my $sumSignalZero;
		my $sumSignalTwo;
		my $sumSignalFour;
		my $o_ratio;
		
		print "CORR_STRING:$corr_string\n";
		$pep_var_mod=~s/HASH\(.*\)//;
		if($pep_var_mod=~/Oxidation/){
			$pep_seq=$pep_seq."oxi";
		}
		
		if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
		    $esi=1;								   
		    $retention=$1*60; 
		    $comment_prt=$comment.'/ESI';
		    @wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    	
		    
		}else{	
		    $esi=0;
		    $retention="";  
		    $comment_prt=$comment.'/Tof-Tof';
		    my $corr_well=$well_store{$corr_string};

		    @wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order);   
		}	     
			print "Wells2CompareMaxSearch:@wells2compare_max_search\n";
		if($pep_rank<=$rank_thresh){
			if($pep_score > $pep_score_thresh){
		    $maxEnvolopeIntensity=0;
		    foreach my $well_at_moment_max_search(@wells2compare_max_search){
			foreach my $line(@$well_at_moment_max_search){    
			    if(($$line[0]-$pep_calc_mr-1)>3){last;}    
			    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){

					push(@envolopeValues,$$line[$index]);

				if($$line[$index]>=$maxEnvolopeIntensity){
				    $maxEnvolopeIntensity=$$line[$index];
				    $summitWell=$well_at_moment_max_search;
				    
				}
			    }					
			}
		    }

		   for($d=0;$d<=$#envolopeValues;$d++){
		  	 if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   		$maxPosition=$d;
		  	 }
		   
		   } 
		    if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   	}
		    
		    
		    
		    if(defined($summitWell)){
			@wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);

			
			foreach my $well_at_moment(@wells2compare){		
			    my $mass_zero;
			    my $mass_two;
			    my $mass_four;
			    my $mass_minus;
			    my $signal_minus;
			    my $signal_zero;
			    my $signal_two;
			    my $signal_four;
			    my $sn_zero;
			    my $sn_two;
			    my $sn_four;
			    foreach my $line(@$well_at_moment){         
				if(($$line[0]-$pep_calc_mr-1)>1){last;}  
				
				
				if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){  
				    my $well_apex="$well_at_moment"."apex";
				    
				    if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
					$isotope=1;
					foreach my $apexrecord(@$well_apex){
					      
					    
					    if(abs($$apexrecord[0]-$$line[0])<0.05){  
						
						$mass_four=$$apexrecord[0];
						$signal_four=$$apexrecord[$index];
						$sn_four=$$apexrecord[2];
						
						last;
#								
					    }elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
						$mass_two=$$apexrecord[0];
						$signal_two=$$apexrecord[$index];
						$sn_two=$$apexrecord[2];
						
					    }elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
						$mass_zero=$$apexrecord[0];
						$signal_zero=$$apexrecord[$index];
						$sn_zero=$$apexrecord[2];
						
					    }elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
						$mass_minus=$$apexrecord[0];
						$signal_minus=$$apexrecord[$index];
					    }
					    
					    
					    
					}
					
					
				    }else{
				    	 
					$isotope=0;
					foreach my $apexrecord(@$well_apex){
					    
					    
					    if(abs($$apexrecord[0]-$$line[0])<0.05){  
						
						$mass_zero=$$apexrecord[0];
						$signal_zero=$$apexrecord[$index];
						$sn_zero=$$apexrecord[2];
						

					    }elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
						$mass_four=$$apexrecord[0];
						$signal_four=$$apexrecord[$index];
						$sn_four=$$apexrecord[2];
						last;
					    }elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
						$mass_two=$$apexrecord[0];
						$signal_two=$$apexrecord[$index];
						$sn_two=$$apexrecord[2];
					    }elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
						$mass_minus=$$apexrecord[0];
						$signal_minus=$$apexrecord[$index];
					    }
					    
					    
					    
					}
				    }
				    if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
					if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){    
					    if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
					    $sumSignalZero+=$signal_zero;
						$sumSignalTwo+=$signal_two;
						$sumSignalFour+=$signal_four;	
						print OUT "ACC:$accession\t","DESC:$description\t";
						print OUT "QUERY:",$query,"\t"; 
						print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						print OUT "\tZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
						$control=1;
						last;
					    }else{$remark=$remark.'(SN is high)'; last;}
					}else{$remark=$remark.'(Not all peaks are present)'; last;}
				    }else{
					$remark=$remark.'(Spectrum interfering)';
					last;
				    }
				    
				    
				    
				}
			    }
			    
			}
			system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
			open(INTER,$ipc);
			@inter=<INTER>;
			print "ipcresult:@inter\n";
			@zero=split(/,/,$inter[1]);
			
			$ratezero=$zero[2];
			$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;

			@two=split(/,/,$inter[3]);
			$ratetwo=$two[2];
			$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
			@four=split(/,/,$inter[5]);
			$ratefour=$four[2];
			$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
			close INTER;
			if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
					$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
			}
			print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
			print OUT "QUERY:",$query,"\t"; 
		    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
			print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
			$control=1;
			
		    }else{$remark=$remark.'(No matched mass is found)';}
			}else{$remark=$remark.'(pep_score is low)';} 
		}else{
		    $remark=$remark.'(pep_rank is above threshold)';
		}
		if($control == 0){     
		    print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
		    print OUT "QUERY:",$query,"\t"; 
		    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
		} 
#	    }
	}    
	
    }else{
	my $query=$peptides->{query};            
	    my $control=0;   
	    my @wells2compare;
	    my @wells2compare_max_search;
	    my $esi;
	    my $retention;
	    my $pep_calc_mr=$peptides->{pep_calc_mr};    
	    my $pep_exp_mz=$peptides->{pep_exp_mz}; 
		my $pep_exp_z=$peptides->{pep_exp_z};
		my $pep_delta=$peptides->{pep_delta};
		my $pep_expect=$peptides->{pep_expect};
		my $pep_start=$peptides->{pep_start};
		my $pep_end=$peptides->{pep_end};  
	    my $pep_var_mod=$peptides->{pep_var_mod};		
	    my $pep_exp_mr=$peptides->{pep_exp_mr};
	    my $pep_score=$peptides->{pep_score};
	    my $pep_rank=$peptides->{rank};
	    my $seq=$pep_seq=$peptides->{pep_seq};	
	    my $corr_string=$peptides->{pep_scan_title};
	    my $pep_homol=$peptides->{pep_homol};
		my $pep_ident=$peptides->{pep_ident};
	    my $remark;
	    my @envolopeValues;
		my $comment_prt;
		my $sumSignalZero;
		my $sumSignalTwo;
		my $sumSignalFour;
		my $o_ratio;
	    $pep_var_mod=~s/HASH\(.*\)//;
	    if($pep_var_mod=~/Oxidation/){
			$pep_seq=$pep_seq."oxi";
		}
		print "$accession\t$pep_seq\tCORR_STRING:$corr_string\n";
	    
	    if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
			$esi=1;			
			$retention=$1*60;
			$comment_prt=$comment.'/ESI';
			@wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    				
		
	    }else{  
			$esi=0;	
			$retention="";  
			$comment_prt=$comment.'/Tof-Tof';
			my $corr_well=$well_store{$corr_string};

			@wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order);
	    }
		print "Wells2CompareMaxSearch:@wells2compare_max_search\n";
	    
	    
	    if($pep_rank<=$rank_thresh){
	    	if($pep_score > $pep_score_thresh){
		$maxEnvolopeIntensity=0;
		foreach my $well_at_moment_max_search(@wells2compare_max_search){
		    foreach my $line(@$well_at_moment_max_search){   
			if(($$line[0]-$pep_calc_mr-1)>3){last;}    						
			if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){

			    push(@envolopeValues,$$line[$index]);
			    
			    if($$line[$index]>=$maxEnvolopeIntensity){
				$maxEnvolopeIntensity=$$line[$index];
				$summitWell=$well_at_moment_max_search;
			    }
			}					
		    }
		}
		
		for($d=0;$d<=$#envolopeValues;$d++){
			print "envolopeValues:$envolopeValues[$d]\n";
		  	 if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   		$maxPosition=$d;
		  	 }
		   
		   } 
		    if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   	}
		   	

		if(defined($summitWell)){
		    @wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);

		    
		    foreach my $well_at_moment(@wells2compare){		 
			my $mass_zero;
			my $mass_two;
			my $mass_four;
			my $mass_minus;
			my $signal_minus;
			my $signal_zero;
			my $signal_two;
			my $signal_four;
			my $sn_zero;
		    my $sn_two;
			my $sn_four;
			foreach my $line(@$well_at_moment){                   
 
				if(($$line[0]-$pep_calc_mr-1)>1){last;}
			    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
				my $well_apex="$well_at_moment"."apex";
			    
				if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
				    $isotope=1;
				    foreach my $apexrecord(@$well_apex){
					
					
					if(abs($$apexrecord[0]-$$line[0])<0.05){  
					    
					    $mass_four=$$apexrecord[0];
					    $signal_four=$$apexrecord[$index];
					    $sn_four=$$apexrecord[2];
					    last;
					}elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
					    $mass_two=$$apexrecord[0];
					    $signal_two=$$apexrecord[$index];
					    $sn_two=$$apexrecord[2];
					}elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
					    $mass_zero=$$apexrecord[0];
					    $signal_zero=$$apexrecord[$index];
					    $sn_zero=$$apexrecord[2];
					}elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
					    $mass_minus=$$apexrecord[0];
					    $signal_minus=$$apexrecord[$index];
					}
					
					
					
				    }
				    
				    
				}else{ 
				   
				    $isotope=0;
				    foreach my $apexrecord(@$well_apex){
					
					
					if(abs($$apexrecord[0]-$$line[0])<0.05){  
					    
					    $mass_zero=$$apexrecord[0];
					    $signal_zero=$$apexrecord[$index];
					    $sn_zero=$$apexrecord[2];
					}elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
					    $mass_four=$$apexrecord[0];
					    $signal_four=$$apexrecord[$index];
					    $sn_four=$$apexrecord[2];
					    last;
					}elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
					    $mass_two=$$apexrecord[0];
					    $signal_two=$$apexrecord[$index];
					    $sn_two=$$apexrecord[2];
					}elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
					    $mass_minus=$$apexrecord[0];
					    $signal_minus=$$apexrecord[$index];
					}
					
					
					
				    }
				}
				if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
				    if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){     
					if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
						$sumSignalZero+=$signal_zero;
						$sumSignalTwo+=$signal_two;
						$sumSignalFour+=$signal_four;	
					    print OUT "ACC:$accession\t","DESC:$description\t";
					    print OUT "QUERY:",$query,"\t"; 
					     print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
					    print OUT "ZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
					    $control=1;
					    last;
					}else{$remark=$remark.'(SN is high)'; last; }	
				    }else{$remark=$remark.'(Not all peaks are present)'; last;}			
				}else{
				    $remark=$remark.'(Spectrum interfering)';
				    last;
				}
			    }
			}
			
		    }
		    system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
			open(INTER,$ipc);
			@inter=<INTER>;
			print "ipcresult:@inter\n";
			@zero=split(/,/,$inter[1]);
			$ratezero=$zero[2];
#			print "@zero\n";
#			print "$ratezero\n";
			$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
#			print "$ratezero\n";
			@two=split(/,/,$inter[3]);
			$ratetwo=$two[2];
			$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
			@four=split(/,/,$inter[5]);
			$ratefour=$four[2];
			$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
			close INTER;
			if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
					$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
			}
			print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
			print OUT "QUERY:",$query,"\t"; 
		    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
			print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
			$control=1;
		    
		    
		    
		}else{$remark=$remark.'(No matched mass is found)';}
	    }else{$remark=$remark.'(pep_score is low)';}
	    }else{$remark=$remark.'(pep_rank is above threshold)';}
	    
	    if($control == 0){    
		print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
		print OUT "QUERY:",$query,"\t"; 
		print OUT "Retention:","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
	    } 
	    
	    

    }
    
}else{
    foreach $hit(@$hit_array){
	
	
	$fragments=$hit->{protein};            
	if(ref($fragments) eq "ARRAY"){       
	    foreach my $fragment(@$fragments){           
		my $description=$fragment->{prot_desc};   
		my $accession=$fragment->{accession};  
		my $prot_score=$fragment->{prot_score};
		my $prot_mass=$fragment->{prot_mass};
		my $peptides=$fragment->{peptide};
		if(ref($peptides) eq "ARRAY"){
		    foreach my $peptide(@$peptides){
			my $query=$peptide->{query};

 			    my $control=0;   
			    my @wells2compare;
			    my @wells2compare_max_search;
			    my $esi;
			    my $retention;
			    my $pep_calc_mr=$peptide->{pep_calc_mr};
			    my $pep_exp_mz=$peptide->{pep_exp_mz}; 
				my $pep_exp_z=$peptide->{pep_exp_z};
				my $pep_delta=$peptide->{pep_delta};
				my $pep_expect=$peptide->{pep_expect};
				my $pep_start=$peptide->{pep_start};
				my $pep_end=$peptide->{pep_end};     
			    my $pep_var_mod=$peptide->{pep_var_mod};		
			    my $pep_exp_mr=$peptide->{pep_exp_mr};
			    my $pep_score=$peptide->{pep_score};
			    my $pep_rank=$peptide->{rank};
			    my $seq=$pep_seq=$peptide->{pep_seq};
			    my $remark;
			    my $corr_string=$peptide->{pep_scan_title};
			    my $pep_homol=$peptide->{pep_homol};
				my $pep_ident=$peptide->{pep_ident};
			    my $summitWell;
			    my @envolopeValues;
				my $comment_prt;
				my $sumSignalZero;
				my $sumSignalTwo;
				my $sumSignalFour;
				my $o_ratio;
			    $pep_var_mod=~s/HASH\(.*\)//;
			    if($pep_var_mod=~/Oxidation/){
					$pep_seq=$pep_seq."oxi";
				}
			    print "$accession\t$pep_seq\tCORR_STRING:$corr_string\n";
			    
			    if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
				$esi=1;	
				$comment_prt=$comment.'/ESI';
				$retention=$1*60; 			
				@wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    	
				
			    }else{	
				$esi=0;
				$retention="";  
				$comment_prt=$comment.'/Tof-Tof';
				my $corr_well=$well_store{$corr_string};
				@wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order);   
			    }	  
				print "Wells2CompareMaxSearch:@wells2compare_max_search\n";   
			    if($pep_rank<=$rank_thresh){
			    	if($pep_score > $pep_score_thresh){
				$maxEnvolopeIntensity=0;
				foreach my $well_at_moment_max_search(@wells2compare_max_search){
				    foreach my $line(@$well_at_moment_max_search){ 
					if(($$line[0]-$pep_calc_mr-1)>3){last;}       
					if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
						push(@envolopeValues,$$line[$index]);
						

					    if($$line[$index]>=$maxEnvolopeIntensity){
						$summitWell=$well_at_moment_max_search;
						$maxEnvolopeIntensity=$$line[$index];
					    }
					}					
				    }
				}
				for($d=0;$d<=$#envolopeValues;$d++){
					
		  	 if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   		$maxPosition=$d;
		  	 }
		   
		   } 
		    if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   	}
				
				
				

				if(defined($summitWell)){
				    @wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);

				    
				    
				    foreach my $well_at_moment(@wells2compare){		
					my $mass_zero;
					my $mass_two;
					my $mass_four;
					my $mass_minus;
					my $signal_minus;
					my $signal_zero;
					my $signal_two;
					my $signal_four;
					my $sn_zero;
			    	my $sn_two;
			    	my $sn_four;
					foreach my $line(@$well_at_moment){         
					    
					    if(($$line[0]-$pep_calc_mr-1)>1){last;}
					    
					    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){  
						my $well_apex="$well_at_moment"."apex";
						
						if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
						    $isotope=1;
						    foreach my $apexrecord(@$well_apex){
							
							
							if(abs($$apexrecord[0]-$$line[0])<0.05){  
							    
							    $mass_four=$$apexrecord[0];
							    $signal_four=$$apexrecord[$index];
							    $sn_four=$$apexrecord[2];
							    last;
							}elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
							    $mass_two=$$apexrecord[0];
							    $signal_two=$$apexrecord[$index];
							    $sn_two=$$apexrecord[2];
							}elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
							    $mass_zero=$$apexrecord[0];
							    $signal_zero=$$apexrecord[$index];
							    $sn_zero=$$apexrecord[2];
							}elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
							    $mass_minus=$$apexrecord[0];
							    $signal_minus=$$apexrecord[$index];
							}
				
							
							
						    }
						    
						    
						}else{ 
							
						    $isotope=0;
						    foreach my $apexrecord(@$well_apex){
							
							
							if(abs($$apexrecord[0]-$$line[0])<0.05){  
							    
							    $mass_zero=$$apexrecord[0];
							    $signal_zero=$$apexrecord[$index];
							    $sn_zero=$$apexrecord[2];
							}elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
							    $mass_four=$$apexrecord[0];
							    $signal_four=$$apexrecord[$index];
							    $sn_four=$$apexrecord[2];
							    last;
							}elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
							    $mass_two=$$apexrecord[0];
							    $signal_two=$$apexrecord[$index];
							    $sn_two=$$apexrecord[2];
							}elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
							    $mass_minus=$$apexrecord[0];
							    $signal_minus=$$apexrecord[$index];
							}
							
							
							
						    }
						}
						if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
						    if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){     
							if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
								$sumSignalZero+=$signal_zero;
								$sumSignalTwo+=$signal_two;
								$sumSignalFour+=$signal_four;	
							    print OUT "ACC:$accession\t","DESC:$description\t";
							    print OUT "QUERY:",$query,"\t"; 
							    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
							    print OUT "ZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
							    $control=1;
							    last;
							}else{$remark=$remark.'(SN is high)'; last;}
						    }else{$remark=$remark.'(Not all peaks are present)'; last;}
						}else{
						    $remark=$remark.'(Spectrum interfering)';
						    last;
						}
						
					    }
					}
					
				    }
				    system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
					open(INTER,$ipc);
					@inter=<INTER>;
					print "ipcresult:@inter\n";
					@zero=split(/,/,$inter[1]);
					$ratezero=$zero[2];
					
					$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;

					@two=split(/,/,$inter[3]);
					$ratetwo=$two[2];
					$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					@four=split(/,/,$inter[5]);
					$ratefour=$four[2];
					$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					close INTER;
					if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
						$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
					}
					print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
					print OUT "QUERY:",$query,"\t"; 
		    		print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
					print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
					$control=1;
				    
				    
				}else{$remark=$remark.'(No matched mass is found)';}	
			    }else{$remark=$remark.'(pep_score is low)';}
			    }else{$remark=$remark.'(pep_rank is above threshold)';}   
			    if($control == 0){     
				print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
				print OUT "QUERY:",$query,"\t"; 
				print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
			    } 
			    
		    }    
		    
		}else{
		    my $query=$peptides->{query};            
		    

			my $control=0;   
			my @wells2compare;
			my @wells2compare_max_search;
			my $esi;
			my $retention;
			my $pep_calc_mr=$peptides->{pep_calc_mr};    
			my $pep_exp_mz=$peptides->{pep_exp_mz}; 
			my $pep_exp_z=$peptides->{pep_exp_z};
			my $pep_delta=$peptides->{pep_delta};
			my $pep_expect=$peptides->{pep_expect};
			my $pep_start=$peptides->{pep_start};
			my $pep_end=$peptides->{pep_end};  
			my $pep_var_mod=$peptides->{pep_var_mod};		
			my $pep_exp_mr=$peptides->{pep_exp_mr};
			my $pep_score=$peptides->{pep_score};
			my $pep_rank=$peptides->{rank};
			my $seq=$pep_seq=$peptides->{pep_seq};	
			my $corr_string=$peptides->{pep_scan_title};
			my $pep_homol=$peptides->{pep_homol};
			my $pep_ident=$peptides->{pep_ident};
			my $remark;
			my $summitWell;
			my @envolopeValues;
			my $comment_prt;
			my $sumSignalZero;
			my $sumSignalTwo;
			my $sumSignalFour;
			my $o_ratio;
			$pep_var_mod=~s/HASH\(.*\)//;   
			if($pep_var_mod=~/Oxidation/){
				$pep_seq=$pep_seq."oxi";
			} 
			    print "CORR_STRING:$corr_string\n";
			
			if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
			    $esi=1;	
			    $comment_prt=$comment.'/ESI';
			    
			    $retention=$1*60;
			    
			    @wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    				
			    
			}else{  
			    $esi=0;	
			    $retention="";  
			    $comment_prt=$comment.'/Tof-Tof';
			    my $corr_well=$well_store{$corr_string};
			    @wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order);
			}

			if($pep_rank<=$rank_thresh){
				if($pep_score > $pep_score_thresh){
			    $maxEnvolopeIntensity=0;
			    foreach my $well_at_moment_max_search(@wells2compare_max_search){
				foreach my $line(@$well_at_moment_max_search){    
				    if(($$line[0]-$pep_calc_mr-1)>3){last;}    
				    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
				    	push(@envolopeValues,$$line[$index]);
				    	
					if($$line[$index]>=$maxEnvolopeIntensity){
					    $summitWell=$well_at_moment_max_search;
					    $maxEnvolopeIntensity=$$line[$index];
					}
				    }					
				}
			    }
			    
			    for($d=0;$d<=$#envolopeValues;$d++){

		  	 		if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   				$maxPosition=$d;
		  	 		}
		   
		   		} 
		    if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   	}

			    if(defined($summitWell)){
				@wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);

			    	
				foreach my $well_at_moment(@wells2compare){		
				    my $mass_zero;
				    my $mass_two;
				    my $mass_four;
				    my $mass_minus;
				    my $signal_minus;
				    my $signal_zero;
				    my $signal_two;
				    my $signal_four;
				    my $sn_zero;
			  		my $sn_two;
			    	my $sn_four;
				    foreach my $line(@$well_at_moment){					                    
					if(($$line[0]-$pep_calc_mr-1)>1){last;}
					if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
					    my $well_apex="$well_at_moment"."apex";
					    
					    if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
						$isotope=1;
						foreach my $apexrecord(@$well_apex){
						    
						    
						    if(abs($$apexrecord[0]-$$line[0])<0.05){  
							
								$mass_four=$$apexrecord[0];
								$signal_four=$$apexrecord[$index];
								$sn_four=$$apexrecord[2];
								last;
						    }elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
				    			$mass_two=$$apexrecord[0];
				    			$signal_two=$$apexrecord[$index];
				    			$sn_two=$$apexrecord[2];
						    }elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
				    			$mass_zero=$$apexrecord[0];
				    			$signal_zero=$$apexrecord[$index];
				    			$sn_zero=$$apexrecord[2];
						    }elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
				    			$mass_minus=$$apexrecord[0];
				    			$signal_minus=$$apexrecord[$index];
						    }
						    
						    
						    
						}
						
						
					    }else{ 
					    	
						$isotope=0;
						foreach my $apexrecord(@$well_apex){
						    
						    
						    if(abs($$apexrecord[0]-$$line[0])<0.05){  
							
								$mass_zero=$$apexrecord[0];
								$signal_zero=$$apexrecord[$index];
								$sn_zero=$$apexrecord[2];
						    }elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
				    			$mass_four=$$apexrecord[0];
				    			$signal_four=$$apexrecord[$index];
				    			$sn_four=$$apexrecord[2];
				    			last;
						    }elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
				    			$mass_two=$$apexrecord[0];
				    			$signal_two=$$apexrecord[$index];
				    			$sn_two=$$apexrecord[2];
						    }elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
				    			$mass_minus=$$apexrecord[0];
				    			$signal_minus=$$apexrecord[$index];
						    }
						    
						    
						    
						}
					    }
					    if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
						if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){     
						    if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
							$sumSignalZero+=$signal_zero;
							$sumSignalTwo+=$signal_two;
							$sumSignalFour+=$signal_four;
							
							print OUT "ACC:$accession\t","DESC:$description\t";
							print OUT "QUERY:",$query,"\t"; 
							 print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
							print OUT "ZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
							$control=1;
							last;
						    }else{ $remark=$remark.'(SN is high)';  last;}
						}else{$remark=$remark.'(Not all peaks are present)';  last;}				
					    }else{
						$remark=$remark.'(Spectrum interfering)';
						last;
					    }
					    
					    
					    
					    
					    
					}
				    }
				    
				}
				system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
					open(INTER,$ipc);
					@inter=<INTER>;
					
					@zero=split(/,/,$inter[1]);
					$ratezero=$zero[2];					
					$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;

					@two=split(/,/,$inter[3]);
					$ratetwo=$two[2];
					$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					@four=split(/,/,$inter[5]);
					$ratefour=$four[2];
					$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					close INTER;
					if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
						$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
					}
					print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
					print OUT "QUERY:",$query,"\t"; 
		    		print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
					print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
					$control=1;
				
				
				
				
			    }else{$remark=$remark.'(No matched mass is found)';}
			}else{$remark=$remark.'(pep_score is low)';}
			}else{$remark=$remark.'(pep_rank is above threshold)';}
			if($control == 0){     
			    print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
			    print OUT "QUERY:",$query,"\t"; 
			    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
			} 


		}
		
		
	    } 
	}else{

	    my $description=$fragments->{prot_desc};
	    my $accession=$fragments->{accession};
	    my $prot_score=$fragments->{prot_score}; 
	    my $prot_mass=$fragments->{prot_mass};	
	    my $peptides=$fragments->{peptide};
	    if(ref($peptides) eq "ARRAY"){
		foreach my $peptide(@$peptides){
		    my $query=$peptide->{query};

			my $control=0;   
			my @wells2compare;
			my @wells2compare_max_search;
			my $esi;
			my $retention;
			my $pep_calc_mr=$peptide->{pep_calc_mr}; 
			my $pep_exp_mz=$peptide->{pep_exp_mz}; 
			my $pep_exp_z=$peptide->{pep_exp_z};
			my $pep_delta=$peptide->{pep_delta};
			my $pep_expect=$peptide->{pep_expect};
			my $pep_start=$peptide->{pep_start};
			my $pep_end=$peptide->{pep_end};    
			my $pep_var_mod=$peptide->{pep_var_mod};		
			my $pep_exp_mr=$peptide->{pep_exp_mr};
			my $pep_score=$peptide->{pep_score};
			my $pep_rank=$peptide->{rank};
			my $seq=$pep_seq=$peptide->{pep_seq};
			my $corr_string=$peptide->{pep_scan_title};
			my $pep_homol=$peptide->{pep_homol};
			my $pep_ident=$peptide->{pep_ident};
			my $remark;
			my $summitWell;
			my @envolopeValues;
			my $comment_prt;
			my $sumSignalZero;
			my $sumSignalTwo;
			my $sumSignalFour;
			my $o_ratio;
			
			$pep_var_mod=~s/HASH\(.*\)//;
			if($pep_var_mod=~/Oxidation/){
			$pep_seq=$pep_seq."oxi";
			}
			
			if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
			    $esi=1;			
			    $retention=$1*60;
			    $comment_prt=$comment.'/ESI';
			    @wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    
			    
			}else{ 	
			    $esi=0; 
			    $retention="";  
			    $comment_prt=$comment.'/Tof-Tof';
			    my $corr_well=$well_store{$corr_string};
			    @wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order); 
			    
			}

			
			if($pep_rank<=$rank_thresh){
				    if($pep_score > $pep_score_thresh){
			    $maxEnvolopeIntensity=0;
			    foreach my $well_at_moment_max_search(@wells2compare_max_search){
				foreach my $line(@$well_at_moment_max_search){
				    if(($$line[0]-$pep_calc_mr-1)>3){last;}

				    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
				    	push(@envolopeValues,$$line[$index]);
				    	

					if($$line[$index]>=$maxEnvolopeIntensity){
					    $summitWell=$well_at_moment_max_search;
					    $maxEnvolopeIntensity=$$line[$index];
					}
				    }					
				}
			    }

				for($d=0;$d<=$#envolopeValues;$d++){
					print "envolopeValues:$envolopeValues[$d]\n";
		 		 	 if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   					$maxPosition=$d;
		  	 		 }
		   
		   		} 
		    if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   	}
			    if(defined($summitWell)){
				@wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);

				foreach my $well_at_moment(@wells2compare){		
				    my $mass_zero;
				    my $mass_two;
				    my $mass_four;
				    my $mass_minus;
				    my $signal_minus;
				    my $signal_zero;
				    my $signal_two;
				    my $signal_four;
				    my $sn_zero;
			    	my $sn_two;
			    	my $sn_four;
				    foreach my $line(@$well_at_moment){                    
					
					if(($$line[0]-$pep_calc_mr-1)>1){last;}
					if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
					    
					    my $well_apex="$well_at_moment"."apex";
					    if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
						$isotope=1;
						foreach my $apexrecord(@$well_apex){
						    
						    
				    		if(abs($$apexrecord[0]-$$line[0])<0.05){  
						    
						    $mass_four=$$apexrecord[0];
						    $signal_four=$$apexrecord[$index];
						    $sn_four=$$apexrecord[2];
						    last;
				    		}elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
						    $mass_two=$$apexrecord[0];
						    $signal_two=$$apexrecord[$index];
						    $sn_two=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
						    $mass_zero=$$apexrecord[0];
						    $signal_zero=$$apexrecord[$index];
						    $sn_zero=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
						    $mass_minus=$$apexrecord[0];
						    $signal_minus=$$apexrecord[$index];
				    		}
						
						
						
					    }
						
						
					}else{ 
						
					    $isotope=0;
					    foreach my $apexrecord(@$well_apex){
						
						
				    		if(abs($$apexrecord[0]-$$line[0])<0.05){  
						    
						    $mass_zero=$$apexrecord[0];
						    $signal_zero=$$apexrecord[$index];
						    $sn_zero=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
						    $mass_four=$$apexrecord[0];
						    $signal_four=$$apexrecord[$index];
						    $sn_four=$$apexrecord[2];
						    last;
				    		}elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
						    $mass_two=$$apexrecord[0];
						    $signal_two=$$apexrecord[$index];
						    $sn_two=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
						    $mass_minus=$$apexrecord[0];
						    $signal_minus=$$apexrecord[$index];
				    		}
						
						
						
					    }
					}
					    if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
						if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){     
						    if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
							$sumSignalZero+=$signal_zero;
							$sumSignalTwo+=$signal_two;
							$sumSignalFour+=$signal_four;
							print OUT "ACC:$accession\t","DESC:$description\t";
							print OUT "QUERY:",$query,"\t"; 
							 print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
							print OUT "\tZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
							$control=1;
							last;
						    }else{ $remark=$remark.'(SN is high)'; last;}	
						}else{$remark=$remark.'(Not all peaks are present)'; last;}
					    }else{
						$remark=$remark.'(Spectrum interfering)';
						last;
					    }
					 
					}
				    }
				}
				system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
					open(INTER,$ipc);
					@inter=<INTER>;
					
					@zero=split(/,/,$inter[1]);
					$ratezero=$zero[2];
					
					$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;

					@two=split(/,/,$inter[3]);
					$ratetwo=$two[2];
					$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					@four=split(/,/,$inter[5]);
					$ratefour=$four[2];
					$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					close INTER;
					if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
						$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
					}
					print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
					print OUT "QUERY:",$query,"\t"; 
		    		print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
					print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
					$control=1;
				
				
			    }else{$remark=$remark.'(No matched mass is found)';} 
			}else{$remark=$remark.'(pep_score is low)';}
			}else{$remark=$remark.'(pep_rank is above threshold)';}
			if($control == 0){     
			    print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
			    print OUT "QUERY:",$query,"\t"; 
			    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
			} 

		}
		
	    }else{
		my $query=$peptides->{query};
		

		    my $control=0;   
		    my @wells2compare;
		    my @wells2compare_max_search;
		    my $esi;
		    my $retention;
		    my $pep_calc_mr=$peptides->{pep_calc_mr};
		    my $pep_exp_mz=$peptides->{pep_exp_mz}; 
			my $pep_exp_z=$peptides->{pep_exp_z};
			my $pep_delta=$peptides->{pep_delta};
			my $pep_expect=$peptides->{pep_expect};
			my $pep_start=$peptides->{pep_start};
			my $pep_end=$peptides->{pep_end};      

		    my $pep_var_mod=$peptides->{pep_var_mod};		
		    my $pep_exp_mr=$peptides->{pep_exp_mr};               
		    my $pep_score=$peptides->{pep_score};
		    my $pep_rank=$peptides->{rank};
		    my $seq=$pep_seq=$peptides->{pep_seq};  
		    my $corr_string=$peptides->{pep_scan_title};
		    my $pep_homol=$peptides->{pep_homol};
			my $pep_ident=$peptides->{pep_ident};
		    my $remark;
		    my $summitWell;
		    my @envolopeValues;
			my $comment_prt;
			my $sumSignalZero;
			my $sumSignalTwo;
			my $sumSignalFour;
			my $o_ratio;
			
		    $pep_var_mod=~s/HASH\(.*\)//;
		    if($pep_var_mod=~/Oxidation/){
			$pep_seq=$pep_seq."oxi";
			}

		    
		    
		    if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
				$esi=1;		   
				$retention=$1*60;
				$comment_prt=$comment.'/ESI';
				@wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    
			
		    }else{  
				$esi=0;
				$retention="";  
				$comment_prt=$comment.'/Tof-Tof';
				my $corr_well=$well_store{$corr_string};

				@wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order); 
		    }

		    if($pep_rank<=$rank_thresh){
		    	if($pep_score > $pep_score_thresh){
			$maxEnvolopeIntensity=0;
			foreach my $well_at_moment_max_search(@wells2compare_max_search){
			    foreach my $line(@$well_at_moment_max_search){  
				if(($$line[0]-$pep_calc_mr-1)>3){last;}      
				if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
					push(@envolopeValues,$$line[$index]);
					

				    if($$line[$index]>=$maxEnvolopeIntensity){
					$summitWell=$well_at_moment_max_search;
					$maxEnvolopeIntensity=$$line[$index];
				    }
				}					
			    }
			}
			for($d=0;$d<=$#envolopeValues;$d++){

		  	 if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   		$maxPosition=$d;
		  	 }
		   
		   } 
		    if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   	}

			if(defined($summitWell)){
			    @wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);

			    
			    foreach my $well_at_moment(@wells2compare){		
				my $mass_zero;
				my $mass_two;
				my $mass_four;
				my $mass_minus;	
				my $signal_minus;
				my $signal_zero;
				my $signal_two;
				my $signal_four;
				my $sn_zero;
			    my $sn_two;
			    my $sn_four;
				foreach my $line(@$well_at_moment){       
					if(($$line[0]-$pep_calc_mr-1)>1){last;}
				    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
					my $well_apex="$well_at_moment"."apex";
					if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
					    $isotope=1;
					    foreach my $apexrecord(@$well_apex){
						
						
				    		if(abs($$apexrecord[0]-$$line[0])<0.05){ 
						    $mass_four=$$apexrecord[0];
						    $signal_four=$$apexrecord[$index];
						    $sn_four=$$apexrecord[2];
						    last;
				    		}elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
						    $mass_two=$$apexrecord[0];
						    $signal_two=$$apexrecord[$index];
						    $sn_two=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
						    $mass_zero=$$apexrecord[0];
						    $signal_zero=$$apexrecord[$index];
						    $sn_zero=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
						    $mass_minus=$$apexrecord[0];
						    $signal_minus=$$apexrecord[$index];
				    		}
						
						
						
					    }
					    
					    
					}else{ 
						
					    $isotope=0;
					    foreach my $apexrecord(@$well_apex){
						
						
				    		if(abs($$apexrecord[0]-$$line[0])<0.05){  
						    $mass_zero=$$apexrecord[0];
						    $signal_zero=$$apexrecord[$index];
						    $sn_zero=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
						    $mass_four=$$apexrecord[0];
						    $signal_four=$$apexrecord[$index];
						    $sn_four=$$apexrecord[2];
						    last;
				    		}elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
						    $mass_two=$$apexrecord[0];
						    $signal_two=$$apexrecord[$index];
						    $sn_two=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
						    $mass_minus=$$apexrecord[0];
						    $signal_minus=$$apexrecord[$index];
				    		}
						
						
						
					    }
					}
					if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
					    if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){     
						if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
						    $sumSignalZero+=$signal_zero;
							$sumSignalTwo+=$signal_two;
							$sumSignalFour+=$signal_four;
						    print OUT "ACC:$accession\t","DESC:$description\t";
						    print OUT "QUERY:",$query,"\t"; 
						     print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						    print OUT "\tZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
						    $control=1;
						    last;
						}else{$remark=$remark.'(SN is high)'; last;}
					    }else{$remark=$remark.'(Not all peaks are present)'; last;}	
					}else{
					    $remark=$remark.'(Spectrum interfering)';
					    last;
					}
					
				    }
				}
				
			    }
			    system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
					open(INTER,$ipc);
					@inter=<INTER>;
					print "ipcresult:@inter\n";
					@zero=split(/,/,$inter[1]);
					$ratezero=$zero[2];

					$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;

					@two=split(/,/,$inter[3]);
					$ratetwo=$two[2];
					$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					@four=split(/,/,$inter[5]);
					$ratefour=$four[2];
					$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					close INTER;
					if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
						$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
					}
					print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
					print OUT "QUERY:",$query,"\t"; 
		    		print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
					print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
					$control=1;
			    
			    
			    
			}else{$remark=$remark.'(No matched mass is found)';}
		    }else{$remark=$remark.'(pep_score is low)';}
		    }else{$remark=$remark.'(pep_rank is above threshold)';}
		  if($control == 0){     
		      print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
		      print OUT "QUERY:",$query,"\t"; 
		      print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
		  }   
		   

	    }

	}
    }
}

undef($hit_array);

undef($xs);
undef($btdx);
}

########################################################################################################
########################################################################################################
########################################################################################################
########################################################################################################
if(-e $esi_file){
    $esi_handle=XMLin($esi_file);
    
$hit_array=$esi_handle->{hits}->{hit};


if(ref($hit_array) eq "HASH"){
    my $fragment=$hit_array->{protein};
   
    my $description=$fragment->{prot_desc};   
    my $accession=$fragment->{accession};  
    my $prot_score=$fragment->{prot_score};
     my $prot_mass=$fragment->{prot_mass};
	
    my $peptides=$fragment->{peptide};    
    if(ref($peptides) eq "ARRAY"){
	foreach my $peptide(@$peptides){
	    my $query=$peptide->{query};
	    

		my $control=0;   
		my @wells2compare;
		my @wells2compare_max_search;
		my $esi;
		my $retention;
		my $pep_calc_mr=$peptide->{pep_calc_mr}; 
		my $pep_exp_mz=$peptide->{pep_exp_mz}; 
		my $pep_exp_z=$peptide->{pep_exp_z};
		my $pep_delta=$peptide->{pep_delta};
		my $pep_expect=$peptide->{pep_expect};
		my $pep_start=$peptide->{pep_start};
		my $pep_end=$peptide->{pep_end};    
		my $pep_var_mod=$peptide->{pep_var_mod};		
		my $pep_exp_mr=$peptide->{pep_exp_mr};
		my $pep_score=$peptide->{pep_score};
		my $pep_rank=$peptide->{rank};
		my $seq=$pep_seq=$peptide->{pep_seq};
		my $remark;
		my $corr_string=$peptide->{pep_scan_title};
		my $pep_homol=$peptide->{pep_homol};
		my $pep_ident=$peptide->{pep_ident};
		my $summitWell;
		my @envolopeValues;
		my $comment_prt;
		my $sumSignalZero;
		my $sumSignalTwo;
		my $sumSignalFour;
		my $o_ratio;
		$pep_var_mod=~s/HASH\(.*\)//;
		if($pep_var_mod=~/Oxidation/){
			$pep_seq=$pep_seq."oxi";
		}

		
		if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
		    $esi=1;								   
		    $retention=$1*60; 
		    $comment_prt=$comment.'/ESI/ESImode';			
		    @wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    	
		    
		}else{	
		    $esi=0;
		    $retention="";  
		    $comment_prt=$comment.'/Tof-Tof/ESImode';

		    my $corr_well=$well_store{$corr_string};

			
		    @wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order);   
		}	     
		
		if($pep_rank<=$rank_thresh){
			if($pep_score > $pep_score_thresh){
		    $maxEnvolopeIntensity=0;
		    foreach my $well_at_moment_max_search(@wells2compare_max_search){
			foreach my $line(@$well_at_moment_max_search){  
			    if(($$line[0]-$pep_calc_mr-1)>3){last;}      
			    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
			    	push(@envolopeValues,$$line[$index]);
				if($$line[$index]>=$maxEnvolopeIntensity){
				    $summitWell=$well_at_moment_max_search;
				    $maxEnvolopeIntensity=$$line[$index];
				}
			    }					
			}
		    }
		    for($d=0;$d<=$#envolopeValues;$d++){

		  	 if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   		$maxPosition=$d;
		  	 }
		   
		   } 
		    if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   	}

		    if(defined($summitWell)){
			@wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);
			
			foreach my $well_at_moment(@wells2compare){		
			    my $mass_zero;
			    my $mass_two;
			    my $mass_four;
			    my $mass_minus;
			    my $signal_minus;
			    my $signal_zero;
			    my $signal_two;
			    my $signal_four;
			    my $sn_zero;
			    my $sn_two;
			    my $sn_four;
			    foreach my $line(@$well_at_moment){         
				
				if(($$line[0]-$pep_calc_mr-1)>1){last;}
				
				if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){  
				    my $well_apex="$well_at_moment"."apex";
				    
				    if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
					$isotope=1;
					foreach my $apexrecord(@$well_apex){
					    
					    
					    if(abs($$apexrecord[0]-$$line[0])<0.05){  
						
						$mass_four=$$apexrecord[0];
						$signal_four=$$apexrecord[$index];
						$sn_four=$$apexrecord[2];
						last;

					    }elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
						$mass_two=$$apexrecord[0];
						$signal_two=$$apexrecord[$index];
						$sn_two=$$apexrecord[2];
					    }elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
						$mass_zero=$$apexrecord[0];
						$signal_zero=$$apexrecord[$index];
						$sn_zero=$$apexrecord[2];
					    }elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
						$mass_minus=$$apexrecord[0];
						$signal_minus=$$apexrecord[$index];
					    }
					    
					    
				
					}
					
					
				    }else{ 
				    	 
					$isotope=0;
					foreach my $apexrecord(@$well_apex){
					    
					    
					    if(abs($$apexrecord[0]-$$line[0])<0.05){  						
							$mass_zero=$$apexrecord[0];
							$signal_zero=$$apexrecord[$index];
							$sn_zero=$$apexrecord[2];

					    }elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
							$mass_four=$$apexrecord[0];
							$signal_four=$$apexrecord[$index];
							$sn_four=$$apexrecord[2];
							last;
					    }elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
							$mass_two=$$apexrecord[0];
							$signal_two=$$apexrecord[$index];
							$sn_two=$$apexrecord[2];
					    }elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
							$mass_minus=$$apexrecord[0];
							$signal_minus=$$apexrecord[$index];
					    }

					}
				    }
				    if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
					if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){    
					    if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
						$sumSignalZero+=$signal_zero;
						$sumSignalTwo+=$signal_two;
						$sumSignalFour+=$signal_four;
						print OUT "ACC:$accession\t","DESC:$description\t";
						print OUT "QUERY:",$query,"\t"; 
						print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\t","SummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\tRemark:",$remark;
						print OUT "\tZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
						$control=1;
						last;
					    }else{$remark=$remark.'(SN is high)'; last;}
					}else{$remark=$remark.'(Not all peaks are present)'; last;}
				    }else{
					$remark=$remark.'(Spectrum interfering)';
					last;
				    }
				  }
			    }
			    
			}
			system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
					open(INTER,$ipc);
					@inter=<INTER>;

					@zero=split(/,/,$inter[1]);
					$ratezero=$zero[2];

					$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;

					@two=split(/,/,$inter[3]);
					$ratetwo=$two[2];
					$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					@four=split(/,/,$inter[5]);
					$ratefour=$four[2];
					$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					close INTER;
					if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
						$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
					}
					print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
					print OUT "QUERY:",$query,"\t"; 
		    		print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
					print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
					$control=1;
			
			
			
			
		    }else{$remark=$remark.'(No matched mass is found)';}
		}else{$remark=$remark.'(pep_score is low)';}
		}   else{
		    $remark=$remark.'(pep_rank is above threshold)';
		}
		if($control == 0){     
		    print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
		    print OUT "QUERY:",$query,"\t"; 
		    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_score:",$pep_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
		} 

	}    
	
    }else{
	my $query=$peptides->{query};            
	

	    my $control=0;  
	    my @wells2compare;
	    my @wells2compare_max_search;
	    my $esi;
	    my $retention;
	    my $pep_calc_mr=$peptides->{pep_calc_mr};   
	    my $pep_exp_mz=$peptides->{pep_exp_mz}; 
		my $pep_exp_z=$peptides->{pep_exp_z};
		my $pep_delta=$peptides->{pep_delta};
		my $pep_expect=$peptides->{pep_expect};
		my $pep_start=$peptides->{pep_start};
		my $pep_end=$peptides->{pep_end};  
	    my $pep_var_mod=$peptides->{pep_var_mod};		
	    my $pep_exp_mr=$peptides->{pep_exp_mr};
	    my $pep_score=$peptides->{pep_score};
	    my $pep_rank=$peptides->{rank};
	    my $seq=$pep_seq=$peptides->{pep_seq};	
	    my $corr_string=$peptides->{pep_scan_title};
	    my $pep_homol=$peptides->{pep_homol};
		my $pep_ident=$peptides->{pep_ident};
	    my $remark;
	    my $summitWell;
	    my @envolopeValues;
		my $comment_prt;
		my $sumSignalZero;
		my $sumSignalTwo;
		my $sumSignalFour;
		my $o_ratio;
		$pep_var_mod=~s/HASH\(.*\)//;
		if($pep_var_mod=~/Oxidation/){
			$pep_seq=$pep_seq."oxi";
		}

	    
	    if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
			$esi=1;			
			$retention=$1*60;
			$comment_prt=$comment.'/ESI/ESImode';
			@wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    				
		
	    }else{  
			$esi=0;	
			$retention="";  
			$comment_prt=$comment.'/Tof-Tof/ESImode';

			my $corr_well=$well_store{$corr_string};

		   
			@wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order);
	    }

	    if($pep_rank<=$rank_thresh){
	    	if($pep_score > $pep_score_thresh){
		$maxEnvolopeIntensity=0;
		foreach my $well_at_moment_max_search(@wells2compare_max_search){
		    foreach my $line(@$well_at_moment_max_search){  
			if(($$line[0]-$pep_calc_mr-1)>3){last;}      
			if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
				push(@envolopeValues,$$line[$index]);
			    if($$line[$index]>=$maxEnvolopeIntensity){
					$summitWell=$well_at_moment_max_search;
					$maxEnvolopeIntensity=$$line[$index];
			    }
			}					
		    }
		}
		for($d=0;$d<=$#envolopeValues;$d++){

		  	 if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   		$maxPosition=$d;
		  	 }
		   
		   } 
		    if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   	}
		
		
		

		
		if(defined($summitWell)){
		    @wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);
		    
		    
		    foreach my $well_at_moment(@wells2compare){		
			my $mass_zero;
			my $mass_two;
			my $mass_four;
			my $mass_minus;
			my $signal_minus;
			my $signal_zero;
			my $signal_two;
			my $signal_four;
			my $sn_zero;
			my $sn_two;
			my $sn_four;
			foreach my $line(@$well_at_moment){                    
			    

				if(($$line[0]-$pep_calc_mr-1)>1){last;}
			    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
				my $well_apex="$well_at_moment"."apex";
				
				if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
				    $isotope=1;
				    foreach my $apexrecord(@$well_apex){
					
					
					if(abs($$apexrecord[0]-$$line[0])<0.05){  
					    $mass_four=$$apexrecord[0];
					    $signal_four=$$apexrecord[$index];
					    $sn_four=$$apexrecord[2];
					    last;
					}elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
					    $mass_two=$$apexrecord[0];
					    $signal_two=$$apexrecord[$index];
					    $sn_two=$$apexrecord[2];
					}elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
					    $mass_zero=$$apexrecord[0];
					    $signal_zero=$$apexrecord[$index];
					    $sn_zero=$$apexrecord[2];
					}elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
					    $mass_minus=$$apexrecord[0];
					    $signal_minus=$$apexrecord[$index];
					}

				    }

				}else{ 
					
				    $isotope=0;
				    foreach my $apexrecord(@$well_apex){
					
					
					if(abs($$apexrecord[0]-$$line[0])<0.05){  
					    $mass_zero=$$apexrecord[0];
					    $signal_zero=$$apexrecord[$index];
					    $sn_zero=$$apexrecord[2];
					}elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
					    $mass_four=$$apexrecord[0];
					    $signal_four=$$apexrecord[$index];
					    $sn_four=$$apexrecord[2];
					    last;
					}elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
					    $mass_two=$$apexrecord[0];
					    $signal_two=$$apexrecord[$index];
					    $sn_two=$$apexrecord[2];
					}elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
					    $mass_minus=$$apexrecord[0];
					    $signal_minus=$$apexrecord[$index];
					}
					
				    }
				}
				if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
				    if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){     
					if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
					    $sumSignalZero+=$signal_zero;
						$sumSignalTwo+=$signal_two;
						$sumSignalFour+=$signal_four;
					    
					    print OUT "ACC:$accession\t","DESC:$description\t";
					    print OUT "QUERY:",$query,"\t"; 
					    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\t","SummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";
					    print OUT "ZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
					    $control=1;
					    last;
					}else{$remark=$remark.'(SN is high)';  last;}	
				    }else{$remark=$remark.'(Not all peaks are present)'; last;}			
				}else{
				    $remark=$remark.'(Spectrum interfering)';
				    last;
				}

			    }
			}
			
		    }
		    system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
					open(INTER,$ipc);
					@inter=<INTER>;
;
					@zero=split(/,/,$inter[1]);
					$ratezero=$zero[2];

					$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;

					@two=split(/,/,$inter[3]);
					$ratetwo=$two[2];
					$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					@four=split(/,/,$inter[5]);
					$ratefour=$four[2];
					$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					close INTER;
					if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
						$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
					}
					print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
					print OUT "QUERY:",$query,"\t"; 
		    		print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
					print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
					$control=1;

				}else{$remark=$remark.'(No matched mass is found)';}
	    }else{$remark=$remark.'(pep_score is low)';}
	    }else{$remark=$remark.'(pep_rank is above threshold)';}
	    
	    if($control == 0){     
		print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
		print OUT "QUERY:",$query,"\t"; 
		print OUT "Retention:","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
	    } 
    }

}else{
    foreach my $hit(@$hit_array){	

	$fragments=$hit->{protein};            
	if(ref($fragments) eq "ARRAY"){        
	    foreach my $fragment(@$fragments){           
		my $description=$fragment->{prot_desc};   
		my $accession=$fragment->{accession};   
		my $prot_score=$fragment->{prot_score};
		 my $prot_mass=$fragment->{prot_mass};
	
		my $peptides=$fragment->{peptide};
		if(ref($peptides) eq "ARRAY"){
		    foreach my $peptide(@$peptides){
			my $query=$peptide->{query};

 			    my $control=0;   
			    my @wells2compare;
			    my @wells2compare_max_search;
			    my $esi;
			    my $retention;
			    my $pep_calc_mr=$peptide->{pep_calc_mr};  
			    my $pep_exp_mz=$peptide->{pep_exp_mz}; 
				my $pep_exp_z=$peptide->{pep_exp_z};
				my $pep_delta=$peptide->{pep_delta};
				my $pep_expect=$peptide->{pep_expect};
				my $pep_start=$peptide->{pep_start};
				my $pep_end=$peptide->{pep_end};   
			    my $pep_var_mod=$peptide->{pep_var_mod};		
			    my $pep_exp_mr=$peptide->{pep_exp_mr};
			    my $pep_score=$peptide->{pep_score};
			    my $pep_rank=$peptide->{rank};
			    my $seq=$pep_seq=$peptide->{pep_seq};
			    my $remark;
			    my $corr_string=$peptide->{pep_scan_title};
			    my $pep_homol=$peptide->{pep_homol};
				my $pep_ident=$peptide->{pep_ident};
			    my $summitWell;
			    my @envolopeValues;
				my $comment_prt;
				my $sumSignalZero;
				my $sumSignalTwo;
				my $sumSignalFour;
				my $o_ratio;
			    $pep_var_mod=~s/HASH\(.*\)//;
			    if($pep_var_mod=~/Oxidation/){
					$pep_seq=$pep_seq."oxi";
				}

			    if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
					$esi=1;	
					$comment_prt=$comment.'/ESI/ESImode';
					$retention=$1*60; 			
					@wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    	
				
			    }else{	
					$esi=0;
					$retention="";  
					$comment_prt=$comment.'/Tof-Tof/ESImode';
					my $corr_well=$well_store{$corr_string};
					@wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order);   
			    }	     
			    if($pep_rank<=$rank_thresh){
			    	if($pep_score > $pep_score_thresh){
				$maxEnvolopeIntensity=0;
				foreach my $well_at_moment_max_search(@wells2compare_max_search){
				    foreach my $line(@$well_at_moment_max_search){     
					if(($$line[0]-$pep_calc_mr-1)>3){last;}   
					if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
						push(@envolopeValues,$$line[$index]);
					    if($$line[$index]>=$maxEnvolopeIntensity){
						$summitWell=$well_at_moment_max_search;
						$maxEnvolopeIntensity=$$line[$index];
					    }
					}					
				    }
				}

		    	for($d=0;$d<=$#envolopeValues;$d++){

		  	 		if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   				$maxPosition=$d;
		  	 		}
		   
		   		} 
		    	if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   		}	
				if(defined($summitWell)){
					@wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);
				    foreach my $well_at_moment(@wells2compare){		
					my $mass_zero;
					my $mass_two;
					my $mass_four;
					my $mass_minus;
					my $signal_minus;
					my $signal_zero;
					my $signal_two;
					my $signal_four;
					my $sn_zero;
			    	my $sn_two;
			    	my $sn_four;
					foreach my $line(@$well_at_moment){        
					    if(($$line[0]-$pep_calc_mr-1)>1){last;}
					    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){  
						my $well_apex="$well_at_moment"."apex";
						
						if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
						    $isotope=1;
						    foreach my $apexrecord(@$well_apex){
							
							
							if(abs($$apexrecord[0]-$$line[0])<0.05){  
							    
							    $mass_four=$$apexrecord[0];
							    $signal_four=$$apexrecord[$index];
							    $sn_four=$$apexrecord[2];
							    last;
							}elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
							    $mass_two=$$apexrecord[0];
							    $signal_two=$$apexrecord[$index];
							    $sn_two=$$apexrecord[2];
							}elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
							    $mass_zero=$$apexrecord[0];
							    $signal_zero=$$apexrecord[$index];
							    $sn_zero=$$apexrecord[2];
							}elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
							    $mass_minus=$$apexrecord[0];
							    $signal_minus=$$apexrecord[$index];
							}
							
							
							
						    }
						    
						    
						}else{ 
							
						    $isotope=0;
						    foreach my $apexrecord(@$well_apex){
							
							
							if(abs($$apexrecord[0]-$$line[0])<0.05){  
							    
							    $mass_zero=$$apexrecord[0];
							    $signal_zero=$$apexrecord[$index];
							    $sn_zero=$$apexrecord[2];
							}elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
							    $mass_four=$$apexrecord[0];
							    $signal_four=$$apexrecord[$index];
							    $sn_four=$$apexrecord[2];
							    last;
							}elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
							    $mass_two=$$apexrecord[0];
							    $signal_two=$$apexrecord[$index];
							    $sn_two=$$apexrecord[2];
							}elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
							    $mass_minus=$$apexrecord[0];
							    $signal_minus=$$apexrecord[$index];
							}
							
							
							
						    }
						}
						if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
						    if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){     
							if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
							    $sumSignalZero+=$signal_zero;
								$sumSignalTwo+=$signal_two;
								$sumSignalFour+=$signal_four;
							    print OUT "ACC:$accession\t","DESC:$description\t";
							    print OUT "QUERY:",$query,"\t"; 
							    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\t","SummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";
							    print OUT "ZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
							    $control=1;
							    last;
							}else{$remark=$remark.'(SN is high)'; last;}
						    }else{$remark=$remark.'(Not all peaks are present)'; last;}
						}else{
						    $remark=$remark.'(Spectrum interfering)';
						    last;
						}
						
					    }
					}
					
				    }
				   system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
					open(INTER,$ipc);
					@inter=<INTER>;
					
					@zero=split(/,/,$inter[1]);
					$ratezero=$zero[2];
					
					$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;

					@two=split(/,/,$inter[3]);
					$ratetwo=$two[2];
					$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					@four=split(/,/,$inter[5]);
					$ratefour=$four[2];
					$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					close INTER;
					if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
						$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
					}
					print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
					print OUT "QUERY:",$query,"\t"; 
		    		print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
					print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
					$control=1; 
				    
				}else{$remark=$remark.'(No matched mass is found)';}	
			    }else{$remark=$remark.'(pep_score is low)';}
			    }else{$remark=$remark.'(pep_rank is above threshold)';}   
			    if($control == 0){     
				print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
				print OUT "QUERY:",$query,"\t"; 
				print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
			    } 

		    }    
		    
		}else{
		    my $query=$peptides->{query};            
			my $control=0;  
			my @wells2compare;
			my @wells2compare_max_search;
			my $esi;
			my $retention;
			my $pep_calc_mr=$peptides->{pep_calc_mr};    
			my $pep_exp_mz=$peptides->{pep_exp_mz}; 
			my $pep_exp_z=$peptides->{pep_exp_z};
			my $pep_delta=$peptides->{pep_delta};
			my $pep_expect=$peptides->{pep_expect};
			my $pep_start=$peptides->{pep_start};
			my $pep_end=$peptides->{pep_end};  
			my $pep_var_mod=$peptides->{pep_var_mod};		
			my $pep_exp_mr=$peptides->{pep_exp_mr};
			my $pep_score=$peptides->{pep_score};
			my $pep_rank=$peptides->{rank};
			my $seq=$pep_seq=$peptides->{pep_seq};	
			my $corr_string=$peptides->{pep_scan_title};
			my $pep_homol=$peptides->{pep_homol};
			my $pep_ident=$peptides->{pep_ident};
			my $remark;
			my $summitWell;
			my @envolopeValues;
			my $comment_prt;
			my $sumSignalZero;
			my $sumSignalTwo;
			my $sumSignalFour;
			my $o_ratio;
			$pep_var_mod=~s/HASH\(.*\)//;
			if($pep_var_mod=~/Oxidation/){
			$pep_seq=$pep_seq."oxi";
			}

			if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
			    $esi=1;	
			    $comment_prt=$comment.'/ESI/ESImode';
			    
			    $retention=$1*60;
			    
			    @wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    				
			    
			}else{  
			    $esi=0;	
			    $retention="";  
			    $comment_prt=$comment.'/Tof-Tof/ESImode';

			    my $corr_well=$well_store{$corr_string};
				
			    @wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order);
			}
			
			if($pep_rank<=$rank_thresh){
				if($pep_score > $pep_score_thresh){
			    $maxEnvolopeIntensity=0;
			    foreach my $well_at_moment_max_search(@wells2compare_max_search){
				foreach my $line(@$well_at_moment_max_search){   
				    if(($$line[0]-$pep_calc_mr-1)>3){last;}     
				    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
				    	push(@envolopeValues,$$line[$index]);
					if($$line[$index]>=$maxEnvolopeIntensity){
					    $summitWell=$well_at_moment_max_search;
					    $maxEnvolopeIntensity=$$line[$index];
					}
				    }					
				}
			    }
			    
			    for($d=0;$d<=$#envolopeValues;$d++){

		  	 		if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   				$maxPosition=$d;
		  	 		}
		   
		   		} 
		    	if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   		}
			    
			    

			    if(defined($summitWell)){
				@wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);
			    	
				foreach my $well_at_moment(@wells2compare){		
				    my $mass_zero;
				    my $mass_two;
				    my $mass_four;
				    my $mass_minus;
				    my $signal_minus;
				    my $signal_zero;
				    my $signal_two;
				    my $signal_four;
				    my $sn_zero;
			    	my $sn_two;
			    	my $sn_four;
				    foreach my $line(@$well_at_moment){					                    
					if(($$line[0]-$pep_calc_mr-1)>1){last;}
					if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
					    my $well_apex="$well_at_moment"."apex";
					    
					    if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
						$isotope=1;
						foreach my $apexrecord(@$well_apex){
						    
						    
						    if(abs($$apexrecord[0]-$$line[0])<0.05){  
							
								$mass_four=$$apexrecord[0];
								$signal_four=$$apexrecord[$index];
								$sn_four=$$apexrecord[2];
								last;
						    }elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
				    			$mass_two=$$apexrecord[0];
				    			$signal_two=$$apexrecord[$index];
				    			$sn_two=$$apexrecord[2];
						    }elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
				    			$mass_zero=$$apexrecord[0];
				    			$signal_zero=$$apexrecord[$index];
				    			$sn_zero=$$apexrecord[2];
						    }elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
				    			$mass_minus=$$apexrecord[0];
				    			$signal_minus=$$apexrecord[$index];
						    }
						    
						    
						    
						}
						
						
					    }else{ 
					    	
						$isotope=0;
						foreach my $apexrecord(@$well_apex){
						    
						    
						    if(abs($$apexrecord[0]-$$line[0])<0.05){  
							
								$mass_zero=$$apexrecord[0];
								$signal_zero=$$apexrecord[$index];
								$sn_zero=$$apexrecord[2];
						    }elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
				    			$mass_four=$$apexrecord[0];
				    			$signal_four=$$apexrecord[$index];
				    			$sn_four=$$apexrecord[2];
				    			last;
						    }elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
				    			$mass_two=$$apexrecord[0];
				    			$signal_two=$$apexrecord[$index];
				    			$sn_two=$$apexrecord[2];
						    }elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
				    			$mass_minus=$$apexrecord[0];
				    			$signal_minus=$$apexrecord[$index];
						    }
						    
						    
						    
						}
					    }
					    if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
						if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){     
						    if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
							$sumSignalZero+=$signal_zero;
							$sumSignalTwo+=$signal_two;
							$sumSignalFour+=$signal_four;
							print OUT "ACC:$accession\t","DESC:$description\t";
							print OUT "QUERY:",$query,"\t"; 
							print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\t","SummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";
							print OUT "ZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
							$control=1;
							last;
						    }else{ $remark=$remark.'(SN is high)';  last;}
						}else{$remark=$remark.'(Not all peaks are present)'; last;}				
					    }else{
						$remark=$remark.'(Spectrum interfering)';
						last;
					    }
					
					}
				    }
				    
				}
				system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
					open(INTER,$ipc);
					@inter=<INTER>;
					
					@zero=split(/,/,$inter[1]);
					$ratezero=$zero[2];

					$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					@two=split(/,/,$inter[3]);
					$ratetwo=$two[2];
					$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					@four=split(/,/,$inter[5]);
					$ratefour=$four[2];
					$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					close INTER;
					if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
						$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
					}
					print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
					print OUT "QUERY:",$query,"\t"; 
		    		print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
					print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
					$control=1;
				
				
			    }else{$remark=$remark.'(No matched mass is found)';}
			}else{$remark=$remark.'(pep_score is low)';}
			}else{$remark=$remark.'(pep_rank is above threshold)';}
			if($control == 0){     
			    print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
			    print OUT "QUERY:",$query,"\t"; 
			    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
			} 
			
			
			
			

		}
		

		
	    } 
	}else{

	    my $description=$fragments->{prot_desc};
	    my $accession=$fragments->{accession};
	    my $prot_score=$fragments->{prot_score};
	    my $prot_mass=$fragments->{prot_mass}; 	
	    my $peptides=$fragments->{peptide};
	    if(ref($peptides) eq "ARRAY"){
		foreach my $peptide(@$peptides){
		    my $query=$peptide->{query};
		    

			my $control=0;   
			my @wells2compare;
			my @wells2compare_max_search;
			my $esi;
			my $retention;
			my $pep_calc_mr=$peptide->{pep_calc_mr}; 
			my $pep_exp_mz=$peptide->{pep_exp_mz}; 
			my $pep_exp_z=$peptide->{pep_exp_z};
			my $pep_delta=$peptide->{pep_delta};
			my $pep_expect=$peptide->{pep_expect};
			my $pep_start=$peptide->{pep_start};
			my $pep_end=$peptide->{pep_end};    
			my $pep_var_mod=$peptide->{pep_var_mod};		
			my $pep_exp_mr=$peptide->{pep_exp_mr};
			my $pep_score=$peptide->{pep_score};
			my $pep_rank=$peptide->{rank};
			my $seq=$pep_seq=$peptide->{pep_seq};
			my $corr_string=$peptide->{pep_scan_title};
			my $pep_homol=$peptide->{pep_homol};
			my $pep_ident=$peptide->{pep_ident};
			my $remark;
			my $summitWell;
			my @envolopeValues;
			my $comment_prt;
			my $sumSignalZero;
			my $sumSignalTwo;
			my $sumSignalFour;
			my $o_ratio;
			
			$pep_var_mod=~s/HASH\(.*\)//;
			if($pep_var_mod=~/Oxidation/){
				$pep_seq=$pep_seq."oxi";
			}
		
			if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
			    $esi=1;			
			    $retention=$1*60;
			    $comment_prt=$comment.'/ESI/ESImode';
			    @wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    
			    
			}else{ 	
			    $esi=0; 
			    $retention="";  
			    $comment_prt=$comment.'/Tof-Tof/ESImode';

			    my $corr_well=$well_store{$corr_string};
			    @wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order); 
			    
			}
			if($pep_rank<=$rank_thresh){ 
				if($pep_score > $pep_score_thresh){
			    $maxEnvolopeIntensity=0;
			    foreach my $well_at_moment_max_search(@wells2compare_max_search){
				foreach my $line(@$well_at_moment_max_search){  
				    if(($$line[0]-$pep_calc_mr-1)>3){last;}      
				    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
				    	push(@envolopeValues,$$line[$index]);
					if($$line[$index]>=$maxEnvolopeIntensity){
					    $summitWell=$well_at_moment_max_search;
					    $maxEnvolopeIntensity=$$line[$index];
					}
				    }					
				}
			    }
			    for($d=0;$d<=$#envolopeValues;$d++){

		  	 		if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   				$maxPosition=$d;
		  	 		}
		   
		   		} 
		    	if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   		}
			    

			    if(defined($summitWell)){
				@wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);
				
				foreach my $well_at_moment(@wells2compare){		
				    my $mass_zero;
				    my $mass_two;
				    my $mass_four;
				    my $mass_minus;
				    my $signal_minus;
				    my $signal_zero;
				    my $signal_two;
				    my $signal_four;
				    my $sn_zero;
			    	my $sn_two;
			    	my $sn_four;
				    foreach my $line(@$well_at_moment){                     
					if(($$line[0]-$pep_calc_mr-1)>1){last;}
					if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
					    
					    my $well_apex="$well_at_moment"."apex";
					    if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
						$isotope=1;
						foreach my $apexrecord(@$well_apex){
						    
						    
				    		if(abs($$apexrecord[0]-$$line[0])<0.05){  
						    
						    	$mass_four=$$apexrecord[0];
						    	$signal_four=$$apexrecord[$index];
						    	$sn_four=$$apexrecord[2];
						    	last;
				    		}elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
						    	$mass_two=$$apexrecord[0];
						    	$signal_two=$$apexrecord[$index];
						    	$sn_two=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
						    	$mass_zero=$$apexrecord[0];
						    	$signal_zero=$$apexrecord[$index];
						    	$sn_zero=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
						   		$mass_minus=$$apexrecord[0];
						    	$signal_minus=$$apexrecord[$index];
				    		}
						
						
						
					    }
						
						
					    }else{ 
					    	
						$isotope=0;
						foreach my $apexrecord(@$well_apex){
						    
						    
						    if(abs($$apexrecord[0]-$$line[0])<0.05){  
							
								$mass_zero=$$apexrecord[0];
								$signal_zero=$$apexrecord[$index];
								$sn_zero=$$apexrecord[2];
							    }elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
								$mass_four=$$apexrecord[0];
								$signal_four=$$apexrecord[$index];
								$sn_four=$$apexrecord[2];
								last;
							    }elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
								$mass_two=$$apexrecord[0];
								$signal_two=$$apexrecord[$index];
								$sn_two=$$apexrecord[2];
							    }elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
								$mass_minus=$$apexrecord[0];
								$signal_minus=$$apexrecord[$index];
							    }
						    
						    
						    
						}
					    }
					    if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
						if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){     
						    if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
							$sumSignalZero+=$signal_zero;
							$sumSignalTwo+=$signal_two;
							$sumSignalFour+=$signal_four;
							
							print OUT "ACC:$accession\t","DESC:$description\t";
							print OUT "QUERY:",$query,"\t"; 
							print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\t","SummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";
							print OUT "\tZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
							$control=1;
							last;
						    }else{ $remark=$remark.'(SN is high)'; last;}	
						}else{$remark=$remark.'(Not all peaks are present)'; last;}
					    }else{
						$remark=$remark.'(Spectrum interfering)';
						last;
					    }
					 
					}
				    }
				}
				system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
					open(INTER,$ipc);
					@inter=<INTER>;
					
					@zero=split(/,/,$inter[1]);
					$ratezero=$zero[2];

					$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;

					@two=split(/,/,$inter[3]);
					$ratetwo=$two[2];
					$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					@four=split(/,/,$inter[5]);
					$ratefour=$four[2];
					$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					close INTER;
					if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
						$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
					}
					print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
					print OUT "QUERY:",$query,"\t"; 
		    		print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
					print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
					$control=1;
				
			    }else{$remark=$remark.'(No matched mass is found)';} 
			}else{$remark=$remark.'(pep_score is low)';}
			}else{$remark=$remark.'(pep_rank is above threshold)';}
			if($control == 0){     
			    print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
			    print OUT "QUERY:",$query,"\t"; 
			    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
			} 
			
			
			

		}
		
	    }else{
		my $query=$peptides->{query};
		

		    my $control=0;   
		    my @wells2compare;
		    my @wells2compare_max_search;
		    my $esi;
		    my $retention;
		    my $pep_calc_mr=$peptides->{pep_calc_mr};    
		    my $pep_exp_mz=$peptides->{pep_exp_mz}; 
			my $pep_exp_z=$peptides->{pep_exp_z};
			my $pep_delta=$peptides->{pep_delta};
			my $pep_expect=$peptides->{pep_expect};
			my $pep_start=$peptides->{pep_start};
			my $pep_end=$peptides->{pep_end};  

		    my $pep_var_mod=$peptides->{pep_var_mod};		
		    my $pep_exp_mr=$peptides->{pep_exp_mr};                
		    my $pep_score=$peptides->{pep_score};
		    my $pep_rank=$peptides->{rank};
		    my $seq=$pep_seq=$peptides->{pep_seq};  
		    my $corr_string=$peptides->{pep_scan_title};
		    my $pep_homol=$peptides->{pep_homol};
			my $pep_ident=$peptides->{pep_ident};
		    my $remark;
		    my $summitWell;
		    my @envolopeValues;
			my $comment_prt;
			my $sumSignalZero;
			my $sumSignalTwo;
			my $sumSignalFour;
			my $o_ratio;
			
		    $pep_var_mod=~s/HASH\(.*\)//;
		    if($pep_var_mod=~/Oxidation/){
				$pep_seq=$pep_seq."oxi";
			}

		    
		    
		    if($corr_string=~/Cmpd.*,\s\+MSn\(.*\),\s(.*)min/){
				$esi=1;		   
				$retention=$1*60;
				$comment_prt=$comment.'/ESI/ESImode';
				@wells2compare_max_search=&well2inspectesi($retention,$offset,$start_alpha,$well_toler_max_search,$flow_type,$colle_sec);    
			
		    }else{  
				$esi=0;
				$retention="";  
				$comment_prt=$comment.'/Tof-Tof/ESImode';

				my $corr_well=$well_store{$corr_string};

		
				@wells2compare_max_search=&well2inspect($corr_well,$well_toler_max_search,\@well_order); 
		    }
		    
		    if($pep_rank<=$rank_thresh){
		    	if($pep_score > $pep_score_thresh){
			$maxEnvolopeIntensity=0;
			foreach my $well_at_moment_max_search(@wells2compare_max_search){
			    foreach my $line(@$well_at_moment_max_search){     
				if(($$line[0]-$pep_calc_mr-1)>3){last;}   
				if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
					push(@envolopeValues,$$line[$index]);
				    if($$line[$index]>=$maxEnvolopeIntensity){
						$summitWell=$well_at_moment_max_search;
						$maxEnvolopeIntensity=$$line[$index];
				    }
				}					
			    }
			}
			for($d=0;$d<=$#envolopeValues;$d++){

		  	 if($envolopeValues[$d]==$maxEnvolopeIntensity){
		   		$maxPosition=$d;
		  	 }
		   
		   } 
		    if($maxPosition==0 || $maxPosition == $#envolopeValues){
		  			 $comment_prt=$comment_prt.'(Maximum apears at boundary)';
		   	}

			if(defined($summitWell)){
			    @wells2compare=&well2inspect($summitWell,$well_toler,\@well_order);
			    
			    
			    foreach my $well_at_moment(@wells2compare){		
				my $mass_zero;
				my $mass_two;
				my $mass_four;
				my $mass_minus;	
				my $signal_minus;
				my $signal_zero;
				my $signal_two;
				my $signal_four;
				my $sn_zero;
			    my $sn_two;
			    my $sn_four;
				foreach my $line(@$well_at_moment){        
					if(($$line[0]-$pep_calc_mr-1)>1){last;}
				    if(abs($$line[0]-$pep_calc_mr-1)<=$mass_devi){
					my $well_apex="$well_at_moment"."apex";
					if($pep_var_mod=~/18O\(2\)\s\(C-term\)/){ 
					    $isotope=1;
					    foreach my $apexrecord(@$well_apex){
						
						
				    		if(abs($$apexrecord[0]-$$line[0])<0.05){  
						    
						    	$mass_four=$$apexrecord[0];
						    	$signal_four=$$apexrecord[$index];
						    	$sn_four=$$apexrecord[2];
						    	last;
				    		}elsif(($$apexrecord[0]-$$line[0])<-1.91 && ($$apexrecord[0]-$$line[0])>-2.09){
						    	$mass_two=$$apexrecord[0];
						    	$signal_two=$$apexrecord[$index];
						    	$sn_two=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<-3.91 && ($$apexrecord[0]-$$line[0])>-4.09){
						    	$mass_zero=$$apexrecord[0];
						    	$signal_zero=$$apexrecord[$index];
						    	$sn_zero=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<-4.91 && ($$apexrecord[0]-$$line[0])>-5.09){
						    	$mass_minus=$$apexrecord[0];
						    	$signal_minus=$$apexrecord[$index];
				    		}
						
						
						
					    }
					    
					    
					}else{ 
						 
					    $isotope=0;
					    foreach my $apexrecord(@$well_apex){
						
						
				    		if(abs($$apexrecord[0]-$$line[0])<0.05){  
						    
						    	$mass_zero=$$apexrecord[0];
						    	$signal_zero=$$apexrecord[$index];
						    	$sn_zero=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<4.09 && ($$apexrecord[0]-$$line[0])>3.91){
						    	$mass_four=$$apexrecord[0];
						    	$signal_four=$$apexrecord[$index];
						    	$sn_four=$$apexrecord[2];
						    	last;
				    		}elsif(($$apexrecord[0]-$$line[0])<2.09 && ($$apexrecord[0]-$$line[0])>1.91){
						    	$mass_two=$$apexrecord[0];
						    	$signal_two=$$apexrecord[$index];
						    	$sn_two=$$apexrecord[2];
				    		}elsif(($$apexrecord[0]-$$line[0])<-0.91 && ($$apexrecord[0]-$$line[0])>-1.09){
						    	$mass_minus=$$apexrecord[0];
						    	$signal_minus=$$apexrecord[$index];
				    		}
						
				
						
					    }
					}
					if(!defined($signal_minus) || (defined($signal_minus) && defined($signal_zero) && ($signal_minus/$signal_zero)<$minus_threshold)){
					    if(defined($signal_zero) && defined($signal_two) && defined($signal_four)){     
						if($sn_zero>$sn_thresh || $sn_two>$sn_thresh || $sn_four>$sn_thresh){
						    $sumSignalZero+=$signal_zero;
							$sumSignalTwo+=$signal_two;
							$sumSignalFour+=$signal_four;
						    print OUT "ACC:$accession\t","DESC:$description\t";
						    print OUT "QUERY:",$query,"\t"; 
						    print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\tMaldi_well:",$well_at_moment,"\t","SummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";
						    print OUT "\tZERO:$mass_zero\|$signal_zero\tTWO:$mass_two\|$signal_two\tFOUR:$mass_four\|$signal_four\t\n\n";
						    $control=1;
						    last;
						}else{$remark=$remark.'(SN is high)'; last;}
					    }else{$remark=$remark.'(Not all peaks are present)'; last;}	
					}else{
					    $remark=$remark.'(Spectrum interfering)';
					    last;
					}
					
				    }
				}
				
			    }
			    system("ipc -a $seq -f 100 -d 0 -c H > $ipc");
					open(INTER,$ipc);
					@inter=<INTER>;
					print "@inter\n";
					@zero=split(/,/,$inter[1]);
					$ratezero=$zero[2];

					$ratezero=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;

					@two=split(/,/,$inter[3]);
					$ratetwo=$two[2];
					$ratetwo=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					@four=split(/,/,$inter[5]);
					$ratefour=$four[2];
					$ratefour=~s/\srel\.\sInt\.\=\s(.*)\n/$1/;
					close INTER;
					if(defined($ratezero) && defined($ratetwo) && defined($ratefour) && ($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero)!=0 && $pep_seq=~/[KR](oxi)?$/){
						$o_ratio=$sumSignalZero/($sumSignalFour-($ratefour*$sumSignalZero/$ratezero)+$sumSignalTwo*(1-($ratetwo/$ratezero))-(1-($ratetwo/$ratezero))*$ratetwo*$sumSignalZero/$ratezero);
					}
					print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
					print OUT "QUERY:",$query,"\t"; 
		    		print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t","\tSummitWell:",$summitWell,"\t\t\tComment:$comment_prt","\t";    
						
					print OUT "\tTotal_zero:$sumSignalZero\tTotal_two:$sumSignalTwo\tTotal_four:$sumSignalFour\t\tR0:$ratezero\tR2:$ratetwo\tR4:$ratefour\tRATIO(16/18):\t$o_ratio\tPepQuantQualityIndex:\t$pepQuantQualityIndex\n\n";
					$control=1;
			    
			    
			}else{$remark=$remark.'(No matched mass is found)';}
		    }else{$remark=$remark.'(pep_score is low)';}
		    }else{$remark=$remark.'(pep_rank is above threshold)';}
		    if($control == 0){     
			print OUT "ACC:$accession\t","DESC:$description\t","MW:$prot_mass\t";
			print OUT "QUERY:",$query,"\t"; 
			print OUT "Retention:$retention","\tCalc_mass:",$pep_calc_mr,"\tObs.mass:",$pep_exp_mr,"\tDelta:",$pep_delta,"\tObs.mz:",$pep_exp_mz,"\tCharge:",$pep_exp_z,"\tStart:",$pep_start,"\tEnd:",$pep_end,"\tProt_score:",$prot_score,"\tpep_homol:",$pep_homol,"\tpep_ident:",$pep_ident,"\tpep_score:",$pep_score,"\tpep_rank:",$pep_rank,"\tpep_seq:",$pep_seq,"\tVar_mod:",$pep_var_mod,"\t\t\t\t\tComment:$comment_prt","\tRemark:",$remark,"\t"x4,"\n\n";
		    }   

	    }

	}
    }
}

}

close OUT;


sub well2inspect{
    my $well;
    my @well_order;
    ($well,$tolerance,$well_order_ref)=@_;   

    my $well_index; 
    my @well2inspect; 
    my $count;  

    @well_order=@$well_order_ref;

    foreach $location(@well_order){

	if("$location" eq "$well"){
	    $well_index=$count;
	    last;
	}
	$count++;    
    }   
    
    
    @well2inspect=(@well_order[($well_index-$tolerance)..($well_index+$tolerance)]);
    return @well2inspect;
}


sub well2inspectesi{
    my ($retention,$offset,$start,$tolerance,$flow_type,$colle_sec)=@_;   
    my $direction=1;    
    my @well_order; 
    my $well_index;   
    for ($start..Z){
	my $alpha=$_;
	my @digit_order=(1..24);
	
	if($direction==(-1)){
	    @digit_order=reverse(@digit_order);
	}
	foreach(@digit_order){
	    push(@well_order,"$alpha"."$_");
	}
	if($flow_type==(-1)){
	    $direction=$direction*(-1);
	}
    }
    
    my $pre_well_index=(($retention-$offset)/$colle_sec)-1; 
    
    if($pre_well_index-int($pre_well_index)!=0){
        $well_index=int($pre_well_index)+1;    
    }else{
        $well_index=$pre_well_index;          
    }
    
    my @well2inspect=(@well_order[($well_index-$tolerance)..($well_index+$tolerance)]);
    return @well2inspect;
    
    
    
    
}













