package me.kahjiit.OITC.arena;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.OITC;
import me.kahjiit.OITC.Settings;
import me.kahjiit.OITC.Utils;

public class Timer {

	private final Arena arena;
	private int countdown, id, timer;
	
	public Timer(Arena arena) {
		this.arena = arena;
	}
	
	public int getID() {
		return id;
	}
	
	public int getTime() {
		return timer;
	}
	
	public void start() {
		arena.updateSign();
		countdown = Settings.getInstance().getCountdown(arena);
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(OITC.getPlugin(), new Runnable() {
			public void run() {
				if (countdown > 0) {
					for (UUID uuid : arena.getPlayers()) {
						Messages.sendActionBarMessage(Bukkit.getPlayer(uuid),
								ChatColor.GOLD + "You will be teleported in " + ChatColor.WHITE + countdown + ChatColor.GOLD + " seconds.");
					}
					if (arena.getPlayers().size() < Settings.getInstance().getAutoStartPlayers(arena)) {
						Messages.sendArenaMessage(arena, ChatColor.YELLOW + "The countdown got cancelled: Too many players have left!");
						stop();
					}
				}
				else {
					Bukkit.getScheduler().cancelTask(id);
					for (UUID uuid : arena.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						Messages.sendTitleMessage(player, ChatColor.YELLOW + "The game will start in "
								+ ChatColor.WHITE + "10 " + ChatColor.YELLOW + "seconds.", null, 5, 60, 5);
						arena.setDefaultInventory(player);
						player.teleport(arena.getMap().getSpawn());
						player.setAllowFlight(false);
					}
					
					arena.setState(GameState.STARTING);
					arena.updateSign();
					
					id = Bukkit.getScheduler().scheduleSyncDelayedTask(OITC.getPlugin(), new Runnable() {
						public void run() {
							arena.setState(GameState.INGAME);
							for (UUID uuid : arena.getPlayers()) {
								Messages.sendTitleMessage(Bukkit.getPlayer(uuid), "Eliminate other players",
										ChatColor.GOLD + "Map: " + arena.getMap().getName() + " / Made by: " + Settings.getInstance().getAuthor(arena), 5, 60, 5);
							}
							
							arena.getBar().setSidebars(arena.getBar().getGameSidebar());
							arena.healPlayers();
							startTimer();
							arena.updateSign();
						}
					}, 200L);
				}
				countdown --;
			}
		}, 0, 20);
	}
	
	public void startTimer() {
		timer = 0;
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(OITC.getPlugin(), new Runnable() {
			public void run() {
				if (timer < Settings.getInstance().getTime(arena)) {
					if (arena.getPlayers().size() < Settings.getInstance().getAutoStartPlayers(arena)) {
						Messages.sendArenaMessage(arena, ChatColor.AQUA + "The game has ended: Too many players have left!");
						Settings.getInstance().setGameStats(arena, false);
						stop();
					}
					if (timer % 10 == 0) {
						for (UUID uuid : arena.getAlivePlayers()) {
							Player player = Bukkit.getPlayer(uuid);
							try {
								player.setCompassTarget(arena.getNearestPlayer(player).getLocation());
							}
							catch (Exception exception) { }
						}
					}
					
					timer ++;
					arena.getBar().setSidebars(arena.getBar().getGameSidebar());
				}
				else {
					Messages.sendArenaMessage(arena, ChatColor.AQUA + "Time is up! The game will be resetted.");
					Settings.getInstance().setGameStats(arena, true);
					stop();
				}
			}
		}, 0L, 20L);
	}
	
	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
		
		if (arena.getState() == GameState.STARTING) {
			for (UUID uuid : arena.getPlayers()) {
				Bukkit.getPlayer(uuid).teleport(Settings.getInstance().getLocation(arena.getID() + ".Lobby"));
				Messages.sendArenaMessage(arena, ChatColor.WHITE + "The countdown got cancelled as too many players have left.");
				arena.setState(GameState.WAITING);
				arena.updateSign();
			}
		}
		
		if (arena.getState() == GameState.INGAME) {
			arena.setState(GameState.RESTARTING);
			arena.healPlayers();
			arena.updateSign();
			for (UUID uuid : arena.getPlayers()) {
				Utils.shootRandomFirework(Bukkit.getPlayer(uuid).getLocation());
			}

			id = Bukkit.getScheduler().scheduleSyncDelayedTask(OITC.getPlugin(), new Runnable() {
				public void run() {
					for (UUID uuid : arena.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						if (player != null) {
							for (Player p : Bukkit.getOnlinePlayers()) {
								p.showPlayer(player);
							}
							arena.clearItems(player);
							arena.getBar().removeSidebar(player);
							arena.getStats(player).reset();
							player.setGameMode(GameMode.ADVENTURE);
							player.teleport(Settings.getInstance().getLocation(arena.getID() + ".Lobby"));
						}
					}

					arena.setMap(Manager.getRandomMap());
					arena.setState(GameState.WAITING);
					arena.updateSign();
					Messages.sendArenaMessage(arena, ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "---" + ChatColor.RESET
							+ ChatColor.GOLD + " Next map: " + arena.getMap().getName() + " " + ChatColor.RESET
							+ ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "---");
					
					if (arena.canStart()) {
						start();
					}
				}
			}, 200L);
		}
	}
}