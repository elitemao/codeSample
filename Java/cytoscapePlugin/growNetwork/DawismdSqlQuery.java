package growNetwork;

public class DawismdSqlQuery {

	public static String spaccToGeneId="select id from kegg_genes_dblinks where dbname=? AND identifier=?";
	public static String geneIdToPathwayId="select org,number,name from kegg_genes_pathway where id=?";
	public static String pathwayIdToReactionId="select reaction from kegg_pathway_reaction where entry=?";
	public static String reactionIdToEquation="select equation from kegg_reaction where entry=?";
	public static String keggCompIdToPathwayId="select number,org,name from dawismd.kegg_compound_pathway where entry=?";
	public static String refPathwayIdToCsmlString="select csml from redoxnw.kegg_ref_pathway_csml where kegg_ref_pathway_id=?";
	public static String ecToSpacc="select primary_accession from enzyme_uniprot where ec_number= ? ";
	public static String ecToCommonNameByKegg="select name from kegg_enzyme_name where entry=?";
	public static String ecToCommonNameByBrenda="select bsynonym from brenda_synonyms where enzyme=?";
	public static String ecToPathwayByKegg="select org,number,name from kegg_enzyme_pathway where entry=?";
	public static String spidToEcByEnzyme_UniprotTableDawismd="select ec_number from enzyme_uniprot where entry_name=?";


}
