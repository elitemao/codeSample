package growNetwork;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
import java.util.*;

import webService.WrapperSimple;
import growNetwork.DawismdSqlQuery;
// EcToSpaccWS-------------
//v0: use enzyme_enzyme2uniprot table in DawisMd
//v0 20110702: change the query syntax for dawismd on agbi

public class EcToSpaccWS {
//	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//	static final String DATABASE_URL = "jdbc:mysql://bdbhost/RedoxNW";
	String spacc = "";
	ArrayList spaccStore = new ArrayList();

	public EcToSpaccWS(String ec) {

		

//			Class.forName(JDBC_DRIVER);
//
//			Connection connection = DriverManager.getConnection(DATABASE_URL,
//					"lhmao", "2ujiijgl");
			// String
			// warning=((connection.getWarnings()).getNextWarning()).toString();
			// System.out.println("wariningingi:"+warning);

//			String query1 = "select primary_accession from enzyme_uniprot where ec_number= ? ";

			WrapperSimple w=new WrapperSimple();
			String[] attributes={ec};
			
			Vector v=w.getResults(1, DawismdSqlQuery.ecToSpacc, attributes);
			Iterator it=v.iterator();
			
//			PreparedStatement ps1 = connection.prepareStatement(query1);

//			ps1.setString(1, ec);
//			ResultSet rs1 = ps1.executeQuery();

			while (it.hasNext()) {
				
				String spacc = ((String[])(it.next()))[0];
				if(!spacc.equals("")){
					spaccStore.add(spacc);
				}
			}

		

	}

	public ArrayList getSpacc() {

		return spaccStore;
	}

	public static void main(String[] args) {
		EcToSpaccWS stc = new EcToSpaccWS(args[0]);
		ArrayList<String> spaccs = stc.getSpacc();
		// String x="";
		for (String x : spaccs) {
			System.out.println("Spacc:" + x);
		}

	}

}
