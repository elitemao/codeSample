package gonParser;


public class Connector {
	String fromEntityOrProcessLabel;
	
	String connectorLabel;
	
	String connectorName;
	String toEntityOrProcessLabel;
	
	String threshold;

//	public Connector(){} 
		
	public Connector(String fromEntityOrProcessLabel_, String connectorLabel_,
			String connectorName_, String toEntityOrProcessLabel_,
			String threshold_) {
		this.fromEntityOrProcessLabel = fromEntityOrProcessLabel_;
		this.connectorLabel = connectorLabel_;
		this.connectorName = connectorName_;
		this.toEntityOrProcessLabel = toEntityOrProcessLabel_;
		this.threshold = threshold_;
	}

	public String getSourceObjectLabel() {
		return fromEntityOrProcessLabel;
	}

	public String getConnectorLabel() {
		return connectorLabel;
	}

	
	public String getConnectorName() {
		return connectorName;

	}

	public String getSinkObjectLabel() {

		return toEntityOrProcessLabel;
	}

	
	public String getThreshold() {
		return threshold;
	}
}
