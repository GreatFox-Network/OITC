package me.kahjiit.OITC.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.Settings;
import me.kahjiit.OITC.arena.Arena;
import me.kahjiit.OITC.arena.GameState;
import me.kahjiit.OITC.stats.Leaderboards;

public class SignEvent implements Listener {

	Leaderboards lead = new Leaderboards(Settings.getInstance().getPlayerScores());
	int rank;
	
	@EventHandler
	public void onSignCreate(SignChangeEvent event) {
		Player player = event.getPlayer();
		
		if (event.getLine(0).equalsIgnoreCase("oitc") && player.hasPermission("oitc.admin")) {
			if (event.getLine(1).equalsIgnoreCase("lead")) {
				try {
					rank = Integer.parseInt(event.getLine(2));
				}
				catch (Exception exception) {
					Messages.sendPlayerMessage(player, "Could not create leaderboard sign: That's not a rank number.");
					return;
				}
				
				Settings.getInstance().setLocation("Main.Leaderboard." + rank, event.getBlock().getLocation());
			}
			
			for (Arena arena : Manager.getArenas()) {
				if (event.getLine(1).equalsIgnoreCase(arena.getID() + "")) {
					event.setLine(1, arena.getMap().getName());
					event.setLine(2, ChatColor.BOLD + "OITC-" + arena.getID());
					
					if (arena.getState() == GameState.INGAME) {
						event.setLine(0, ChatColor.RED + ChatColor.BOLD.toString() + "[IN-GAME]");
					}
					if (arena.getState() == GameState.RESTARTING) {
						event.setLine(0, ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "[RESTARTING]");
					}
					if (arena.getState() == GameState.STARTING) {
						event.setLine(0, ChatColor.GOLD + ChatColor.BOLD.toString() + "[STARTING]");
					}
					if (arena.getState() == GameState.WAITING) {
						event.setLine(0, ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "[WAITING]");
					}
					
					event.setLine(3, ChatColor.DARK_BLUE.toString() + arena.getPlayers().size() + "/" + Settings.getInstance().getMaxPlayers(arena));
					Settings.getInstance().setLocation(arena.getID() + ".Sign", event.getBlock().getLocation());
					Messages.sendPlayerMessage(player, "Successfully set the join sign for the arena " + arena.getID() + ".");
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onSignInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getState() instanceof Sign) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				if (sign.getLine(2).contains("OITC")) {
					if (Manager.isPlaying(player)) {
						Messages.sendPlayerMessage(player, "You are already playing.");
						return;
					}
					
					for (Arena arena : Manager.getArenas()) {
						if (sign.getLine(2).contains(arena.getID() + "")) {
							Bukkit.dispatchCommand(player, "oitc join " + arena.getID());
							return;
						}
					}
				}
			}
		}
	}
}