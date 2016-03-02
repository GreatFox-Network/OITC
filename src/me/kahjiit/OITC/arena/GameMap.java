package me.kahjiit.OITC.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.kahjiit.OITC.Settings;

public class GameMap {
	
	private final String name;
	
	private Arena arena;
	private List<Location> spawns = new ArrayList<Location>();
	
	public GameMap(String name) {
		this.name = name;
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Location> getSpawns() {
		return spawns;
	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}
	
	public void addSpawn(Location location) {
		Settings.getInstance().setLocation("Maps." + getName() + ".Spawns." + (spawns.size() + 1), location);

		if (!spawns.contains(location)) {
			spawns.add(location);
		}
	}
	
	public Location getSpawn() {
		Random r = new Random();
		int index = r.nextInt(spawns.size());
		return spawns.get(index);
	}
	
	public void spawnPlayers() {
		for (UUID uuid : arena.getPlayers()) {
			Bukkit.getPlayer(uuid).teleport(getSpawn());
		}
	}
}