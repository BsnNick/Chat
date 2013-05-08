package me.SgtMjrME;

import com.earth2me.essentials.Essentials;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.SgtMjrME.Channels.BaseChannel;
import me.SgtMjrME.Channels.Channel;
import me.SgtMjrME.RCWars.Object.WarPlayers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RCChat extends JavaPlugin {
	static public RCChat instance;
	private static ArrayList<String> webVal = new ArrayList<String>();
	private Logger log;
	public static Essentials e;
	public PluginManager pm;
	static public ConcurrentHashMap<Player, Perm> permissions = new ConcurrentHashMap<Player, Perm>();
	private double time;
	private BaseChannel defaultChannel;
	private PlayerListener playerListener;
	public HashSet<String> onlineHelpers = new HashSet<String>();
	public final String channelName="RCChat";

	public void onEnable() {
		instance = this;
		this.log = getServer().getLogger();
		this.pm = getServer().getPluginManager();
		RCChat.e = null;
		if (this.pm.isPluginEnabled("Essentials")) {
			RCChat.e = ((Essentials) this.pm.getPlugin("Essentials"));
			if (RCChat.e != null)
				this.log.info("Essentials Loaded into RCChat");
			else
				this.log.warning("Essentials not loaded");
		}
		Channel.loadChannels(this);
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(getDataFolder() + "/config.yml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		String defaulttemp = config.getString("defaultchannel");
		if (defaulttemp != null) {
			if (Channel.get(defaulttemp) != null) {
				this.defaultChannel = Channel.get(defaulttemp);
			} else
				this.defaultChannel = Channel.get("l");
		} else {
			this.defaultChannel = Channel.get("l");
			this.log.log(Level.SEVERE, "Check config! defaultchannel not set!");
		}
		this.time = config.getDouble("delay", 1.0D);
		String s = config.getString("webcheck");
		if (s != null) {
			String[] s1 = s.split(",");
			for (String s2 : s1) {
				webVal.add(s2);
			}
		}
		//This informs Bukkit that you will send messages through that channel
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, channelName);
		
		//This informs Bukkit that you want to receive messages through that channel, to myPluginMessageListener
		Bukkit.getMessenger().registerIncomingPluginChannel(this, channelName, new ChatClientComm(this));
		
		this.playerListener = new PlayerListener(this);
		this.pm.registerEvents(this.playerListener, this);
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player p = (Player) sender;
		if (commandLabel.equalsIgnoreCase("onlinestaff")) {
			Iterator<String> i = this.onlineHelpers.iterator();
			while(i.hasNext()){
				String s = i.next();
				Player temp = getServer().getPlayer(s);
				if (temp != null)
					p.sendMessage(temp.getDisplayName());
				else
					i.remove();
			}
			return true;
		}
		Perm perm = (Perm) RCChat.permissions.get(p);
		if ((commandLabel.equalsIgnoreCase("chatdebug")) && (perm.hasPerm(2))) {
			if (Channel.debugPlayers.contains(p)){
				p.sendMessage("Removing chat sight");
				Channel.debugPlayers.remove(p);
			}
			else{
				p.sendMessage("Adding chat sight");
				Channel.debugPlayers.add(p);
			}
			return true;
		}
		if (commandLabel.equalsIgnoreCase("g")) {
			BaseChannel c = Channel.get("g");
			if (c == null){
				p.sendMessage(ChatColor.RED + "Channel not found");
				return true;
			}
			if (!perm.hasPerm(4)) {
					p.sendMessage(ChatColor.RED + c.getPermErr());
					return false;
				}
			if (args.length == 0) {
				Channel.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			Channel.tempChannel.put(p, c);
			Set<Player> nullSetPlayer = new HashSet<Player>();
			nullSetPlayer.add(p);
			final AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, p,
					args2str(args), nullSetPlayer);
			e.setFormat("  %1$s  %2$s");
			getServer().getScheduler().runTaskAsynchronously(this,
					new Runnable() {
						public void run() {
							RCChat.this.playerListener.onPlayerChat(e);
						}
					});
			return true;
		}
		if (commandLabel.equalsIgnoreCase("l")) {
			BaseChannel c = Channel.get("l");
			if (c == null){
				p.sendMessage(ChatColor.RED + "Channel not found");
				return true;
			}
			if (!perm.hasPerm(5)) {
					p.sendMessage(ChatColor.RED + c.getPermErr());
					return false;
				}
			if (args.length == 0) {
				Channel.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			Channel.tempChannel.put(p, c);
			Set<Player> nullSetPlayer = new HashSet<Player>();
			nullSetPlayer.add(p);
			final AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, p,
					args2str(args), nullSetPlayer);
			e.setFormat("  %1$s  %2$s");
			getServer().getScheduler().runTaskAsynchronously(this,
					new Runnable() {
						public void run() {
							RCChat.this.playerListener.onPlayerChat(e);
						}
					});
			return true;
		}
		if (commandLabel.equalsIgnoreCase("dc")) {
			BaseChannel c = Channel.get("dc");
			if (c == null){
				p.sendMessage(ChatColor.RED + "Channel not found");
				return true;
			}
			if (!perm.hasPerm(3)) {
					p.sendMessage(ChatColor.RED + c.getPermErr());
					return false;
				}
			if (args.length == 0) {
				Channel.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			Channel.tempChannel.put(p, c);
			Set<Player> nullSetPlayer = new HashSet<Player>();
			nullSetPlayer.add(p);
			final AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, p,
					args2str(args), nullSetPlayer);
			e.setFormat("  %1$s  %2$s");
			getServer().getScheduler().runTaskAsynchronously(this,
					new Runnable() {
						public void run() {
							RCChat.this.playerListener.onPlayerChat(e);
						}
					});
			return true;
		}
		if (commandLabel.equalsIgnoreCase("me")) {
			BaseChannel c = Channel.get("me");
			if (c == null){
				p.sendMessage(ChatColor.RED + "Channel not found");
				return true;
			}
			if (!perm.hasPerm(2)) {
				p.sendMessage(ChatColor.RED + c.getPermErr());
				return false;
			}
			if (args.length == 0) {
				Channel.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			Channel.tempChannel.put(p, c);
			Set<Player> nullSetPlayer = new HashSet<Player>();
			nullSetPlayer.add(p);
			final AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, p,
					args2str(args), nullSetPlayer);
			e.setFormat("  %1$s  %2$s");
			getServer().getScheduler().runTaskAsynchronously(this,
					new Runnable() {
						public void run() {
							RCChat.this.playerListener.onPlayerChat(e);
						}
					});
			return true;
		}
		if (commandLabel.equalsIgnoreCase("m")) {
			BaseChannel c = Channel.get("m");
			if (c == null){
				p.sendMessage(ChatColor.RED + "Channel not found");
				return true;
			}
			if (!perm.hasPerm(1)) {
					p.sendMessage(ChatColor.RED + c.getPermErr());
					return false;
				}
			if (args.length == 0) {
				Channel.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			Channel.tempChannel.put(p, c);
			Set<Player> nullSetPlayer = new HashSet<Player>();
			nullSetPlayer.add(p);
			final AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, p,
					args2str(args), nullSetPlayer);
			e.setFormat("  %1$s  %2$s");
			getServer().getScheduler().runTaskAsynchronously(this,
					new Runnable() {
						public void run() {
							RCChat.this.playerListener.onPlayerChat(e);
						}
					});
			return true;
		}
		if (commandLabel.equalsIgnoreCase("rc")) {
			if (!this.pm.isPluginEnabled("RCWars") || 
					WarPlayers.getRace(p) == null) {
				p.sendMessage(ChatColor.RED + "You are not in Wars");
				return false;
			}
			BaseChannel c = Channel.get("rc");
			if (c == null){
				p.sendMessage(ChatColor.RED + "Channel not found");
				return true;
			}
			if (!perm.hasPerm(6)) {
					p.sendMessage(ChatColor.RED + c.getPermErr());
					return false;
				}
			if (args.length == 0) {
				Channel.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			Channel.tempChannel.put(p, c);
			Set<Player> nullSetPlayer = new HashSet<Player>();
			nullSetPlayer.add(p);
			final AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, p,
					args2str(args), nullSetPlayer);
			e.setFormat("  %1$s  %2$s");
			getServer().getScheduler().runTaskAsynchronously(this,
					new Runnable() {
						public void run() {
							RCChat.this.playerListener.onPlayerChat(e);
						}
					});
			return true;
		}
		if (commandLabel.equalsIgnoreCase("fc")) {
			if (!this.pm.isPluginEnabled("Factions")) {
				p.sendMessage(ChatColor.RED + "You are not in Factions");
				return false;
			}
			BaseChannel c = Channel.get("fc");
			if (c == null){
				p.sendMessage(ChatColor.RED + "Channel not found");
				return true;
			}
			if (!perm.hasPerm(20)) {
					p.sendMessage(ChatColor.RED + c.getPermErr());
					return false;
				}
			if (args.length == 0) {
				Channel.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			Channel.tempChannel.put(p, c);
			Set<Player> nullSetPlayer = new HashSet<Player>();
			nullSetPlayer.add(p);
			final AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, p,
					args2str(args), nullSetPlayer);
			e.setFormat("  %1$s  %2$s");
			getServer().getScheduler().runTaskAsynchronously(this,
					new Runnable() {
						public void run() {
							RCChat.this.playerListener.onPlayerChat(e);
						}
					});
			return true;
		}
		if ((commandLabel.equalsIgnoreCase("setchannel")) && (perm.hasPerm(12))) {
			if (args.length < 2)
				return false;
			Player temp = getServer().getPlayer(args[0]);
			if (temp == null) {
				p.sendMessage("Player not found");
				return false;
			}
			BaseChannel c = Channel.get(args[1]);
			if (c == null) {
				p.sendMessage("Channel not found");
				return false;
			}
			changePlayerChannel(p, c);
		} else {
			if ((commandLabel.equalsIgnoreCase("chatmute"))
					&& (perm.hasPerm(13))) {
				if (args.length < 1)
					return false;
				Player temp = getServer().getPlayer(args[0]);
				if (temp == null) {
					p.sendMessage("Player not found");
					return false;
				}
				if ((Channel.muted.get(temp) != null)
						&& (((Boolean) Channel.muted.get(temp)))) {
					p.sendMessage("Player already muted");
				} else {
					Channel.muted.put(temp, true);
					temp.sendMessage(ChatColor.RED + "You have been muted");
					p.sendMessage("Player has been muted");
				}
				return true;
			}
			if ((commandLabel.equalsIgnoreCase("chatunmute"))
					&& (perm.hasPerm(13))) {
				if (args.length < 1)
					return false;
				Player temp = getServer().getPlayer(args[0]);
				if (temp == null) {
					p.sendMessage("Player not found");
					return false;
				}
				if ((Channel.muted.get(temp) != null)
						&& (!((Boolean) Channel.muted.get(temp)))) {
					p.sendMessage("Player wasn't muted");
				} else {
					Channel.muted.put(temp, false);
					temp.sendMessage(ChatColor.GREEN + "You have been unmuted");
					p.sendMessage("Player has been unmuted");
				}
				return true;
			}
		}
		return true;
	}

/*	public void sendMessage(Player p, BaseChannel c, String format, String s) {
		if (s == null)
			return;
		if ((this.pm.isPluginEnabled("RCWars"))
				&& (WarPlayers.getRace(p) != null)) {
			format = format.replace("%1$s", WarPlayers.getRace(p).getCcolor()
					+ "%1$s" + c.getColor());
			String replaceName = "";
			if (this.e != null) {
				replaceName = ChatColor.stripColor(this.e.getUserMap()
						.getUser(p.getName())._getNickname());
				if (replaceName == null)
					replaceName = this.e.getUserMap().getUser(p.getName())
							.getName();
				else
					replaceName = "~" + replaceName;
			} else {
				replaceName = ChatColor.stripColor(p.getDisplayName());
			}
			if (p.hasPermission("rcchat.m"))
				format = format.replace("%1$s", WarPlayers.getRace(p)
						.getDisplay()
						+ " "
						+ ChatColor.GOLD
						+ replaceName
						+ ' ' + WarRank.getPlayer(p).display());
			else
				format = format.replace("%1$s", WarPlayers.getRace(p)
						.getDisplay()
						+ " "
						+ ChatColor.YELLOW
						+ replaceName
						+ ' ' + WarRank.getPlayer(p).display());
		} else if ((this.pm.isPluginEnabled("RCGuilds"))
				&& (GuildPlayer.getPlayer(p) != null)
				&& (GuildPlayer.getPlayer(p).getRank() != null)) {
			format = format.replace("%1$s", p.getDisplayName() + " "
					+ GuildPlayer.getPlayer(p).getRank().getSuffix() + "&r");
			format = format.replaceAll("(&([a-f0-9 r]))", "§$2");
		} else {
			format = format.replace("%1$s", p.getDisplayName());
		}
		s = c.setMessage(p, s);
		format = format.replace("%2$s", s);
		s = format;

		sendDebug(p, format, "");
		Player[] all = getServer().getOnlinePlayers();
		Perm perm = (Perm) RCChat.permissions.get(p);
		if (perm == null) {
			final Player hold = p;
			getServer().getScheduler().runTask(this, new Runnable() {
				public void run() {
					RCChat.permissions.put(hold, new Perm(hold));
				}
			});
		}

		if (c.getDistance() == 0) {
			if ((perm.hasPerm(2)) && (!c.isWorld())) {
				for (Player end : all)
					end.sendMessage(s);
			} else if ((perm.hasPerm(1)) || (perm.hasPerm(3)))
				if (c.getPermission().equals("rcchat.m")) {
					for (Player end : all) {
						if (((Perm) RCChat.permissions.get(end)).hasPerm(1))
							end.sendMessage(s);
					}
				} else
					for (Player end : all)
						if (((Perm) RCChat.permissions.get(end)).hasPerm(3))
							end.sendMessage(s);
			return;
		}
		String pstring;
		Player player;
		Race r;
		if ((c.isWorld()) && (c.getDistance() == -1)) {
			if (this.pm.getPlugin("RCWars") == null)
				return;
			Iterator<String> i = WarPlayers.listPlayers();
			while (i.hasNext()) {
				pstring = (String) i.next();
				player = getServer().getPlayer(pstring);
				if (player != null) {
					r = WarPlayers.getRace(player);
					if (r.equals(WarPlayers.getRace(p))
							&& permissions.get(player))
						player.sendMessage(s);
				}
			}
		} else {
			if (c.isWorld()) {// g?
				final String copy = s;
				final Player copyp = p;
				Bukkit.getScheduler().runTask(this, new Runnable() {

					@Override
					public void run() {
						Iterator<Player> peoples = copyp.getWorld()
								.getPlayers().iterator();
						while (peoples.hasNext()) {
							Player player1 = peoples.next();
							if (permissions.get(player1).hasPerm(17))
								peoples.next().sendMessage(copy);
						}

					}

				});

				return;
			}
			if (!c.isWorld()) {// l?
				final String copy = s;
				final Player copyp = p;
				final Channel cc = c;
				getServer().getScheduler().runTask(this, new Runnable() {
					public void run() {
						Iterator<Entity> things = copyp.getNearbyEntities(
								cc.getDistance(), cc.getDistance(),
								cc.getDistance()).iterator();
						while (things.hasNext()) {
							Entity check = (Entity) things.next();
							if ((check instanceof Player)) {
								Player end = (Player) check;
								if (permissions.get(end).hasPerm(18))
									end.sendMessage(copy);
							}
						}
						copyp.sendMessage(copy);
					}
				});
			}
		}
	}*/

	private String args2str(String[] args) {
		if (args.length == 0)
			return null;
		String s = args[0];
		for (int x = 1; x < args.length; x++)
			s = s + " " + args[x];
		return s;
	}

	public void addPlayerDefault(Player player) {
		Channel.pChannels.put(player, this.defaultChannel);
	}

	public void removePlayer(Player p) {
		Channel.pChannels.remove(p);
		Channel.tempChannel.remove(p);
		RCChat.permissions.remove(p);
	}

	public void addTemp(Player p, BaseChannel c) {
		Channel.tempChannel.put(p, c);
	}

	public void removeTemp(Player p) {
		Channel.tempChannel.remove(p);
	}

	public BaseChannel tempContains(Player p) {
		return Channel.tempChannel.get(p);
	}

	public BaseChannel pContains(Player p) {
		return Channel.pChannels.get(p);
	}

	public void changePlayerChannel(Player p, BaseChannel c) {
		Channel.pChannels.put(p, c);
	}

	public Boolean isMuted(Player p) {
		return Channel.muted.get(p);
	}

	public void fromRunnable(Player p, String format, String message) {
		if ((isMuted(p) != null) && (isMuted(p).booleanValue()))
			return;
		Perm perm = (Perm) RCChat.permissions.get(p);
		if (perm == null) {
			RCChat.permissions.put(p, new Perm(p));
			perm = (Perm) RCChat.permissions.get(p);
		}
		if (!perm.hasPerm(12)) {
			if ((Channel.delay.get(p) != null)
					&& ((System.currentTimeMillis() - Channel.delay.get(p))
							/ 1000L < this.time))
				return;
			Channel.delay.put(p, System.currentTimeMillis());
		}

		final Player hold = p;
		BaseChannel c;
		if ((c = tempContains(p)) != null) {
			getServer().getScheduler().scheduleSyncDelayedTask(this,
					new Runnable() {
						public void run() {
							RCChat.this.removeTemp(hold);
						}
					}, 1L);
		} else if ((c = pContains(p)) != null) {
			//Nothing atm?
		} else {
			addPlayerDefault(p);
			c = pContains(p);
		}
		c.sendMessage(p, format, message);
	}

	public static ArrayList<String> getWeb() {
		return webVal;
	}
}
