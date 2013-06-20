package me.SgtMjrME.Channels;

import org.bukkit.entity.Player;

import me.SgtMjrME.RCChat;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyHelper {
	
	static public String format(String s, Player p){
		try {
			Town t = TownyUniverse.getDataSource().getResident(p.getName()).getTown();
			if (t != null){ //more for fun than anything
				s = RCChat.townyTag.replace("%TOWN%", t.getName()) + s;
			}
		} catch (NotRegisteredException e) {
			//No town found... I'm fine with that really
		}
		return s;
	}

}
