package eu.cubixmc.com.partyandfriends.msg;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import eu.cubixmc.com.partyandfriends.Main;

public class MsgManager {
	
	private Main main;
	
	public MsgManager(Main main) {
		this.main = main;
	}
	
	String table = "utils";
	
	public int isAllowed(UUID player){
		try {
			PreparedStatement ps = main.getSql().getConnection().prepareStatement("SELECT msg_allow FROM " + table + " WHERE uuid = ?");
			ps.setString(1, player.toString());
			ResultSet rs = ps.executeQuery();
			int allow = 0;
			while(rs.next()) {
				allow = rs.getInt("msg_allow");
			}
			ps.close();
			return allow;
		}catch(SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public void setAllow(int allow, UUID player) {
		try {
			PreparedStatement ps = main.getSql().getConnection().prepareStatement("UPDATE " + table + " SET msg_allow = ? WHERE uuid = ?");
			ps.setInt(1, allow);
			ps.setString(2,  player.toString());
			ps.executeUpdate();
			ps.close();
		}catch (SQLException e){
			e.printStackTrace();
		}
	}

}
