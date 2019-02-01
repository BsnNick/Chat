package me.SgtMjrME.Channels;

import java.util.Iterator;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TownyChat extends BaseChannel {

	public TownyChat(RCChat pl) {
		super(pl, "twc");
//		this.pl = pl;
//		try {
//			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/twc.yml");
//			setName(cfg.getString("name"));
//			setDisp(cfg.getString("disp"));
//			setPermission(cfg.getString("permission"));
//			setColor(ChatColor.valueOf(cfg.getString("chatColor")));
//			setPermErr(ChatColor.translateAlternateColorCodes('&', cfg.getString("permerr")));
//			setOtherErr(ChatColor.translateAlternateColorCodes('&', cfg.getString("othererr")));
//			setTag(true);
//		} catch (IOException | InvalidConfigurationException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	void getDestination(AsyncPlayerChatEvent e) {
	}

	@Override
	public int getPerm() {
		return 22;
	}

}
