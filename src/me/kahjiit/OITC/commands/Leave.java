package me.kahjiit.OITC.commands;

import org.bukkit.entity.Player;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.arena.Arena;

public class Leave extends SubCommand {

	public void onCommand(Player player, String[] args) {
		if (!Manager.isPlaying(player)) {
			Messages.sendPlayerMessage(player, "You are not playing.");
			return;
		}
		
		Arena arena = Manager.getArena(player);
		
		arena.removePlayer(player);
		Messages.sendPlayerMessage(player, "You have left your arena.");
	}

	public String name() {
		return "leave";
	}

	public String description() {
		return "Leaves your current arena.";
	}
}