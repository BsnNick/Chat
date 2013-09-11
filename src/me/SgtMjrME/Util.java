package me.SgtMjrME;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class Util {
	@SuppressWarnings("unchecked")
	public static <T> T[] addArr(T[] first, T second){
		if (second == null) return first;
		if (first == null){
			T[] out = (T[]) Array.newInstance(second.getClass(), 1);
			out[0] = second;
			return out;
		}
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

	public static boolean inFactions(Player p) {
		return p.getWorld().equals(RCChat.factionWorld);
	}
}
