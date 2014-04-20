package me.SgtMjrME;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.event.MessageEvent;
import lilypad.client.connect.api.event.EventListener;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.GetWhoamiRequest;
import lilypad.client.connect.api.request.impl.MessageRequest;
import me.SgtMjrME.Channels.BaseChannel;
import me.SgtMjrME.Channels.Message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LilyPadHandler implements Runnable{
	
	RCChat rcchat;
	Connect c;
	String whoAmI = "";
	static HashMap<String, List<String> > servers = new HashMap<String, List<String> >();

	public LilyPadHandler(RCChat rcChat, List<String> ser, final String whoami) {
		rcchat = rcChat;
		for(String s : ser){
			servers.put(s, new ArrayList<String>());
		}
		//Does this need to be this long? Probably not, but that's what the plugin I looked at needed. 
		c = (Connect)((Plugin) rcchat.getServer().getPluginManager().getPlugin("LilyPad-Connect")).getServer().getServicesManager().getRegistration(Connect.class).getProvider();
		c.registerEvents(this);
		Bukkit.getScheduler().runTaskLater(rcchat, new Runnable(){
			@Override
			public void run() {
				if (whoami == null)
					try {
						whoAmI = c.request(new GetWhoamiRequest()).await().getIdentification();
					} catch (InterruptedException | RequestException e) {
						whoAmI = whoami;
					}
				else whoAmI = whoami;
				servers.remove(whoami); //Remove this server if they accidentally included it (No double-messaging!)
			}
		}, 200L);
		Bukkit.getScheduler().runTaskTimerAsynchronously(rcchat, this, 1000, 100);
	}
	
	public void sendMessage(BaseChannel channel, String message){
		try {
			MessageRequest mes = new MessageRequest(new ArrayList<String>(servers.keySet()), 
					"rcchat." + channel.getPerm() + "." + channel.getName(), "[" + whoAmI + "] " + message);
			c.request(mes);
		} catch (UnsupportedEncodingException | RequestException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String channel, String message, String server){
		try {
			MessageRequest mes = new MessageRequest(server, 
					channel, "[" + whoAmI + "] " + message);
			c.request(mes);
		} catch (UnsupportedEncodingException | RequestException e) {
			e.printStackTrace();
		}
	}
	
	//Names is comma delimited list
	private void setServers(String server, String names){
		if (servers.get(server) == null) return;
		synchronized(servers){
			servers.get(server).clear();
			servers.get(server).addAll(Arrays.asList(names.split(",")));
		}
	}

	@EventListener
	public void onMessage(MessageEvent mes) {
		if (mes.getChannel().equals("rcchat.players")){
			try {
				setServers(mes.getSender(),mes.getMessageAsString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return;
		}
		if (!mes.getChannel().contains("rcchat")) return;
		String[] parts = mes.getChannel().split("\\.");
		if (parts.length != 3){
			rcchat.getLogger().severe("[RCCHAT] Could not read channel information, channel was " + mes.getChannel()
					);
			return;
		}
		if (parts[1].equals("msg")){
			String[] moreSplit = parts[2].split(",");
			Player dest = Bukkit.getPlayer(moreSplit[0]);
			if (dest == null) return;
			try {
				dest.sendMessage(mes.getMessageAsString());
				Message.pReply.put(moreSplit[0], moreSplit[1]);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return;
		}
		Perm perm;
		for(Player p : rcchat.getServer().getOnlinePlayers()){
			perm = RCChat.getPerm(p);
			if (perm == null) continue;
			try{
				if (perm.hasPerm(Integer.parseInt(parts[1]))) p.sendMessage(mes.getMessageAsString());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		String players = "";
		for(Player p : Bukkit.getOnlinePlayers()){
			players += p.getName() + ",";
		}
		MessageRequest mr;
		try {
			for(List<String> temp : servers.values()){
				mr = new MessageRequest(temp,"rcchat.players",players);
				c.request(mr);
			}
		} catch (UnsupportedEncodingException | RequestException e) {
			e.printStackTrace();
		}
	}

	public static String findServer(String string) {
		for(String s : servers.keySet()){
			if (servers.get(s).contains(string)) return s;
		}
		return null;
	}

}
