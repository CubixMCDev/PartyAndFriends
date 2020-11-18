package eu.cubixmc.com.partyandfriends.friends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.cubixmc.com.partyandfriends.Main;
import eu.cubixmc.com.partyandfriends.sql.FriendsManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class FriendsCommand extends Command {
	
	public HashMap<ProxiedPlayer, ProxiedPlayer> requestFriends = new HashMap<ProxiedPlayer, ProxiedPlayer>();
	public FriendsManager friendsM;
	public Main main;
	
	public FriendsCommand(Main main) {
		super("friends", null, "f", "ami", "friend", "amis");
		this.main = main;
		this.friendsM = new FriendsManager(main);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent("Commande non utilisable pour la console !"));
			return;
		}
		
		ProxiedPlayer p = (ProxiedPlayer) sender;
		
		if(args.length == 0) {
			displayHelp(p);
			return;
		}
		
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("accept")) {
				if(!requestFriends.containsKey(p)) {
					sendMessage(p, main.getFriendsPrefix() + "§cVous n'avez pas de demandes d'ami en cours !");
					return;
				}
				
				if(requestFriends.get(p) == null) {
					sendMessage(p, "§cImpossible d'accepter votre demande d'ami. Veuillez réessayez.");
					return;
				}
				
				if(friendsM.isFriendWith(p.getUniqueId(), requestFriends.get(p).getUniqueId())) {
					sendMessage(p, main.getFriendsPrefix() + "§eVous êtes déjà ami avec ce joueur !");
					return;
				}
				
				friendsM.addFriend(p.getUniqueId(), requestFriends.get(p).getUniqueId());
				friendsM.addFriend(requestFriends.get(p).getUniqueId(), p.getUniqueId());
				sendMessage(p, main.getFriendsPrefix() + "§7Vous êtes désormais ami avec §a" + requestFriends.get(p).getName() + "§b !");
				sendMessage(requestFriends.get(p), main.getFriendsPrefix() + "§7Vous êtes désormais ami avec §a" + p.getName() + "§b !");
			
				requestFriends.remove(p);
			}else if (args[0].equalsIgnoreCase("refuse")) {
				if(!requestFriends.containsKey(p)) {
					sendMessage(p, main.getFriendsPrefix() + "§cVous n'avez pas de demandes d'ami en cours !");
					return;
				}
				
				if(requestFriends.get(p) == null) {
					sendMessage(p, "§cImpossible d'accepter votre demande d'ami. Veuillez réessayez.");
					return;
				}
				
				if(friendsM.isFriendWith(p.getUniqueId(), requestFriends.get(p).getUniqueId())) {
					sendMessage(p, main.getFriendsPrefix() + "§eVous êtes déjà ami avec ce joueur !");
					return;
				}
				
				sendMessage(requestFriends.get(p), main.getFriendsPrefix() + "§7Vous avez §crefusé §7la demande d'ami de §6" + p.getName());
				sendMessage(p, main.getFriendsPrefix() + "§6" + requestFriends.get(p).getName() + " §7a §crefusé §7votre demande d'ami.");
				
				requestFriends.remove(p);
			}else if (args[0].equalsIgnoreCase("list")) {
				
				if(friendsM.getFriends(p.getUniqueId()).size() == 0) {
					sendMessage(p, main.getFriendsPrefix() + "§cVous n'avez actuellement pas d'amis sur le serveur.");
				}else {
					List<String> onlineFriends = new ArrayList<String>();
					List<String> offlineFriends = new ArrayList<String>();
					
					for(String friend : friendsM.getFriendsNames(p.getUniqueId())) {
						if(ProxyServer.getInstance().getPlayer(friend) == null) {
							offlineFriends.add(friend + " ");
						}else {
							onlineFriends.add(friend + " ");
						}
					}
					
					sendMessage(p, "§7» §e§lSystème §f§l| §eListe d'amis");
					sendMessage(p, "");
					
					if(onlineFriends.isEmpty()) {
						sendMessage(p, "§7Amis en ligne : §cAucun(s) ami(s) en ligne.");
					}
					
					if(onlineFriends.size() >= 1) sendMessage(p, "§7Amis en ligne : §8[§a" + onlineFriends.toString().replace("[", "").replace("]", "") + "§8]");
					
					if(offlineFriends.isEmpty()) {
						sendMessage(p, "§7Amis hors ligne : §cAucun(s) ami(s) hors ligne.");
					}
					
					if(offlineFriends.size() >= 1) sendMessage(p, "§7Amis hors ligne : §8[§c" + offlineFriends.toString().replace("[", "").replace("]", "") + "§8]");
				}
				
			}else if(args[0].equalsIgnoreCase("add")) {
				if(args.length < 2) {
					displayHelp(p);
					return;
				}
			} else if(args[0].equalsIgnoreCase("remove")) {
				if(args.length < 2) {
					displayHelp(p);
					return;
				}
			} else if (args[0].equalsIgnoreCase("enable")) {
				if(friendsM.isAllowed(p.getUniqueId()) == 0) {
					friendsM.setAllow(1, p.getUniqueId());
					sendMessage(p, main.getFriendsPrefix() + "§aVous acceptez désormais les demandes d'ami !");
				}else {
					sendMessage(p, main.getFriendsPrefix() + "§cVous acceptez déjà les demandes d'ami !");
					return;
				}
			}else if (args[0].equalsIgnoreCase("disable")) {
				if(friendsM.isAllowed(p.getUniqueId()) == 1) {
					friendsM.setAllow(0, p.getUniqueId());
					sendMessage(p, main.getFriendsPrefix() + "§aVous refusez désormais les demandes d'ami !");
				}else {
					sendMessage(p, main.getFriendsPrefix() + "§cVous refusez déjà pas les demandes d'ami !");
					return;
				}
			}
			
		}
		
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("add")) {
				
				String targetName = args[1];
				if(ProxyServer.getInstance().getPlayer(targetName) != null) {
					ProxiedPlayer target = ProxyServer.getInstance().getPlayer(targetName);
					if(friendsM.isAllowed(target.getUniqueId()) == 1) {
						if(requestFriends.containsValue(p)) {
							sendMessage(p, main.getFriendsPrefix() + "§cVous avez déjà une demande d'amis en cours");
							return;
						}
						if(ProxyServer.getInstance().getPlayer(targetName) == p) {
							sendMessage(p, main.getFriendsPrefix() + "§cVous ne pouvez vous ajouter vous même en ami !");
							return;
						}
						
						if(friendsM.isFriendWith(p.getUniqueId(), target.getUniqueId())) {
							sendMessage(p, main.getFriendsPrefix() + "§7Vous êtes déjà ami avec ce joueur !");
							return;
						}
						
						requestFriends.put(target, p);
						TextComponent invitemsg = new TextComponent(main.getFriendsPrefix() + "§6" + p.getName() + "  §7vous a demandé en ami !");
						TextComponent accept = new TextComponent(" §a[✔] ");
						accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("§aACCEPTER")).create()));
						accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends accept"));
						invitemsg.addExtra(accept);
						TextComponent refuse = new TextComponent("§c[✖]");
						refuse.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("§cREFUSER")).create()));
						refuse.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends refuse"));
						invitemsg.addExtra(refuse);
						target.sendMessage(invitemsg);
						sendMessage(p, main.getFriendsPrefix() + "§7La demande d'ami a bien été envoyé à §b" + target.getName());
					}else if(friendsM.isAllowed(target.getUniqueId()) == 0){
						sendMessage(p, main.getFriendsPrefix() + "§b" + target.getName() + " §7n'accepte pas les demandes d'ami !");
						return;
					}
				}else {
					sendMessage(p, main.getFriendsPrefix() + "§cLe joueur n'existe pas ou est hors ligne !");
					return;
				}
			} else if(args[0].equalsIgnoreCase("remove")) {
				if(args.length < 2) {
					displayHelp(p);
					return;
				}
				String targetName = args[1];
				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(targetName);
				if(!friendsM.isFriendWith(p.getUniqueId(), target.getUniqueId())) {
					sendMessage(p, main.getFriendsPrefix() + "§cVous n'êtes pas ami avec §e" + target.getName());
					return;
				}
				sendMessage(p, main.getFriendsPrefix() + "§7Vous n'êtes désormais plus ami avec §6" + target.getName());
				friendsM.removeFriend(p.getUniqueId(), target.getUniqueId());
				friendsM.removeFriend(target.getUniqueId(), p.getUniqueId());
			}
		}

	}

	private void displayHelp(ProxiedPlayer p) {
		
		sendMessage(p, " ");
		sendMessage(p, "§7» §e§lSystème §f§l❘ §eAmis");
		sendMessage(p, " ");
		sendMessage(p, "§8■ §6/friends add <pseudo> §7» §eAjoute un nouvel ami.");
		sendMessage(p, "§8■ §6/friends remove <pseudo> §7» §eSupprimer un ami.");
		sendMessage(p, "§8■ §6/friends accept|refuse §7» §eAccepter/Refuser une demande d'ami.");
		sendMessage(p, "§8■ §6/friends enable|disable. §7» §eGestion des autorisations.");
		sendMessage(p, "§8■ §6/friends list §7» §eVoir votre liste d'amis.");
		sendMessage(p, " ");
	}

	private void sendMessage(ProxiedPlayer p, String string) {
		p.sendMessage(new TextComponent(string));
		
	}

}
