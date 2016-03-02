package me.kahjiit.OITC.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.Settings;
import me.kahjiit.OITC.arena.Arena;
import me.kahjiit.OITC.arena.GameState;

public class Join extends SubCommand {

	public void onCommand(Player player, String[] args) {
		if (args.length == 1) {
			Messages.sendPlayerMessage(player, "Please specify the arena you want to join.");
			return;
		}
		
		if (Manager.isPlaying(player)) {
			Messages.sendPlayerMessage(player, "You are already playing.");
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
			Messages.sendPlayerMessage(player, "An arena with that ID doesn't exist.");
			return;
		}
		
		Arena arena = Manager.getArena(id);
		
		if (arena.getState() != GameState.WAITING) {
			Messages.sendPlayerMessage(player, "That arena is not joinable.");
			return;
		}
		
		if (arena.getPlayers().size() >= Settings.getInstance().getMaxPlayers(arena)) {
			Messages.sendPlayerMessage(player, "That arena is full.");
			return;
		}
		
		arena.addPlayer(player);
		Messages.sendPlayerMessage(player, ChatColor.YELLOW + "You have joined OITC" + arena.getID() + ". Type "
				+ ChatColor.WHITE + "/leave" + ChatColor.YELLOW + " to return to the lobby.");
		Messages.sendArenaMessage(arena, ChatColor.WHITE + player.getName() + ChatColor.GRAY + " has joined the lobby. "
				+ Messages.alivePlayersSize(arena, Settings.getInstance().getMaxPlayers(arena)));
	}

	public String name() {
		return "join";
	}

	public String description() {
		return "Joins the selected arena.";
	}
}