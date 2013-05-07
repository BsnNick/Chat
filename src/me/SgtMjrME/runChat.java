package me.SgtMjrME;

import org.bukkit.entity.Player;

public class runChat implements Runnable {
	private RCChat pl;
	private Player p;
	private String format;
	private String message;

	runChat(RCChat pl, Player p, String format, String message) {
		this.pl = pl;
		this.p = p;
		this.format = format;
		this.message = message;
	}

	public void run() {
		this.pl.fromRunnable(this.p, this.format, this.message);
	}
}