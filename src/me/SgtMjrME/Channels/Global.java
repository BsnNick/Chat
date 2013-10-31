package me.SgtMjrME.Channels;

import java.io.IOException;
import java.util.Iterator;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Global extends BaseChannel {

	RCChat pl;

	public Global(RCChat pl) {
		this.pl = pl;
		try {
			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/g.yml");
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
		if (!perm.hasPerm(4)) {
			p.sendMessage(getPermErr());
			e.getRecipients().clear();
			e.setCancelled(true);
			return;
		}
		
		//Next, generate the proper list
		//Note: This is probably INCREDIBLY thread unsafe
		//As such, will probably have to change
		//However... just makes my code SOOO clean :(
		e.getRecipients().clear();
		e.getRecipients().addAll(p.getWorld().getPlayers());
		Iterator<Player> i = e.getRecipients().iterator();
		while(i.hasNext()){
			if (!RCChat.getPerm(i.next()).hasPerm(17))
				i.remove();
		}
		receiveDestination(e);
	}

	@Override
	public int getPerm() {
		return 4;
	}

}
