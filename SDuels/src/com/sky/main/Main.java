package com.sky.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin implements Listener{

	Battle battle = new Battle(this);
	
	public YamlConfiguration yml;
	
	public HashMap<Player, Player> duels = new HashMap<Player, Player>();
	public static HashMap<Player, PlayerData> playerData = new HashMap<Player, PlayerData>();
	public HashMap<Location, Location> arenas = new HashMap<Location, Location>();
	public List<Location> occupiedArenas = new ArrayList<Location>();
	
	public String duelInviteMessage = "§e Has challenged you to a duel. Click to accept!";
	public String[] helpmessage;
	
	@Override
	public void onEnable() 
	{
		getLogger().info("Enabling Sduels");
		File path = new File(getDataFolder() + "/players");
	    if(!path.exists()){
	    	path.mkdirs();
	    }
	    for(Player player: Bukkit.getOnlinePlayers())
	    {
	    	Main.playerData.put(player, this.getPlayerData(player));
	    }
	    reload();
	}
	
	@Override	
	public void onDisable() 
	{
		getLogger().info("Disabling Sduels");		
	}
	
	public void reload()
	{
		String path = this.getDataFolder() + "/config.yml";
		File file = FileManager.getFile(path);
		this.yml = YamlConfiguration.loadConfiguration(file);
		
		loadArenas();		
		loadMessages();
	}
	
	public void loadArenas()
	{
		ConfigurationSection cs = yml.getConfigurationSection("arenas");
		if(cs==null)
		{
			Bukkit.getLogger().warning("No arenas are found in the config!");
			return;
		}
		for(String string: cs.getKeys(false))
		{
			Location loc1 = null, loc2 = null;
			for(int i = 1; i < 3; i++)
			{
				String[] location = cs.getString(string + ".location" + i).split(",");
				if(location == null)
				{
					break;
				}
				try
				{
					int x = Integer.parseInt(location[0]);
					int y = Integer.parseInt(location[1]);
					int z = Integer.parseInt(location[2]);
					float yaw = new Float(location[3]);
					float pitch = new Float(location[4]);
					if(i==1)
					{
						loc1 = new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
					}
					else
					{
						loc2 = new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
					}
				}
				catch(NumberFormatException e)
				{
					Bukkit.getLogger().severe("Strings for the arenas configuration are not filled out correctly. Error on " + string + ".location" + i);
					continue;
				}
				
			}
			if(loc1==null || loc2==null)
			{
				continue;
			}
			arenas.put(loc1, loc2);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadMessages()
	{
		List<String> helpmessage = (List<String>) yml.getList("helpmessage");
		this.helpmessage = new String[helpmessage.size()];
		int i = 0;
		for(String string: helpmessage)
		{
			this.helpmessage[i] = string.replace("&", "§");
			i++;
		}
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		PlayerData PD = this.getPlayerData(player);
		Main.playerData.put(player, PD);	    
	}
	
	public void onLogout(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();		
		this.savePlayerData(player);
	}
	
	public PlayerData getPlayerData(OfflinePlayer objective)
	{
		if(objective == null)
		{
			this.getLogger().info("PlayerData returning for player  = null");
			String nullpath = this.getDataFolder() + "/players/null.yml";
			return new PlayerData(nullpath, null);
		}
		
		if(playerData.containsKey(objective))
		{
			return playerData.get(objective);
		}
		
		String path = this.getDataFolder() + "/players/" + objective.getUniqueId() + ".yml";
		this.getLogger().info("PlayerData retrieved for player " + objective.getName());

		return new PlayerData(path, objective);
		
	}
	
	public void savePlayerData(OfflinePlayer player)
	{
		PlayerData PD = playerData.get(player);
		
		if(PD.path.equals(this.getDataFolder() + "/MinrCheckpoint/players/thiswillneverbearealplayername.yml"))
		{
			System.out.println("PlayerData for " + player.getName() + " is null!");
			return;
		}
		
		this.getLogger().info("PlayerData saved on logout for player " + player.getName());
		PD.save();
		Main.playerData.remove(player);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		  if(cmd.getName().equalsIgnoreCase("duel"))
		  {
			  if(!(sender instanceof Player))
			  {				  
				  return true;
			  }
			  Player player = (Player) sender;
			  if(args.length == 0)
			  {
				  sendHelpMessage(player);
				  return true;
			  }
			  
			  if(args[0].equalsIgnoreCase("accept"))
			  {
				  //accept a duel.
				  if(args.length < 2)
				  {
					  //we need a playername.
					  return true;
				  }
				  else
				  {
					 Player p2 = Bukkit.getPlayer(args[1]);
					 //make sure player2 is online and exists.
					 if(p2 == null)
					  {
						  player.sendMessage("§cCould not find player §6" + args[0]);
						  return true;
					  }
					  if(!p2.isOnline())
					  {
						  player.sendMessage("§6" + args[0] + " §cis not online!");
						  return true;					  
					  }
					  
					  //get challenger from the hashmap.
					  Player check = duels.get(player);
					  if(check == p2)
					  {
						  duels.remove(player);

						  //the numbers match up, lets battle!
						  battle.startBattle(player, p2);
					  }
				  }
			  }			  
			  else
			  {
				  //args[0] was not accept, so we assume its a player.
				  Player p2 = Bukkit.getPlayer(args[0]);
				  //check if player2 is actually online and exists.
				  if(p2 == null)
				  {
					  player.sendMessage("§cCould not find player §6" + args[0]);
					  return true;
				  }
				  if(!p2.isOnline())
				  {
					  player.sendMessage("§6" + args[0] + " §cis not online!");
					  return true;					  
				  }
				  
				  //put p2-player pair in the hashmap, so we can accept it easily. this will put in (challenged, challenger).
				  duels.put(p2, player);
				  
				  //message player 2 that someone challenged them.
			  	  TextComponent textDuel = new TextComponent("§e" + player.getName() + duelInviteMessage);
				  textDuel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,  new ComponentBuilder("§eClick to accept the duel.").create() ));
				  textDuel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + player.getName()));
				  player.spigot().sendMessage(textDuel);
			  }
			  
		  }
		return true;
	}
	
	public void sendHelpMessage(Player player)
	{
		for(String string: this.helpmessage)
		{
			player.sendMessage(string);
		}
	}
	
}
