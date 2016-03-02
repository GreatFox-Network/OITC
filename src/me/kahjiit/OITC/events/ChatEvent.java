package me.kahjiit.OITC.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.Settings;

public class ChatEvent implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if (!Manager.isPlaying(player)) {
			for (UUID uuid : Manager.getPlayers()) {
				event.getRecipients().remove(Bukkit.getPlayer(uuid));
			}
			return;
		}
		
		event.setCancelled(true);
		if (Settings.getInstance().getPlayerStat(player, "Level") >= 56) {
			Messages.sendArenaMessage(Manager.getArena(player), ChatColor.WHITE + player.getName() + " ("
					+ ChatColor.GOLD + "Prestige" + ChatColor.WHITE + "): " + ChatColor.GRAY + event.getMessage());
		}
		Bukkit.getLogger().info("[OITC] [Arena " + Manager.getArena(player).getID() + "] " + player.getName() + ": " + event.getMessage());
	}
	
	@EventHandler
	public void onCommandSend(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (!Manager.isPlaying(player)) {
			return;
		}
		
		if (player.hasPermission("oitc.admin")) {
			return;
		}
		
		if (event.getMessage().startsWith("/oitc") || event.getMessage().equalsIgnoreCase("/leave")) {
			return;
		}
		
		player.sendMessage(ChatColor.RED + "You may not use commands while playing OITC.");
		event.setCancelled(true);
	}
}