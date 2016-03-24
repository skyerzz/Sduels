package com.sky.main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Inventories{

	Main main;
	public Inventories(Main main)
	{
		this.main = main;
		getStrings(main.yml);
	}

	String menuname = "�9Kit Menu";
	String playernull = "�cThis player does not appear in our database!";
	
	
	
	public void getStrings(YamlConfiguration yml)
	{
		String temp = yml.getString("menuname");
		if(temp!=null)
		{
			this.menuname = temp.replace("&", "�");
		}
		
		temp = yml.getString("playernonexsistant");
		if(temp!=null)
		{
			this.playernull = temp.replace("&", "�");
		}
		
	}
	
	public void showMenu(Player player, int page)
	{
		int start = page*45 - 45;//starts at 0
		if(Kits.kits.isEmpty())
		{
			System.out.println("There are no kits specified! cannot open inventory!");
			return;
		}
		if(start > Kits.kits.size()-1)
		{
			start = 0;
		}
		Inventory menu = Bukkit.createInventory(null, 54, menuname);

		if(page>1)
		{
			//they are definately not on page 1, so we add a page back arrow.
			int prevpage = page-1;
			ItemStack back = new ItemStack(Material.ARROW, prevpage);
			{
				ItemMeta meta = back.getItemMeta();
				meta.setDisplayName("Page " + prevpage);
				back.setItemMeta(meta);
				menu.setItem(45, back);
			}
		}
		if(start+44 < Kits.kits.size())
		{
			//There are more pages to be discovered, lets give them a next page arrow
			int nextpage = page-1;
			ItemStack next = new ItemStack(Material.ARROW, nextpage);
			{
				ItemMeta meta = next.getItemMeta();
				meta.setDisplayName("Page " + nextpage);
				next.setItemMeta(meta);
				menu.setItem(53, next);
			}
		}
		
		int currentslot = 0;
		int i = 0;
		for(String string: Kits.kits.keySet())
		{	
			if(i<start)
			{
				i++;
				continue;
			}
			if(currentslot > 44)
			{
				break;
			}
			Kit kit = Kits.kits.get(string);
			ItemStack item = kit.menuitem;
			menu.setItem(currentslot, item);
			i++;
		}
		
		player.openInventory(menu);
	}

	public void showStats(Player sender, OfflinePlayer player, int page)
	{
		PlayerData PD = Main.getPlayerData(player);
		if(!PD.exists)
		{
			sender.sendMessage(this.playernull);
			return;
		}
		Inventory menu = Bukkit.createInventory(null, 54, menuname);

		ItemStack wins = new ItemStack(Material.GOLD_BLOCK, 1);
		{
			ItemMeta meta = wins.getItemMeta();
			meta.setDisplayName("�6Wins: " + PD.wins);
			wins.setItemMeta(meta);
			menu.setItem(48, wins);
		}
		
		ItemStack losses = new ItemStack(Material.COAL_BLOCK, 1);
		{
			ItemMeta meta = losses.getItemMeta();
			meta.setDisplayName("�8Losses: " + PD.losses);
			losses.setItemMeta(meta);
			menu.setItem(50, losses);
		}
		
		int start = page*45 - 45;//starts at 0
		if(page>1)
		{
			//they are definately not on page 1, so we add a page back arrow.
			int prevpage = page-1;
			ItemStack back = new ItemStack(Material.ARROW, prevpage);
			{
				ItemMeta meta = back.getItemMeta();
				meta.setDisplayName("Page " + prevpage);
				back.setItemMeta(meta);
				menu.setItem(45, back);
			}
		}
		if(start+44 < Kits.kits.size())
		{
			//There are more pages to be discovered, lets give them a next page arrow
			int nextpage = page-1;
			ItemStack next = new ItemStack(Material.ARROW, nextpage);
			{
				ItemMeta meta = next.getItemMeta();
				meta.setDisplayName("Page " + nextpage);
				next.setItemMeta(meta);
				menu.setItem(53, next);
			}
		}
		
		int currentslot = 0;
		int i = 0;
		for(String string: Kits.kits.keySet())
		{	
			if(i<start)
			{
				i++;
				continue;
			}
			if(currentslot > 44)
			{
				break;
			}
			Kit kit = Kits.kits.get(string);
			ItemStack item = kit.menuitem;
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(" ");
			lore.add("�6Kit wins: " + PD.kitwins.get(string));
			lore.add("�7Kit losses: " + PD.kitloss.get(string));
			lore.add("�7Total kit uses: " + (PD.kitloss.get(string) + PD.kitwins.get(string)));
			lore.add("");
			if(PD.kitloss.get(string)==0)
			{
				if(PD.kitwins.get(string)!=0)
				{
					lore.add("Win percentage: 100%");
				}
				else
				{
					lore.add("Win percentage: N/A");
				}
			}
			lore.add("Win percentage: " + (PD.kitwins.get(string) / PD.kitloss.get(string) * 100) + "%");
			menu.setItem(currentslot, item);
			i++;
		}
		
		
		
		sender.openInventory(menu);
	}

}
