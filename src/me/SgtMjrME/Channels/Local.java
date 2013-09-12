package me.SgtMjrME.Channels;

import java.io.IOException;
import java.util.List;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Local extends BaseChannel {

	final RCChat pl;
	int d;

	public Local(RCChat pl) {
		this.pl = pl;
		try {
			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/l.yml");
			setName(cfg.getString("name"));
			setDisp(cfg.getString("disp"));
			setPermission(cfg.getString("permission"));
			setColor(ChatColor.valueOf(cfg.getString("chatColor")));
			setPermErr(ChatColor.translateAlternateColorCodes('&',
					cfg.getString("permerr")));
			setOtherErr(ChatColor.translateAlternateColorCodes('&',
					cfg.getString("othererr")));
			d = cfg.getInt("distance");
			setTag(true);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

	}

	@Override
	void getDestination(final AsyncPlayerChatEvent e) {
		// This one has to be done sync'd, due to getNearbyEntities
		//F*** IT, NOT SYNC'D ANYMORE

		// First, check if player has perms
		final Player p = e.getPlayer();
		Perm perm = RCChat.getPerm(p);
		if (!perm.hasPerm(5)) {
			p.sendMessage(getPermErr());
			e.getRecipients().clear();
			e.setCancelled(true);
			return;
		}

		// Start location
		// Nested run's... how fun
		e.getRecipients().clear();
		List<Entity> ent = p.getNearbyEntities(d, d, d);
		for (Entity entity : ent){
			if (entity == null || !(entity instanceof Player)) continue;
			Player other = (Player) entity;
			Perm operm = RCChat.getPerm(other);
			if (operm != null && operm.hasPerm(18))
				e.getRecipients().add(other);
		}
		if (perm.hasPerm(18)) e.getRecipients().add(p);
		receiveDestination(e);
//		pl.getServer().getScheduler().runTask(pl, new Runnable() {
//			@Override
//			public void run() {
//				final List<Entity> ent = p.getNearbyEntities(d, d, d);
//				pl.getServer().getScheduler()
//						.runTaskAsynchronously(pl, new Runnable() {
//							@Override
//							public void run() {
//								List<Player> out = new ArrayList<Player>();
//								for (Entity e : ent) {
//									if (e == null)
//										continue;
//									if (!(e instanceof Player))
//										continue;
//									Player other = (Player) e;
//									Perm perm = RCChat.getPerm(other);
//									if (perm != null && perm.hasPerm(18))
//										out.add(other);
//									else if (perm == null) {
//										Bukkit.getLogger()
//												.warning(
//														"RCCHAT encountered an error, " +
//														"null permissions, " +
//														"that could not be resolved.  Failing safetly");
//									}
//								}
//								Perm perm = RCChat.getPerm(p);
//								if (perm != null && perm.hasPerm(18))
//									out.add(p);
//								receiveDestination(out, p, format, message);
//							}
//						});
//			}
//		});
	}

}
