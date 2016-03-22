package com.sky.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Battle implements Listener{

	Inventories inv;
	
	Main main;
	public Battle(Main main)
	{
		this.main = main;
		inv = new Inventories(this.main);
		loadStrings(main.yml);
	}
	
	public void loadStrings(YamlConfiguration yml)
	{	
		String temp = yml.getString("duelcancelledmessage");
		if(temp!=null)
		{
			this.cancelled = temp.replace("&", "§");
		}
		
		temp = yml.getString("startbattle");
		if(temp!=null)
		{
			this.startbattle = temp.replace("&", "§");
		}
		
		temp = yml.getString("countdownmessage");
		if(temp!=null)
		{
			this.countdownmessage = temp.replace("&", "§");
		}
	}
	
	public String cancelled = "§cNo available arenas, Duel is cancelled!";
	public String startbattle = "§6GO!";
	public String countdownmessage = "§6The match begins in <seconds> seconds!";
	
	public HashMap<Player, Player> duels = new HashMap<Player, Player>();
	public ArrayList<Player> ingame = new ArrayList<Player>();
	public ArrayList<Player> freeze = new ArrayList<Player>();
	public ArrayList<Player> choosing = new ArrayList<Player>();
	
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if(freeze.contains(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}
	
	public void onTeleport(PlayerTeleportEvent event)
	{
		//TODO: make other player win here.
	}
	
	public void startBattle(Player challenger, Player defender)
	{
		duels.put(challenger, defender);
		duels.put(defender, challenger);
		
		Location loc = this.getArena();
		if(loc==null)
		{
			//no available arenas are found, we cancel the match
			duels.remove(challenger);
			duels.remove(defender);
			challenger.sendMessage(cancelled);
			defender.sendMessage(cancelled);
			return;
		}
		//make sure that this arena cannot be used by other people
		Main.occupiedArenas.put(loc, Main.arenas.get(loc));
		Main.arenas.remove(loc);
		
		//make both players choose their kit
		choosing.add(challenger);
		choosing.add(defender);		
		inv.showMenu(challenger);
		inv.showMenu(defender);
		
	}
	
	public void countDown(Player challenger, Player defender)
	{		
		new BukkitRunnable() 
		{
			int i = 0;
			   @Override
			   public void run() 
			   {
				   if(i>=5)
				   {
					   //start the battle, unfreeze them and cancel the runnable.
					   challenger.sendMessage(startbattle);
					   defender.sendMessage(startbattle);
					   freeze.remove(challenger);
					   freeze.remove(defender);
					   this.cancel();
					   return;
				   }
				   //send both players the countdown message
				   String count = 5-i + "";
				   String currentcountdownmessage = countdownmessage.replace("<seconds>", count);
				   challenger.sendMessage(currentcountdownmessage);
				   defender.sendMessage(currentcountdownmessage);
			   }
		}.runTaskTimer(this.main, 0L, 20L);

	}
	
	public Location getArena()
	{
		Random rand = new Random();
		int randomNumb = rand.nextInt(Main.arenas.size());
		int i = 0;
		for(Location loc : Main.arenas.keySet())
		{
			if(i==randomNumb)
			{				
				return loc;
			}
		}
		return null;
	}
}
