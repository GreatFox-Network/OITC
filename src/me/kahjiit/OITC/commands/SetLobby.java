package me.kahjiit.OITC.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.Settings;

public class SetLobby extends SubCommand {

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
			Messages.sendPlayerMessage(player, "An arena with that ID doesn't exist.");
			return;
		}

		Settings.getInstance().setLocation(args[1] + ".Lobby", player.getLocation());
		Messages.sendPlayerMessage(player, "You have set the waiting lobby for the arena " + args[1] + ".");
	}

	public String name() {
		return "setlobby";
	}

	public String description() {
		return "Sets the waiting lobby for an arena.";
	}
}