package me.SgtMjrME.Channels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

public class JailChat extends BaseChannel{

	RCChat pl;
	
	public JailChat(RCChat pl) {
		this.pl = pl;
		try {
			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/jc.yml");
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

		// Questionable call, never used aslist
		List<Player> players = new ArrayList<Player>(Arrays.asList(pl
				.getServer().getOnlinePlayers()));
		
		// Remove non-permission
		Iterator<Player> i = players.iterator();
		while (i.hasNext()) {
			if (!RCChat.getPerm(i.next()).hasPerm(1))
				i.remove();
		}
		players.add(p);
		// send
		receiveDestination(players, p, format, message);
	}
	
	@Override
	public boolean isJail(){
		return true;
	}

}
