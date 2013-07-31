package me.SgtMjrME;

import me.SgtMjrME.Channels.Channel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
//import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
//import org.bukkit.plugin.Plugin;

//@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {
	private final RCChat pl;

	PlayerListener(RCChat r) {
		this.pl = r;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChangeFormat(AsyncPlayerChatEvent e) {
		e.setFormat("  %1$s  %2$s");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChangeWorld(PlayerTeleportEvent e) {
		if (e.isCancelled())
			return;

		RCChat.permissions.remove(e.getPlayer());
		RCChat.permissions.put(e.getPlayer(), new Perm(e.getPlayer()));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		this.pl.fromRunnable(e.getPlayer(), e.getFormat(), e.getMessage());
	}

//	@EventHandler(priority = EventPriority.LOWEST)
//	public void onPlayerOldChat(PlayerChatEvent e) {
//		for (Plugin p : this.pl.getServer().getPluginManager().getPlugins()) {
//			PlayerChatEvent.getHandlerList().unregister(p);
//		}
//		e.setCancelled(true);
//	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent e) {
		this.pl.removePlayer(e.getPlayer());
		this.pl.onlineHelpers.remove(e.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLeave(PlayerQuitEvent e) {
		if (RCChat.indebug){
			if (RCChat.playerLogin.containsKey(e.getPlayer().getName())){
				int val = RCChat.playerLogin.get(e.getPlayer().getName()) + 1;
				RCChat.playerLogin.remove(e.getPlayer().getName());
				RCChat.playerLogin.put(e.getPlayer().getName(), val);
			}
			else{
				RCChat.playerLogin.put(e.getPlayer().getName(), 1);
			}
			for (Player p : Channel.debugPlayers) p.sendMessage(ChatColor.RED + "Player " + e.getPlayer().getName() + " has logged off");
		}
		this.pl.removePlayer(e.getPlayer());
		this.pl.onlineHelpers.remove(e.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVanish(PlayerCommandPreprocessEvent e) {
		if (e.isCancelled())
			return;
		if ((!e.getMessage().contains("/vanish"))
				&& (!e.getMessage().equals("/v")))
			return;
		if (e.isAsynchronous()) {// Don't expect this to ever happen, but it's
									// important
			final PlayerCommandPreprocessEvent ev = new PlayerCommandPreprocessEvent(
					e.getPlayer(), e.getMessage());
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.pl,
					new Runnable() {
						public void run() {
							PlayerListener.this.onVanish(ev);
						}
					});
			return;
		}
		if (!e.getPlayer().hasPermission("rcchat.m"))
			return;
		if (RCChat.e.getVanishedPlayers().contains(e.getPlayer().getName()))
			this.pl.onlineHelpers.add(e.getPlayer().getName());
		else
			this.pl.onlineHelpers.remove(e.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLogin(PlayerJoinEvent e) {
		RCChat.permissions.put(e.getPlayer(), new Perm(e.getPlayer()));
		if (RCChat.indebug){
			for (Player p : Channel.debugPlayers){
				p.sendMessage(ChatColor.GREEN + "Player " + e.getPlayer().getName() + " has logged on");
			}
		}
		if ((e.getPlayer().hasPermission("rcchat.m")))
			this.pl.onlineHelpers.add(e.getPlayer().getName());
	}
}