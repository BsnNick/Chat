package me.SgtMjrME;

import java.util.Arrays;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class Util {
	public static <T> T[] addArr(T[] first, T second){
		if (second == null) return first;
		T[] result = Arrays.copyOf(first, first.length + 1);
		result[first.length] = second;
		return result;
	}

	public static boolean inTowny(Player p) {
		World pw = p.getWorld();
		for (World w : RCChat.townyWorld){
			if (w.equals(pw)) return true;
		}
		return false;
	}
}
