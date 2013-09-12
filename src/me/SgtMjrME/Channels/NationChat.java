package me.SgtMjrME.Channels;

import java.io.IOException;
import java.util.Iterator;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
	void getDestination(AsyncPlayerChatEvent e) {
		// First, check if player has perms
		Player p = e.getPlayer();
		Perm perm = RCChat.getPerm(p);
		if (!perm.hasPerm(22)) {
			p.sendMessage(getPermErr());
			e.getRecipients().clear();
			e.setCancelled(true);
			return;
		}
		e.getRecipients().clear();
		try {
			Nation n = TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation();
			if (n != null)
				e.getRecipients().addAll(TownyUniverse.getOnlinePlayers(n));
			else{
				p.sendMessage("Not part of a nation");
				e.setCancelled(true);
				return;
			}
		} catch (NotRegisteredException ex) {
			p.sendMessage(ChatColor.RED + "Error displaying message, Towny Nation not found");
			e.setCancelled(true);
			return;
		} catch (Exception ex){
			p.sendMessage(ChatColor.RED + "Error retreiving nation");
			e.setCancelled(true);
			return;
		}
		

		// Remove non-permission
		Iterator<Player> i = e.getRecipients().iterator();
		while (i.hasNext()) {
			if (!RCChat.getPerm(i.next()).hasPerm(23))
				i.remove();
		}
		// send
		receiveDestination(e);
	}

}
