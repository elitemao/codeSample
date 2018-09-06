package gonParser;

public class EntityLabelGenerator {
	int i = 0;

	public EntityLabelGenerator() {

	}

	public String generate() {
		String entityLabel = "e" + i;
		i++;
		return entityLabel;
	}
}
