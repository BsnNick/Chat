package me.SgtMjrME.Channels;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Player;

import me.SgtMjrME.RCChat;

public class Channel {

	// All channels
	public static ConcurrentHashMap<String, BaseChannel> channels = new ConcurrentHashMap<String, BaseChannel>();

	// What channel a player is in
	public static ConcurrentHashMap<Player, BaseChannel> pChannels = new ConcurrentHashMap<Player, BaseChannel>();

	// Used when a player uses /<channel> <message>
	public static ConcurrentHashMap<Player, BaseChannel> tempChannel = new ConcurrentHashMap<Player, BaseChannel>();

	// Currently half-implemented, stores muted values of a player
	public static ConcurrentHashMap<Player, Boolean> muted = new ConcurrentHashMap<Player, Boolean>();

	// Last message sent by player
	public static ConcurrentHashMap<Player, Long> delay = new ConcurrentHashMap<Player, Long>();

	// ChatDebug players, added to every message
	public static CopyOnWriteArrayList<Player> debugPlayers = new CopyOnWriteArrayList<Player>();

	public static boolean hasChannel(String s) {
		return channels.containsKey(s);
	}

	public static BaseChannel get(String s) {
		return channels.get(s);
	}

	public static void loadChannels(RCChat pl) {
		channels.putIfAbsent("l", new Local(pl));
		channels.putIfAbsent("g", new Global(pl));
		channels.putIfAbsent("dc", new Donator(pl));
		channels.putIfAbsent("m", new Mod(pl));
		channels.putIfAbsent("me", new Me(pl));
		if (pl.getServer().getPluginManager().isPluginEnabled("Factions")){
			channels.putIfAbsent("fc", new FactionChat(pl));
		}
		if (pl.getServer().getPluginManager().isPluginEnabled("RCWars")){
			channels.putIfAbsent("rc", new RaceChat(pl));
		}
		if (pl.getServer().getPluginManager().isPluginEnabled("Towny")){
			try{channels.putIfAbsent("twc", new TownyChat(pl));
			} catch(Exception e){
				//nothing
			}
		}
	}
}