package me.SgtMjrME.Channels;

import java.io.IOException;
import java.util.Iterator;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;
import me.SgtMjrME.Util;
import me.SgtMjrME.ClassUpdate.WarRank;
import me.SgtMjrME.Object.WarPlayers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public abstract class BaseChannel {

	private String name;
	private String disp;
	@Deprecated
	private String permission;
	private ChatColor color = ChatColor.GRAY;
	private String permErr = "You do not have proper permissions";
	private String otherErr = "Nobody can hear you :(";
	YamlConfiguration cfg = new YamlConfiguration();
	private boolean useTag = false;
	private boolean isCrossServer = false;
	RCChat pl;
	
	void setTag(boolean i){useTag = i;}
	
	public BaseChannel(RCChat pl, String s){
		this.pl = pl;
		try {
			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/" + s + ".yml");
			setName(cfg.getString("name"));
			setDisp(cfg.getString("disp"));
			setPermission(cfg.getString("permission"));
			setColor(ChatColor.valueOf(cfg.getString("chatColor")));
			setPermErr(ChatColor.translateAlternateColorCodes('&', cfg.getString("permerr")));
			setOtherErr(ChatColor.translateAlternateColorCodes('&', cfg.getString("othererr")));
			setTag(true);
			setCrossServer(cfg.getBoolean("crossserver", false));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	// Decides whom the message is going to be sent to
	// MUST send to receiveDestination, either sync or async
	abstract void getDestination(AsyncPlayerChatEvent e);

	// Changes the message to include proper formatting
	void alterMessage(AsyncPlayerChatEvent e) {
		Perm perm = RCChat.getPerm(e.getPlayer());
		e.setFormat(setFormat(e.getPlayer(), e.getFormat()));
		e.setMessage(setMessage(perm, e.getMessage()));
	}

	// Continues from getDestination (due to return stuff)
	void receiveDestination(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (e.getRecipients() == null) {
			// Some reason it didn't work, I'll assume perms (easier to
			// diagnose)
			p.sendMessage(permErr);
			e.setCancelled(true);
			return;
		} else if (e.getRecipients().isEmpty()) {
			p.sendMessage(otherErr);
			String debugMes = ChatColor.WHITE + "[RCCD ERR] "
					+ p.getName() + " " + e.getFormat() + " " + e.getMessage();
			for (Player player : Channel.debugPlayers)
				player.sendMessage(debugMes);
			e.setCancelled(true);
			return;
		}
		alterMessage(e);
		String debugMes = ChatColor.WHITE + "[RCCD] " + this.getDisp() + " "+ e.getPlayer().getName()
					 + " " + e.getMessage();
		for (Player pl : Channel.debugPlayers) {
			if (!p.getName().equals(pl.getName())) pl.sendMessage(debugMes);
		}
		if (this.isCrossServer && RCChat.lph != null){
			RCChat.lph.sendMessage(this, e.getFormat().replace("%1$s", p.getName()) + e.getMessage());
		}
	}

	// Sends the message out
	public void sendMessage(AsyncPlayerChatEvent e) {
		if (RCChat.instance.pm.isPluginEnabled("RCWars") && this instanceof RaceChat){
			e.getPlayer().sendMessage(ChatColor.RED + "You are not in Wars");
			e.getRecipients().clear();
			e.setCancelled(true);
			return;
		}
		if (RCChat.instance.pm.isPluginEnabled("Factions") && this instanceof FactionChat){
			e.getPlayer().sendMessage(ChatColor.RED + "You are not in Factions");
			e.getRecipients().clear();
			e.setCancelled(true);
			return;
		}
		if (RCChat.instance.pm.isPluginEnabled("Towny") && (this instanceof TownyChat || this instanceof NationChat)){
			e.getPlayer().sendMessage(ChatColor.RED + "You are not in Towny");
			e.getRecipients().clear();
			e.setCancelled(true);
			return;
		}
		getDestination(e);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisp() {
		return this.disp;
	}

	public void setDisp(String disp) {
		this.disp = disp;
	}

	public String getPermission() {
		return this.permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public ChatColor getColor() {
		return this.color;
	}

	public void setColor(ChatColor color) {
		this.color = color;
	}

	public String setFormat(Player p, String s) {
		if (Channel.get("rc") != null && (WarPlayers.getRace(p) != null)) {
			s = s.replace("%1$s", WarPlayers.getRace(p).getCcolor() + "%1$s"
					+ getColor());
			//TODO WHY THE HELL DID I DO THIS
			String replaceName = "";
			if (RCChat.e != null) {
				replaceName = ChatColor.stripColor(RCChat.e.getUserMap()
						.getUser(p.getName())._getNickname());
				if (replaceName == null)
					replaceName = RCChat.e.getUserMap().getUser(p.getName())
							.getName();
				else
					replaceName = "~" + replaceName;
			} else {
				replaceName = ChatColor.stripColor(p.getDisplayName());
			}
			if (p.hasPermission("rcchat.m"))
				s = s.replace("%1$s", WarPlayers.getRace(p)
						.getDisplay()
						+ " "
						+ ChatColor.GOLD
						+ replaceName
						+ ' ' + WarRank.getPlayer(p).display());
			else
				s = s.replace("%1$s", WarPlayers.getRace(p)
						.getDisplay()
						+ " "
						+ ChatColor.YELLOW
						+ replaceName
						+ ' ' + WarRank.getPlayer(p).display());
//		} else if ((RCChat.instance.pm.isPluginEnabled("RCGuilds"))
//				&& (GuildPlayer.getPlayer(p) != null)
//				&& (GuildPlayer.getPlayer(p).getRank() != null)) {
//			s = s.replace("%1$s", p.getDisplayName() + " "
//					+ GuildPlayer.getPlayer(p).getRank().getSuffix() + "&r");
//			s = s.replaceAll("(&([a-f0-9 r]))", "§$2");
//		} else {
//			s = s.replace("%1$s", p.getDisplayName());
		}
		if (RCChat.factionWorld != null && Util.inFactions(p) && useTag){
			s = FactionHelper.format(s,p);
		}
		else if (RCChat.townyWorld != null && Util.inTowny(p) && useTag){
			s = TownyHelper.format(s, p);
		}
//		if (Channel.get("fc") != null){
//			if (RCChat.factionWorld != null){
//				FPlayer fp = FPlayers.i.get(p);
//				if (fp != null){
//					if (fp.hasFaction() && p.getWorld().equals(RCChat.factionWorld)){
//						s = fp.getTag() + s;
//					}
//				}
//			}
//		}
//		if (Channel.get("tc") != null){
//			if (RCChat.townyWorld != null){
//				FPlayer fp = FPlayers.i.get(p);
//				if (fp != null){
//					if (fp.hasFaction() && p.getWorld().equals(RCChat.factionWorld)){
//						s = fp.getTag() + s;
//					}
//				}
//			}
//		}
		String out = ChatColor.translateAlternateColorCodes('&', this.disp)
				+ ChatColor.RESET + s + this.color;
		return out;
	}

	public String setMessage(Perm p, String s) {
		if (p.hasPerm(9))
			s = s.replaceAll("(&([a-f0-9 r]))", "§$2");
		if (p.hasPerm(8))
			s = s.replace("&k", "§k");
		if (p.hasPerm(10))
			s = s.replaceAll("(&([l-o]))", "§$2");
		if (!p.hasPerm(11)) {
			Iterator<String> i = RCChat.getWeb().iterator();
			while (i.hasNext())
				if (s.contains((CharSequence) i.next()))
					s = s.replace('.', ' ');
		}
		String out = this.color + s;
		return out;
	}

	public String getPermErr() {
		return permErr;
	}

	public void setPermErr(String permErr) {
		this.permErr = permErr;
	}

	public String getOtherErr() {
		return otherErr;
	}

	public void setOtherErr(String otherErr) {
		this.otherErr = otherErr;
	}

	public boolean isJail() {
		return false;
	}
	
	public void setCrossServer(boolean bool){
		isCrossServer = bool;
	}
	
	public boolean isCrossServer(){
		return isCrossServer;
	}

	abstract public int getPerm();

}
