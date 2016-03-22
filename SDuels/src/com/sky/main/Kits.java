package com.sky.main;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kits {

	public Kits(Main main)
	{}
	
	public void givePlayerKit(Player player, String kit)
	{
		switch(kit.toLowerCase())
		{
		case "potion":
			potion(player);
			break;
		case "gapple":
			gapple(player);
			break;
		case "mcsg":
			mcsg(player);
			break;
		default:
			errorInventory(player);
		}
	}
	
	public void errorInventory(Player player)
	{
		player.getInventory().clear();
		for(int i = 0; i < 36; i++)
		{
			player.getInventory().setItem(i, new ItemStack(Material.BEDROCK, -1));
		}
	}
	
	public void mcsg(Player player)
	{
		player.getInventory().clear();
		
	}
	
	public void gapple(Player player)
	{
		player.getInventory().clear();
		
	}
	
	public void potion(Player player)
	{
		player.getInventory().clear();
		
	}
}
