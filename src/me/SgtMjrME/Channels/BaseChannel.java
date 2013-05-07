package me.SgtMjrME.Channels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.SgtMjrME.Perm;
import me.SgtMjrME.RCChat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public abstract class BaseChannel {

	private String name;
	private String disp;
	@Deprecated
	private String permission;
	private ChatColor color = ChatColor.GRAY;
	private String permErr = "You do not have proper permissions";
	private String otherErr = "Nobody can hear you :(";
	YamlConfiguration cfg = new YamlConfiguration();

	//Decides whom the message is going to be sent to
	//MUST send to receiveDestination, either sync or async
	abstract void getDestination(final Player p, String format, String message);
	
	//Changes the message to include proper formatting
	String alterMessage(Perm p, String f, String s){
		f = setFormat(f);
		s = setMessage(p, s);
		f += s;
		return f;
	}
	
	//Continues from getDestination (due to return stuff)
	void receiveDestination(List<Player> players, Player player, String format, String message){
		if (players == null){
			//Some reason it didn't work, I'll assume perms (easier to diagnose)
			player.sendMessage(permErr);
			return;
		}
		else if (players.isEmpty()){
			player.sendMessage(otherErr);
			String debugMes = ChatColor.WHITE + "[RCCD ERR] " + player.getName() + " " + format
					+ " " + message;
			for(Player p : Channel.debugPlayers) p.sendMessage(debugMes);
		}
		alterMessage(RCChat.permissions.get(player), format, message);
		List<Player> debug = new ArrayList<Player>();
		for(Player p : Channel.debugPlayers) debug.add(p);
		for(Player p : players){
			if (debug.contains(p.getName())){
				String debugMes = ChatColor.WHITE + "[RCCD] " + player.getName() + " " + format
						+ " " + message;
				p.sendMessage(debugMes);
				debug.remove(p);
				continue;
			}
			p.sendMessage(message);
		}
		for(Player p : debug){
			String debugMes = ChatColor.WHITE + "[RCCD] " + player.getName() + " " + format
					+ " " + message;
			p.sendMessage(debugMes);
		}
	}
	
	//Sends the message out
	public void sendMessage(Player player, String format, String message){
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

	public String setFormat(String s) {
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
