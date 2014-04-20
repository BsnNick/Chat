package me.SgtMjrME.Channels;

import java.util.HashMap;

import me.SgtMjrME.LilyPadHandler;
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
		e.setCancelled(true);
		
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
			String server = LilyPadHandler.findServer(split[0]);
			if (server == null){
				//Player not on any server, or hasn't reloaded them yet
				e.getPlayer().sendMessage(ChatColor.RED + "Player not found, "); 
				return;
			}
			send(e,server,split[0]);
		} else {
			//Player found!
			alterMessageMsg(e);
			e.getRecipients().add(target);
		}
		pReply.put(p.getName(), split[0]);
	}
	
	private void alterMessageMsg(AsyncPlayerChatEvent e){
		e.setMessage(e.getMessage().substring(e.getMessage().trim().indexOf(" ")));
		alterMessage(e);
	}

	private void send(AsyncPlayerChatEvent e, String server, String player) {
		alterMessageMsg(e);
		e.setMessage(ChatColor.translateAlternateColorCodes('&', 
				e.getFormat()
				.replace("%1$s", e.getPlayer().getDisplayName())
				.replace("%2$s", e.getMessage())));
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
