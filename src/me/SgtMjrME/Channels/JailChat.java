package me.SgtMjrME.Channels;

import java.util.Iterator;

import me.SgtMjrME.RCChat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class JailChat extends BaseChannel{

	public JailChat(RCChat pl) {
		super(pl, "jc");
//		this.pl = pl;
//		try {
//			cfg.load(pl.getDataFolder().getAbsolutePath() + "/channels/jc.yml");
//			setName(cfg.getString("name"));
//			setDisp(cfg.getString("disp"));
//			setPermission(cfg.getString("permission"));
//			setColor(ChatColor.valueOf(cfg.getString("chatColor")));
//			setPermErr(ChatColor.translateAlternateColorCodes('&', cfg.getString("permerr")));
//			setOtherErr(ChatColor.translateAlternateColorCodes('&', cfg.getString("othererr")));
//		} catch (IOException | InvalidConfigurationException e) {
//			e.printStackTrace();
//		}
	}
	
	@Override
	void getDestination(AsyncPlayerChatEvent e) {

//		// Questionable call, never used aslist
//		List<Player> players = new ArrayList<Player>(Arrays.asList(pl
//				.getServer().getOnlinePlayers()));
		
		// Remove non-permission
		Iterator<Player> i = e.getRecipients().iterator();
		while (i.hasNext()) {
			if (!RCChat.getPerm(i.next()).hasPerm(1))
				i.remove();
		}
//		players.add(p);
		// send
		receiveDestination(e);
	}
	
	@Override
	public boolean isJail(){
		return true;
	}

	@Override
	public int getPerm() {
		return 5;
	}

}
