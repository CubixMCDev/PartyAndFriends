package eu.cubixmc.com.partyandfriends.party;

import java.util.HashMap;

import eu.cubixmc.com.partyandfriends.Main;
import eu.cubixmc.com.partyandfriends.sql.FriendsManager;
import eu.cubixmc.com.partyandfriends.sql.PartyManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyCmd extends Command{
	
	private HashMap<ProxiedPlayer, ProxiedPlayer> requestsParty = new HashMap<ProxiedPlayer, ProxiedPlayer>();
	private Main main;
	private PartyManager partyM;
	private FriendsManager friendsM;
	
	public PartyCmd(Main main) {
		super("party", null, "p", "g", "groupe", "group");
		this.main = main;
		this.partyM = new PartyManager(main);
		this.friendsM = new FriendsManager(main);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent("Vous devez être un joueur pour executer cette commande !"));
			return;
		}
		
		ProxiedPlayer p = (ProxiedPlayer) sender;
		
		/*
		 *  /p invite <pseudo>
		 */
		
		if(args.length == 0) {
			displayHelp(p);
			return;
		}
		
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("tp")) {
				
				if(!partyM.isInParty(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§eVous n'êtes pas dans un groupe !");
					return;
				}
				
				if(partyM.isLeader(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§7Vous ne pouvez pas vous téléporter à vous même !");
					return;
				}
				
				String leaderName = partyM.getParty(p.getName()).getLeader();
				ProxiedPlayer leader = ProxyServer.getInstance().getPlayer(leaderName);
				if(leader == null) {
					sendMessage(p, main.getPartyPrefix() + "§7Le §6chef §7de groupe est actuellement §chors ligne§7.");
					return;
				}
				
				sendMessage(p, main.getPartyPrefix() + "§6Téléportation à §9" + leader.getServer().getInfo().getName() + "§6."); 
				p.connect(leader.getServer().getInfo());	
				
			}else if(args[0].equalsIgnoreCase("accept")) {
				
				if(!(requestsParty.containsKey(p))) {
					sendMessage(p, main.getPartyPrefix() + "§cVous n'avez pas de demande de groupe en cours.");
					return;
				}
				
				if(requestsParty.get(p) == null) {
					sendMessage(p, "§cErreur lors de la création de la party.");
					return;
				}
						
				sendMessage(p, main.getPartyPrefix() + "§6" + p.getName() + " §7a rejoint le groupe !");
				sendMessage(requestsParty.get(p), main.getPartyPrefix() + "§6" + p.getName() + " §7a rejoint le groupe !");
				partyM.addMember(requestsParty.get(p).getUniqueId(), p.getName());
				requestsParty.remove(p);
				
				
			}else if(args[0].equalsIgnoreCase("refuse")) {
				
				if(!requestsParty.containsKey(p)) {
					sendMessage(p, main.getFriendsPrefix() + "§cVous n'avez pas de demandes de groupe en cours !");
					return;
				}
				
				if(requestsParty.get(p) == null) {
					sendMessage(p, "§cImpossible d'accepter votre demande d'ami. Veuillez réessayez.");
					return;
				}
				
				sendMessage(p, main.getFriendsPrefix() + "§7Vous avez §crefusé §7l'invitation de §6" + p.getName());
				sendMessage(requestsParty.get(p), main.getFriendsPrefix() + "§6" + requestsParty.get(p).getName() + " §7a §crefusé §7votre invitation.");
				requestsParty.remove(p);
				
			}else if(args[0].equalsIgnoreCase("disband")) {
				if(!partyM.isInParty(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§eVous n'êtes pas dans un groupe !");
					return;
				}
				
				if(!partyM.isLeader(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§7Vous devez être le §6chef §7de groupe !");
					return;
				}
				
				Party party = partyM.getParty(p.getName());
				for(String member : party.getMembers()) {
					if(ProxyServer.getInstance().getPlayer(member) == null) continue;
					sendMessage(ProxyServer.getInstance().getPlayer(member), main.getPartyPrefix() + "§c" + p.getName() + " §7a dissous le groupe.");
				}
				sendMessage(p, main.getPartyPrefix() + "§7Vous venez de §cdissoudre §7votre groupe.");
				partyM.disband(p.getName());
					
			}else if(args[0].equalsIgnoreCase("enable")) {
				if(partyM.getAllow(p.getUniqueId()) == 1) {
					sendMessage(p, main.getPartyPrefix() + "§cVous acceptez déjà les demandes de groupe !");
					return;
				}
				sendMessage(p, main.getPartyPrefix() + "§aVous acceptez désormais les demandes de groupe !");
				partyM.setAllow(1, p.getUniqueId());
				
			}else if(args[0].equalsIgnoreCase("disable")) {
				if(partyM.getAllow(p.getUniqueId()) == 0) {
					sendMessage(p, main.getPartyPrefix() + "§cVous acceptez déjà les demandes de groupe §a!");
					return;
				}
				sendMessage(p, main.getPartyPrefix() + "§eSeul vos amis peuvent désormais vous invitez §edans un groupe !");
				partyM.setAllow(0, p.getUniqueId());
				
			}else if(args[0].equalsIgnoreCase("info")) {
				
				if(!partyM.isInParty(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§eVous n'êtes pas dans un groupe !");
					return;
				}
				sendMessage(p, "§7» §6Groupe §f| §eInformations du groupe");
				sendMessage(p, "");
				sendMessage(p, "§8■ §7Leader : §6" + partyM.getLeader(p.getName()));
				StringBuilder sb = new StringBuilder();
				for(String member : partyM.getMembers(p.getName())) sb.append(member + " ");
				sendMessage(p, "§8■ §7Membres : §e" + sb.toString().replace(";", "").replace(",", ""));
				
			}else if(args[0].equalsIgnoreCase("leave")){
				if(!partyM.isInParty(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§eVous n'êtes pas dans un groupe !");
					return;
				}
				
				Party party = partyM.getParty(p.getName());
				if(party.getLeader().equalsIgnoreCase(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§7Veuillez d'abord §cdissoudre §7le groupe ou sélectionner un nouveau chef de groupe !");
					return;
				}
				
				for(String m : party.getMembers()) {
					ProxiedPlayer member = ProxyServer.getInstance().getPlayer(m);
					if(member == null) continue;
					sendMessage(member, main.getPartyPrefix() + "§c" + p.getName() + " §7a quitté le groupe.");
				}
				if(ProxyServer.getInstance().getPlayer(party.getLeader()) != null) sendMessage(ProxyServer.getInstance().getPlayer(party.getLeader()), main.getPartyPrefix() + "§c" + p.getName() + " §7a quitté le groupe.");
				partyM.removePlayer(p.getName());
			}
		}
		
		if(args.length > 1) {
			
			if(args.length == 1) {
				displayHelp(p);
				return;
			}
			
			if(args[0].equalsIgnoreCase("invite")) {
				
				if(args.length == 1) {
					displayHelp(p);
					return;
				}
				
				String targetName = args[1];
				
				
				if(partyM.isInParty(p.getName()) && !partyM.isLeader(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§7Vous devez être le §6chef §7de groupe !");
					return;
				}
				
				if(ProxyServer.getInstance().getPlayer(targetName) == null) {
					sendMessage(p, main.getPartyPrefix() + "§cLe joueur est hors ligne ou n'existe pas !");
					return;
				}
				
				if(partyM.getAllow(ProxyServer.getInstance().getPlayer(targetName).getUniqueId()) == 0) {
					if(!friendsM.isFriendWith(ProxyServer.getInstance().getPlayer(targetName).getUniqueId(), p.getUniqueId())) {
						sendMessage(p, main.getPartyPrefix() + "§e" + ProxyServer.getInstance().getPlayer(targetName).getName() + " §7n'accepte que les demandes de groupe de ses §eamis §7!");
						return;
					}
				}
				
				if(requestsParty.containsValue(p)) {
					sendMessage(p, main.getPartyPrefix() + "§cVous avez déjà une demande de groupe en cours.");
					return;
				}
				
				if(ProxyServer.getInstance().getPlayer(targetName) == p) {
					sendMessage(p, main.getPartyPrefix()  + "§cVous ne pouvez pas vous inviter vous même !");
					return;
				}
				
				/*if() {
					return;
				}*/
				
				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(targetName);
				if(partyM.isInParty(p.getName())) {
					if(partyM.getParty(p.getName()).getMembers().contains(target.getName())) {
						sendMessage(p, main.getPartyPrefix() + "§e" + target.getName() + " §cest déjà dans votre groupe !");
						return;
					}
				}
				
				if(partyM.isInParty(target.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§6" + target.getName() + " §7est déjà dans un groupe !");
					return;
				}
				
				requestsParty.put(target, p);
				TextComponent invitemsg = new TextComponent(main.getPartyPrefix() + "§e" + p.getName() + " §7vous a invité(e) à rejoindre son §egroupe§7.");
				TextComponent accept = new TextComponent(" §a[✔] ");
				accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("§aACCEPTER")).create()));
				accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept"));
				invitemsg.addExtra(accept);
				
				TextComponent refuse = new TextComponent("§c[✖]");
				refuse.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("§cREFUSER")).create()));
				refuse.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party refuse"));
				invitemsg.addExtra(refuse);
				
				target.sendMessage(invitemsg);
				sendMessage(p, main.getPartyPrefix() + " §7Invitation de groupe envoyée à §e" + target.getName());

			}else if(args[0].equalsIgnoreCase("kick")) {
				
				if(args.length == 1) {
					displayHelp(p);
					return;
				}
				
				if(!partyM.isInParty(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§eVous n'êtes pas dans un groupe !");
					return;
				}
				
				String targetName = args[1];
				Party party = partyM.getParty(p.getName());
				String target = findMember(party, targetName);
				
				if(!partyM.isLeader(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§7Vous devez être le §6chef §7de groupe !");
					return;
				}

				if(!party.getMembers().contains(target)) {
					sendMessage(p, main.getPartyPrefix() + "§e" + targetName + " §7n'est pas dans votre groupe !");
					return;
				}
				
				for(String member : party.getMembers()) {
					ProxiedPlayer m = ProxyServer.getInstance().getPlayer(member);
					if(m == null) continue;
					sendMessage(m, main.getPartyPrefix() + "§c" + target + " §7a quitté le groupe.");
				}
				sendMessage(p, main.getPartyPrefix() + "§c" + target + " §7a quitté le groupe.");
				partyM.removePlayer(target);
				
			}else if(args[0].equalsIgnoreCase("lead")) {
				
				if(args.length == 1) {
					displayHelp(p);
					return;
				}
				
				if(!partyM.isInParty(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§eVous n'êtes pas dans un groupe !");
					return;
				}
				
				String targetName = args[1];
				
				if(!partyM.isLeader(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§7Vous devez être le §6chef §7de groupe !");
					return;
				}
				
				if(ProxyServer.getInstance().getPlayer(targetName) == null) {
					sendMessage(p, main.getPartyPrefix() + " §cLe joueur n'existe pas ou est hors ligne !");
					return;
				}
				
				if(ProxyServer.getInstance().getPlayer(targetName).getName().equalsIgnoreCase(p.getName())) {
					sendMessage(p, main.getPartyPrefix() + "§cErreur. Vous êtes déjà chef de groupe !");
					return;
				}
				
				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(targetName);
				partyM.setLeader(target.getName());
				for(String m : partyM.getParty(target.getName()).getMembers()) {
					ProxiedPlayer member = ProxyServer.getInstance().getPlayer(m);
					if(member == null) continue;
					sendMessage(member, main.getPartyPrefix() + "§6" + target.getName() + " §7est désormais le nouveau §6chef §7de groupe !");
				}
				
			}else if(args[0].equalsIgnoreCase("follow")) {
				if(args.length == 1) {
					displayHelp(p);
					return;
				}
				
				String toggle = args[1];
				if(toggle.equalsIgnoreCase("on")) {
					if(partyM.getFollow(p.getUniqueId()) == 1) {
						sendMessage(p, main.getPartyPrefix() + "§cVous avez déjà activé le suivi de groupe !");
						return;
					}
					sendMessage(p, main.getPartyPrefix() + "§aVous suivez désormais le groupe !");
					partyM.setFollow(1, p.getUniqueId());
				}else if(toggle.equalsIgnoreCase("off")) {
					if(partyM.getFollow(p.getUniqueId()) == 0) {
						sendMessage(p, main.getPartyPrefix() + "§cVous avez déjà désactivé le suivi de groupe !");
						return;
					}
					sendMessage(p, main.getPartyPrefix() + "§7Vous ne suivez désormais plus le groupe !");
					partyM.setFollow(0, p.getUniqueId());
				}else {
					displayHelp(p);
					return;
				}
			}
		}
		
	}

	private void displayHelp(ProxiedPlayer p) {
	    p.sendMessage(new TextComponent("§7» §e§lAide §f❘ §6Groupe"));
	    p.sendMessage(new TextComponent(" "));
	    p.sendMessage(new TextComponent("§8■ §6/party invite <joueur> §7» §eInviter un §ejoueur dans votre groupe."));
	    p.sendMessage(new TextComponent("§8■ §6/party accept <joueur> §7» §eRejoindre la party d'un autre joueur qui vous a invité."));
	    p.sendMessage(new TextComponent("§8■ §6/party kick <joueur> §7» §eRetirer un joueur de votre §egroupe."));
	    p.sendMessage(new TextComponent("§8■ §d*§6<message> §7» §eParler de parler dans le chat de votre groupe."));
	    p.sendMessage(new TextComponent("§8■ §6/party info §7» §eVous liste les informations concernant votre groupe."));
	    p.sendMessage(new TextComponent("§8■ §6/party leader <joueur> §7» §ePromouvoir un joueur au rang de §echef du groupe."));
	    p.sendMessage(new TextComponent("§8■ §6/party tp §7» §ePermet de vous téléporter au chef de groupe."));
	    p.sendMessage(new TextComponent("§8■ §6/party leave §7» §ePermet de quitter votre groupe actuelle."));
	    p.sendMessage(new TextComponent("§8■ §6/party follow on|off §7» §ePermet de désactiver/activer le suivi §ede groupe."));
	    p.sendMessage(new TextComponent("§8■ §6/party disable §7» §ePermet de désactiver la réception §ed'invitation de groupe. §6(Sauf amis.)"));
	    p.sendMessage(new TextComponent(" "));
		
	}
	
	private void sendMessage(ProxiedPlayer p, String string) {
		p.sendMessage(new TextComponent(string));
	}
	
	private String findMember(Party party, String member) {
		for(String m : party.getMembers()) {
			if(m.equalsIgnoreCase(member)) return m;
		}
		return null;
	}

}
