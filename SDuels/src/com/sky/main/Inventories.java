package com.sky.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class Inventories{

	public Inventories(Main main)
	{
		getStrings(main.yml);
	}

	String menuname = "§9Kit Menu";
	
	String potionName = "§6Potion PvP", gappleName = "§6Gapple PvP", mcsgName = "§6MCSG";
	ArrayList<String> potionlore = new ArrayList<String>(), gapplelore = new ArrayList<String>(), mcsglore = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	public void getStrings(YamlConfiguration yml)
	{
		String temp = yml.getString("menuname");
		if(temp!=null)
		{
			this.menuname = temp.replace("&", "§");
		}
		
		temp = yml.getString("potionname");
		if(temp!=null)
		{
			this.potionName = temp.replace("&", "§");
		}
		
		;
		for(String string: (List<String>) yml.getList("potionlore"))
		{
			
			this.potionlore.add(string.replace("&", "§"));
		}
		
		temp = yml.getString("gapplename");
		if(temp!=null)
		{
			this.gappleName = temp.replace("&", "§");
		}
		
		;
		for(String string: (List<String>) yml.getList("gapplelore"))
		{
			this.gapplelore.add(string.replace("&", "§"));
		}
		
		temp = yml.getString("mcsgname");
		if(temp!=null)
		{
			this.mcsgName = temp.replace("&", "§");
		}
		
		;
		for(String string: (List<String>) yml.getList("mcsglore"))
		{
			this.mcsglore.add(string.replace("&", "§"));
		}
		
	}
	
	public void showMenu(Player player)
	{
		Inventory menu = Bukkit.createInventory(null, 9, menuname);

		Potion pot = new Potion(PotionType.INSTANT_HEAL);
		ItemStack potion = pot.toItemStack(1);
		{
			ItemMeta meta = potion.getItemMeta();
			meta.setDisplayName(this.potionName);
			ArrayList<String> list = this.potionlore;			
			meta.setLore(list);
			potion.setItemMeta(meta);
			menu.setItem(2, potion);
		}
		
		ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
		{
			ItemMeta meta = gapple.getItemMeta();
			meta.setDisplayName(this.gappleName);
			ArrayList<String> list = this.gapplelore;			
			meta.setLore(list);
			gapple.setItemMeta(meta);
			menu.setItem(4, gapple);
		}
		
		ItemStack mcsg = new ItemStack(Material.FISHING_ROD, 1);
		{
			ItemMeta meta = mcsg.getItemMeta();
			meta.setDisplayName(this.mcsgName);
			ArrayList<String> list = this.mcsglore;			
			meta.setLore(list);
			mcsg.setItemMeta(meta);
			menu.setItem(6, mcsg);
		}
		
		player.openInventory(menu);
	}
}
