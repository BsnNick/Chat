package me.SgtMjrME.Channels;

import java.io.IOException;
import java.util.Iterator;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
			setTag(true);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	void getDestination(AsyncPlayerChatEvent e) {
		// First, check if player has perms
		Player p = e.getPlayer();
		Perm perm = RCChat.getPerm(p);
		if (!perm.hasPerm(20)) {
			p.sendMessage(getPermErr());
			return;
		}

		// TODO
		FPlayer fp = FPlayers.i.get(p);
		if (fp == null || fp.getFaction() == null){
			p.sendMessage(getOtherErr());
			e.getRecipients().clear();
			e.setCancelled(true);
			return;
		}
		
		e.getRecipients().clear();
		e.getRecipients().addAll(fp.getFaction().getOnlinePlayers());

		// Remove non-permission
		Iterator<Player> i = e.getRecipients().iterator();
		while (i.hasNext()) {
			if (!RCChat.getPerm(i.next()).hasPerm(21))
				i.remove();
		}
		// send
		receiveDestination(e);
	}

}
