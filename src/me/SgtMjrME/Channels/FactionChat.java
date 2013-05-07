package me.SgtMjrME.Channels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

public class FactionChat extends BaseChannel {

	final RCChat pl;

	public FactionChat(RCChat pl) {
		this.pl = pl;
		try {
			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/fc.yml");
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
		if (!perm.hasPerm(20)) {
			p.sendMessage(getPermErr());
			return;
		}

		// TODO
		FPlayer fp = FPlayers.i.get(p);
		if (fp == null || fp.getFaction() == null){
			p.sendMessage(getOtherErr());
			return;
		}
		
		List<Player> players = new ArrayList<Player>(fp.getFaction().getOnlinePlayers());

		// Remove non-permission
		Iterator<Player> i = players.iterator();
		while (i.hasNext()) {
			if (!RCChat.permissions.get(i.next()).hasPerm(21))
				i.remove();
		}
		// send
		receiveDestination(players, p, format, message);
	}

}
