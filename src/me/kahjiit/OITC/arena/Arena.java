package me.kahjiit.OITC.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.kahjiit.OITC.Manager;
import me.kahjiit.OITC.Messages;
import me.kahjiit.OITC.Settings;

public class Arena {

	private final int arenaID;
	
	private GameMap map;
	private GameState state;
	private Sidebar bar;
	private Timer timer;
	private Map<UUID, PlayerStats> players;
	
	public Arena(int id) {
		this.arenaID = id;
		bar = new Sidebar(this);
		players = new HashMap<UUID, PlayerStats>();
		state = GameState.WAITING;
		timer = new Timer(this);
		
		setMap(Manager.getRandomMap());
	}
	
	public List<UUID> getAlivePlayers() {
		List<UUID> list = new ArrayList<UUID>();
		for (UUID uuid : getPlayers()) {
			if (getStats(Bukkit.getPlayer(uuid)).isAlive()) {
				list.add(uuid);
			}
		}
		return list;
	}
	
	public Sidebar getBar() {
		return bar;
	}
	
	public int getID() {
		return arenaID;
	}
	
	public GameMap getMap() {
		return map;
	}
	
	public List<UUID> getPlayers() {
		List<UUID> list = new ArrayList<UUID>();
		for (UUID uuid : players.keySet()) {
			list.add(uuid);
		}
		return list;
	}
	
	public GameState getState() {
		return state;
	}
	
	public Timer getTimer() {
		return timer;
	}
	
	public void setMap(GameMap map) {
		try {
			if (this.map != null) {
				this.map.setArena(null);
			}
			this.map = map;
			map.setArena(this);
		}
		catch (Exception exception) {
			Bukkit.getLogger().warning("[OITC] No available map could have been found for arena OITC" + getID());
		}
	}
	
	public void setState(GameState state) {
		this.state = state;
	}
	
	public void addPlayer(Player player) {
		if (!players.containsKey(player.getUniqueId())) {
			players.put(player.getUniqueId(), new PlayerStats());
			Manager.addPlayer(player, this);
			
			if (Settings.getInstance().getArenas().contains(getID() + ".Lobby")) {
				player.teleport(Settings.getInstance().getLocation(getID() + ".Lobby"));
			}
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.showPlayer(player);
			}
			
			bar.removeSidebar(player);
			clearItems(player);
			player.setAllowFlight(false);
			player.setHealth(20);
			player.setFoodLevel(20);
			updateSign();
			
			if (canStart() && getPlayers().size() == Settings.getInstance().getAutoStartPlayers(this)) {
				timer.start();
			}
		}
	}
	
	public void removePlayer(Player player) {
		if (players.containsKey(player.getUniqueId())) {
			if (Settings.getInstance().getArenas().contains("Main.Lobby")) {
				player.teleport(Settings.getInstance().getLocation("Main.Lobby"));
			}
			
			if (getState() == GameState.INGAME || getState() == GameState.STARTING) {
				Messages.sendArenaMessage(this, ChatColor.WHITE + player.getName() + ChatColor.GRAY + " has left the game. "
								+ Messages.alivePlayersSize(this, Settings.getInstance().getMaxPlayers(this)));
				
				if (getPlayers().size() < Settings.getInstance().getAutoStartPlayers(this)) {
					Settings.getInstance().setGameStats(player, this, true);
					Settings.getInstance().save();
					Messages.sendArenaMessage(this, ChatColor.AQUA + "The game has been cancelled because too many players have left!");
					
					timer.stop();
				}
			}
			
			clearItems(player);
			getStats(player).setAlive(false);
			player.setGameMode(GameMode.ADVENTURE);
			player.setScoreboard(bar.getLobbySidebar(player));
			
			players.remove(player.getUniqueId());
			Manager.removePlayer(player);
			updateSign();
		}
	}
	
	public void addArrow(Player player) {
		ItemStack arrow = new ItemStack(Material.ARROW, 1);
		player.getInventory().addItem(arrow);
	}
	
	public boolean canStart() {
		if (getState() == GameState.WAITING && getPlayers().size() >= Settings.getInstance().getAutoStartPlayers(this)) {
			return true;
		}
		return false;
	}
	
	public void clearItems(Player player) {
		player.getInventory().clear();
		player.getInventory().setBoots(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setHelmet(null);
		player.getInventory().setLeggings(null);
	}
	
	public Player getNearestPlayer(Player player) {
		double distance = 10000000.0D;
		Player nearestPlayer = null;
		for (UUID uuid : getAlivePlayers()) {
			Player target = Bukkit.getPlayer(uuid);
			if ((target != null) && (target != player)
					&& (player.getLocation().distance(target.getLocation()) < distance)) {
				distance = player.getLocation().distance(target.getLocation());
				nearestPlayer = target;
			}
		}
		return nearestPlayer;
	}
	
	public PlayerStats getStats(Player player) {
		if (players.containsKey(player.getUniqueId())) {
			return players.get(player.getUniqueId());
		}
		return null;
	}
	
	public void healPlayers() {
		for (UUID uuid : getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				player.setHealth(20);
				player.setFoodLevel(20);
				player.setSaturation(10);
			}
		}
	}
	
	public void setDefaultInventory(Player player) {
		clearItems(player);
		
		ItemStack bow = new ItemStack(Material.BOW, 1);
		ItemStack sword = new ItemStack(Material.WOOD_SWORD, 1);
		ItemStack arrow = new ItemStack(Material.ARROW, 1);
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		
		ItemMeta compassMeta = compass.getItemMeta();
		
		compassMeta.setDisplayName("Nearest Player");
		
		compass.setItemMeta(compassMeta);
		
		if (player.hasPermission("oitc.perks")) {
			bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
			sword.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
		}
		
		player.getInventory().setItem(0, bow);
		player.getInventory().setItem(1, sword);
		player.getInventory().setItem(2, arrow);
		player.getInventory().setItem(8, compass);
		player.getInventory().setHelmet(helmet);
	}
	
	public void setSpectatorInventory(Player player) {
		clearItems(player);
		
		ItemStack bed = new ItemStack(Material.BED, 1);
		ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
		ItemMeta bedMeta = bed.getItemMeta();
		ItemMeta pearlMeta = pearl.getItemMeta();
		
		bedMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Retire");
		pearlMeta.setDisplayName(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Teleport");
		
		bed.setItemMeta(bedMeta);
		pearl.setItemMeta(pearlMeta);
		
		player.getInventory().setItem(4, pearl);
		player.getInventory().setItem(8, bed);
	}
	
	public void updateSign() {
		if (Settings.getInstance().getArenas().contains(getID() + ".Sign")) {
			if (Settings.getInstance().getLocation(getID() + ".Sign").getBlock().getState() instanceof Sign) {
				Sign sign = (Sign) Settings.getInstance().getLocation(getID() + ".Sign").getBlock().getState();
				sign.setLine(1, getMap().getName());
				sign.setLine(3, ChatColor.DARK_BLUE.toString() + players.size() + "/" + Settings.getInstance().getMaxPlayers(this));
				
				if (getState() == GameState.INGAME) {
					sign.setLine(0, ChatColor.RED + ChatColor.BOLD.toString() + "[IN-GAME]");
				}
				if (getState() == GameState.RESTARTING) {
					sign.setLine(0, ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "[RESTARTING]");
				}
				if (getState() == GameState.STARTING) {
					sign.setLine(0, ChatColor.GOLD + ChatColor.BOLD.toString() + "[STARTING]");
				}
				if (getState() == GameState.WAITING) {
					sign.setLine(0, ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "[WAITING]");
				}
				sign.update();
			}
		}
	}
}