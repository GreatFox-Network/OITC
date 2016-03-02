package me.kahjiit.OITC.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.SkullMeta;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Utils;

public class InventoryEvent implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (!Manager.isPlaying(player)) {
			return;
		}
		
		if (ChatColor.stripColor(event.getInventory().getName()).equalsIgnoreCase("Living Players")) {
			event.setCancelled(true);
			
			if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()) {
				player.closeInventory();
				return;
			}
			
			if (event.getCurrentItem().getType() == Material.SKULL_ITEM) {
				SkullMeta meta = (SkullMeta) event.getCurrentItem().getItemMeta();
				if (Bukkit.getPlayer(meta.getOwner()) != null) {
					player.teleport(Bukkit.getPlayer(meta.getOwner()));
				}
			}
		}
		
		if (!Manager.getArena(player).getStats(player).isAlive()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (!Manager.isPlaying(player)) {
			return;
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			/*if (Manager.getArena(player).getStats(player).isAlive()) {
				return;
			}*/
			
			if (player.getItemInHand().getType() == Material.BED) {
				Bukkit.dispatchCommand(player, "leave");
			}
			
			if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
				Utils.openPlayerHeadsInventory(player, Manager.getArena(player));
			}
		}
	}
}