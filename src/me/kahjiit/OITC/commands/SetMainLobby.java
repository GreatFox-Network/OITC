package me.kahjiit.OITC.commands;

import org.bukkit.entity.Player;

import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.Settings;

public class SetMainLobby extends SubCommand {

	public void onCommand(Player player, String[] args) {
		if (!player.hasPermission("oitc.admin")) {
			Messages.onPlayerNoPermission(player);
			return;
		}
		
		Settings.getInstance().setLocation("Main.Lobby", player.getLocation());
		Messages.sendPlayerMessage(player, "You have set the main lobby for OITC.");
	}

	public String name() {
		return "setmainlobby";
	}

	public String description() {
		return "Sets the main lobby where players gather.";
	}
}