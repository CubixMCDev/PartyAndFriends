package eu.cubixmc.com.partyandfriends.msg;

import eu.cubixmc.com.partyandfriends.sql.FriendsManager;
import eu.cubixmc.com.partyandfriends.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReplyCmd extends Command{
	
	private FriendsManager friendsM;
	public Main main;
	
	public ReplyCmd(Main main) {
		super("r", null, "reply");
		this.main = main;
		friendsM = new FriendsManager(main);
	}

	@SuppressWarnings("unused")
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(!(sender instanceof ProxiedPlayer)) return;
		
		ProxiedPlayer p = (ProxiedPlayer) sender;
		
		if(args.length == 0) {
			p.sendMessage(new TextComponent("§8■ §cErreur. §7Utilisation §8: §e/r <joueur> <message>§7."));
			return;
		}
		
		if(MsgCmd.messages.containsKey(p)) {
			
			String msg = "";
			ProxiedPlayer target = MsgCmd.messages.get(p);
			
			if(!target.isConnected()) {
				p.sendMessage(new TextComponent("§8■ §cErreur. Ce joueur n'est pas en ligne."));
				MsgCmd.messages.remove(p);
				return;
			}	
			
			for(int i = 0; i != args.length; i++) msg += args[i] + " ";
			
			if(friendsM.isAllowed(target.getUniqueId()) == 1) {	
				
				if(!target.isConnected()) {
					p.sendMessage(new TextComponent("§8■ §cErreur. Ce joueur n'est pas en ligne."));
					MsgCmd.messages.remove(p);
					return;
				}			
				if(target != p) {
					if(msg != null) {
						p.sendMessage(new TextComponent("§7Envoyé à §3" + target.getName() + " §8» " + msg.replace("", "§d")));
						target.sendMessage(new TextComponent("§7Reçu de §3" + p.getName() + " §8» " + msg.replace("", "§d")));
						MsgCmd.messages.put(target, p);
					}else {
						p.sendMessage(new TextComponent("§8■ §cErreur. §7Utilisation §8» §e/r <message>§7."));
						return;
					}
				}else {
					p.sendMessage(new TextComponent("§8■ §cErreur. Vous ne pouvez vous envoyer des messages à vous même."));
					return;
				}
			}else {			
				if(!target.isConnected()) {
					p.sendMessage(new TextComponent("§8■ §cErreur. Ce joueur n'est pas en ligne."));
					MsgCmd.messages.remove(p);
					return;
				}
				if(friendsM.isFriendWith(p.getUniqueId(), target.getUniqueId())) {
					if(target != p) {
						for(int i = 0; i != args.length; i++) msg += args[i] + " ";	
						if(msg != null) {
							p.sendMessage(new TextComponent("§7Envoyé à §3" + target.getName() + " §8» " + msg.replace("", "§d")));
							target.sendMessage(new TextComponent("§7Reçu de §3" + p.getName() + " §8» " + msg.replace("", "§d")));
							MsgCmd.messages.put(target, p);
						}else {
							p.sendMessage(new TextComponent("§8■ §cErreur. §7Utilisation §8» §e/r <message>§7."));
							return;
						}
					}else {
						p.sendMessage(new TextComponent("§8■ §cErreur. Vous ne pouvez vous envoyer des messages à vous même."));
						return;
					}
				}
			}
		}else {
			p.sendMessage(new TextComponent("§8■ §cErreur. §7Vous n'avez personne à qui répondre."));
			return;
		}
	}

}
