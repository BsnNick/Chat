package me.SgtMjrME;

import java.util.Iterator;

import me.SgtMjrME.Channels.BaseChannel;
import me.SgtMjrME.Channels.Channel;
import me.SgtMjrME.Channels.Mod;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ChatClientComm implements PluginMessageListener {
	private RCChat plugin;
	
	public ChatClientComm(RCChat p){
		plugin = p;
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		if (p == null || !p.isOnline()) return;
		
		String strMsg = new String(message);
		if (strMsg.length() < 10) return;
    	String pktType = strMsg.substring(0,8);
    	pktType = pktType.trim();
    	if (pktType.equals("channels")){ //Just a list of channels
    		rcsPacket pkt = new rcsPacket(channel, plugin.channelName, , p);
    		
    		pkt.writeInt(Channel.channels.size());
    		for (Iterator<String> iter = Channel.channels.keySet().iterator(); iter.hasNext();){
    			BaseChannel bc = Channel.channels.get(iter.next());
    			pkt.writeChars(bc.getName(), true);
    			pkt.writeChars(bc.getDisp(), true);
    			pkt.writeBoolean(bc instanceof Mod);
    		}
    		pkt.send();
    	}
	}

}
