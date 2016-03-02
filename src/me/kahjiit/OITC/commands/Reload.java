package me.kahjiit.OITC.commands;

import org.bukkit.entity.Player;

import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.Settings;

public class Reload extends SubCommand {

	public void onCommand(Player player, String[] args) {
		if (!player.hasPermission("oitc.admin")) {
			Messages.onPlayerNoPermission(player);
			return;
		}
		
		Settings.getInstance().load();
		Messages.sendPlayerMessage(player, "All configuration files have been reloaded.");
	}

	public String name() {
		return "reload";
	}

	public String description() {
		return "Reloads all configuration settings";
	}
}