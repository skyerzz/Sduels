package com.sky.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
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
		
		temp = yml.getString("readymessage");
		if(temp!=null)
		{
			this.readyMessage = temp.replace("&", "§");
		}
	}
	
	public String cancelled = "§cNo available arenas, Duel is cancelled!";
	public String startbattle = "§6GO!";
	public String countdownmessage = "§6The match begins in <seconds> seconds!";
	public String readyMessage = "§6Your opponent is ready!";
	
	public HashMap<Player, Player> duels = new HashMap<Player, Player>();
	public HashMap<Player, String> kits = new HashMap<Player, String>();
	public ArrayList<Player> ingame = new ArrayList<Player>();
	public ArrayList<Player> freeze = new ArrayList<Player>();
	public ArrayList<Player> choosing = new ArrayList<Player>();
	
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if(freeze.contains(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event)
	{
		double distance = event.getFrom().distance(event.getTo());
		System.out.println("" + distance);
		if(distance < 10)
		{
			return;
		}
		Player winner = this.duels.get(event.getPlayer());
		this.win(winner);
	}
		
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if(!(event.getPlayer() instanceof Player))
		{
			return;
		}
		Player player = (Player) event.getPlayer();
		if(this.choosing.contains(player))
		{
			inv.showMenu(player);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if(event.getCurrentItem()==null)
		{
			return;
		}
		ItemStack clicked = event.getCurrentItem();
		
		if(event.getClickedInventory()==null)
		{
			return;
		}
		if(event.getClickedInventory().getName()==null)
		{
			return;
		}
		if(!(event.getWhoClicked() instanceof Player))
		{
			return;
		}
		
		String invName = event.getClickedInventory().getName();
		Player player = (Player) event.getWhoClicked();
		if(invName.equals(this.inv.menuname))
		{
			Material test = clicked.getType();
			if(test == Material.POTION)
			{
				//TODO: give potion kit
				this.kits.put(player, "potion");
				ready(player);
				return;
			}
			if(test == Material.GOLDEN_APPLE)
			{
				//TODO: give golden apple kit
				this.kits.put(player, "gapple");
				ready(player);
				return;
			}
			if(test == Material.FISHING_ROD)
			{
				//TODO: give MCSG kit
				this.kits.put(player, "mcsg");
				ready(player);
				return;
			}
		}
	}
	
	public void ready(Player player)
	{
		Player opponent = this.duels.get(player);
		opponent.sendMessage(this.readyMessage);
		this.choosing.remove(player);
		if(!this.choosing.contains(opponent))
		{
			this.countDown(player, opponent);
		}
		player.closeInventory();
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
	
	public void countDown(Player player, Player opponent)
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
					   player.sendMessage(startbattle);
					   opponent.sendMessage(startbattle);
					   freeze.remove(player);
					   freeze.remove(opponent);
					   this.cancel();
					   return;
				   }
				   //send both players the countdown message
				   String count = 5-i + "";
				   String currentcountdownmessage = countdownmessage.replace("<seconds>", count);
				   player.sendMessage(currentcountdownmessage);
				   opponent.sendMessage(currentcountdownmessage);
			   }
		}.runTaskTimer(this.main, 0L, 20L);

	}
	
	public void win(Player player)
	{
		Player loser = this.duels.get(player);
		PlayerData pPD = Main.getPlayerData(player);
		pPD.wins++;
		switch(kits.get(player))
		{
		case "potion":
			pPD.kitWinPotion++;
			break;
		case "gapple":
			pPD.kitWinGapple++;
			break;
		case "mcsg":
			pPD.kitWinMCSG++;
			break;
		default: break;
		}
		
		PlayerData lPD = Main.getPlayerData(loser);
		lPD.losses++;
		switch(kits.get(player))
		{
		case "potion":
			lPD.kitLossPotion++;
			break;
		case "gapple":
			lPD.kitLossGapple++;
			break;
		case "mcsg":
			lPD.kitLossMCSG++;
			break;
		default: break;
		}
		
		this.duels.remove(player);
		this.duels.remove(loser);
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
