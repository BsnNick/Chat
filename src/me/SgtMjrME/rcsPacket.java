package me.SgtMjrME;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class rcsPacket {
	private ByteArrayOutputStream byteStream;
	private DataOutputStream dataStream;
	private Exception error = null;
	private Player player;
	private String type;
	private String typeSent;
	private String channel;
	private JavaPlugin plugin;
	
	public rcsPacket(String pChan, JavaPlugin plug, String t, Player p){
		channel = pChan;
		plugin = plug;
		player = p;
		type = t;
		typeSent = t;
		if (typeSent.length() > 10)
			typeSent = t.substring(0, 10);
		else
			while (typeSent.length() < 10) typeSent += " ";
		
		try {
			byteStream = new ByteArrayOutputStream();
			try {
				dataStream = new DataOutputStream(byteStream);
				dataStream.writeChars(typeSent);
			} catch (Exception e){
				error = e;
				byteStream = null;
				dataStream = null;
			}
		} catch (Exception e){
			error = e;
			byteStream = null;
			dataStream = null;
		}
	}
	
	public void writeInt(int value){ try{dataStream.writeInt(value);}catch(Exception e){error = e;}}
	public void writeBoolean(boolean value){ try{dataStream.writeBoolean(value);}catch(Exception e){error = e;}}
	public void writeDouble(double value){ try{dataStream.writeDouble(value);}catch(Exception e){error = e;}}
	public void writeChars(String value, boolean length){
		try{
			if (length) dataStream.writeInt(value.length());
			dataStream.writeChars(value);
		} catch(Exception e){
			error = e;
		}
	}

	public boolean send(){
		if (dataStream != null){
			try {
				dataStream.flush();
				dataStream.close();
			} catch (Exception e) {
				error = e;
			}
			
			if (error == null)
				player.sendPluginMessage(plugin, channel, byteStream.toByteArray());
	    	else
	    		Bukkit.getLogger().info("Cannot send "+type.trim()+" to "+player.getDisplayName());
		}
					
		return error == null;
	}
}
