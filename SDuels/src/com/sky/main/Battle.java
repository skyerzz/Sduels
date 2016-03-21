package com.sky.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Battle implements Listener{

	Main main;
	public Battle(Main main)
	{
		this.main = main;
	}
	
	public String cancelled = "§cNo available arenas, Duel is cancelled!";
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
			duels.remove(challenger);
			duels.remove(defender);
			challenger.sendMessage(cancelled);
			defender.sendMessage(cancelled);
			return;
		}
		//make sure that this arena isnt used by others now
		Main.occupiedArenas.put(loc, Main.arenas.get(loc));
		Main.arenas.remove(loc);
		
		//make both players choose their kit
		choosing.add(challenger);
		choosing.add(defender);
		
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
