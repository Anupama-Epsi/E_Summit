package com.epsilon.DBUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.epsilon.Utilities.Reporting;
import com.epsilon.configuration.ReadPropertiesFile;

public class DBConfiguration {	
	private static Connection con = null;
	static ReadPropertiesFile read =  new ReadPropertiesFile();
	String schemaName = read.readRunProperties("SCHEMA").trim();
	public Reporting logger = new Reporting();
	
	public void getDBConnection(){
		try {
			String dbUrl = read.readRunProperties("DB_URL").trim();					
			String username = read.readRunProperties("DB_USR").trim();		
			String password = read.readRunProperties("DB_PWD").trim();				
			String dbDriver = read.readRunProperties("DB_DRIVER").trim();	
			Class.forName(dbDriver);	
			//Create Connection to DB		
			con = DriverManager.getConnection(dbUrl,username,password);
			System.out.println("Connected to DB");
		}catch(Exception e) {
			System.out.println("Failed to connect or execute in DB due to exception" + e.getMessage());
		}
	}

	public ResultSet executeQuery(String query) {
		ResultSet rs = null;
		try {
			//Set Schema given in run property file
			String schema = "ALTER SESSION SET CURRENT_SCHEMA = "+schemaName;
			//Create Statement Object		
			//Statement stmt = con.createStatement();		
			Statement stmt = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);		
			rs= stmt.executeQuery(schema);			
			// Execute the SQL Query. Store results in ResultSet		
			rs= stmt.executeQuery(query);
		}catch(Exception e) {
			logger.logFail("Failed to executeQuery due to exception : " + e.getMessage());
		}
		return rs;
	}

	public void closeDB() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}
