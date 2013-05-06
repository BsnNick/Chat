package me.SgtMjrME.RCChat;

import java.util.ArrayList;
import org.bukkit.entity.Player;

class Perm
{
  ArrayList<Boolean> perms = new ArrayList<Boolean>(20);

  public Perm(Player p) { this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.*")));//0
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.m")));//1
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.me")));//2
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.dc")));//3
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.g")));//4
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.l")));//5
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.rc")));//6
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.change")));//7
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.rand")));//8
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.color")));//9
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.specialchar")));//10
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.web")));//11
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.exempt")));//12
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.mute")));//13
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.msee")));//14
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.mesee")));//15
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.dcsee")));//16
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.gsee")));//17
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.lsee")));//18
    this.perms.add(Boolean.valueOf(p.hasPermission("rcchat.rcsee")));//19
  }

  public boolean hasPerm(int i) {
    return ((Boolean)this.perms.get(i)).booleanValue();
  }
}