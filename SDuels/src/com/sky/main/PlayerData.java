package com.sky.main;

import java.io.File;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerData
{
	public final String path;

	public int wins = 0;

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
	}

	public void loadCommonData()
	{
		String temp = this.yml.getString("wins");
		if(temp != null)
		{
			this.wins = this.yml.getInt("wins");
		}

	}

	public void saveCommonData()
	{
		this.newyml.set("wins", this.wins);
	}
	
	public void save()
	{
		this.saveCommonData();
		FileManager.saveFile(this.path, this.newyml);
	}


}