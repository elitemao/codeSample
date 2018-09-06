package growNetwork;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

//import webService.EstablishConnectionDawisMd;
import webService.WrapperSimple;

//v0 20101124:disable the use of EstablishConnectionDawisMd
//v0 20110707: use dawismd on agbi

public class SpidToSpaccWS {
//	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//	static final String DATABASE_URL = "jdbc:mysql://bdbhost/RedoxNW";
	public SpidToSpaccWS(){
		
	}
	
	public static ArrayList doit(String spid){
		ArrayList spaccStore=new ArrayList();
//		try {
			
//			Class.forName(JDBC_DRIVER);
//
//			Connection connection = DriverManager.getConnection(DATABASE_URL, "lhmao",
//					Pwd.p2);
//			new EstablishConnectionDawisMd();
//			String query="select spacc from unip_spacc where spid=?";
		String query="select accession_numbers from uniprot_accession_numbers where uniprot_id=?";
			WrapperSimple w=new WrapperSimple();
//			System.out.println("after wrapperSimple");
			String[] details = {spid};  
			
			Vector v = w.getResults(1, query,details); //2 is redoxnw

			Iterator it = v.iterator();
			while(it.hasNext()){
				String[] result=(String[])it.next();
				String spacc=result[0];
//			System.out.println(spacc);
				if(!spacc.equalsIgnoreCase("")){
					spaccStore.add(spacc);
				}
			}
//			PreparedStatement ps=connection.prepareStatement(query);
//			ps.setString(1,spid);
//			ResultSet rs=ps.executeQuery();
//			if(rs.next()){
//				spacc=rs.getObject(1).toString();
//				
//			}

			

//		}catch(ClassNotFoundException classex){
//			classex.printStackTrace();
//		}catch(SQLException sqlex){
//			sqlex.printStackTrace();
//		}
		return spaccStore;
	}
	
	
	
	
}
