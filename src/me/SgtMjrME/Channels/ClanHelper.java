package me.SgtMjrME.Channels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class ClanHelper {
	
	static boolean active = true;
	static private SimpleClans sc = null;
	
	static private void checkManager(){
		if (active && sc == null){
			sc = SimpleClans.getInstance();
			if (sc == null) active = false;
		}
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
		for (ClanPlayer c : SimpleClans.getInstance().getClanManager().getClanByPlayerName(p.getName()).getAllMembers()){
			Player tmp = c.toPlayer();
			if (tmp != null) l.add(tmp);
		}
		return l;
	}

	public static Collection<? extends Player> setAllyRecipients(Player player) {
		List<Player> l = new ArrayList<Player>();
		l.addAll(setRecipients(player));
		for(ClanPlayer c : SimpleClans.getInstance().getClanManager().getClanByPlayerName(player.getName()).getAllAllyMembers()){
			Player tmp = c.toPlayer();
			if (tmp != null) l.add(tmp);
		}
		return l;
	}
	
}
