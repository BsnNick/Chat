package me.SgtMjrME.Channels;

import java.util.List;

import me.SgtMjrME.LilyPadHandler;
import me.SgtMjrME.Pair;
import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Reply extends BaseChannel {
	
	public Reply(RCChat pl) {
		super(pl, "r");
	}

	@Override
	void getDestination(AsyncPlayerChatEvent e) {
		//This class will differ from other basechannels in that it does NOT call to receiveDestination
		e.getRecipients().clear();
//		e.setCancelled(true);
		
		Player p = e.getPlayer();
		
		// First, check if player has perms
		Perm perm = RCChat.getPerm(p);
		if (!perm.hasPerm(26)) {
			p.sendMessage(getPermErr());
			return;
		}
		String target = Message.pReply.get(p.getName());
		if (target == null){
			//No reply from this server
			e.getPlayer().sendMessage(ChatColor.RED + "Nobody to reply to");
			return;
		} else {
			//Previous reply found - lets see if we can find the player
			Player targetPlayer = Bukkit.getPlayer(target);
			if (targetPlayer == null){
				//Player not on this server - try other servers
				if (RCChat.lph == null){
					e.getPlayer().sendMessage(ChatColor.RED + "Player not found"); 
					e.setCancelled(true);
					return; //No other servers can exist
				}
				String server = null;
				List<Pair> poss = LilyPadHandler.findServer(target);
				if (poss.size() == 1){
					//There is an exact match, or only 1 match - message should work
					server = poss.get(0).server;
				} else if (poss.size() > 1){
					p.sendMessage(ChatColor.RED + "Player not found - did you mean:");
					for(Pair pair : poss){
						p.sendMessage(ChatColor.RED + "(" + pair.server + ") " + pair.player);
					}
				}
				if (server == null){
					//Player not on any server, or hasn't reloaded them yet
					e.getPlayer().sendMessage(ChatColor.RED + "Player not found on any server"); 
					e.setCancelled(true);
					return;
				}
				send(e,server,target);
			} else {
				//Player found!
				alterMessageMsg(e,targetPlayer.getDisplayName());
				e.getRecipients().add(targetPlayer);
				Message.pReply.put(target, p.getName());
			}
		}
		p.sendMessage(e.getFormat()
				.replace("%2$s", e.getMessage()));
		Message.pReply.put(p.getName(), target);
	}
	
	private void alterMessageMsg(AsyncPlayerChatEvent e, String tar){
		e.setFormat(e.getFormat().replace("%1$s", e.getPlayer().getDisplayName() + "->" + tar));
		alterMessage(e);
	}

	private void send(AsyncPlayerChatEvent e, String server, String player) {
		alterMessageMsg(e,player);
		e.setMessage(e.getFormat()
				.replace("%2$s", e.getMessage()));
		if (RCChat.lph != null){
			RCChat.lph.sendMessage("rcchat.msg." + player + "," + e.getPlayer().getDisplayName(), e.getMessage(), server);
		}
	}

	@Override
	public int getPerm() {
		// TODO Auto-generated method stub
		return 25;
	}

}
