package growNetwork;

import java.util.Iterator;
import java.util.Vector;
//import webService.EstablishConnectionDawisMd;
import webService.WrapperSimple;

//SpaccToSpidWS---------
//v0: use webservice to access redoxnw on tunicata
//v0 20101124:disable the use of EstablishConnectionDawisMd

public class SpaccToSpidWS {
//	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//	static final String DATABASE_URL = "jdbc:mysql://bdbhost/RedoxNW";
	public SpaccToSpidWS(){
		
	}
	
	public static String doit(String spacc){
		String spid="";
//		try {
//			
//			Class.forName(JDBC_DRIVER);
//
//			Connection connection = DriverManager.getConnection(DATABASE_URL, "lhmao",
//					Pwd.p2);

			String query="select spid from unip_spacc where spacc=?";
			String query1="select internal_index from iproclass_uniprotkb_accession where accession_no=?";
			String query2="select uniprotkb_id from iproclass_uniprotkb_id where internal_index=?";
//			PreparedStatement ps=connection.prepareStatement(query);
//			ps.setString(1,spacc);
//			ResultSet rs=ps.executeQuery();
//			if(rs.next()){
//				spid=rs.getObject(1).toString();
//				
//			}
//			new EstablishConnectionDawisMd();
			WrapperSimple w = new WrapperSimple();
			String[] details = { spacc };  

			Vector v = w.getResults(2, query,details); // 2 is redoxnw
			Iterator it= v.iterator();

			while(it.hasNext()){
				String[] result=(String[])it.next();
				if(!result[0].equalsIgnoreCase("")){
					spid=result[0];
				}
			}

			if(spid==""){
				Vector v1=w.getResults(2,query1,details);
				Iterator it1=v1.iterator();
				while(it1.hasNext()){
					String[] result1=(String[])it1.next();
					if(!result1[0].equalsIgnoreCase("")){
						String interanl_index=result1[0];
						Vector v2=w.getResults(2, query2, details);
						Iterator it2=v2.iterator();
						while(it2.hasNext()){
							String[] result2=(String[])it2.next();
							if(!result2[0].equalsIgnoreCase("")){
								spid=result2[0];
							}
							
						}
					}
					
				}
				
			}
			
//		}catch(ClassNotFoundException classex){
//			classex.printStackTrace();
//		}catch(SQLException sqlex){
//			sqlex.printStackTrace();
//		}
		return spid;
	}
}
