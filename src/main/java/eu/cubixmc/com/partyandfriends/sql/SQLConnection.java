package eu.cubixmc.com.partyandfriends.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import eu.cubixmc.com.partyandfriends.Main;

public class SQLConnection {
	
	private Connection connection;
	private String urlbase,host,database,user,pass;
	public Main main;
	
	public SQLConnection(Main main, String urlbase, String host, String database, String user, String pass) {
		this.main = main;
		this.urlbase = urlbase;
		this.host = host;
		this.database = database;
		this.user = user;
		this.pass = pass;
	}

	public void connection(){
		if(!isConnected()){
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(urlbase + host + "/" + database, user, pass);
				System.out.println("Party & Friends, Connection : ON");
			} catch (SQLException | ClassNotFoundException e) {				
				e.printStackTrace();
			}
		}
	}
	
	public void disconnect(){
		if(isConnected()){
			try {
				connection.close();
				System.out.println("Party & Friends, Connection : OFF");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isConnected(){
		return connection != null;
	}
	
	public Connection getConnection() {
		return connection;
	}
	

}
