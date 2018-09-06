#!usr/perl/bin -w
##input the file to sort and calculate avg, file to save the calculated result, file to save the plot in the command line
##the negative quantification won't be included in the calculation of average and standard deviation
##this is suitable for the merged file from O16O18("16/18") or Silac("H/L")
##sort the records belonging to certain protein by peptide sequence
##for the codes to sum peak area spanning tolerance wells

use Math::NumberCruncher;
use GD::Graph::points;

$input=shift(@ARGV);       
$output=shift(@ARGV);      
$plot_out=shift(@ARGV);

print "IN:$input\n";
print "OUT:$output\n";

open(IN,$input)||die("cann't open the merged file: $!");
open(OUT,">$output")||die("cann't open the file to save:$!");

while($line=<IN>){
	chomp($line);
if($line=~/Total_zero:.*Total_two:.*Total_four:/){

    if($line=~m/^ACC:gi\|(\d*)\t/){  
	
		$gi=$1;       
		$gi_array="gi".$gi;      
		push(@$gi_array,$line);  
		$gi_count{"$gi_array"}=1; 
    }elsif($line=~/^ACC:([\d\w\_]*)\t/){ 

		$gi_array=$1;

			push(@$gi_array,$line);  
			$gi_count{"$gi_array"}=1; 
     }    
}
}

@key=keys(%gi_count);  

foreach my$gino(@key){  

    my @for_statis=();
    my @identified_pep=();
    my @quanted_pep=();
    my $std=undef;
    my $oratio_average;
    my $totalWeightedRatio;
	my $weightedORatio;
    my $totalQuantIndex;
    my @prot_score;
    my $high_prot_score;
    my $low_prot_score;
    foreach my $same_gi(@$gino){  
        if($same_gi=~/pep_seq:(\w*)\t/){
        	$pep_seq=$1;
           push(@identified_pep,$pep_seq);       
        
        }
        if($same_gi=~/Prot_score:(\d*)\t/){
           push(@prot_score,$1);
        }

		if($same_gi=~/RATIO\(.*\/.*\):\t(.*)\tPepQuantQualityIndex:\t(.*)/){   
	  
	   		my $oratio=$1;	    
	   		my $quantIndex=$2;

	    	if($oratio ne "" && $oratio>=0){
	    		$totalWeightedRatio=$totalWeightedRatio+$quantIndex*$oratio;
				$totalQuantIndex=$totalQuantIndex+$quantIndex;
				push(@for_statis,$oratio);  
				push(@quanted_pep,$pep_seq);

	    	}
		}
    }
    @unique_identified_pep=Math::NumberCruncher->Unique(\@identified_pep);
    @unique_quanted_pep=Math::NumberCruncher->Unique(\@quanted_pep);
    ($high_prot_score,$low_prot_score)=Math::NumberCruncher->Range(\@prot_score);

    if(scalar(@for_statis) != 0){               
	$ref=Math::NumberCruncher->new();

	$oratio_average=$ref->Mean(\@for_statis);

	$std=$ref->StandardDeviation(\@for_statis);
	$gi_average{"$gino"}=$oratio_average;   
	$gi_std{"$gino"}=$std;           	
	$gi_weighted_ratio{"$gino"}=$weightedORatio;
	$gi_identified_pep{"$gino"}=scalar(@unique_identified_pep);
	$gi_quanted_pep{"$gino"}=scalar(@unique_quanted_pep);
	$gi_high_prot_score{"$gino"}=$high_prot_score;
    }else{

	$gi_average{"$gino"}=-99999;   #
	$gi_std{"$gino"}=-99999;  
	$gi_weighted_ratio{"$gino"}=-99999;
	$gi_identified_pep{"$gino"}=scalar(@unique_identified_pep);
	$gi_quanted_pep{"$gino"}=scalar(@unique_quanted_pep);
    $gi_high_prot_score{"$gino"}=$high_prot_score;
    }
}


foreach(sort {$gi_average{$b}<=>$gi_average{$a}} keys(%gi_average)){ 
    push(@sort_gi,$_); 
    push(@sort_value,$gi_average{$_}); 
	push(@sorted_weighted_oratio,$gi_weighted_ratio{$_});
	push(@sorted_identified_pep,$gi_identified_pep{$_});
	push(@sorted_quanted_pep,$gi_quanted_pep{$_});
	push(@sorted_high_prot_score,$gi_high_prot_score{$_});
}

$log_ratio_max=0;
$log_ratio_min=0;
foreach(@sort_value){              

	if($_==0){
		push(@log_sort_value,-99999);
		$log_ratio_min=-5;
		next;
	}
    $log_machine=Math::NumberCruncher->new();
    my $log_ratio_avg=$log_machine->log($_);

    push(@log_sort_value,$log_ratio_avg);
    if($log_ratio_avg>$log_ratio_max){$log_ratio_max=$log_ratio_avg;}  
    if($log_ratio_avg<$log_ratio_min){$log_ratio_min=$log_ratio_avg;}  
}

$protein_number=@sort_gi;

for($i=0;$i < $protein_number;$i++){
    print OUT $sort_gi[$i],"\t\=\>\t","Identi.Unique.Pep:\t",$sorted_identified_pep[$i],"\tQuant.Unique.Pep:\t",$sorted_quanted_pep[$i],"\tBest Prot Score:\t",$sorted_high_prot_score[$i],"\tAVG:\t",$sort_value[$i],"\tSTDEV:\t",$gi_std{"$sort_gi[$i]"},"\tWeightedRatio:\t",$sorted_weighted_oratio[$i],"\n";
    my $gino=$sort_gi[$i];

    @sorted_by_peptide=map{$_->[0]}
    sort {
	@a_fields=@$a[1..$#$a];
	@b_fields=@$b[1..$#$b];
	$a_fields[8] cmp $b_fields[8];
    } map {[$_,split /\t/]}@$gino;    
    my $gistring=join("\n",@sorted_by_peptide);
    
    print OUT "$gistring\n\n";    
}

$graph=GD::Graph::points->new(1000,700);
@use_axes=(1,1);
$graph->set(
	    x_label=>'GI number',
	    y_label=>'Relative enrichment(H/L,Log)',

	    x_labels_vertical=>1,

	    zero_axis=>1,
	    markers=>4,
	    marker_size=>2,

	    use_axis=>\@use_axes,

	    y_label_skip=>2,

	    y_max_value=>$log_ratio_max,
	    y_min_value=>$log_ratio_min

	    ) or die $graph->error;


@data=(\@sort_gi,\@log_sort_value);

$gd=$graph->plot(\@data) or die $graph->error;

open(IMG,">$plot_out") or die $!;

binmode IMG;
print IMG $gd->png;

close IMG;
close IN;
close OUT; 


