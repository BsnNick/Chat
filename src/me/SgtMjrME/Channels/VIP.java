package me.SgtMjrME.Channels;

import java.io.IOException;
import java.util.Iterator;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class VIP extends BaseChannel {

	final RCChat pl;

	public VIP(RCChat pl) {
		this.pl = pl;
		try {
			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/dc.yml");
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
	void getDestination(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		p.sendMessage(ChatColor.RED + "DC is being phased out, please use sc");
		
		// First, check if player has perms
		Perm perm = RCChat.getPerm(p);
		if (!perm.hasPerm(3)) {
			p.sendMessage(getPermErr());
			e.getRecipients().clear();
			e.setCancelled(true);
			return;
		}

//		// Questionable call, never used aslist
//		List<Player> players = new ArrayList<Player>(Arrays.asList(pl
//				.getServer().getOnlinePlayers()));

		// Remove non-permission
		Iterator<Player> i = e.getRecipients().iterator();
		while (i.hasNext()) {
			if (!RCChat.getPerm(i.next()).hasPerm(17))
				i.remove();
		}
		
		// send
		receiveDestination(e);
	}

}
