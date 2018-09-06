package gonParser;


public class MetabolicConnectorLabelGenerator {
	int i = 0;

	public MetabolicConnectorLabelGenerator() {

	}

	public String generate() {
		String connectorLabel = "c" + i;
		i++;
		return connectorLabel;
	}

}
