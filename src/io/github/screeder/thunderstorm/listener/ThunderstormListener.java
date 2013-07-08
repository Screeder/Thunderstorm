package io.github.screeder.thunderstorm.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import io.github.screeder.thunderstorm.Thunderstorm;
import io.github.screeder.thunderstorm.event.ThunderstormEvent;

public class ThunderstormListener implements Listener {
	
	Thunderstorm plugin;
	
	public ThunderstormListener(Thunderstorm instance)
	{
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(plugin.bLightning)
			if(event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING)
				event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		if(plugin.bFire)
			if(event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING)
				event.setCancelled(true);
	}
	
	@EventHandler
	public void onThunderstorm(ThunderstormEvent event)
	{
		plugin.runTask(event.getWorld());
	}
}
