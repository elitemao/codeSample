package gonParser;


public class MetaboliteEntityLabelGenerator {
	static int i = 0;

	public MetaboliteEntityLabelGenerator() {

	}

	public String generate() {
		String entityLabel = "m" + i;
		i++;
		return entityLabel;
	}
}
