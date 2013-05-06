package me.SgtMjrME.RCChat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Channel
{
  @SuppressWarnings("unused")
private final RCChat pl;
  private final YamlConfiguration cfg;
  private String name;
  private String disp;
  private String permission;
  private boolean world;
  private int distance;
  private ChatColor color;
  public static ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

  Channel(RCChat r, YamlConfiguration config)
  {
    this.pl = r;
    this.cfg = config;
    setName(this.cfg.getString("name"));
    setDisp(this.cfg.getString("disp"));
    setPermission(this.cfg.getString("permission"));
    setDistance(this.cfg.getInt("distance"));
    setWorld(this.cfg.getBoolean("world"));
    try { setColor(ChatColor.valueOf(this.cfg.getString("chatColor"))); } catch (Exception e) {
      setColor(ChatColor.GRAY);
    }
  }

  public String getName() { return this.name; }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDisp() {
    return this.disp;
  }

  public void setDisp(String disp) {
    this.disp = disp;
  }

  public String getPermission() {
    return this.permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public boolean isWorld() {
    return this.world;
  }

  public void setWorld(boolean world) {
    this.world = world;
  }

  public int getDistance() {
    return this.distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public ChatColor getColor() {
    return this.color;
  }

  public void setColor(ChatColor color) {
    this.color = color;
  }

  public String setFormat(String s) {
    String out = ChatColor.translateAlternateColorCodes('&', this.disp) + 
      ChatColor.RESET + s + this.color;
    return out;
  }

  public String setMessage(Player p, String s)
  {
    if (p.hasPermission("rcchat.color"))
      s = s.replaceAll("(&([a-f0-9 r]))", "§$2");
    if (p.hasPermission("rcchat.rand"))
      s = s.replace("&k", "§k");
    if (p.hasPermission("rcchat.specialchar"))
      s = s.replaceAll("(&([l-o]))", "§$2");
    if (!p.hasPermission("rcchat.web")) {
      Iterator<String> i = RCChat.getWeb().iterator();
      while (i.hasNext())
        if (s.contains((CharSequence)i.next()))
          s = s.replace('.', ' ');
    }
    String out = this.color + s;
    return out;
  }

  public static boolean hasChannel(String s)
  {
    return channels.containsKey(s);
  }

  public static Channel get(String s) {
    return (Channel)channels.get(s);
  }

  public static void loadChannels(RCChat pl) {
    File f = new File(pl.getDataFolder() + "/channels");
    File[] files = f.listFiles();
    for (File temp : files)
      if (temp.getName().endsWith(".yml"))
      {
        YamlConfiguration cconfig = new YamlConfiguration();
        try {
          cconfig.load(temp);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InvalidConfigurationException e) {
          e.printStackTrace();
        }
        channels.put(cconfig.getString("name"), new Channel(pl, cconfig));
      }
  }
}