package eu.cubixmc.com.partyandfriends.msg;

import java.util.HashMap;

import eu.cubixmc.com.partyandfriends.sql.FriendsManager;
import eu.cubixmc.com.partyandfriends.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MsgCmd extends Command{

	public static HashMap<ProxiedPlayer, ProxiedPlayer> messages = new HashMap<ProxiedPlayer, ProxiedPlayer>();
	public Main main;
	private MsgManager msgM;
	private FriendsManager friendsM;
	
	public MsgCmd(Main main) {
		super("w", null, "mp", "msg", "tell");
		this.msgM = new MsgManager(main);
		this.friendsM = new FriendsManager(main);
	}

	@SuppressWarnings("unused")
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(!(sender instanceof ProxiedPlayer)) return;
		
		ProxiedPlayer p = (ProxiedPlayer) sender;
		
		if(args.length == 0) {
			p.sendMessage(new TextComponent("§8■ §cErreur. §7Utilisation §8: §e/msg <joueur> <message>§7."));
			return;
		}
		
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("enable")) {
				p.sendMessage(new TextComponent("§a■ Tout le monde peur désormais vous envoyez des messages privés !"));
				msgM.setAllow(1, p.getUniqueId());
				return;
			}
			if(args[0].equalsIgnoreCase("disable")) {
				p.sendMessage(new TextComponent("§e■ Messages privés activés pour amis uniquement."));
				msgM.setAllow(0, p.getUniqueId());
				return;
			}
		}
		
		if(args.length > 1) {
			String targetName = args[0];
			
			if (targetName.equalsIgnoreCase(p.getName())) {
				p.sendMessage(new TextComponent("§8■ §cErreur. Vous ne pouvez vous envoyer des messages à vous même."));
				return;
			}
			
			if(ProxyServer.getInstance().getPlayer(targetName) == null) {
				p.sendMessage(new TextComponent("§8■ §cErreur. Ce joueur n'est pas en ligne."));
				return;
			}else {
				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(targetName);
				
				if(msgM.isAllowed(target.getUniqueId()) == 1) {
					String msg = "";
					
					for(int i = 1; i != args.length; i++) msg += args[i] + " ";
					
					if(target != p) {
						if(msg != null) {
							p.sendMessage(new TextComponent("§7Envoyé à §3" + target.getName() + " §8» " + msg.replace("", "§d")));
							target.sendMessage(new TextComponent("§7Reçu de §3" + p.getName() + " §8» " + msg.replace("", "§d")));
							messages.put(target, p);
						}else {
							p.sendMessage(new TextComponent("§8■ §cErreur. §7Utilisation §8: §e/msg <joueur> <message>§7."));
							return;
						}
					}else {
						p.sendMessage(new TextComponent("§8■ §cErreur. Vous ne pouvez vous envoyer des messages à vous même."));
						return;
					}
				}else {
					if(friendsM.isFriendWith(p.getUniqueId(), target.getUniqueId())) {	
						String msg = "";
						
						for(int i = 1; i != args.length; i++) msg += args[i] + " ";
						
						if(target != p) {
							if(msg != null) {
								p.sendMessage(new TextComponent("§7Envoyé à §3" + target.getName() + " §8» " + msg.replace("", "§d")));
								target.sendMessage(new TextComponent("§7Reçu de §3" + p.getName() + " §8» " + msg.replace("", "§d")));
								messages.put(target, p);
							}else {
								p.sendMessage(new TextComponent("§8■ §cErreur. §7Utilisation §8: §e/msg <joueur> <message>§7."));
								return;
							}
						}else {
							p.sendMessage(new TextComponent("§8■ §cErreur. Vous ne pouvez vous envoyer des messages à vous même."));
							return;
						}
					}else {
						p.sendMessage(new TextComponent("§8■ §b" + target.getName() + "§7 n'accepte que les messages de ses §eamis §7!"));
						return;
					}
				}
			}
		}
		
	}

}
