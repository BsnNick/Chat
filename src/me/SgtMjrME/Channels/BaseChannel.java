package me.SgtMjrME.Channels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;
import me.SgtMjrME.RCWars.ClassUpdate.WarRank;
import me.SgtMjrME.RCWars.Object.WarPlayers;
import net.realmc.rcguilds.GuildPlayer;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

public abstract class BaseChannel {

	private String name;
	private String disp;
	@Deprecated
	private String permission;
	private ChatColor color = ChatColor.GRAY;
	private String permErr = "You do not have proper permissions";
	private String otherErr = "Nobody can hear you :(";
	YamlConfiguration cfg = new YamlConfiguration();

	// Decides whom the message is going to be sent to
	// MUST send to receiveDestination, either sync or async
	abstract void getDestination(final Player p, String format, String message);

	// Changes the message to include proper formatting
	String alterMessage(Player p, String f, String s) {
		Perm perm = RCChat.permissions.get(p);
		f = setFormat(p, f);
		s = setMessage(perm, s);
		f = f.replace("%2$s", s);
		return f;
	}

	// Continues from getDestination (due to return stuff)
	void receiveDestination(List<Player> players, Player player, String format,
			String message) {
		if (players == null) {
			// Some reason it didn't work, I'll assume perms (easier to
			// diagnose)
			player.sendMessage(permErr);
			return;
		} else if (players.isEmpty()) {
			player.sendMessage(otherErr);
			String debugMes = ChatColor.WHITE + "[RCCD ERR] "
					+ player.getName() + " " + format + " " + message;
			for (Player p : Channel.debugPlayers)
				p.sendMessage(debugMes);
		}
		String out = alterMessage(player, format, message);
		List<Player> debug = new ArrayList<Player>();
		for (Player p : Channel.debugPlayers)
			debug.add(p);
		for (Player p : players) {
			if (debug.contains(p.getName())) {
				String debugMes = ChatColor.WHITE + "[RCCD] "
						+ player.getName() + " " + format + " " + message;
				p.sendMessage(debugMes);
				debug.remove(p);
				continue;
			}
			p.sendMessage(out);
		}
		for (Player p : debug) {
			String debugMes = ChatColor.WHITE + "[RCCD] " + player.getName()
					+ " " + format + " " + message;
			p.sendMessage(debugMes);
		}
	}

	// Sends the message out
	public void sendMessage(Player player, String format, String message) {
		getDestination(player, format, message);
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
		} else if ((RCChat.instance.pm.isPluginEnabled("RCGuilds"))
				&& (GuildPlayer.getPlayer(p) != null)
				&& (GuildPlayer.getPlayer(p).getRank() != null)) {
			s = s.replace("%1$s", p.getDisplayName() + " "
					+ GuildPlayer.getPlayer(p).getRank().getSuffix() + "&r");
			s = s.replaceAll("(&([a-f0-9 r]))", "§$2");
		} else {
			s = s.replace("%1$s", p.getDisplayName());
		}
		if (Channel.get("fc") != null){
			FPlayer fp = FPlayers.i.get(p);
			if (fp != null){
				Faction f = fp.getFaction();
				if (f != null){
					s = f.getTag(fp) + s;
				}
			}
		}
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

}
