package eu.cubixmc.com.partyandfriends.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import eu.cubixmc.com.partyandfriends.Main;

public class SQL {
	
	public static Object SQL_Receive(String query, String column) {
		Object obj = null;
		try {
			Main.getInstance().getSql().connection();
			Statement sql = Main.getInstance().getSql().getConnection().createStatement();
			ResultSet rs = sql.executeQuery(query);
			
			while(rs.next()) {
				obj = rs.getObject(column);
			}
			sql.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
	public static List<String> SQL_ReceiveL(String query, String column) throws ClassNotFoundException{
		List<String> obj = new ArrayList<String>();
		try {
			Main.getInstance().getSql().connection();
			Statement sql = Main.getInstance().getSql().getConnection().createStatement();
			ResultSet rs = sql.executeQuery(query);
			
			while(rs.next()) {
				obj.add(rs.getObject(column).toString());
			}
			sql.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static void SQL_Update(String query) throws ClassNotFoundException{
		try {
			Main.getInstance().getSql().connection();
			Statement sql = Main.getInstance().getSql().getConnection().createStatement();
			sql.executeUpdate(query);
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
