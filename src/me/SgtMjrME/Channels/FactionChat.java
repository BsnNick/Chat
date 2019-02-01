package me.SgtMjrME.Channels;

import java.util.Iterator;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class FactionChat extends BaseChannel {

	public FactionChat(RCChat pl) {
		super(pl, "fc");
	}

	@Override
	void getDestination(AsyncPlayerChatEvent e) {
	}

	@Override
	public int getPerm() {
		return 20;
	}

}
