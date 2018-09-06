package growNetwork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

//import webService.EstablishConnectionDawisMd;

import webService.WrapperSimple;

// modified from SpidToEc.
// use webservice instead of bdbhost
// deactivate EstablishConnectionDawisMd
//v1 20110708: return a list of ec number. Before it only return one ec.
//v1 20110717: use dawismd on agbi instead of redoxnw

/**
 * @author  hang-mao
 */
public class SpidToEcWS1 {
	@SuppressWarnings("unchecked")
	//	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	//	static final String DATABASE_URL = "jdbc:mysql://bdbhost/RedoxNW?autoReconnect=true";
	ArrayList<String> ecList = new ArrayList<String>();

	
	public static void main(String[] args) {
		SpidToEcWS1 stc = new SpidToEcWS1(args[0]);
		System.out.println("EC:" + stc.getEc());
	}
	public SpidToEcWS1(String spid) {
//		String query1 = "select ec from unip_ec where spid=?";


//		new EstablishConnectionDawisMd();
		WrapperSimple w = new WrapperSimple();

		String[] details = {spid};  ///select 10 reactions in case of too many related enzymes like for "C00026" 

//		Vector v = w.getResults(2, query1,details);// 2 is redoxnw, 1 is dawismd
		
		//use enzyme_uniprot table in dawismd on agbi
		Vector v=w.getResults(1, DawismdSqlQuery.spidToEcByEnzyme_UniprotTableDawismd, details);

		Iterator it = v.iterator();
		while (it.hasNext()) {
			String[] results = (String[]) it.next();
			String ec=results[0];
			ecList.add(ec);
			//				reactionEquation.put(results[0],results[3]);
			//				reactionDefinition.put(results[0], results[2]);
		}


		//			Class.forName(JDBC_DRIVER);
		//
		//			Connection connection = DriverManager.getConnection(DATABASE_URL,
		//					"lhmao", Pwd.p2);
		// String
		// warning=((connection.getWarnings()).getNextWarning()).toString();
		// System.out.println("wariningingi:"+warning);



		//			PreparedStatement ps1 = connection.prepareStatement(query1);
		//
		//			ps1.setString(1, spid);
		//			ResultSet rs1 = ps1.executeQuery();
		//
		//			while (rs1.next()) {
		//				ec = (rs1.getObject(1)).toString();
		//
		//			}


	}

	/**
	 * @return
	 * @uml.property  name="ec"
	 */
	public ArrayList<String> getEc() {

		return ecList;
	}

	

}
