package com.sky.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.minr.achievements.Achievement;
import org.minr.achievements.Achievements;
import org.minr.checkpoint.Checkpoint;
import org.minr.checkpoint.MapData;

public class PlayerData
{
	public final String path;


	private YamlConfiguration yml, newyml = new YamlConfiguration();
	
	//private OfflinePlayer player;

	public PlayerData(String path, OfflinePlayer player)
	{
	//	this.player = player;
		this.path = path;
		File file = FileManager.getFile(path);
		this.yml = YamlConfiguration.loadConfiguration(file);
		this.load();
	}

	// Functions
	public void load()
	{
		this.loadCommonData();
	}

	public void loadCommonData()
	{
		String temp = this.yml.getString("muted");
		if(temp != null)
		{
			this.mutedDate = new Date(this.yml.getLong("muted"));
		}

	}

	public void saveCommonData()
	{

		if(this.firstLogin != null)
		{
			this.newyml.set("firstlogin", this.firstLogin.getTime());
		}
	}

}