package me.SgtMjrME.Channels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

public class Mod extends BaseChannel {

	final RCChat pl;

	public Mod(RCChat pl) {
		this.pl = pl;
		try {
			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/m.yml");
			setName(cfg.getString("name"));
			setDisp(cfg.getString("disp"));
			setPermission(cfg.getString("permission"));
			setColor(ChatColor.valueOf(cfg.getString("chatColor")));
			setPermErr(ChatColor.translateAlternateColorCodes('&', cfg.getString("permerr")));
			setOtherErr(ChatColor.translateAlternateColorCodes('&', cfg.getString("othererr")));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	void getDestination(Player p, String format, String message) {
		// First, check if player has perms
		Perm perm = RCChat.permissions.get(p);
		if (!perm.hasPerm(1)) {
			p.sendMessage(getPermErr());
			return;
		}

		// Questionable call, never used aslist
		List<Player> players = new ArrayList<Player>(Arrays.asList(pl
				.getServer().getOnlinePlayers()));

		// Remove non-permission
		Iterator<Player> i = players.iterator();
		while (i.hasNext()) {
			if (!RCChat.permissions.get(i.next()).hasPerm(14))
				i.remove();
		}
		// send
		receiveDestination(players, p, format, message);
	}
}
