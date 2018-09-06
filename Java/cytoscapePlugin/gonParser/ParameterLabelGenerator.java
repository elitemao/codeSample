package gonParser;

public class ParameterLabelGenerator {
	int i = 0;

	public ParameterLabelGenerator() {

	}

	public String generate() {
		String parameterLabel = "m" + i;
		i++;
		return parameterLabel;
	}
}
