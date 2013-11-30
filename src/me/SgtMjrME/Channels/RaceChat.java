package me.SgtMjrME.Channels;

import java.util.Iterator;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;
import me.SgtMjrME.Object.Race;
import me.SgtMjrME.Object.WarPlayers;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class RaceChat extends BaseChannel {

	public RaceChat(RCChat pl) {
		super(pl, "rc");
//		this.pl = pl;
//		try {
//			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/rc.yml");
//			setName(cfg.getString("name"));
//			setDisp(cfg.getString("disp"));
//			setPermission(cfg.getString("permission"));
//			setColor(ChatColor.valueOf(cfg.getString("chatColor")));
//			setPermErr(ChatColor.translateAlternateColorCodes('&', cfg.getString("permerr")));
//			setOtherErr(ChatColor.translateAlternateColorCodes('&', cfg.getString("othererr")));
//		} catch (IOException | InvalidConfigurationException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	void getDestination(AsyncPlayerChatEvent e) {
		// First, check if player has perms
		Player p = e.getPlayer();
		Perm perm = RCChat.getPerm(p);
		if (!perm.hasPerm(6)) {
			p.sendMessage(getPermErr());
			e.getRecipients().clear();
			e.setCancelled(true);
			return;
		}
		
		Race r = WarPlayers.getRace(p);
		if (r == null){
			p.sendMessage(getOtherErr());
			e.getRecipients().clear();
			e.setCancelled(true);
			return;
		}
		Iterator<String> it = WarPlayers.listPlayers();
		String pstring;
		Player player;
		e.getRecipients().clear();
		while (it.hasNext()) {
			pstring = (String) it.next();
			player = pl.getServer().getPlayer(pstring);
			if (player != null) {
				r = WarPlayers.getRace(player);
				if (r.equals(WarPlayers.getRace(p))
						&& RCChat.getPerm(player).hasPerm(19))
					e.getRecipients().add(player);
			}
			else it.remove();
		}

		// send
		receiveDestination(e);
	}

	@Override
	public int getPerm() {
		return 6;
	}

}
