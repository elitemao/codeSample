package gonParser;

//to comply with the default label nomenclature in Cytoscape

public class ProcessLabelGenerator {
	int i = 0;

	public ProcessLabelGenerator() {

	}

	public String generate() {
		String processLabel = "p" + i;
		i++;
		return processLabel;
	}
}
