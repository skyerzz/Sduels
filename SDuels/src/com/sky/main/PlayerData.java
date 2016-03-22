package com.sky.main;

import java.io.File;
import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerData
{
	public final String path;

	public int wins = 0, losses = 0;
	public int kitLossPotion = 0, kitLossGapple = 0, kitLossMCSG = 0;
	public int kitWinPotion = 0, kitWinGapple = 0, kitWinMCSG = 0;

	public HashMap<String, Integer> previousDuels = new HashMap<String, Integer>();
	
	private YamlConfiguration yml, newyml = new YamlConfiguration();
	

	public PlayerData(String path, OfflinePlayer player)
	{
	//	this.player = player;
		this.path = path;
		File file = FileManager.getFile(path);
		this.yml = YamlConfiguration.loadConfiguration(file);
		this.load();
	}

	
	public void load()
	{
		this.loadCommonData();
		this.loadPreviousDuels();
	}
	
	public void addPreviousDuel(String uuid, int amount)
	{
		if(!this.previousDuels.containsKey(uuid))
		{
			this.previousDuels.put(uuid, amount);
		}
		else
		{
			int prevamount = this.previousDuels.get(uuid);
			this.previousDuels.replace(uuid, prevamount + amount);
		}
	}

	public void loadPreviousDuels()
	{
		for(String string: this.yml.getConfigurationSection("previousduels").getKeys(false))
		{
			int battles = this.yml.getInt("previousduels." + string);
			this.addPreviousDuel(string, battles);
		}
	}
	
	public void loadCommonData()
	{
		String temp = this.yml.getString("wins");
		if(temp != null)
		{
			this.wins = this.yml.getInt("wins");
		}
		
		temp = this.yml.getString("losses");
		if(temp != null)
		{
			this.losses = this.yml.getInt("losses");
		}
		
		temp = this.yml.getString("kitwins.potion");
		if(temp != null)
		{
			this.kitWinPotion = this.yml.getInt("kitwins.potion");
		}
		
		temp = this.yml.getString("kitwins.gapple");
		if(temp != null)
		{
			this.kitWinGapple = this.yml.getInt("kitwins.gapple");
		}
		
		temp = this.yml.getString("kitwins.mcsg");
		if(temp != null)
		{
			this.kitWinMCSG = this.yml.getInt("kitwins.mcsg");
		}
		
		temp = this.yml.getString("kitloss.potion");
		if(temp != null)
		{
			this.kitLossPotion = this.yml.getInt("kitloss.potion");
		}
		
		temp = this.yml.getString("kitloss.gapple");
		if(temp != null)
		{
			this.kitLossGapple = this.yml.getInt("kitloss.gapple");
		}
		
		temp = this.yml.getString("kitloss.mcsg");
		if(temp != null)
		{
			this.kitLossMCSG = this.yml.getInt("kitwloss.mcsg");
		}

	}

	public void saveCommonData()
	{
		this.newyml.set("wins", this.wins);
		this.newyml.set("losses", this.losses);
		
		this.newyml.set("kitwins.potion", this.kitWinPotion);
		this.newyml.set("kitwins.gapple", this.kitWinGapple);
		this.newyml.set("kitwins.mcsg", this.kitWinMCSG);
		this.newyml.set("kitloss.potion", this.kitLossPotion);
		this.newyml.set("kitloss.gapple", this.kitLossGapple);
		this.newyml.set("kitloss.mcsg", this.kitLossMCSG);
	}
	
	public void save()
	{
		this.saveCommonData();
		FileManager.saveFile(this.path, this.newyml);
	}

	public void savePreviousDuels()
	{
		for(String uuid: this.previousDuels.keySet())
		{
			this.newyml.set("previousduels." + uuid, this.previousDuels.get(uuid));
		}
	}

}