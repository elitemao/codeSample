package gonParser;
// to comply with the default label nomenclature in Cytoscape
public class ConnectorLabelGenerator{

	int i;

	public ConnectorLabelGenerator() {
		i=0;
	}
	
	
	public String generate() {
		String connectorLabel = "c" + i;
		i++;
		return connectorLabel;
	}

}




