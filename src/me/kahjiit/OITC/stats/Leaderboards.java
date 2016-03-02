package me.kahjiit.OITC.stats;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.kahjiit.OITC.Settings;

public class Leaderboards {
	
	public Leaderboards(Map<UUID, Integer> players) {
		this.players = players;
	}
	
	private int number;
	private Map<UUID, Integer> players;
	
	@SuppressWarnings("unchecked")
	public Map<UUID, Integer> getLeaderboards() {
		Map<UUID, Integer> unsorted = players;
		
		ScoreComparator compare = new ScoreComparator(unsorted);
		
		TreeMap<UUID, Integer> leaderboard = new TreeMap<UUID, Integer>(compare);
		leaderboard.putAll(unsorted);
		
		return leaderboard;
	}
	
	public UUID getPlayerFromRank(int rank) {
		number = 1;
		for (UUID uuid : getLeaderboards().keySet()) {
			if (number == rank) {
				return uuid;
			}
			number ++;
		}
		return null;
	}
	
	public String trimString(String string, int begin, int end) {
		return string.substring(begin, end);
	}
	
	public void updateLeaderboards() {
		try {
			for (String index : Settings.getInstance().getArenas().getConfigurationSection("Main.Leaderboard").getKeys(false)) {
				if (Settings.getInstance().getLocation("Main.Leaderboard." + index).getBlock().getState() instanceof Sign) {
					try {
						Sign sign = (Sign) Settings.getInstance().getLocation("Main.Leaderboard." + index).getBlock().getState();
						int rank = Integer.parseInt(index);
						Player player = Bukkit.getPlayer(getPlayerFromRank(rank));
						
						if (player != null) {
							if (player.getName().length() > 12) {
								sign.setLine(0, index + ". " + ChatColor.DARK_BLUE + trimString(player.getName(), 0, 12));
							}
							else {
								sign.setLine(0, ChatColor.BOLD.toString() + index + ". " + ChatColor.DARK_BLUE + player.getName());
							}
							sign.setLine(1, ChatColor.GOLD + ChatColor.BOLD.toString() + "Level: " + ChatColor.BLACK + Settings.getInstance().getPlayerStat(player, "Level"));
							sign.setLine(2, ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Kills: " + ChatColor.BLACK + Settings.getInstance().getPlayerStat(player, "Kills"));
							sign.setLine(3, ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Deaths: " + ChatColor.BLACK + Settings.getInstance().getPlayerStat(player, "Deaths"));
						}
						else if (Bukkit.getServer().getOfflinePlayer(getPlayerFromRank(rank)) != null) {
							OfflinePlayer op = Bukkit.getOfflinePlayer(getPlayerFromRank(rank));
							if (op.getName().length() > 12) {
								sign.setLine(0, ChatColor.BOLD.toString() + index + ". " + ChatColor.DARK_BLUE + trimString(op.getName(), 0, 12));
							}
							else {
								sign.setLine(0, ChatColor.BOLD.toString() + index + ". " + ChatColor.DARK_BLUE + op.getName());
							}
							sign.setLine(1, ChatColor.GOLD + ChatColor.BOLD.toString() + "Level: " + ChatColor.BLACK + Settings.getInstance().getPlayerStatOffline(op, "Level"));
							sign.setLine(2, ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Kills: " + ChatColor.BLACK + Settings.getInstance().getPlayerStatOffline(op, "Kills"));
							sign.setLine(3, ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Deaths: " + ChatColor.BLACK + Settings.getInstance().getPlayerStatOffline(op, "Deaths"));
						}
						sign.update();
					}
					catch (Exception exception) {
						Bukkit.getLogger().severe("[OITC] Failed to refresh Leaderboard #" + index + ": " + exception.getCause());
					}
				}
			}
		}
		catch (Exception exception) {
			Bukkit.getLogger().severe("[OITC] Leaderboard signs have not been found!");
		}
	}
}