package me.SgtMjrME.Channels;

import java.util.HashMap;
import java.util.List;

import me.SgtMjrME.LilyPadHandler;
import me.SgtMjrME.Pair;
import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Message extends BaseChannel {
	
	//<player to look up, player who last sent them message>
	static public HashMap<String, String> pReply = new HashMap<String, String>();

	public Message(RCChat pl) {
		super(pl, "msg");
	}

	@Override
	void getDestination(AsyncPlayerChatEvent e) {
		//This class will differ from other basechannels in that it does NOT call to receiveDestination
		//This class is irregular - it will never send via the main chat
		e.getRecipients().clear();
//		e.setCancelled(true);
		Player p = e.getPlayer();
		
		// First, check if player has perms
		Perm perm = RCChat.getPerm(p);
		if (!perm.hasPerm(25)) {
			p.sendMessage(getPermErr());
			return;
		}
		String[] split = e.getMessage().split(" ");
		Player target = Bukkit.getPlayer(split[0]);
		if (target == null){
			//Player not on this server - try other servers
			if (RCChat.lph == null){
				e.getPlayer().sendMessage(ChatColor.RED + "Player not found"); 
				e.setCancelled(true);
				return; //Lilypad doesn't exist, no other servers exist... STOP
			}
			String server = null;
			List<Pair> poss = LilyPadHandler.findServer(split[0]);
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
			send(e,server,poss.get(0).player);
		} else {
			//Player found on current server
			alterMessageMsg(e, target.getDisplayName());
			e.getRecipients().add(target);
			pReply.put(split[0], p.getName());
		}
		p.sendMessage(e.getFormat()
				.replace("%2$s", e.getMessage()));
		pReply.put(p.getName(), split[0]);
	}
	
	private void alterMessageMsg(AsyncPlayerChatEvent e, String tar){
		int val = e.getMessage().trim().indexOf(" ");
		e.setMessage(e.getMessage().substring(val == -1 ? 0 : val + 1));
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
