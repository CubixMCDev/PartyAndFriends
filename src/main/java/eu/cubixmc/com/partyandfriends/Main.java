package eu.cubixmc.com.partyandfriends;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.cubixmc.com.partyandfriends.friends.FriendsCommand;
import eu.cubixmc.com.partyandfriends.friends.JoinListener;
import eu.cubixmc.com.partyandfriends.msg.MsgCmd;
import eu.cubixmc.com.partyandfriends.msg.ReplyCmd;
import eu.cubixmc.com.partyandfriends.party.Party;
import eu.cubixmc.com.partyandfriends.party.PartyCmd;
import eu.cubixmc.com.partyandfriends.party.listeners.PartyListener;
import eu.cubixmc.com.partyandfriends.sql.SQL;
import eu.cubixmc.com.partyandfriends.sql.SQLConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Main extends Plugin{
	
	private String partyPrefix = "§8■ §eGroupe §8» ";
	private String friendsPrefix = "§8■ §bAmis §8» ";
	private File file;
	private Configuration config;
	private SQLConnection database;
	private static Main instance;
	private ArrayList<Party> partys = new ArrayList<Party>();
	private List<String> cache;
	private List<String> members;
	
	@Override
	public void onEnable() {
		instance = this;
		getProxy().getPluginManager().registerCommand(this, new MsgCmd(this));
		getProxy().getPluginManager().registerCommand(this, new ReplyCmd(this));
		getProxy().getPluginManager().registerCommand(this, new PartyCmd(this));
		getProxy().getPluginManager().registerCommand(this, new FriendsCommand(this));
		getProxy().getPluginManager().registerListener(this, new JoinListener(this));
		getProxy().getPluginManager().registerListener(this, new PartyListener(this));
		createConfig();
		System.out.println("[Party&Friends] Le plugin est désormais ON");
		database = new SQLConnection(this, "jdbc:mysql://", "localhost", "cubixbase", "Cubix", "CubixTMPlanet2019");
		database.connection();
		loadPartys();
	}
	
	private void loadPartys() {
		try {
			cache = SQL.SQL_ReceiveL("SELECT leader_name FROM party", "leader_name");
			members = SQL.SQL_ReceiveL("SELECT members FROM party", "members");
			for(String leader : cache) {
				for(String member : members) {
					Party party = new Party(partys.size() + 1, leader, new ArrayList<String>());
					String membersList[] = member.split(";");
					for(String m : membersList) {
						party.getMembers().add(m);
					}
					partys.add(party);
					
				}
			}
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void createConfig() {
		file = new File(ProxyServer.getInstance().getPluginsFolder(), "/PartyAndFriends/partys.yml");
		
		try {
			if(!file.exists()) {
				file.createNewFile();
			}
			
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			saveConfig();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void saveConfig() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		System.out.println("[Party&Friends] Le plugin est désormais ON");
		database.disconnect();
	}

	public String getPartyPrefix() {
		return partyPrefix;
	}
	
	public Configuration getConfig() {
		return config;
	}
	
	public SQLConnection getSql() {
		return database;
	}

	public String getFriendsPrefix() {
		return friendsPrefix;
	}
	
	public ArrayList<Party> getPartys(){
		return partys;
	}

	public static Main getInstance() {
		return instance;
	}
	
}
