package calico.plugins.historyrecorder.reader;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;



public class Database {
	private String dbUrl = "jdbc:mysql://localhost/calicohistory";
	private Connection connection = null;
	//private String queryBase = "select * from events ";
	
	public Database() {}
	
	public Database(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	
	public void connect(){
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection (dbUrl, "root", "");
		
		} catch (ClassNotFoundException e) {
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
					"\t\t\tFAILED CONNECTION\n" +
					"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
					"\t\t\tFAILED CONNECTION\n" +
					"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			e.printStackTrace();
		}
	}
	
	public void test(){
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("Insert into events set client='Zoot', cuid=1234, location='g4', image_name='blort', time='time...'");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void add(CalicoCanvasState state){
		System.out.println("Got here");
		try {
			String query = "Insert into events set "+
					"client='"+state.user+"', "+
					"cuid='"+state.canvasUUID+"', "+
					"location='"+state.gridCoordText+"', "+
					"image_name='"+state.imgName+"', "+
					"time='"+state.time+"'";
			
			query = "Insert into events set client = 'working!'";
			System.out.println("query: "+query);
			System.out.println("connection is : "+connection);
			Statement stmt = connection.createStatement();
			System.out.println("Got connection");
			stmt.executeUpdate(query);
			System.out.println("done!");

		} catch (SQLException e) {	
			System.out.println("broke... "+e.toString());
			e.printStackTrace();}
	}
	
	public void disconnect(){
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
