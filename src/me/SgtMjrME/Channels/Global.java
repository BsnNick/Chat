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

public class Global extends BaseChannel {

	RCChat pl;

	public Global(RCChat pl) {
		this.pl = pl;
		try {
			cfg.load(pl.getDataFolder().getAbsolutePath() + "/g.yml");
			setName(cfg.getString("name"));
			setDisp(cfg.getString("disp"));
			setPermission(cfg.getString("permission"));
			setColor(ChatColor.valueOf(cfg.getString("chatColor")));
			setPermErr(cfg.getString("permerr"));
			setOtherErr(cfg.getString("othererr"));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	void getDestination(Player p, String format, String message) {
		// First, check if player has perms
		Perm perm = RCChat.permissions.get(p);
		if (!perm.hasPerm(4)) {
			p.sendMessage(getPermErr());
			return;
		}
		
		//Next, generate the proper list
		//Note: This is probably INCREDIBLY thread unsafe
		//As such, will probably have to change
		//However... just makes my code SOOO clean :(
		List<Player> players = new ArrayList<Player>(p.getWorld().getPlayers());
		Iterator<Player> i = players.iterator();
		while(i.hasNext()){
			if (!RCChat.permissions.get(i.next()).hasPerm(17))
				i.remove();
		}
		receiveDestination(players, p, format, message);
	}

}
