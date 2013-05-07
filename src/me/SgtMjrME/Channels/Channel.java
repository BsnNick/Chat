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
		channels.put("l", new Local(pl));
	}
}