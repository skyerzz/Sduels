package com.sky.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{

	@Override
	public void onEnable() 
	{
		getLogger().info("Enabling Sduels");
	}
	
	@Override	
	public void onDisable() 
	{
		getLogger().info("Disabling Sduels");		
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
			  
		  }
		return true;
	}
	
	public void sendHelpMessage(Player player)
	{
		player.sendMessage("§7Help for SDuel");
		player.sendMessage("TODO");
	}
	
}
