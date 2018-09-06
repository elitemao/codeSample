package webService;

//WrapperModi-------
//modified from Wapper. make it access redoxnw on tunicata too.
// add access to Benjamin's Hprd on tunicata
//WrapperSimple--------
//modified from WrapperModi. Created by myself. Use VerySimpleDBServiceStub directly.
//20110111: By using WrapperSimple, EstablishConnectionDawisMd can be abandoned.
//WrapperSimple---------------------
//v0:add another version of getResult method which can confine the number of retrieved record
//v0 20110617: use dawismd on Agbi, and redoxnw on tunicata

import java.rmi.RemoteException;

import java.util.Vector;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;



@SuppressWarnings("unchecked")
public class WrapperSimple{
	String databaseName="";
	String serverName="";
	public WrapperSimple(){
//		JOptionPane.showMessageDialog(new JFrame(), "xxxxxxxxxxx");
	}

	public Vector getResults(int database, String query, String[] attributes){
//		JOptionPane.showMessageDialog(new JFrame(), "nnnnnnnnn");
		Vector v=new Vector();
		
		if(database==1){
			databaseName="dawismd"; //dawis-md of agbi is called "dawismd" instead of "dawis_md"
			serverName="agbi";
		}else if(database==2){
			databaseName="redoxnw";
			serverName="tunicata";
		}else if(database==3){
			databaseName="db_hprd";
			serverName="tunicata";
		}
//		switch(database){
//		case 1:
//			databaseName="dawis_md";
//		case 2:
//			databaseName="redoxnw";
//		case 3:
//			databaseName="db_hprd";
//		case 4:
//			databaseName="db_jaspar";
//		}
//		
		try{
//			System.out.println(databaseName);
			String url="http://"+serverName+".techfak.uni-bielefeld.de/axis2/services/VerySimpleDBService";
			VerySimpleDBServiceStub stub=new VerySimpleDBServiceStub(url);
//JOptionPane.showMessageDialog(new JFrame(), "eeeeeeeeeee");
			Options options = stub._getServiceClient().getOptions();
			options.setTimeOutInMilliSeconds(999999999);

			ServiceClient client = stub._getServiceClient();
			client.setOptions(options);
//			JOptionPane.showMessageDialog(new JFrame(), "zzzzzzzzzz");
			stub._setServiceClient(client);
//			JOptionPane.showMessageDialog(new JFrame(), "gggggggggggg");
			VerySimpleDBServiceStub.PreparedQuery pquery = new VerySimpleDBServiceStub.PreparedQuery();
//			JOptionPane.showMessageDialog(new JFrame(), "ssssssssss");
			pquery.setQuery(query);
//			JOptionPane.showMessageDialog(new JFrame(), "wwwwwwwwwww");
			pquery.setParameters(attributes);

			VerySimpleDBServiceStub.PreparedQueryDB prequest = new VerySimpleDBServiceStub.PreparedQueryDB();
			prequest.setDatabase(databaseName);

			prequest.setQuery(pquery);

			try {

				VerySimpleDBServiceStub.DBColumn[] data = stub.preparedQueryDB(prequest).get_return();

				if (data != null) {

					for (VerySimpleDBServiceStub.DBColumn db_object : data) {

						v.add(db_object.getColumn());
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundExceptionException0 e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLExceptionException1 e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessExceptionException2 e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationExceptionException3 e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			stub.cleanup();
		}catch(AxisFault e){

			e.printStackTrace();

		}	
		return v;
	}
	
	// this version of getResults method can confine the number of retrived records
	public Vector getResults(int database, String query, String[] attributes, int limitOfResult){
		Vector v=new Vector();
		
		if(database==1){
			databaseName="dawismd"; //on tunicata->dawis_md, on agbi->dawismd
			serverName="agbi";
		}else if(database==2){
			databaseName="redoxnw";
			serverName="tunicata";
		}else if(database==3){
			databaseName="db_hprd";
			serverName="tunicata";
		}
//		switch(database){
//		case 1:
//			databaseName="dawis_md";
//		case 2:
//			databaseName="redoxnw";
//		case 3:
//			databaseName="db_hprd";
//		case 4:
//			databaseName="db_jaspar";
//		}
//		
		try{
//			System.out.println(databaseName);
			String url="http://"+serverName+".techfak.uni-bielefeld.de/axis2/services/VerySimpleDBService";
			VerySimpleDBServiceStub stub=new VerySimpleDBServiceStub(url);

			Options options = stub._getServiceClient().getOptions();
			options.setTimeOutInMilliSeconds(999999999);

			ServiceClient client = stub._getServiceClient();
			client.setOptions(options);

			stub._setServiceClient(client);

			VerySimpleDBServiceStub.PreparedQuery pquery = new VerySimpleDBServiceStub.PreparedQuery();

			pquery.setQuery(query);

			pquery.setParameters(attributes);

			VerySimpleDBServiceStub.PreparedQueryDB prequest = new VerySimpleDBServiceStub.PreparedQueryDB();
			prequest.setDatabase(databaseName);

			prequest.setQuery(pquery);

			try {

				VerySimpleDBServiceStub.DBColumn[] data = stub.preparedQueryDB(prequest).get_return();
				int noOfRecord=0;
				if (data != null) {

					for (VerySimpleDBServiceStub.DBColumn db_object : data) {
						if(noOfRecord>=limitOfResult){
							break;
						}

						v.add(db_object.getColumn());
						noOfRecord++;
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundExceptionException0 e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLExceptionException1 e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessExceptionException2 e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationExceptionException3 e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			stub.cleanup();
		}catch(AxisFault e){

			e.printStackTrace();

		}	
		return v;
	}
}
