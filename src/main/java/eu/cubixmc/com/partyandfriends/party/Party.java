package eu.cubixmc.com.partyandfriends.party;

import java.util.ArrayList;

public class Party {
	
	private ArrayList<String> members;
	private String leader;
	private int id;
	
	public Party(int id, String leader, ArrayList<String> members) {
		this.id = id;
		this.members = members;
		this.leader = leader;
	}

	public ArrayList<String> getMembers() {
		return members;
	}

	public String getLeader() {
		return leader;
	}
	
	public void setLeader(String newLeader) {
		this.leader = newLeader;
	}

	public int getId() {
		return id;
	}

}
