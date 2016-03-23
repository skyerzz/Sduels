package com.sky.main;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class Kits {

	public Kits(Main main)
	{}
	
	public void givePlayerKit(Player player, String kit)
	{
		//clear all their potion effects first, and clear their inventory.
		for(PotionEffect effect: player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
		player.getInventory().clear();
		
		//find which kit to give
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
		
		Enchantment[] ench = null;
		int[] level = null;
		player.getInventory().setChestplate(this.getIronGear("chestplate", ench, level));
		player.getInventory().setHelmet(this.getIronGear("helmet", ench, level));
		player.getInventory().setLeggings(this.getIronGear("leggings", ench, level));
		player.getInventory().setBoots(this.getIronGear("boots", ench, level));
		
		player.getInventory().setItem(0, this.getSword("stone", ench, level));
		
		player.getInventory().addItem(new ItemStack(Material.BOW, 1));
		player.getInventory().addItem(new ItemStack(Material.ARROW, 16));		

		player.getInventory().addItem(new ItemStack(Material.FISHING_ROD, 1));
		

		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 4));
		

		player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 8));
	}
	
	public void gapple(Player player)
	{
		Enchantment[] ench = new Enchantment[1];
		ench[0]=Enchantment.PROTECTION_ENVIRONMENTAL; ench[1] = Enchantment.DURABILITY;
		int[] level = new int[1];
		level[0] = 4; level[1] = 3;
		
		player.getInventory().setChestplate(this.getDiamondGear("chestplate", ench, level));
		player.getInventory().setHelmet(this.getDiamondGear("helmet", ench, level));
		player.getInventory().setLeggings(this.getDiamondGear("leggings", ench, level));
		player.getInventory().setBoots(this.getDiamondGear("boots", ench, level));
		
		player.getInventory().addItem(this.getDiamondGear("chestplate", ench, level));
		player.getInventory().addItem(this.getDiamondGear("helmet", ench, level));
		player.getInventory().addItem(this.getDiamondGear("leggings", ench, level));
		player.getInventory().addItem(this.getDiamondGear("boots", ench, level));
		
		ench[0] = Enchantment.DAMAGE_ALL; ench[1] = Enchantment.FIRE_ASPECT;
		level[1] = 2;
		
		player.getInventory().addItem(this.getSword("diamond", ench, level));		

		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 32, (short) 1));
		
		Potion strength = new Potion(PotionType.STRENGTH, 1);
		player.getInventory().addItem(strength.toItemStack(2));
		

		Potion speed = new Potion(PotionType.SPEED, 2);
		player.getInventory().addItem(speed.toItemStack(2));
		
	}
	
	public void potion(Player player)
	{
		Enchantment[] ench = new Enchantment[0];
		ench[0]=Enchantment.PROTECTION_ENVIRONMENTAL;
		int[] level = new int[0];
		level[0] = 1;
		
		player.getInventory().setChestplate(this.getDiamondGear("chestplate", ench, level));
		player.getInventory().setHelmet(this.getDiamondGear("helmet", ench, level));
		player.getInventory().setLeggings(this.getDiamondGear("leggings", ench, level));
		player.getInventory().setBoots(this.getDiamondGear("boots", ench, level));
		
		
		ench[0] = Enchantment.DAMAGE_ALL;
		player.getInventory().setItem(0, this.getSword("diamond", ench, level));
		
		Potion pot = new Potion(PotionType.INSTANT_HEAL, 2);
		pot.setSplash(true);
		player.getInventory().setItem(1, pot.toItemStack(32));
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 50000, 2));
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 50000, 1));
		
		player.getInventory().setItem(2, new ItemStack(Material.BAKED_POTATO, 32));
	}
	
	public ItemStack getDiamondGear(String type, Enchantment[] enchant, int[] level)
	{
		ItemStack item;
		switch(type.toLowerCase())
		{
		case "chestplate":
			item = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
			break;
		case "boots":
			item = new ItemStack(Material.DIAMOND_BOOTS, 1);
			break;
		case "leggings":
			item = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
			break;
		case "helmet":
			item = new ItemStack(Material.DIAMOND_HELMET, 1);
			break;
		default:
			return new ItemStack(Material.BEDROCK, -1);
		}
		
		if(enchant == null)
		{
			return item;
		}
		for(int i = 0; i < enchant.length; i++)
		{
			try
			{
				item.addUnsafeEnchantment(enchant[i], level[i]);
			}
			catch(NullPointerException | ArrayIndexOutOfBoundsException e)
			{
				break;
			}
		}		
		return item;
	}
	
	public ItemStack getIronGear(String type, Enchantment[] enchant, int[] level)
	{
		ItemStack item;
		switch(type.toLowerCase())
		{
		case "chestplate":
			item = new ItemStack(Material.IRON_CHESTPLATE, 1);
			break;
		case "boots":
			item = new ItemStack(Material.IRON_BOOTS, 1);
			break;
		case "leggings":
			item = new ItemStack(Material.IRON_LEGGINGS, 1);
			break;
		case "helmet":
			item = new ItemStack(Material.IRON_HELMET, 1);
			break;
		default:
			return new ItemStack(Material.BEDROCK, -1);
		}
		
		if(enchant == null)
		{
			return item;
		}
		for(int i = 0; i < enchant.length; i++)
		{
			try
			{
				item.addUnsafeEnchantment(enchant[i], level[i]);
			}
			catch(NullPointerException | ArrayIndexOutOfBoundsException e)
			{
				break;
			}
		}		
		return item;
	}
	
	public ItemStack getSword(String type, Enchantment[] enchant, int[] level)
	{
		ItemStack item;
		switch(type.toLowerCase())
		{
		case "diamond":
			item = new ItemStack(Material.DIAMOND_SWORD, 1);
			break;
		case "iron":
			item = new ItemStack(Material.IRON_SWORD, 1);
			break;
		case "stone":
			item = new ItemStack(Material.STONE_SWORD, 1);
			break;
		default:
			return new ItemStack(Material.BEDROCK, -1);
		}
		
		if(enchant == null)
		{
			return item;
		}
		for(int i = 0; i < enchant.length; i++)
		{
			try
			{
				item.addUnsafeEnchantment(enchant[i], level[i]);
			}
			catch(NullPointerException | ArrayIndexOutOfBoundsException e)
			{
				break;
			}
		}		
		return item;
	}
}
