package me.SgtMjrME;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Perm {
	ArrayList<Boolean> perms = new ArrayList<Boolean>(22);

	public Perm(Player p) {
		// What I have to do just to make concurrent permissions checks...
		this.perms.add(p.hasPermission("rcchat.*"));// 0
		this.perms.add(p.hasPermission("rcchat.m"));// 1
		this.perms.add(p.hasPermission("rcchat.me"));// 2
		this.perms.add(p.hasPermission("rcchat.dc"));// 3
		this.perms.add(p.hasPermission("rcchat.g"));// 4
		this.perms.add(p.hasPermission("rcchat.l"));// 5
		this.perms.add(p.hasPermission("rcchat.rc"));// 6
		this.perms.add(p.hasPermission("rcchat.change"));// 7
		this.perms.add(p.hasPermission("rcchat.rand"));// 8
		this.perms.add(p.hasPermission("rcchat.color"));// 9
		this.perms.add(p.hasPermission("rcchat.specialchar"));// 10
		this.perms.add(p.hasPermission("rcchat.web"));// 11
		this.perms.add(p.hasPermission("rcchat.exempt"));// 12
		this.perms.add(p.hasPermission("rcchat.mute"));// 13
		this.perms.add(p.hasPermission("rcchat.msee"));// 14
		this.perms.add(p.hasPermission("rcchat.mesee"));// 15
		this.perms.add(p.hasPermission("rcchat.dcsee"));// 16
		this.perms.add(p.hasPermission("rcchat.gsee"));// 17
		this.perms.add(p.hasPermission("rcchat.lsee"));// 18
		this.perms.add(p.hasPermission("rcchat.rcsee"));// 19
		this.perms.add(p.hasPermission("rcchat.fc"));// 20
		this.perms.add(p.hasPermission("rcchat.fcsee"));// 21
		this.perms.add(p.hasPermission("rcchat.tc"));// 22
		this.perms.add(p.hasPermission("rcchat.tcsee"));// 23
	}

	public boolean hasPerm(int i) {
		return ((Boolean) this.perms.get(i)).booleanValue();
	}

	public void displayPerms(Player p) {
		p.sendMessage(perms.get(0) ? ChatColor.GREEN + "All" : ChatColor.RED + "All");
		p.sendMessage(perms.get(1) ? ChatColor.GREEN + "Mod" : ChatColor.RED + "Mod");
		p.sendMessage(perms.get(2) ? ChatColor.GREEN + "Me" : ChatColor.RED + "Me");
		p.sendMessage(perms.get(3) ? ChatColor.GREEN + "Donator" : ChatColor.RED + "Donator");
		p.sendMessage(perms.get(4) ? ChatColor.GREEN + "Global" : ChatColor.RED + "Global");
		p.sendMessage(perms.get(5) ? ChatColor.GREEN + "Local" : ChatColor.RED + "Local");
		p.sendMessage(perms.get(6) ? ChatColor.GREEN + "Race" : ChatColor.RED + "Race");
		p.sendMessage(perms.get(7) ? ChatColor.GREEN + "Change" : ChatColor.RED + "Change");
		p.sendMessage(perms.get(8) ? ChatColor.GREEN + "Random" : ChatColor.RED + "Random");
		p.sendMessage(perms.get(9) ? ChatColor.GREEN + "Color" : ChatColor.RED + "Color");
		p.sendMessage(perms.get(10) ? ChatColor.GREEN + "Characters" : ChatColor.RED + "Characters");
		p.sendMessage(perms.get(11) ? ChatColor.GREEN + "URL" : ChatColor.RED + "URL");
		p.sendMessage(perms.get(12) ? ChatColor.GREEN + "Exempt" : ChatColor.RED + "Exempt");
		p.sendMessage(perms.get(13) ? ChatColor.GREEN + "Mute" : ChatColor.RED + "Mute");
		p.sendMessage(perms.get(20) ? ChatColor.GREEN + "Faction" : ChatColor.RED + "Faction");
	}
}