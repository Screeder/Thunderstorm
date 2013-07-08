package io.github.screeder.thunderstorm.event;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ThunderstormEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
    private boolean canceled;
    private World world;
 
    public ThunderstormEvent(boolean status, World world) {
    	canceled = status;
    	this.world = world;
    }
 
    public boolean isCanceled() {
        return canceled;
    }
    
    public World getWorld() {
    	return world;
    }
 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
