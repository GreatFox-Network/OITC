package me.kahjiit.OITC.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.Settings;
import me.kahjiit.OITC.arena.GameMap;

public class CreateMap extends SubCommand {

	public void onCommand(Player player, String[] args) {
		if (!player.hasPermission("oitc.admin")) {
			Messages.onPlayerNoPermission(player);
			return;
		}
		
		if (args.length == 1) {
			player.sendMessage(ChatColor.RED + "Please specify a map.");
			return;
		}
		
		if (Manager.mapExists(args[1])) {
			player.sendMessage(ChatColor.RED + "A map by that name already exists.");
			return;
		}
		
		Settings.getInstance().getArenas().set("Maps." + args[1] + ".Author", player.getName());
		Settings.getInstance().save();
		
		Manager.addMap(new GameMap(args[1]));
		Messages.sendPlayerMessage(player, "You have created the map " + args[1] + ".");
	}

	public String name() {
		return "createmap";
	}
	
	public String description() {
		return "Creates a new map.";
	}
}