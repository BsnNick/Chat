package me.SgtMjrME;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.SgtMjrME.Channels.BaseChannel;
import me.SgtMjrME.Channels.Channel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

public class RCChat extends JavaPlugin {
	static public RCChat instance;
	private static ArrayList<String> webVal = new ArrayList<String>();
	private Logger log;
	public static Essentials e;
	public PluginManager pm;
	static public ConcurrentHashMap<Player, Perm> permissions = new ConcurrentHashMap<Player, Perm>();
	public static World factionWorld;
	public static World[] townyWorld;
//	private double time;
	private BaseChannel defaultChannel;
	private PlayerListener playerListener;
	public HashSet<String> onlineHelpers = new HashSet<String>();
	public final String channelName="RCChat";
	private static Location jailLL;
	private static Location jailUR;
	public static String townyTag;
	public static LilyPadHandler lph = null;
	
	//Going to be used to see all player logins/logoffs.
	public static boolean indebug = false;
	public static ConcurrentHashMap<String, Integer> playerLogin = new ConcurrentHashMap<String, Integer>(50);

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
			if (getServer().getPluginManager().isPluginEnabled("LilyPad-Connect")){
				List<String> servers = new ArrayList<String>();
				String[] str = config.getString("servers", "").split(",");
				if (str.length != 0 && str[0] != ""){
					for(String s : str){
						servers.add(s);
					}
					lph = new LilyPadHandler(this, servers, config.getString("curServer", null));
				}
			}
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
//		this.time = config.getDouble("delay", 1.0D);
		String s = config.getString("webcheck");
		if (s != null) {
			String[] s1 = s.split(",");
			for (String s2 : s1) {
				webVal.add(s2);
			}
		}
		s = config.getString("factions");
		if (s != null){
			factionWorld = getServer().getWorld(s);
		}
		s = config.getString("towny");
		if (s != null){
			for (String str : s.split(","))
				townyWorld = Util.addArr(townyWorld, getServer().getWorld(str));
		}
		s = config.getString("townyTag");
		if (s != null){
			townyTag = ChatColor.translateAlternateColorCodes('&', s);
		}
		//This informs Bukkit that you will send messages through that channel
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, channelName);
		
		//This informs Bukkit that you want to receive messages through that channel, to myPluginMessageListener
		Bukkit.getMessenger().registerIncomingPluginChannel(this, channelName, new ChatClientComm(this));
		
		this.playerListener = new PlayerListener(this);
		this.pm.registerEvents(this.playerListener, this);
		
		try{
			jailLL = str2Loc(config.getString("jailLL"));
			jailUR = str2Loc(config.getString("jailUR"));
		} catch (Exception e){
			Bukkit.getLogger().severe("[RCCHAT] Could not load jail location! If there is no jail, ignore this message");
			jailLL = null;
			jailUR = null;
		}
	}
	
	@Override
	public void onDisable(){
		if (lph != null){
			lph.deregister();
		}
	}
	
	public Location str2Loc(String s) {
		String[] s1 = s.split(" ");
		Location loc = new Location(getServer().getWorld(s1[0]), str2d(s1[1]),
				str2d(s1[2]), str2d(s1[3]), (float) str2d(s1[4]),
				(float) str2d(s1[5]));
		return loc;
	}

	public double str2d(String s) {
		return Double.parseDouble(s);
	}

	public static String loc2str(Location loc) {
		String output = loc.getWorld().getName();
		output = output.concat(" " + loc.getX() + " " + loc.getY() + " "
				+ loc.getZ() + " " + loc.getYaw() + " " + loc.getPitch());
		return output;
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
		Perm perm = (Perm) RCChat.getPerm(p);
		if ((commandLabel.equalsIgnoreCase("chatdebug")) && (perm.hasPerm(2))) {
			if (Channel.debugPlayers.contains(p)){
				p.sendMessage("Removing chat sight");
				Channel.debugPlayers.remove(p);
				if (Channel.debugPlayers.size() == 0){
					indebug = false;
					for(String s : playerLogin.keySet()){
						p.sendMessage(s + ":" + playerLogin.get(s));
					}
					playerLogin.clear();
					playerLogin = new ConcurrentHashMap<String, Integer>(50);
				}
			}
			else{
				p.sendMessage("Adding chat sight");
				Channel.debugPlayers.add(p);
				indebug = true;
			}
			return true;
		}
		if (commandLabel.equalsIgnoreCase("chatperms") && p.hasPermission("rcchat.seeperms")){
			if (args.length == 0) 
				perm.displayPerms(p);
			else if (p.hasPermission("rcchat.me") && args.length > 0) {
				try{
					Perm otherperm = getPerm(Bukkit.getPlayer(args[0]));
					if (otherperm == null){
						p.sendMessage("Player not found");
						return true;
					}
					otherperm.displayPerms(p);
				}
				catch (Exception e){
					p.sendMessage("Player not found");
				}
			}
		} else if (commandLabel.equalsIgnoreCase("setjailarea") && p.hasPermission("rcchat.admin")){
			jailLL=null;
			jailUR=null;
			playerListener.jailSetter = p.getName();
			p.sendMessage(ChatColor.GREEN + "Setting jail area");
		} else if ((commandLabel.equalsIgnoreCase("setchannel")) && (perm.hasPerm(12))) {
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
		}
		BaseChannel c = Channel.get(commandLabel);
		if (c == null) {
			p.sendMessage(ChatColor.RED + "Channel not found");
			return true;
		}
		if (!perm.hasPerm(c.getPerm())){
			p.sendMessage(ChatColor.RED + c.getPermErr());
			return false;
		}
		if (args.length == 0) {
			Channel.pChannels.put(p, c);
			p.sendMessage(ChatColor.GREEN + "Chat set to " 
					+ ChatColor.translateAlternateColorCodes('&', 
							c.getDisp()));
			return true;
		}
		Channel.tempChannel.put(p, c);
		p.chat(args2str(args));
		return true;
	}
	
	//returns true if both set, false otherwise;
	public boolean setJail(Location l){
		if (jailLL == null) jailLL = l;
		else if (jailUR == null){
			jailUR = l;
			double x1=jailLL.getX(),x2=jailUR.getX(),y1=jailLL.getY(),y2=jailUR.getY(),z1=jailLL.getZ(),z2=jailUR.getZ(),temp;
			if (x1 > x2){temp=x1;x1=x2;x2=temp;}
			if (y1 > y2){temp=y1;y1=y2;y2=temp;}
			if (z1 > z2){temp=z1;z1=z2;z2=temp;}
			jailLL = new Location(jailLL.getWorld(),x1,y1,z1);
			jailUR = new Location(jailUR.getWorld(),x2,y2,z2);
			YamlConfiguration cfg = new YamlConfiguration();
			try {
				cfg.load(getDataFolder() + "/config.yml");
				cfg.set("jailLL", loc2str(jailLL));
				cfg.set("jailUR", loc2str(jailUR));
				cfg.save(getDataFolder() + "/config.yml");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return true;
		}
		//Not sure what happened if you got here. 
		return false;
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
//		Channel.delay.remove(p.getName());
		Channel.muted.remove(p);
		Channel.debugPlayers.remove(p);
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
		Channel.tempChannel.put(p, c);
	}

	@Deprecated
	public Boolean isMuted(Player p) {
		return false;
//		return Channel.muted.get(p);
	}

	public void fromRunnable(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (isMuted(p)){
			e.getRecipients().clear();
			e.setCancelled(true);
			return;
		}
		if (!p.hasPermission("rcchat.m")){
			BaseChannel b;
			if (isJailed(p.getLocation()))
				changePlayerChannel(p, Channel.get("jc"));
			else if ((b = pContains(p)) != null && b.equals(Channel.get("jc"))){
				addPlayerDefault(p);
				Channel.tempChannel.remove(p);
			}
		}
		Perm perm = (Perm) RCChat.getPerm(p);
		if (perm == null) {
			RCChat.permissions.put(p, new Perm(p));
			perm = (Perm) RCChat.permissions.get(p);
		}
//		if (!perm.hasPerm(12)) {
//			if ((Channel.delay.contains(p.getName()))
//					&& ((System.currentTimeMillis() - Channel.delay.get(p.getName()))
//							/ 1000L < this.time)){
//				System.out.println("Player " + p.getName() + " is spamming RCChat");
//				return;
//			}
//			Channel.delay.put(p.getName(), System.currentTimeMillis());
//		}
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
			//Nothing atm? It already set the channel
		} else {
			addPlayerDefault(p);
			c = pContains(p);
		}
		c.sendMessage(e);
	}

	public static ArrayList<String> getWeb() {
		return webVal;
	}
	
	public static Perm getPerm(Player p){
		if (p == null) return null;
		if (permissions.contains(p)) return permissions.get(p);
		Perm newperm = new Perm(p);
		permissions.put(p, newperm);
		return newperm;
	}

	public static boolean isJailed(Location location) {
		if (jailLL == null || jailUR == null) return false;
		if (!location.getWorld().equals(jailLL.getWorld())) return false;
		return (jailLL.getX() <= location.getX() 
				&& jailLL.getY() <= location.getY()
				&& jailLL.getZ() <= location.getZ()
				&& jailUR.getX() >= location.getX()
				&& jailUR.getY() >= location.getY()
				&& jailUR.getZ() >= location.getZ());
	}
}
