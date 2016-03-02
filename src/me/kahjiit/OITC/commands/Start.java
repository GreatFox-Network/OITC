package me.kahjiit.OITC.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.arena.Arena;
import me.kahjiit.OITC.arena.GameState;

public class Start extends SubCommand {

	public void onCommand(Player player, String[] args) {
		if (!player.hasPermission("oitc.admin")) {
			Messages.onPlayerNoPermission(player);
			return;
		}
		
		int id;
		
		try {
			id = Integer.parseInt(args[1]);
		}
		catch (Exception exception) {
			player.sendMessage(ChatColor.RED + "That's not a number.");
			return;
		}
		
		if (!Manager.arenaExists(id)) {
			Messages.sendPlayerMessage(player, "An arena by that ID doesn't exist.");
			return;
		}
		
		Arena arena = Manager.getArena(id);
		
		if (arena.getState() != GameState.WAITING) {
			Messages.sendPlayerMessage(player, "That arena has already started.");
			return;
		}
		
		arena.getTimer().start();
		Messages.sendPlayerMessage(player, "You have started the arena " + arena.getID() + ".");
	}

	public String name() {
		return "start";
	}

	public String description() {
		return "Forces a game to start.";
	}
}