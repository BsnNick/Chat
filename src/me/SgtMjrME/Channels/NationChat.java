package me.SgtMjrME.Channels;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class NationChat extends BaseChannel {

	final RCChat pl;

	public NationChat(RCChat pl) {
		this.pl = pl;
		try {
			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/nc.yml");
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
	void getDestination(Player p, String format, String message) {
		// First, check if player has perms
		Perm perm = RCChat.getPerm(p);
		if (!perm.hasPerm(22)) {
			p.sendMessage(getPermErr());
			return;
		}
		List<Player> players;
		try {
			Nation n = TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation();
			if (n != null)
				players = TownyUniverse.getOnlinePlayers(n);
			else{
				p.sendMessage("Not part of a nation");
				return;
			}
		} catch (NotRegisteredException e) {
			p.sendMessage(ChatColor.RED + "Error displaying message, Towny Nation not found");
			return;
		} catch (Exception e){
			p.sendMessage(ChatColor.RED + "Error retreiving nation");
			return;
		}
		

		// Remove non-permission
		Iterator<Player> i = players.iterator();
		while (i.hasNext()) {
			if (!RCChat.getPerm(i.next()).hasPerm(23))
				i.remove();
		}
		// send
		receiveDestination(players, p, format, message);
	}

}
