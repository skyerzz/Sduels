package com.sky.main;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

	public HashMap<Player, Player> duels = new HashMap<Player, Player>();
	public static HashMap<Player, PlayerData> playerData = new HashMap<Player, PlayerData>();
	
	public String duelInviteMessage = "§e Has challenged you to a duel. Click to accept!";
	
	@Override
	public void onEnable() 
	{
		getLogger().info("Enabling Sduels");
		File path = new File(getDataFolder() + "/players");
	    if(!path.exists()){
	    	path.mkdirs();
	    }
	}
	
	@Override	
	public void onDisable() 
	{
		getLogger().info("Disabling Sduels");		
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
						  //the numbers match up, lets battle!
						  //TODO: initiate battle
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
		player.sendMessage("§7Help for SDuel");
		player.sendMessage("TODO");
	}
	
}
