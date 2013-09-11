package me.SgtMjrME.Channels;

import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

public class FactionHelper {

	public static String format(String s, Player p) {
		try{
		FPlayer fp = FPlayers.i.get(p);
		if (fp != null){
			if (fp.hasFaction()) s = fp.getTag() + s;
		}
		return s;
		}
		catch(Exception e){
			return s;
		}
	}

}
