package me.kahjiit.OITC.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.Settings;
import me.kahjiit.OITC.arena.GameMap;

public class AddSpawn extends SubCommand {

	public void onCommand(Player player, String[] args) {
		if (!player.hasPermission("oitc.admin")) {
			Messages.onPlayerNoPermission(player);
			return;
		}
		
		if (args.length == 1) {
			player.sendMessage(ChatColor.RED + "Please specify a map.");
			return;
		}
		
		if (!Manager.mapExists(args[1])) {
			Messages.sendPlayerMessage(player, "A map by that name doesn't exist.");
			return;
		}
		
		GameMap map = Manager.getMap(args[1]);
		
		for (int index = 1; index <= 20; index ++) {
			if (!Settings.getInstance().getArenas().contains("Maps." + map.getName() + ".Spawns." + index)) {
				Settings.getInstance().setLocation("Maps." + map.getName() + ".Spawns." + index, player.getLocation());
				map.addSpawn(player.getLocation());
				
				Messages.sendPlayerMessage(player, "You have added a spawn to " + map.getName() + " by the index " + index + ".");
				return;
			}
		}
	}

	public String name() {
		return "addspawn";
	}

	public String description() {
		return "Adds a spawn to a map.";
	}
}