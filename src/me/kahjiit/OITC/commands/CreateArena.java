package me.kahjiit.OITC.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.Settings;
import me.kahjiit.OITC.arena.Arena;

public class CreateArena extends SubCommand {

	public void onCommand(Player player, String[] args) {
		if (!player.hasPermission("oitc.admin")) {
			Messages.onPlayerNoPermission(player);
			return;
		}
		
		if (args.length == 1) {
			player.sendMessage(ChatColor.RED + "Please specify an arena.");
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
		
		if (Manager.arenaExists(id)) {
			Messages.sendPlayerMessage(player, "An arena by that ID already exists.");
			return;
		}
		
		Settings.getInstance().getConfig().addDefault("Arenas." + id + ".AutoStartPlayers", 2);
		Settings.getInstance().getConfig().addDefault("Arenas." + id + ".Countdown", 30);
		Settings.getInstance().getConfig().addDefault("Arenas." + id + ".MaxPlayers", 12);
		Settings.getInstance().getConfig().addDefault("Arenas." + id + ".Time", 900);
		
		Arena arena = new Arena(id);
		Manager.addArena(arena);
		
		Settings.getInstance().save();
		Messages.sendPlayerMessage(player, "You have created the arena OITC" + arena.getID() + ".");
	}

	public String name() {
		return "createarena";
	}

	public String description() {
		return "Creates a new arena.";
	}
}