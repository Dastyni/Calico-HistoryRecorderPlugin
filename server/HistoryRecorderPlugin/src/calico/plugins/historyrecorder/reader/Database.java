package calico.plugins.historyrecorder.reader;


import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;

import calico.plugins.PluginFinder;



public class Database {
	private String dbUrl = "jdbc:mysql://localhost/calicohistory";
	private Connection connection = null;
	
	private long sessionIdleAllowInMinutes = 60;
	private long prevEventTime = Integer.MIN_VALUE;
	private int sessionNum = 1;
	
	public Database() {}
	
	public Database(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	
	public void connect(){
		try {
			
			File dir = new File("libs/");
			if (dir.isFile()) {
				return;
			}
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (name.compareTo("mysql-connector-java-5.1.12-bin.jar") == 0);
				}
			});
			(new PluginFinder()).getClass(files[0], "com.mysql.jdbc.Driver");

			
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void add(CalicoCanvasState state){
		try {
			String query = "";
			
			//Create new Session if needed
			if(state.time > prevEventTime + (sessionIdleAllowInMinutes * 60000)){
				query = "Insert into sessions set "+
						"name='DesignSession "+(sessionNum++)+"  --  "+timeToDate(state.time)+"', "+
						"file='"+state.fileName+"', "+
						"time='"+state.time+"', "+
						"detail=''"+", clientlocations='"+state.clientLocations.toString()+"'";
				
				System.out.println("query: "+query);
				Statement stmt = connection.createStatement();
				stmt.executeUpdate(query);
				
				prevEventTime = state.time;
			}
			
			
			query = "Insert into events set "+
					"client='"+state.user+"', "+
					"cuid='"+state.command+"', "+
					"location='"+state.gridCoordText+"', "+
					"image_name='"+state.imgName+"', "+
					"time='"+state.time+"', "+
					"file='"+state.fileName+"'";
			
			//System.out.println("query: "+query);
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(query);

			
			
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
	
	private String timeToDate(long timeStamp){
		java.util.Date time = new java.util.Date(timeStamp);

		Format formatter = new SimpleDateFormat("EE, MMM dd KK:mm:ss a");
		String s = formatter.format(time);
		return s;
	}
}
