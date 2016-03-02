package me.kahjiit.OITC.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.kahjiit.OITC.OITC;

public class Version extends SubCommand {
	
	public void onCommand(Player player, String[] args) {
		player.sendMessage(ChatColor.GOLD + "Running " + ChatColor.YELLOW + "OITC v"
				+ OITC.getPlugin().getDescription().getVersion() + ChatColor.GOLD + " made by the Great Fox Dev. team");
	}

	public String name() {
		return "version";
	}

	public String description() {
		return "Shows the current version of the plugin.";
	}
}