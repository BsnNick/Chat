package me.SgtMjrME.Channels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

public class ClanHelper {
	
	static boolean active = true;
	
	static private void checkManager(){
	}

	static public String format(String s, Player p){
		//Testing
//		checkManager();
//		Clan c = cm.getClanByPlayerName(p.getName());
//		if (c != null) s = c.getTag() + s;
		return s;
	}

	public static List<Player> setRecipients(Player p) {
		checkManager();
		List<Player> l = new ArrayList<Player>();
		return l;
	}

	public static Collection<? extends Player> setAllyRecipients(Player player) {
		List<Player> l = new ArrayList<Player>();
		l.addAll(setRecipients(player));
		return l;
	}
	
}
