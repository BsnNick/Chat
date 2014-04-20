package me.SgtMjrME.Channels;

import me.SgtMjrME.LilyPadHandler;
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
		e.setCancelled(true);
		
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
				String server = LilyPadHandler.findServer(target);
				if (server == null){
					//Player not on any server, or hasn't reloaded them yet
					e.getPlayer().sendMessage(ChatColor.RED + "Player not found"); 
					return;
				}
				send(e,server,target);
			} else {
				//Player found!
				alterMessageMsg(e);
				e.getRecipients().add(targetPlayer);
			}
		}
		Message.pReply.put(p.getName(), target);
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
			RCChat.lph.sendMessage("rcchat.msg." + player + "," + e.getPlayer().getName(), e.getMessage(), server);
		}
	}

	@Override
	public int getPerm() {
		// TODO Auto-generated method stub
		return 25;
	}

}
