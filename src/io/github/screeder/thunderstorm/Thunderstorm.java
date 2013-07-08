package io.github.screeder.thunderstorm;


import io.github.screeder.thunderstorm.event.ThunderstormEvent;
import io.github.screeder.thunderstorm.listener.ThunderstormListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

//org.bukkit.event.entity.EntityDamageEvent

public final class Thunderstorm extends JavaPlugin {
	
	boolean bActive = false;
	//public boolean bDamage = false;
	public boolean bFire = false;
	public boolean bLightning = false;
	Properties config;
	boolean bConstant = false;
	int min, max, constant;
	BukkitTask timer;
	Thunderstorm plugin = this;
	boolean isThundering = false;
	
	@Override
	public void onEnable()
	{
		//ConsoleCommandSender console = getServer().getConsoleSender();
		getLogger().info("onEnable has been invoked!");
		//console.sendMessage("[Thunderstorm] " + ChatColor.RED + "Cannot find config.properties");
		saveDefaultConfig();
		bConstant = getConfig().getBoolean("main.ConstantFrequency");
		min = getConfig().getInt("main.Frequency.Min");
		max = getConfig().getInt("main.Frequency.Max");
		constant = getConfig().getInt("main.Frequency.Constant");
		//bDamage = getConfig().getBoolean("main.BlockDamage");
		bFire = getConfig().getBoolean("main.Fire");
		bLightning = getConfig().getBoolean("main.Lightning");
		new ThunderstormListener(this);
	}

	@Override
	public void onDisable()
	{
		getLogger().info("onDisable has been invoked!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		boolean player = false;
		if(sender instanceof Player)
			player = true;
		List<World> worlds = sender.getServer().getWorlds();
		if(cmd.getName().equalsIgnoreCase("thunderstorm"))
		{
			if( (!player) || (player && ((Player)sender).hasPermission("thunderstorm.admin")) )
				if(args.length == 1 && args[0].equalsIgnoreCase("toggle"))
				{
					changeWeather(sender.getServer().getWorld(worlds.get(0).getName()));
					if(bActive)
						sender.sendMessage(ChatColor.GREEN + "Thunderstorm active");
					else
						sender.sendMessage(ChatColor.GREEN + "Thunderstorm not active");
				}
				else
				{
					sender.sendMessage(ChatColor.GRAY + "###Thunderstorm v0.5 by Screeder###");
					sender.sendMessage(ChatColor.GOLD + "/thunderstorm toggle (to toggle thunderstorm)");
				}
			return true;
		}
		return false;
	}
	
	public void changeWeather(World world)
	{
		if(!bActive)
		{
			if(bConstant == true)
			{
				min = constant;
				max = constant;
			}
			getLogger().info(String.valueOf((Math.random() * (max - min) + min)));
			isThundering = world.isThundering();
			world.setThundering(true);
			runTask(world);
			bActive = !bActive;
		} 
		else
		{
			if(!isThundering)
				world.setThundering(false);
			timer.cancel();
			bActive = !bActive;
		}
	}
	
	public void runTask(final World world)
	{
		timer = new BukkitRunnable()
		{
		    @Override
		    public void run()
		    {	
		    	thunderStrike(world);
		    	ThunderstormEvent event = new ThunderstormEvent(true, world);
		    	Bukkit.getServer().getPluginManager().callEvent(event);
		    	this.cancel();
		    }
		}.runTaskTimer(this, (long)(20L * (Math.random() * (max - min) + min)), (long)(20L * (Math.random() * (max - min) + min))); //20L = 20 Ticks = 1 Second
	}
	
	public void thunderStrike(World world)
	{
		int id = -1;
		Random rnd = new Random();
		List<Integer> excludePlayerlist = new ArrayList<Integer>();
		while(excludePlayerlist.size() != world.getPlayers().size())
		{
			id = getRandomWithExclusion(rnd, 0, world.getPlayers().size()-1, excludePlayerlist);
			excludePlayerlist.add(id);
			if(!isBlockAbove(id, world) && world.getPlayers().get(id).getHealth() != 0)
			{
				world.strikeLightning(world.getPlayers().get(id).getLocation());
				break;
			}
		}
	}
	
	public boolean isBlockAbove(int id, World world)
	{
		Location loc = world.getPlayers().get(id).getLocation();
		for(int i = (int) loc.getY(); i < 256; i++)
		{
			loc.setY(i);
			Block block = world.getBlockAt(loc);
			if(block.getType() != Material.AIR)
				return true;
		}
		return false;
	}
	
	public int getRandomWithExclusion(Random rnd, int start, int end, List<Integer> exclude) {
	    int random = start + rnd.nextInt(end - start + 1 - exclude.size());
	    for (int ex : exclude) {
	        if (random < ex) {
	            break;
	        }
	        random++;
	    }
	    return random;
	}
}
