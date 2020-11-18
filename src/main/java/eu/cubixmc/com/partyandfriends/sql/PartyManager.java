package eu.cubixmc.com.partyandfriends.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.cubixmc.com.partyandfriends.party.Party;
import eu.cubixmc.com.partyandfriends.Main;
import net.md_5.bungee.api.ProxyServer;

public class PartyManager {
	
	private Main main;
	
	public PartyManager(Main main) {
		this.main = main;
	}
	
	protected String table = "party";
	private Object cache;
	
	public void addMember(UUID leaderuuid, String member) {
		try {
			cache = SQL.SQL_Receive("SELECT leader_name FROM " + table + " WHERE leader_uuid = '" + leaderuuid.toString() + "'", "leader_name");
			
			if(cache != null) {
				String members = (String) SQL.SQL_Receive("SELECT members FROM " + table + " WHERE leader_uuid = '" + leaderuuid.toString() + "'", "members");
				members += member + ";";
				SQL.SQL_Update("UPDATE " + table + " SET members = '" + members + "' WHERE leader_uuid = '" + leaderuuid.toString() + "'");
				Party party = getParty(ProxyServer.getInstance().getPlayer(leaderuuid).getName());
				party.getMembers().add(member);
			}else {
				SQL.SQL_Update("INSERT INTO " + table + "(party_id, leader_uuid, leader_name, members) VALUES('" + (main.getPartys().size() + 1)  + "', '"+ leaderuuid.toString() + "', '" + ProxyServer.getInstance().getPlayer(leaderuuid).getName() + "', '"+ member + ";')");
				Party party = new Party(main.getPartys().size() + 1, ProxyServer.getInstance().getPlayer(leaderuuid).getName(), new ArrayList<String>());
				party.getMembers().add(member);
				main.getPartys().add(party);
			}
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void removePlayer(String player) {
		Party party = getParty(player);
		String leader = getLeader(player);
		String members = (String) SQL.SQL_Receive("SELECT members FROM " + table + " WHERE leader_name = '" + leader + "'", "members");
		String newMembers = getMembers(leader).toString();
		newMembers = remove(player, members);
		try {
			SQL.SQL_Update("UPDATE party SET members = '" + newMembers + "' WHERE leader_name = '" + leader + "'");
		}catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		party.getMembers().remove(player);		
	}
	
	public boolean isInParty(String player) {
		List<String> leaders = getLeaders();
		if(leaders.contains(player)) return true;
		List<String> members = getMembers();
		for(String member : members) {
			String memberList[] = member.split(";");
			for(String p : memberList) {
				if(p.equalsIgnoreCase(player)) return true;
			}
		}
		return false;
		
	}
	
	public void setLeader(String newLeader) {
		Party party = getParty(newLeader);
		String oldLeader = party.getLeader();
		UUID newLeaderuuid = ProxyServer.getInstance().getPlayer(newLeader).getUniqueId();
		party.setLeader(newLeader);
		try {
			PreparedStatement ps = main.getSql().getConnection().prepareStatement("UPDATE party SET leader_name = ?, leader_uuid = ? WHERE party_id = ?");
			ps.setString(1, newLeader);
			ps.setString(2, newLeaderuuid.toString());
			ps.setInt(3, party.getId());
			ps.executeUpdate();
			ps.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		party.getMembers().remove(newLeader);
		removePlayer(newLeader);
		addMember(newLeaderuuid, oldLeader);
		
	}
	
	public String setNextLeader(String oldLeader) {
		Party party = getParty(oldLeader);
		String newLeader = null;
		for(int i = 0; i < party.getMembers().size(); i++) {
			if(ProxyServer.getInstance().getPlayer(party.getMembers().get(i)) == null) continue;
			else newLeader = party.getMembers().get(i);
		}
		if(ProxyServer.getInstance().getPlayer(newLeader) != null) {
			setLeader(newLeader);
			return newLeader;
		}else{
			disband(oldLeader);
			return null;
		}
	}
	
	public boolean isLeader(String member) {
		if(getLeaders().contains(member)) return true;
		return false;
	}
	
	public String getLeader(String player) {
		Party party = getParty(player);
		return party.getLeader();
	}
	
	public List<String> getMembers() {
		List<String> members = null;
		List<String> result = new ArrayList<String>();
		try {
			members = SQL.SQL_ReceiveL("SELECT members FROM " + table, "members");
			for(String member : members) {
				result.add(member.toString());
			}
			return result;
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<String> getLeaders() {
		List<String> leaders = null;
		List<String> result = new ArrayList<String>();
		try {
			leaders = SQL.SQL_ReceiveL("SELECT leader_name FROM " + table, "leader_name");
			for(String leader : leaders) {
				result.add(leader.toString());
			}
			return result;
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<String> getMembers(String player) {
		if(isLeader(player)) {
			List<String> members = null;
			ArrayList<String> players = new ArrayList<String>();
			try {
				members = SQL.SQL_ReceiveL("SELECT members FROM " + table + " WHERE leader_name = '" + player + "'", "members");
				return members;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return players;
		}else {
			Party party = getParty(player);
			return party.getMembers();
		}
	}
	
	public Party getParty(String player) {
		if(isLeader(player)) {
			for(Party party : main.getPartys()) {
				if(party.getLeader().equalsIgnoreCase(player)) return party;
			}
		}else {
			for(Party party : main.getPartys()) {
				for(String member : party.getMembers()) {
					if(member.equalsIgnoreCase(player)) return party;
				}
			}
		}
		return null;
	}
	
	public void disband(String leader) {
		main.getPartys().remove(getParty(leader));
		main.getLogger().info(main.getPartys().toString());
		try {
			PreparedStatement ps = main.getSql().getConnection().prepareStatement("DELETE FROM " + table + " WHERE leader_name = ?");
			ps.setString(1, leader);
			ps.executeUpdate();
			ps.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String remove(String player, String members) {
		String result = "";
		String[] list = members.split(";");
		for(String name : list) {
			if(!name.equalsIgnoreCase(player)) {
				result += name + ";";
			}
		}
		return result;
	}
	
	public int getFollow(UUID uuid) {
		int follow = 0;
		follow = (Integer) SQL.SQL_Receive("SELECT party_follow FROM utils WHERE uuid = '" + uuid.toString() + "'", "party_follow");
		return follow;
	}
	
	public void setFollow(int follow, UUID playeruuid) {
		try {
			SQL.SQL_Update("UPDATE utils SET party_follow = '" + follow + "' WHERE uuid = '" + playeruuid.toString() + "'");
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public int getAllow(UUID uuid) {
		int allow = 0;
		allow = (Integer) SQL.SQL_Receive("SELECT party_allow FROM utils WHERE uuid = '" + uuid.toString() + "'", "party_allow");
		return allow;
	}
	
	public void setAllow(int allow, UUID playeruuid) {
		try {
			SQL.SQL_Update("UPDATE utils SET party_allow = '" + allow + "' WHERE uuid = '" + playeruuid.toString() + "'");
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
