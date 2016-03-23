package com.sky.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Battle implements Listener{

	Inventories inv;
	Kits Ckits;
	
	Main main;
	public Battle(Main main)
	{
		this.main = main;
		inv = new Inventories(this.main);
		Ckits = new Kits(this.main);
		loadStrings(main.yml);
		loadOthers(main.yml);
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
		
		temp = yml.getString("scoreboardname");
		if(temp!=null)
		{
			this.scoreboardName = temp.replace("&", "§");
		}
		
		temp = yml.getString("bottomscoreboard");
		if(temp!=null)
		{
			this.bottomscoreboard = temp.replace("&", "§");
		}
	}
	
	public void loadOthers(YamlConfiguration yml)
	{
		String temp = yml.getString("afterbattlelocation");
		if(temp==null)
		{
			main.getLogger().severe("afterbattlelocation is not correct in the config!");
			return;
		}
		try
		{
			String[] sLoc = temp.split(",");
			int x = Integer.parseInt(sLoc[0]);
			int y = Integer.parseInt(sLoc[1]);
			int z = Integer.parseInt(sLoc[2]);
			float yaw = new Float(sLoc[3]);
			float pitch = new Float(sLoc[4]);
			String worldname = sLoc[5];
			this.endbattle = new Location(Bukkit.getWorld(worldname), x, y, z ,yaw ,pitch);
		}
		catch(NumberFormatException | ArrayIndexOutOfBoundsException e)
		{
			main.getLogger().severe("afterbattlelocation is not correct in the config!");
		}
	}
	
	public String cancelled = "§cNo available arenas, Duel is cancelled!";
	public String startbattle = "§6GO!";
	public String countdownmessage = "§6The match begins in <seconds> seconds!";
	public String readyMessage = "§6Your opponent is ready!";
	
	public String scoreboardName = "§4Duel";
	public String bottomscoreboard = "§4pvp.sandosity.com";
	
	public HashMap<Player, Player> duels = new HashMap<Player, Player>();
	public HashMap<Player, String> kits = new HashMap<Player, String>();
	public HashMap<Player, Inventory> oldInventory = new HashMap<Player, Inventory>();
	public ArrayList<Player> ingame = new ArrayList<Player>();
	public ArrayList<Player> freeze = new ArrayList<Player>();
	public ArrayList<Player> choosing = new ArrayList<Player>();
	public Location endbattle = new Location(Bukkit.getWorld("world"), 0, 0, 0);
	
	
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
		if(!this.duels.containsKey(event.getPlayer()))
		{
			return;
		}
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
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		if(this.duels.containsKey(event.getPlayer()))
		{
			//ensure the player gets teleported before the connection is closed, to not have them spawn there next time they log in.
			event.getPlayer().teleport(this.endbattle);
			//also make sure that their potion effects run out
			for(PotionEffect effect: event.getPlayer().getActivePotionEffects())
			{
				event.getPlayer().removePotionEffect(effect.getType());
			}
			Player winner = this.duels.get(event.getPlayer());
			this.win(winner);			
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if(this.duels.containsKey(event.getEntity()))
		{
			Player winner = this.duels.get(event.getEntity());
			this.win(winner);			
		}
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
		if(!this.choosing.contains((Player) event.getWhoClicked()))
		{
			return;
		}
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
		Location loc2 = Main.arenas.get(loc);
		
		//save the inventories from the players, so they can get it back after the match.
		Inventory cInv = challenger.getInventory();
		Inventory dInv = defender.getInventory();
		this.oldInventory.put(challenger, cInv);
		this.oldInventory.put(defender, dInv);
		
		//make sure that this arena cannot be used by other people
		Main.occupiedArenas.put(loc, Main.arenas.get(loc));
		Main.arenas.remove(loc);
		
		//teleport the players to said arena
		challenger.teleport(loc);
		defender.teleport(loc2);
		
		//make both players choose their kit, freeze them while they do.
		this.freeze.add(challenger);
		this.freeze.add(defender);
		choosing.add(challenger);
		choosing.add(defender);		
		inv.showMenu(challenger);
		inv.showMenu(defender);
		
	}
	
	public void countDown(Player player, Player opponent)
	{		
		//give both players a scoreboard and their kit
		this.ingame.add(player);
		this.ingame.add(opponent);
		this.updateScoreBoard(player);
		this.updateScoreBoard(opponent);
		Ckits.givePlayerKit(player, this.kits.get(player));
		Ckits.givePlayerKit(opponent, this.kits.get(opponent));
		
		//countdown from 5.
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
		
		//give the players their old inventory back
		Inventory loserInv = this.oldInventory.get(loser);
		loser.getInventory().setContents(loserInv.getContents());
		Inventory playerInv = this.oldInventory.get(player);
		player.getInventory().setContents(playerInv.getContents());		
		this.oldInventory.remove(player);
		this.oldInventory.remove(loser);
		
		//remove effects from both players
		for(PotionEffect effect: player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
		for(PotionEffect effect: loser.getActivePotionEffects())
		{
			loser.removePotionEffect(effect.getType());
		}
		
	
		if(!this.ingame.contains(player))
		{
			//they were not ingame yet, which means the countdown hasnt started. if someone won now
			//its likely to be by a leave of the other player. Therefore we cancel the duel.
			player.sendMessage(this.cancelled);
			loser.sendMessage(this.cancelled);
			this.duels.remove(player);
			this.duels.remove(loser);
			this.kits.remove(player);
			this.kits.remove(loser);

			player.teleport(this.endbattle);
			loser.teleport(this.endbattle);
			return;
		}
		
		//give the winner some stats
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
		default: 
			break;
		}
		pPD.addPreviousDuel(loser.getUniqueId().toString(), 1);
		pPD.addPreviousDuelWin(loser.getUniqueId().toString(), 1);
		if(!player.isOnline())
		{
			pPD.save();
		}
		
		//give the loser some stats
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
		lPD.addPreviousDuel(player.getUniqueId().toString(), 1);
		if(!loser.isOnline())
		{
			lPD.save();
		}
		
		//remove both players from all data stored again and teleport them to specified location in config.
		this.duels.remove(player);
		this.duels.remove(loser);
		this.kits.remove(player);
		this.kits.remove(loser);
		this.ingame.remove(player);
		this.ingame.remove(loser);
		
		player.teleport(this.endbattle);
		loser.teleport(this.endbattle);
	}
	
	public void updateScoreBoard(Player player)
	{
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		final Objective o = board.registerNewObjective(scoreboardName, "dummy");
	    o.setDisplaySlot(DisplaySlot.SIDEBAR);	    
	    
	    //empty line
	    Score s1 = o.getScore("");
	    s1.setScore(3);
	    
	    //set player + chosen kit
	    String kit = this.kits.get(player);
	    if(kit==null)
	    {
	    	kit = "null";
	    }
	    String kitname = "NULL";
	    switch(kit)
	    {
	    case "potion":
	    	kitname = inv.potionName;
	    	break;
	    case "gapple":
	    	kitname = inv.gappleName;
	    	break;
	    case "mcsg":
	    	kitname = inv.mcsgName;
	    	break;
    	default:
    		kitname = "Choosing...";
    		break;
	    }
	    Score s2 = o.getScore(player.getName() + "§67: " + kitname);
	    s2.setScore(2);
	    
	    //get the kit from the opponent next.
	    Player opponent = this.duels.get(player);	    
	    String kit2 = this.kits.get(opponent);
	    if(kit2==null)
	    {
	    	kit2 = "null";
	    }
	    String kitname2 = "NULL";
	    switch(kit2)
	    {
	    case "potion":
	    	kitname2 = inv.potionName;
	    	break;
	    case "gapple":
	    	kitname2 = inv.gappleName;
	    	break;
	    case "mcsg":
	    	kitname2 = inv.mcsgName;
	    	break;
    	default:
    		kitname = "Choosing...";
    		break;
	    }
	    Score s3 = o.getScore(opponent.getName() + "§67: " + kitname2);
	    s3.setScore(1);
	    
	    Score s4 = o.getScore(this.bottomscoreboard);
	    s4.setScore(0);
	    
	    
		player.setScoreboard(board);
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
