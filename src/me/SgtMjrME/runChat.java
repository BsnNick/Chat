package me.SgtMjrME;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public class runChat implements Runnable {
	private RCChat pl;
	AsyncPlayerChatEvent e;

	runChat(RCChat pl, AsyncPlayerChatEvent e) {
		this.pl = pl;
		this.e = e;
	}

	public void run() {
		this.pl.fromRunnable(e);
	}
}