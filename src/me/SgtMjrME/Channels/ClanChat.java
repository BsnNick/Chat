package me.SgtMjrME.Channels;

import java.util.Iterator;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ClanChat extends BaseChannel {

	public ClanChat(RCChat pl) {
		super(pl, "cl");
		// TODO Auto-generated constructor stub
	}

	@Override
	void getDestination(AsyncPlayerChatEvent e) {
		// First, check if player has perms
			Player p = e.getPlayer();
			Perm perm = RCChat.getPerm(p);
			if (!perm.hasPerm(this.getPerm())) {
				p.sendMessage(getPermErr());
				e.getRecipients().clear();
				e.setCancelled(true);
				return;
			}

			e.getRecipients().clear();
			e.getRecipients().addAll(ClanHelper.setRecipients(e.getPlayer()));
			

			// Remove non-permission
			Iterator<Player> i = e.getRecipients().iterator();
			while (i.hasNext()) {
				if (!RCChat.getPerm(i.next()).hasPerm(this.getPerm()))
					i.remove();
			}
			// send
			receiveDestination(e);
	}

	@Override
	public int getPerm() {
		return 24;
	}

}
