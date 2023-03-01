package eu.cubixmc.com.partyandfriends.friends;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import eu.cubixmc.com.partyandfriends.Main;
import eu.cubixmc.com.partyandfriends.sql.FriendsManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class JoinListener implements Listener{
	
	private Main main;
	private FriendsManager friendsM;
	
	public JoinListener(Main main) {
		this.main = main;
		friendsM = new FriendsManager(main);
	}
	
	@EventHandler
	public void onJoin(PostLoginEvent e) {
		
		ProxiedPlayer p = e.getPlayer();
		
		createAccount(p);
		
		if(friendsM.getFriends(p.getUniqueId()).size() >= 1) {
			for(UUID fName : friendsM.getFriends(p.getUniqueId())) {
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(fName);
				if(player == null) continue;
				player.sendMessage(new TextComponent(main.getFriendsPrefix() + "§b" + p.getName() + " §fvient de se §aconnecter§f."));
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerDisconnectEvent e) {
		
		ProxiedPlayer p = e.getPlayer();
		
		if(friendsM.getFriends(p.getUniqueId()).size() >= 1) {
			for(UUID fuuid : friendsM.getFriends(p.getUniqueId())) {
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(fuuid);
				if(player == null) continue;
				player.sendMessage(new TextComponent(main.getFriendsPrefix() + "§b" + p.getName() + " §fvient de se §cdéconnecter§f."));
			}
		}
	}
	
	public void createAccount(ProxiedPlayer p){
		if(!hasAccount(p)){
			// INSERT
			try {
				PreparedStatement q = main.getSql().getConnection().prepareStatement("INSERT INTO utils(uuid, msg_allow, friend_allow) VALUES(?, ?, ?)");
				q.setString(1,  p.getUniqueId().toString());
				q.setInt(2, 1);
				q.setInt(3, 1);
				q.execute();
				q.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean hasAccount(ProxiedPlayer p){
		// SELECT
		
		try {
			PreparedStatement q = main.getSql().getConnection().prepareStatement("SELECT uuid FROM utils WHERE uuid = ?");
			q.setString(1, p.getUniqueId().toString());
			ResultSet result = q.executeQuery();
			boolean hasAccount = result.next();
			q.close();
			return hasAccount;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
