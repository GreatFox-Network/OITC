package me.kahjiit.OITC.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.OITC;
import me.kahjiit.OITC.Settings;
import me.kahjiit.OITC.Utils;
import me.kahjiit.OITC.arena.Arena;
import me.kahjiit.OITC.arena.GameState;
import me.kahjiit.OITC.arena.Sidebar;
import me.kahjiit.OITC.stats.Leaderboards;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class PlayerEvent implements Listener {

	int kills, id;
	Sidebar bar = new Sidebar(null);
	
	@EventHandler
	public void onArrowHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			
			if (arrow.getShooter() instanceof Player) {
				Player player = (Player) event.getEntity();
				Player shooter = (Player) arrow.getShooter();
				
				if (Manager.isPlaying(player) && Manager.isPlaying(shooter)) {
					if (player == shooter) {
						event.setCancelled(true);
						return;
					}
					
					event.setDamage(40);
					arrow.remove();
				}
			}
		}
	}
	
	@EventHandler
	public void onBowShot(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player player = (Player) event.getEntity().getShooter();
			
			if (!Manager.isPlaying(player)) {
				return;
			}
			
			if (event.getEntity().getType() == EntityType.ENDER_PEARL) {
				event.setCancelled(true);
			}
			
			if (Manager.getArena(player).getState() == GameState.STARTING) {
				player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		if (Manager.isPlaying(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPaintingBreak(HangingBreakByEntityEvent event) {
		if (!(event.getRemover() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getRemover();
		
		if (!Manager.isPlaying(player)) {
			return;
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			if (!Manager.isPlaying(player)) {
				return;
			}
			
			if (Manager.getArena(player).getState() != GameState.INGAME) {
				event.setCancelled(true);
			}
			
			if (event.getCause() == DamageCause.FALL) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player) && !(event.getDamager() instanceof Player)) {
			return;
		}
		if (event.getDamager() instanceof Arrow) return; //Stubborn event handler
		
		Player player = (Player) event.getEntity();
		Player damager = (Player) event.getDamager();
		
		if (!Manager.isPlaying(player) || !Manager.isPlaying(damager)) {
			return;
		}
		
		if (!Manager.getArena(damager).getStats(damager).isAlive()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		if (!Manager.isPlaying(player)) {
			return;
		}
		
		Arena arena = Manager.getArena(player);
		
		event.getDrops().clear();
		event.setDeathMessage("");
		event.setDroppedExp(0);
		
		player.getWorld().createExplosion(player.getLocation(), 0F);
		player.getInventory().clear();
		
		id = Bukkit.getScheduler().scheduleSyncDelayedTask(OITC.getPlugin(), new Runnable() {
			public void run() {
				((CraftPlayer) player).getHandle().playerConnection.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
			}
		}, 5L);
		
		if (player.getKiller() != null) {
			player.getKiller().setHealth(20);
			if (event.getEntity().getLastDamageCause().getCause() == DamageCause.ENTITY_ATTACK) {
				onPlayerKill(player, player.getKiller(), "stabbed");
			}
			if (event.getEntity().getLastDamageCause().getCause() == DamageCause.PROJECTILE) {
				onPlayerKill(player, player.getKiller(), "shot");
			}
		}
		else {
			arena.getStats(player).addDeath();
			arena.getStats(player).resetStreak();
			Messages.sendArenaMessage(arena, ChatColor.RED + player.getName() + quickStats(arena, player) + ChatColor.AQUA + " has committed suicide.");
			
			if (arena.getStats(player).getLives() <= 0) {
				onPlayerNoLives(player);
				if (player.getKiller() != null) {
					player.teleport(player.getKiller());
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		if (!Manager.isPlaying((Player) event.getEntity())) {
			return;
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (!Manager.isPlaying(player)) {
			return;
		}
		
		if ((event.getAction() == Action.PHYSICAL) && (event.getClickedBlock().getType() == Material.SOIL)) {
			event.setCancelled(true);
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if (Settings.getInstance().getArenas().contains("Main.Lobby")) {
			player.teleport(Settings.getInstance().getLocation("Main.Lobby"));
		}
		
		if (!Settings.getInstance().getPlayers().contains("Players." + player.getUniqueId())) {
			Settings.getInstance().getPlayers().set("Players." + player.getUniqueId() + ".Kills", 0);
			Settings.getInstance().getPlayers().set("Players." + player.getUniqueId() + ".Deaths", 0);
			Settings.getInstance().getPlayers().set("Players." + player.getUniqueId() + ".Games", 0);
			Settings.getInstance().getPlayers().set("Players." + player.getUniqueId() + ".Wins", 0);
			Settings.getInstance().getPlayers().set("Players." + player.getUniqueId() + ".Level", 1);
			Settings.getInstance().getPlayers().set("Players." + player.getUniqueId() + ".XP", 0);
			Settings.getInstance().save();
		}
		
		player.setLevel(Settings.getInstance().getPlayerStat(player, "Level"));
		player.setScoreboard(bar.getLobbySidebar(player));
	}
	
	public void onPlayerKill(Player player, Player killer, String reason) {
		Arena arena = Manager.getArena(player);
		
		arena.getStats(killer).addKill();
		arena.getStats(killer).addStreak();
		arena.getStats(player).addDeath();
		arena.getStats(killer).addXP(100);
		killer.setHealth(20);
		killer.giveExp(1);
		
		Messages.sendArenaMessage(arena, ChatColor.RED + player.getName() + quickStats(arena, player) + ChatColor.AQUA + " has been " + reason
				+ " by " + ChatColor.RED + killer.getName() + quickStats(arena, killer) + ChatColor.AQUA + " ! +100 XP");
		
		if (arena.getStats(player).getStreak() > 0) {
			if (arena.getStats(player).getStreak() >= 3) {
				Messages.sendArenaMessage(arena, ChatColor.RED + killer.getName() + ChatColor.AQUA
						+ " has broken the killstreak of " + ChatColor.RED + player.getName() + ChatColor.AQUA + "!");
			}
			arena.getStats(player).resetStreak();
		}
		if (arena.getStats(killer).getStreak() >= 3) {
			Messages.sendArenaMessage(arena, ChatColor.RED + killer.getName() + ChatColor.AQUA + " is on a " + ChatColor.GOLD
					+ arena.getStats(killer).getStreak() + ChatColor.AQUA + " killstreak!");
		}
		
		arena.addArrow(killer);
		
		Scoreboard sb = killer.getScoreboard();
		
		if (killer.hasPermission("oitc.perks")) {
			Score score = sb.getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.GREEN + killer.getName());
			kills = score.getScore() + 1;
			score.setScore(kills);
		}
		else {
			Score score = sb.getObjective(DisplaySlot.SIDEBAR).getScore(killer.getName());
			kills = score.getScore() + 1;
			score.setScore(kills);
		}
		
		if (arena.getStats(player).getLives() <= 0) {
			onPlayerNoLives(player);
			if (player.getKiller() != null) {
				player.teleport(player.getKiller());
			}
		}
	}
	
	private String quickStats(Arena arena, Player player) {
		String s = ChatColor.WHITE + " [" + ChatColor.GREEN + arena.getStats(player).getKills() + ChatColor.WHITE + "/" + ChatColor.RED
				+ arena.getStats(player).getDeaths() + ChatColor.WHITE + "]";
		return s;
	}
	
	@EventHandler
	public void onPlayerLevelUp(PlayerLevelChangeEvent event) {
		Player player = event.getPlayer();
		
		if (!Manager.isPlaying(player)) {
			return;
		}
		
		Arena arena = Manager.getArena(player);
		
		Utils.shootRandomFirework(player.getLocation());
		Settings.getInstance().getPlayers().set("Players." + player.getUniqueId() + ".Level", Settings.getInstance().getPlayerStat(player, "Level") + 1);
		Settings.getInstance().save();
		
		for (UUID uuid : arena.getPlayers()) {
			Player p = Bukkit.getPlayer(uuid);
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 0, 0);
		}
		
		Messages.sendArenaMessage(arena, ChatColor.RED + player.getName() + ChatColor.AQUA + " has leveled up to level "
				+ ChatColor.GOLD + Settings.getInstance().getPlayerStat(player, "Level") + ChatColor.AQUA + " !");
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if (!Manager.isPlaying(player)) {
			return;
		}
		
		Arena arena = Manager.getArena(player);
		
		if (arena.getState() == GameState.INGAME) {
			try {
				if (!arena.getStats(player).isAlive() && player.getLocation().distance(arena.getNearestPlayer(player).getLocation()) >= 150) {
					player.teleport(arena.getNearestPlayer(player));
					player.sendMessage(ChatColor.RED + "Don't fly out of the arena!");
				}
				
				if (!arena.getStats(player).isAlive()) {
					return;
				}
				
				/*
				 * IMPORTANT
				 * If the following materials are found inside a map they should be removed or replaced:
				 * Barrier, Water
				 * Needs modifying to prevent interference with maps
				 */
				
				if (player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
					if (player.getHealth() > 0) player.setHealth(0);
				}
				
				if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BARRIER) {
					if (player.getHealth() > 0) player.setHealth(0);
				}
			}
			catch (Exception exception) { }
		}
		
		if (arena.getState() != GameState.STARTING) {
			return;
		}
		
		if (event.getTo().getX() == event.getFrom().getX() || event.getTo().getZ() == event.getFrom().getZ()) {
			return;
		}
		
		player.teleport(player.getLocation());
	}
	
	public void onPlayerNoLives(Player player) {
		Arena arena = Manager.getArena(player);
		
		arena.getStats(player).setAlive(false);
		arena.setSpectatorInventory(player);
		player.setGameMode(GameMode.CREATIVE);
		
		for (UUID uuid : arena.getPlayers()) {
			Bukkit.getPlayer(uuid).hidePlayer(player);
		}
		
		Messages.sendArenaMessage(arena, ChatColor.RED + player.getName() + quickStats(arena, player) + ChatColor.AQUA
				+ " has been eliminated! " + Messages.alivePlayersSize(arena, arena.getPlayers().size()));
		
		if (arena.getAlivePlayers().size() <= 1) {
			Map<UUID, Integer> players = new HashMap<UUID, Integer>();
			for (UUID uuid : arena.getPlayers()) {
				players.put(uuid, arena.getStats(Bukkit.getPlayer(uuid)).getKills());
				arena.clearItems(Bukkit.getPlayer(uuid));
			}
			
			Leaderboards lead = new Leaderboards(players);
			Player first = Bukkit.getPlayer(lead.getPlayerFromRank(1));
			Player second = Bukkit.getPlayer(lead.getPlayerFromRank(2));
			Player third = Bukkit.getPlayer(lead.getPlayerFromRank(3));
			
			arena.getStats(first).addXP(500);
			first.giveExp(5);
			Settings.getInstance().getPlayers().set("Players." + first.getUniqueId() + ".Wins",
					Settings.getInstance().getPlayers().getInt("Players." + first.getUniqueId() + ".Wins") + 1);
			
			Messages.sendArenaMessage(arena, ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "---------------- "
					+ ChatColor.GOLD + " " + arena.getMap().getName() + ChatColor.GOLD + " " + ChatColor.STRIKETHROUGH + " ----------------");
			Messages.sendArenaMessage(arena, ChatColor.GOLD + "The match has ended!");
			Messages.sendArenaMessage(arena, ChatColor.AQUA.toString() + "1st: " + first.getName() + " - " + ChatColor.GOLD
							+ arena.getStats(first).getKills() + ChatColor.AQUA + " Kills +500 XP");
			
			//If the other players are null for some reason
			if (second != null) {
				arena.getStats(second).addXP(300);
				second.giveExp(3);
				Messages.sendArenaMessage(arena, ChatColor.YELLOW + "2nd: " + second.getName() + " - " + ChatColor.GOLD
								+ arena.getStats(second).getKills() + ChatColor.YELLOW + " Kills +300 XP");
			}
			else {
				Messages.sendArenaMessage(arena, ChatColor.WHITE + "2nd:");
			}
			
			if (third != null) {
				arena.getStats(third).addXP(200);
				third.giveExp(2);
				Messages.sendArenaMessage(arena, ChatColor.WHITE + "3rd: " + third.getName() + " - " + ChatColor.GOLD
								+ arena.getStats(third).getKills() + ChatColor.WHITE + " Kills +200 XP");
			}
			else {
				Messages.sendArenaMessage(arena, ChatColor.WHITE + "3rd:");
			}
			
			Messages.sendArenaMessage(arena, ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------------");
			
			Settings.getInstance().setGameStats(arena, true);
			arena.getTimer().stop();
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		
		if (!Manager.isPlaying(player)) {
			return;
		}
		
		if (event.getItem().getItemStack().getType() == Material.ARROW) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if (!Manager.isPlaying(player)) {
			return;
		}
		
		Arena arena = Manager.getArena(player);
		
		Settings.getInstance().setGameStats(player, arena, true);
		arena.removePlayer(player);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		if (!Manager.isPlaying(player)) {
			return;
		}
		
		Arena arena = Manager.getArena(player);
		
		if (!arena.getStats(player).isAlive()) {
			event.setRespawnLocation(arena.getNearestPlayer(player).getLocation());
			Messages.sendTitleMessage(player, ChatColor.RED + "Game Over!", ChatColor.RED + "You don't have any lives left!", 5, 60, 5);
			return;
		}
		
		event.setRespawnLocation(arena.getMap().getSpawn());
		arena.setDefaultInventory(player);
		
		player.sendMessage(ChatColor.AQUA + "You are on " + ChatColor.RED + arena.getStats(player).getKills()
						+ ChatColor.AQUA + " kills and have " + ChatColor.GOLD + arena.getStats(player).getLives() + ChatColor.AQUA + " lives left.");
	}
}