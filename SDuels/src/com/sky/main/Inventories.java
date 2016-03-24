package com.sky.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class Inventories{

	Main main;
	public Inventories(Main main)
	{
		this.main = main;
		getStrings(main.yml);
	}

	String menuname = "§9Kit Menu";
	String playernull = "§cThis player does not appear in our database!";
	
	
	
	@SuppressWarnings("unchecked")
	public void getStrings(YamlConfiguration yml)
	{
		String temp = yml.getString("menuname");
		if(temp!=null)
		{
			this.menuname = temp.replace("&", "§");
		}
		
		temp = yml.getString("playernonexsistant");
		if(temp!=null)
		{
			this.playernull = temp.replace("&", "§");
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

	public void showStats(Player sender, OfflinePlayer player)
	{
		PlayerData PD = Main.getPlayerData(player);
		if(!PD.exists)
		{
			sender.sendMessage(this.playernull);
			return;
		}
		Inventory menu = Bukkit.createInventory(null, 27, menuname);

		ItemStack wins = new ItemStack(Material.GOLD_BLOCK, 1);
		{
			ItemMeta meta = wins.getItemMeta();
			meta.setDisplayName("§6Wins: " + PD.wins);
			wins.setItemMeta(meta);
			menu.setItem(0, wins);
		}
		
		ItemStack losses = new ItemStack(Material.COAL_BLOCK, 1);
		{
			ItemMeta meta = losses.getItemMeta();
			meta.setDisplayName("§8Losses: " + PD.losses);
			losses.setItemMeta(meta);
			menu.setItem(9, losses);
		}
		
		Potion pot = new Potion(PotionType.INSTANT_HEAL);
		ItemStack potionStat = pot.toItemStack(1);
		{
			ItemMeta meta = potionStat.getItemMeta();
			meta.setDisplayName("§6Stats for kit: " + this.potionName);
			ArrayList<String> list = new ArrayList<String>();
			list.add("");
			list.add("§7Kit Uses: " + (PD.kitLossPotion + PD.kitWinPotion));
			list.add("§6Kit Wins: " + PD.kitWinPotion);
			list.add("§7Kit Losses: " + PD.kitLossPotion);
			list.add("");
			if(PD.kitLossPotion!=0)
			{
				list.add("§7Win percentage: " + (PD.kitWinPotion/PD.kitLossPotion)*100 + "%");
			}
			else if(PD.kitWinPotion!=0)
			{
				list.add("§7Win percentage: " + "100%");
			}
			else
			{
				list.add("§7Win percentage: " + "N/A");				
			}
			meta.setLore(list);
			potionStat.setItemMeta(meta);
			menu.setItem(20, potionStat);
		}		
		
		ItemStack gappleStat = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
		{
			ItemMeta meta = gappleStat.getItemMeta();
			meta.setDisplayName("§6Stats for kit: " + this.gappleName);
			ArrayList<String> list = new ArrayList<String>();
			list.add("");
			list.add("§7Kit Uses: " + (PD.kitLossGapple + PD.kitWinGapple));
			list.add("§6Kit Wins: " + PD.kitWinGapple);
			list.add("§7Kit Losses: " + PD.kitLossGapple);
			list.add("");
			if(PD.kitLossGapple!=0)
			{
				list.add("§7Win percentage: " + (PD.kitWinGapple/PD.kitLossGapple)*100 + "%");
			}
			else if(PD.kitWinGapple!=0)
			{
				list.add("§7Win percentage: " + "100%");
			}
			else
			{
				list.add("§7Win percentage: " + "N/A");				
			}
			meta.setLore(list);
			gappleStat.setItemMeta(meta);
			menu.setItem(22, gappleStat);
		}
		
		ItemStack mcsgStat = new ItemStack(Material.FISHING_ROD, 1);
		{
			ItemMeta meta = mcsgStat.getItemMeta();
			meta.setDisplayName("§6Stats for kit: " + this.mcsgName);
			ArrayList<String> list = new ArrayList<String>();
			list.add("");
			list.add("§7Kit Uses: " + (PD.kitLossMCSG + PD.kitWinMCSG));
			list.add("§6Kit Wins: " + PD.kitWinMCSG);
			list.add("§7Kit Losses: " + PD.kitLossMCSG);
			list.add("");
			if(PD.kitLossMCSG!=0)
			{
				list.add("§7Win percentage: " + (PD.kitWinMCSG/PD.kitLossMCSG)*100 + "%");
			}
			else if(PD.kitWinMCSG!=0)
			{
				list.add("§7Win percentage: " + "100%");
			}
			else
			{
				list.add("§7Win percentage: " + "N/A");				
			}
			meta.setLore(list);
			mcsgStat.setItemMeta(meta);
			menu.setItem(24, mcsgStat);
		}
		
		sender.openInventory(menu);
	}

}
