package me.SgtMjrME.Channels;

import java.util.Iterator;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class NationChat extends BaseChannel {

	public NationChat(RCChat pl) {
		super(pl, "nc");
	}
	
	@Override
	void getDestination(AsyncPlayerChatEvent e) {
}

	@Override
	public int getPerm() {
		// TODO Auto-generated method stub
		return 0;
	}
}