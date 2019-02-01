package me.SgtMjrME.Channels;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AllyChat extends BaseChannel {

	public AllyChat(RCChat pl) {
		super(pl, "ac");
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
		e.getRecipients().addAll(ClanHelper.setAllyRecipients(e.getPlayer()));
		

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
		// TODO Auto-generated method stub
		return 24;
	}

}
