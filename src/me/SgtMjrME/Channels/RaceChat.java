package me.SgtMjrME.Channels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;
import me.SgtMjrME.Object.Race;
import me.SgtMjrME.Object.WarPlayers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

public class RaceChat extends BaseChannel {

	final RCChat pl;

	public RaceChat(RCChat pl) {
		this.pl = pl;
		try {
			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/rc.yml");
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
		Perm perm = RCChat.getPerm(p);
		if (!perm.hasPerm(6)) {
			p.sendMessage(getPermErr());
			return;
		}
		
		Race r = WarPlayers.getRace(p);
		if (r == null){
			p.sendMessage(getOtherErr());
			return;
		}
		List<Player> players = new ArrayList<Player>();
		Iterator<String> it = WarPlayers.listPlayers();
		String pstring;
		Player player;
		while (it.hasNext()) {
			pstring = (String) it.next();
			player = pl.getServer().getPlayer(pstring);
			if (player != null) {
				r = WarPlayers.getRace(player);
				if (r.equals(WarPlayers.getRace(p))
						&& RCChat.getPerm(player).hasPerm(19))
					players.add(player);
			}
			else it.remove();
		}

		// send
		receiveDestination(players, p, format, message);
	}

}
