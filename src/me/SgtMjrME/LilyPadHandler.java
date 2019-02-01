package me.SgtMjrME;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.SgtMjrME.Channels.BaseChannel;
import me.SgtMjrME.Channels.Message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LilyPadHandler implements Runnable{
	
	RCChat rcchat;
	String whoAmI = "";
	static HashMap<String, List<String> > servers = new HashMap<String, List<String> >();

	public LilyPadHandler(RCChat rcChat, List<String> ser, final String whoami) {
		rcchat = rcChat;
		for(String s : ser){
			servers.put(s, new ArrayList<String>());
		}
	}
	
	public void sendMessage(BaseChannel channel, String message){
	}
	
	public void sendMessage(String channel, String message, String server){
	}
	
	//Names is comma delimited list
	private void setServers(String server, String names){
		if (servers.get(server) == null) return;
		synchronized(servers){
			servers.get(server).clear();
			servers.get(server).addAll(Arrays.asList(names.split(",")));
		}
	}

	@Override
	public void run() {
		String players = "";
		for(Player p : Bukkit.getOnlinePlayers())
		{
			players += p.getName() + ",";
		}
	}

	/**
	 * Returns a list of all players who match given string in all servers
	 * @param string
	 * @return
	 */
	public static List<Pair> findServer(String string) {
		List<Pair> possibilities = new ArrayList<Pair>();
		for(String s : servers.keySet()){
			for(String p : servers.get(s)){
				if (p.equals(string)){
					//Exact match, send it through immediately
					possibilities.clear();
					possibilities.add(new Pair(s,p));
					return possibilities;
				}
				if (p.contains(string)){
					//Contains some part - this the person they wanted?
					possibilities.add(new Pair(s,p));
				}
			}
		}
		return possibilities;
	}

}
