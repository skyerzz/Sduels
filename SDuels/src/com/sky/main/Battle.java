package com.sky.main;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Battle implements Listener{

	Main main;
	public Battle(Main main)
	{
		this.main = main;
	}
	
	public ArrayList<Player> ingame = new ArrayList<Player>();
	
	public void onPlayerMove(PlayerMoveEvent event)
	{
		
	}
	
	public void startBattle(Player challenger, Player defender)
	{
		
	}
}
