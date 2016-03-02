package me.kahjiit.OITC.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import me.kahjiit.OITC.Manager;

public class BlockEvent implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!Manager.isPlaying(event.getPlayer())) {
			return;
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!Manager.isPlaying(event.getPlayer())) {
			return;
		}
		
		event.setCancelled(true);
 	}
}