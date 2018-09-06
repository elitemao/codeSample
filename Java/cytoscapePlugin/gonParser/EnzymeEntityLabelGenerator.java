package gonParser;

public class EnzymeEntityLabelGenerator {
	static int i = 0;

	public EnzymeEntityLabelGenerator() {

	}

	public String generate() {
		String entityLabel = "e" + i;
		i++;
		return entityLabel;
	}
}
