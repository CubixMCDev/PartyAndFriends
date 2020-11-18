package eu.cubixmc.com.partyandfriends.party.listeners;

import eu.cubixmc.com.partyandfriends.Main;
import eu.cubixmc.com.partyandfriends.msg.MsgCmd;
import eu.cubixmc.com.partyandfriends.party.Party;
import eu.cubixmc.com.partyandfriends.sql.PartyManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyListener implements Listener{
	
	private Main main;
	private PartyManager partyM;
	
	public PartyListener(Main main) {
		this.main = main;
		this.partyM = new PartyManager(main);
	}
	
	@EventHandler
	public void onQuit(PlayerDisconnectEvent e) {
		
		ProxiedPlayer p = e.getPlayer();
		if(partyM.isInParty(p.getName())) {
			Party party = partyM.getParty(p.getName());
			if(party.getLeader().equalsIgnoreCase(p.getName())) {
				String newLeader = partyM.setNextLeader(p.getName());
				if(newLeader == null) {
					return;
				}
				for(String m : party.getMembers()) {
					ProxiedPlayer member = ProxyServer.getInstance().getPlayer(m);
					if(member == null) continue;
					sendMessage(member, main.getPartyPrefix() + "§6" + newLeader + " §7est désormais le nouveau §6chef §7 de groupe !");
				}
				sendMessage(ProxyServer.getInstance().getPlayer(newLeader), main.getPartyPrefix() + "§6" + newLeader + " §7est désormais le nouveau §6chef §7 de groupe !");
			}
		}
		if(MsgCmd.messages.containsKey(p)) MsgCmd.messages.remove(p);
	}
	
	@EventHandler
	public void onServerSwitch(ServerSwitchEvent e) {
		
		ProxiedPlayer p = e.getPlayer();
		ServerInfo server = p.getServer().getInfo();
		
		if(!partyM.isLeader(p.getName())) {
			return;
		}
		
		if(server.getName().equalsIgnoreCase("Hub") || server.getName().equalsIgnoreCase("Auth")) return;
		
		Party party = partyM.getParty(p.getName());
		for(String m : party.getMembers()) {
			ProxiedPlayer member = ProxyServer.getInstance().getPlayer(m);
			if(member == null) continue;
			if(partyM.getFollow(member.getUniqueId()) == 1) {
				member.connect(server);
				sendMessage(member, main.getPartyPrefix() + " §eConnexion au serveur §6" + server.getName() + "§e.");
			}
		}
	}
	
	@EventHandler
	public void onChat(ChatEvent e) {
		
		ProxiedPlayer p = (ProxiedPlayer) e.getSender();
		
		if(partyM.isInParty(p.getName())) {
			if(e.getMessage().startsWith("*")) {
				e.setCancelled(true);
				Party party = partyM.getParty(p.getName());
				for(String m : party.getMembers()) {
					ProxiedPlayer member = ProxyServer.getInstance().getPlayer(m);
					if(member == null) continue;
					sendMessage(p, main.getPartyPrefix() + "§6" + p.getName() + ": §f" + e.getMessage().substring(1));
				}
			}
		}
	}
	
	private void sendMessage(ProxiedPlayer p, String string) {
		p.sendMessage(new TextComponent(string));
	}
	
}
