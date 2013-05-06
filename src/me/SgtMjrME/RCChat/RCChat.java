package me.SgtMjrME.RCChat;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.SgtMjrME.RCWars.ClassUpdate.WarRank;
import me.SgtMjrME.RCWars.Object.Race;
import me.SgtMjrME.RCWars.Object.WarPlayers;
import net.realmc.rcguilds.GuildPlayer;
import net.realmc.rcguilds.GuildRank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class RCChat extends JavaPlugin {
	private static ArrayList<String> webVal = new ArrayList<String>();
	private Logger log;
	Essentials e;
	private PluginManager pm;
	private ConcurrentHashMap<Player, Channel> pChannels = new ConcurrentHashMap<Player, Channel>();
	private ConcurrentHashMap<Player, Channel> tempChannel = new ConcurrentHashMap<Player, Channel>();
	private ConcurrentHashMap<Player, Boolean> muted = new ConcurrentHashMap<Player, Boolean>();
	private ConcurrentHashMap<Player, Long> delay = new ConcurrentHashMap<Player, Long>();
	public ConcurrentHashMap<Player, Perm> permissions = new ConcurrentHashMap<Player, Perm>();
	private ArrayList<Player> seeDebug = new ArrayList<Player>();
	private double time;
	private Channel defaultChannel;
	private PlayerListener playerListener;
	public HashSet<String> onlineHelpers = new HashSet<String>();

	public void onEnable() {
		this.log = getServer().getLogger();
		this.pm = getServer().getPluginManager();
		this.e = null;
		if (this.pm.isPluginEnabled("Essentials")) {
			this.e = ((Essentials) this.pm.getPlugin("Essentials"));
			if (this.e != null)
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
		this.playerListener = new PlayerListener(this);
		this.pm.registerEvents(this.playerListener, this);
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player p = (Player) sender;
		if (commandLabel.equalsIgnoreCase("onlinestaff")) {
			for (String s : this.onlineHelpers) {
				Player temp = getServer().getPlayer(s);
				if (temp != null)
					p.sendMessage(temp.getDisplayName());
			}
			return true;
		}
		Perm perm = (Perm) this.permissions.get(p);
		if ((commandLabel.equalsIgnoreCase("chatdebug")) && (perm.hasPerm(2))) {
			if (this.seeDebug.contains(p))
				this.seeDebug.remove(p);
			else
				this.seeDebug.add(p);
			return true;
		}
		if ((commandLabel.equalsIgnoreCase("g")) && (perm.hasPerm(4))) {
			Channel c = Channel.get("g");
			if (args.length == 0) {
				if (!perm.hasPerm(4)) {
					p.sendMessage(ChatColor.RED + "Cannot set to that mode");
					return false;
				}
				this.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			this.tempChannel.put(p, c);
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
		if ((commandLabel.equalsIgnoreCase("l")) && (perm.hasPerm(5))) {
			Channel c = Channel.get("l");
			if (args.length == 0) {
				if (!perm.hasPerm(5)) {
					p.sendMessage(ChatColor.RED + "Cannot set to that mode");
					return false;
				}
				this.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			this.tempChannel.put(p, c);
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
		if ((commandLabel.equalsIgnoreCase("dc")) && (perm.hasPerm(3))) {
			Channel c = Channel.get("dc");
			if (args.length == 0) {
				if (!perm.hasPerm(3)) {
					p.sendMessage(ChatColor.RED + "Cannot set to that mode");
					return false;
				}
				this.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			this.tempChannel.put(p, c);
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
		if ((commandLabel.equalsIgnoreCase("me")) && (perm.hasPerm(2))) {
			Channel c = Channel.get("me");
			if (args.length == 0) {
				if (!perm.hasPerm(2)) {
					p.sendMessage(ChatColor.RED + "Cannot set to that mode");
					return false;
				}
				this.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			this.tempChannel.put(p, c);
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
		if ((commandLabel.equalsIgnoreCase("m")) && (perm.hasPerm(1))) {
			Channel c = Channel.get("m");
			if (args.length == 0) {
				if (!perm.hasPerm(1)) {
					p.sendMessage(ChatColor.RED + "Cannot set to that mode");
					return false;
				}
				this.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			this.tempChannel.put(p, c);
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
		if ((commandLabel.equalsIgnoreCase("rc")) && (perm.hasPerm(6))
				&& (this.pm.isPluginEnabled("RCWars"))) {
			if (WarPlayers.getRace(p) == null) {
				p.sendMessage(ChatColor.RED + "You are not in Wars");
				return false;
			}
			Channel c = Channel.get("rc");
			if (args.length == 0) {
				if (!perm.hasPerm(6)) {
					p.sendMessage(ChatColor.RED + "Cannot set to that mode");
					return false;
				}
				this.pChannels.put(p, c);
				p.sendMessage(ChatColor.GREEN
						+ "Chat set to "
						+ ChatColor.translateAlternateColorCodes('&',
								c.getDisp()));
				return true;
			}
			this.tempChannel.put(p, c);
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
			Channel c = Channel.get(args[1]);
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
				if ((this.muted.get(temp) != null)
						&& (((Boolean) this.muted.get(temp)).booleanValue())) {
					p.sendMessage("Player already muted");
				} else {
					this.muted.put(temp, Boolean.valueOf(true));
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
				if ((this.muted.get(temp) != null)
						&& (!((Boolean) this.muted.get(temp)).booleanValue())) {
					p.sendMessage("Player wasn't muted");
				} else {
					this.muted.put(temp, Boolean.valueOf(false));
					temp.sendMessage(ChatColor.GREEN + "You have been unmuted");
					p.sendMessage("Player has been unmuted");
				}
				return true;
			}
			if ((commandLabel.equalsIgnoreCase("chatdebug"))
					&& (perm.hasPerm(0))) {
				if (this.seeDebug.contains(p))
					this.seeDebug.remove(p);
				else
					this.seeDebug.add(p);
			}
		}
		return true;
	}

	public void sendMessage(Player p, Channel c, String format, String s) {
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
		Perm perm = (Perm) this.permissions.get(p);
		if (perm == null) {
			final Player hold = p;
			getServer().getScheduler().runTask(this, new Runnable() {
				public void run() {
					RCChat.this.permissions.put(hold, new Perm(hold));
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
						if (((Perm) this.permissions.get(end)).hasPerm(1))
							end.sendMessage(s);
					}
				} else
					for (Player end : all)
						if (((Perm) this.permissions.get(end)).hasPerm(3))
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
					if (r.equals(WarPlayers.getRace(p)) &&
							permissions.get(player))
						player.sendMessage(s);
				}
			}
		} else {
			if (c.isWorld()) {//g?
				final String copy = s;
				final Player copyp = p;
				Bukkit.getScheduler().runTask(this, new Runnable(){
					
					@Override
					public void run() {
						Iterator<Player> peoples = copyp.getWorld().getPlayers().iterator();
						while(peoples.hasNext()){
							Player player1 = peoples.next();
							if (permissions.get(player1).hasPerm(17)) peoples.next().sendMessage(copy);
						}
						
					}
					
				});
				
				return;
			}
			if (!c.isWorld()) {//l?
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
								if (permissions.get(end).hasPerm(18)) end.sendMessage(copy);
							}
						}
						copyp.sendMessage(copy);
					}
				});
			}
		}
	}

	private void sendDebug(Player p, String format, String s) {
		if (this.seeDebug.isEmpty())
			return;
		String out = ChatColor.WHITE + "[RCCD] " + p.getName() + " " + format
				+ " " + s;
		Iterator<Player> i = this.seeDebug.iterator();
		while (i.hasNext())
			((Player) i.next()).sendMessage(out);
	}

	private String args2str(String[] args) {
		if (args.length == 0)
			return null;
		String s = args[0];
		for (int x = 1; x < args.length; x++)
			s = s + " " + args[x];
		return s;
	}

	public void addPlayerDefault(Player player) {
		this.pChannels.put(player, this.defaultChannel);
	}

	public void removePlayer(Player p) {
		this.pChannels.remove(p);
		this.tempChannel.remove(p);
		this.permissions.remove(p);
	}

	public void addTemp(Player p, Channel c) {
		this.tempChannel.put(p, c);
	}

	public void removeTemp(Player p) {
		this.tempChannel.remove(p);
	}

	public Channel tempContains(Player p) {
		return (Channel) this.tempChannel.get(p);
	}

	public Channel pContains(Player p) {
		return (Channel) this.pChannels.get(p);
	}

	public void changePlayerChannel(Player p, Channel c) {
		this.pChannels.put(p, c);
	}

	public Boolean isMuted(Player p) {
		return (Boolean) this.muted.get(p);
	}

	public void fromRunnable(Player p, String format, String message) {
		if ((isMuted(p) != null) && (isMuted(p).booleanValue()))
			return;
		Perm perm = (Perm) this.permissions.get(p);
		if (perm == null) {
			this.permissions.put(p, new Perm(p));
			perm = (Perm) this.permissions.get(p);
		}
		if (!perm.hasPerm(12)) {
			if ((this.delay.get(p) != null)
					&& ((System.currentTimeMillis() - ((Long) this.delay.get(p))
							.longValue()) / 1000L < this.time))
				return;
			this.delay.put(p, Long.valueOf(System.currentTimeMillis()));
		}

		final Player hold = p;
		Channel c;
		if ((c = tempContains(p)) != null) {
			getServer().getScheduler().scheduleSyncDelayedTask(this,
					new Runnable() {
						public void run() {
							RCChat.this.removeTemp(hold);
						}
					}, 1L);
			format = c.setFormat(format);
		} else if ((c = pContains(p)) != null) {
			format = c.setFormat(format);
		} else {
			addPlayerDefault(p);
			c = pContains(p);
			format = c.setFormat(format);
		}
		sendMessage(p, c, format, message);
	}

	public static ArrayList<String> getWeb() {
		return webVal;
	}
}