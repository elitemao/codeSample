package gonParser;


public class MetabolicProcessLabelGenerator {
	int i = 0;

	public MetabolicProcessLabelGenerator() {

	}

	public String generate() {
		String processLabel = "p" + i;
		i++;
		return processLabel;
	}
}
