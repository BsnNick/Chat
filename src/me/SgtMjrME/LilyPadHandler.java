package me.SgtMjrME;

import java.io.UnsupportedEncodingException;
import java.util.List;

import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.MessageEvent;
import lilypad.client.connect.api.MessageEventListener;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.GetWhoamiRequest;
import lilypad.client.connect.api.request.impl.MessageRequest;
import me.SgtMjrME.Channels.BaseChannel;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("deprecation")
public class LilyPadHandler implements MessageEventListener{
	
	RCChat rcchat;
	Connect c;
	String whoAmI = "";
	List<String> servers;

	public LilyPadHandler(RCChat rcChat, List<String> ser) {
		rcchat = rcChat;
		servers = ser;
		//Does this need to be this long? Probably not, but that's what the plugin I looked at needed. 
		c = (Connect)((Plugin) rcchat.getServer().getPluginManager().getPlugin("LilyPad-Connect")).getServer().getServicesManager().getRegistration(Connect.class).getProvider();
		try {
			whoAmI = c.request(new GetWhoamiRequest()).await().getIdentification();
		} catch (RequestException | InterruptedException e) {
			e.printStackTrace();
		}
		c.registerMessageEventListener(this);
	}
	
	public void sendMessage(BaseChannel channel, String message){
		try {
			MessageRequest mes = new MessageRequest(servers, "rcchat." + channel.getPerm() + "." + channel.getName(), "[" + whoAmI + "] " + message);
			c.request(mes);
		} catch (UnsupportedEncodingException | RequestException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Connect c, MessageEvent mes) {
		if (!mes.getChannel().contains("rcchat")) return;
		String[] parts = mes.getChannel().split(".");
		if (parts.length != 3) rcchat.getLogger().severe("[RCCHAT] Could not read channel information");
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


}
