package me.kahjiit.OITC.arena;

import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.kahjiit.OITC.Settings;

public class Sidebar {

	private Arena arena;
	
	public Sidebar(Arena arena) {
		this.arena = arena;
	}
	
	private String getDate() {
		Calendar cal = Calendar.getInstance();
		String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		return cal.get(Calendar.DAY_OF_MONTH) + " " + months[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.YEAR);
	}

	public Scoreboard getGameSidebar() {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = sb.registerNewObjective(ChatColor.GOLD + "OITC " + formatTime(arena.getTimer().getTime()), "oitc");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		for (UUID uuid : arena.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				objective.getScore(player.getName()).setScore(arena.getStats(player).getKills());
			}
		}
		objective.getScore(ChatColor.YELLOW + "Map: " + arena.getMap().getName()).setScore(-1);
		objective.getScore(ChatColor.GOLD + getDate()).setScore(-2);
		return sb;
	}
	
	public Scoreboard getLobbySidebar(Player player) {
		Scoreboard msb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = msb.registerNewObjective(ChatColor.GOLD + "OITC Lobby", "oitc");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.getScore(ChatColor.YELLOW + "Kills: " + ChatColor.WHITE + Settings.getInstance().getPlayerStat(player, "Kills")).setScore(6);
		objective.getScore(ChatColor.YELLOW + "Deaths: " + ChatColor.WHITE + Settings.getInstance().getPlayerStat(player, "Deaths")).setScore(5);
		objective.getScore(ChatColor.GOLD + "KDR: " + ChatColor.WHITE + Settings.getInstance().getKDR(player)).setScore(4);
		objective.getScore(ChatColor.YELLOW + "Games: " + ChatColor.WHITE + Settings.getInstance().getPlayerStat(player, "Games")).setScore(3);
		objective.getScore(ChatColor.YELLOW + "Wins: " + ChatColor.WHITE + Settings.getInstance().getPlayerStat(player, "Wins")).setScore(2);
		objective.getScore(ChatColor.GOLD + "WGR: " + ChatColor.WHITE + Settings.getInstance().getWGR(player)).setScore(1);
		objective.getScore(ChatColor.GREEN + "Level: " + ChatColor.WHITE + Settings.getInstance().getPlayerStat(player, "Level")).setScore(0);
		
		return msb;
	}
	
	public String formatTime(int time) {
		String seconds;
		String minute = time / 60 + "";
		if (time % 60 < 10) {
			seconds = "0" + time % 60;
		}
		else {
			seconds = time % 60 + "";
		}
		String formattedTime = minute + ":" + seconds;
		return formattedTime;
	}
	
	public void removeSidebar(Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
	
	public void setSidebars(Scoreboard scoreboard) {
		for (UUID uuid : arena.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				player.setScoreboard(scoreboard);
			}
		}
	}
}