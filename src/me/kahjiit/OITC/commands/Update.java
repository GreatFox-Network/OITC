package me.kahjiit.OITC.commands;

import org.bukkit.entity.Player;

import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.Settings;
import me.kahjiit.OITC.stats.Leaderboards;

public class Update extends SubCommand {

	public void onCommand(Player player, String[] args) {
		if (!player.hasPermission("oitc.admin")) {
			Messages.onPlayerNoPermission(player);
			return;
		}
		
		Leaderboards lead = new Leaderboards(Settings.getInstance().getPlayerScores());
		
		lead.updateLeaderboards();
		Messages.sendPlayerMessage(player, "All leaderboard signs have been updated.");
	}

	@Override
	public String name() {
		return "update";
	}

	public String description() {
		return "Updates all leaderboard signs.";
	}
}