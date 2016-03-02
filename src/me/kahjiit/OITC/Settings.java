package me.kahjiit.OITC;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.kahjiit.OITC.arena.Arena;
import me.kahjiit.OITC.arena.GameMap;

public class Settings {

	private static Settings instance = new Settings();
	
	private File arenasFile, configFile, playersFile;
	private FileConfiguration arenas, config, players;
	
	public static Settings getInstance() {
		return instance;
	}
	
	public FileConfiguration getArenas() {
		return arenas;
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public FileConfiguration getPlayers() {
		return players;
	}
	
	public void init(Plugin plugin) {
		arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
		configFile = new File(plugin.getDataFolder(), "config.yml");
		playersFile = new File(plugin.getDataFolder(), "players.yml");
		
		try {
			if (!arenasFile.exists()) {
				arenasFile.getParentFile().mkdirs();
				copy(plugin.getResource("arenas.yml"), arenasFile);
			}
			if (!configFile.exists()) {
				configFile.getParentFile().mkdirs();
				copy(plugin.getResource("config.yml"), configFile);
			}
			if (!playersFile.exists()) {
				playersFile.getParentFile().mkdirs();
				copy(plugin.getResource("players.yml"), playersFile);
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		
		arenas = new YamlConfiguration();
		config = new YamlConfiguration();
		players = new YamlConfiguration();
		
		arenas.options().copyDefaults(true);
		config.options().copyDefaults(true);
		players.options().copyDefaults(true);
		
		load();
		setup();
	}
	
	private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } 
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
	
	public void load() {
		try {
			arenas.load(arenasFile);
			config.load(configFile);
			players.load(playersFile);
		}
		catch (Exception exception) {
			Bukkit.getLogger().severe("[OITC] Could not load YML files! Caused by: " + exception.getCause());
		}
	}
	
	public void save() {
		try {
			arenas.save(arenasFile);
			config.save(configFile);
			players.save(playersFile);
		}
		catch (IOException exception) {
			Bukkit.getLogger().severe("[OITC] Could not save YML files! Caused by: " + exception.getCause());
		}
	}
	
	public void setup() {
		try {
			for (String name : arenas.getConfigurationSection("Maps").getKeys(false)) {
				GameMap map = new GameMap(name);
				
				Manager.addMap(map);
				
				for (String index : arenas.getConfigurationSection("Maps." + map.getName() + ".Spawns").getKeys(false)) {
					map.addSpawn(Settings.getInstance().getLocation("Maps." + map.getName() + ".Spawns." + index));
				}
			}
			Bukkit.getLogger().info("[OITC] Finished initializing maps");
		}
		catch (Exception exception) {
			Bukkit.getLogger().severe("[OITC] Failed to load maps!");
		}
		
		try {
			for (String string : config.getConfigurationSection("Arenas").getKeys(false)) {
				Arena arena = new Arena(Integer.parseInt(string));
				
				Manager.addArena(arena);
				arena.updateSign();
			}
			Bukkit.getLogger().info("[OITC] Finished initializing arenas");
		}
		catch (Exception exception) {
			Bukkit.getLogger().severe("[OITC] Failed to load arenas!");
		}
	}
	
	public String getAuthor(Arena arena) {
		return arenas.getString("Maps." + arena.getMap().getName() + ".Author");
	}
	
	public int getAutoStartPlayers(Arena arena) {
		return config.getInt("Arenas." + arena.getID() + ".AutoStartPlayers");
	}
	
	public int getCountdown(Arena arena) {
		return config.getInt("Arenas." + arena.getID() + ".Countdown");
	}
	
	public int getMaxPlayers(Arena arena) {
		return config.getInt("Arenas." + arena.getID() + ".MaxPlayers");
	}
	
	public int getTime(Arena arena) {
		return config.getInt("Arenas." + arena.getID() + ".Time");
	}
	
	public String getKDR(Player player) {
		double kills = players.getInt("Players." + player.getUniqueId() + ".Kills");
		double deaths = players.getInt("Players." + player.getUniqueId() + ".Deaths");
		DecimalFormat df = new DecimalFormat("0.00");
		if (kills == 0.0 && deaths == 0.0) {
			return "0.00";
		}
		if (kills > 0.0 && deaths == 0.0) {
			return df.format(kills);
		}
		if (deaths > 0.0 && kills == 0.0) {
			return "0.00";
		}
		double kdr = kills / deaths;
		return df.format(kdr);
	}
	
	public Map<UUID, Integer> getPlayerScores() {
		Map<UUID, Integer> map = new HashMap<UUID, Integer>();
		for (String player : getPlayers().getConfigurationSection("Players").getKeys(false)) {
			map.put(UUID.fromString(player), getPlayers().getInt("Players." + player + ".XP"));
		}
		return map;
	}
	
	public int getPlayerStat(Player player, String stat) {
		return players.getInt("Players." + player.getUniqueId() + "." + stat);
	}
	
	public int getPlayerStatOffline(OfflinePlayer player, String stat) {
		return players.getInt("Players." + player.getUniqueId() + "." + stat);
	}
	
	public String getWGR(Player player) {
		double games = players.getInt("Players." + player.getUniqueId() + ".Games");
		double wins = players.getInt("Players." + player.getUniqueId() + ".Wins");
		DecimalFormat df = new DecimalFormat("0.00");
		if (games == 0.0) {
			return "0%";
		}
		if (games > 0.0 && wins == 0.0) {
			return "0%";
		}
		if (games == 0.0 && wins > 0.0) {
			return "Inf.";
		}
		if (games == wins) {
			return "100%";
		}
		double wgr = wins / games * 100;
		return df.format(wgr) + "%";
	}
	
	public void setGameStats(Player player, Arena arena, boolean countGames) {
		int kills = arena.getStats(player).getKills();
		int deaths = arena.getStats(player).getDeaths();
		int xp = arena.getStats(player).getXP();
		players.set("Players." + player.getUniqueId() + ".Kills", players.getInt("Players." + player.getUniqueId() + ".Kills") + kills);
		players.set("Players." + player.getUniqueId() + ".Deaths", players.getInt("Players." + player.getUniqueId() + ".Deaths") + deaths);
		if (countGames) {
			players.set("Players." + player.getUniqueId() + ".Games", players.getInt("Players." + player.getUniqueId() + ".Games") + 1);
		}
		players.set("Players." + player.getUniqueId() + ".XP", players.getInt("Players." + player.getUniqueId() + ".XP") + xp);
		save();
	}
	
	public void setGameStats(Arena arena, boolean countGames) {
		for (UUID uuid : arena.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			
			int kills = arena.getStats(player).getKills();
			int deaths = arena.getStats(player).getDeaths();
			int xp = arena.getStats(player).getXP();
			players.set("Players." + player.getUniqueId() + ".Kills", players.getInt("Players." + player.getUniqueId() + ".Kills") + kills);
			players.set("Players." + player.getUniqueId() + ".Deaths", players.getInt("Players." + player.getUniqueId() + ".Deaths") + deaths);
			if (countGames) {
				players.set("Players." + player.getUniqueId() + ".Games", players.getInt("Players." + player.getUniqueId() + ".Games") + 1);
				if (kills >= 20) {
					players.set("Players." + player.getUniqueId() + ".Wins", players.getInt("Players." + player.getUniqueId() + ".Wins") + 1);
				}
			}
			players.set("Players." + player.getUniqueId() + ".XP", players.getInt("Players." + player.getUniqueId() + ".XP") + xp);
		}
		save();
	}

	public Location getLocation(String path) {
		if (arenas.contains(path + ".X")) {
			int x = arenas.getInt(path + ".X");
			int y = arenas.getInt(path + ".Y");
			int z = arenas.getInt(path + ".Z");
			int yaw = arenas.getInt(path + ".Yaw");
			Location location = new Location(Bukkit.getWorld(arenas.getString(path + ".World")), x, y, z, yaw, 0);
			return location;
		}
		return null;
	}
	
	public void setLocation(String path, Location location) {
		arenas.set(path + ".World", location.getWorld().getName());
		arenas.set(path + ".X", (int) location.getX());
		arenas.set(path + ".Y", (int) location.getY());
		arenas.set(path + ".Z", (int) location.getZ());
		arenas.set(path + ".Yaw", (int) location.getYaw());
		arenas.set(path + ".Pitch", 0);
		save();
	}
}