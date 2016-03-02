package me.kahjiit.OITC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.kahjiit.OITC.arena.Arena;
import me.kahjiit.OITC.arena.GameMap;

public class Manager {

	private static Map<Integer, Arena> arenas = new HashMap<Integer, Arena>();
	private static Map<String, GameMap> maps = new HashMap<String, GameMap>();
	private static Map<UUID, Arena> players = new HashMap<UUID, Arena>();
	
	public static void addArena(Arena arena) {
		if (!arenas.containsKey(arena.getID())) {
			arenas.put(arena.getID(), arena);
		}
	}
	
	public static boolean arenaExists(int id) {
		return arenas.containsKey(id);
	}
	
	public static Arena getArena(int id) {
		if (arenas.containsKey(id)) {
			return arenas.get(id);
		}
		return null;
	}
	
	public static List<Arena> getArenas() {
		List<Arena> list = new ArrayList<Arena>();
		for (int id : arenas.keySet()) {
			list.add(getArena(id));
		}
		return list;
	}
	
	public static void removeArena(Arena arena) {
		if (arenas.containsKey(arena.getID())) {
			arenas.remove(arena);
		}
	}
	
	public static void addMap(GameMap map) {
		if (!maps.containsKey(map.getName())) {
			maps.put(map.getName(), map);
		}
	}
	
	public static boolean mapExists(String name) {
		return maps.containsKey(name);
	}
	
	public static GameMap getMap(String name) {
		if (maps.containsKey(name)) {
			return maps.get(name);
		}
		return null;
	}
	
	public static List<GameMap> getMaps() {
		List<GameMap> list = new ArrayList<GameMap>();
		for (String name : maps.keySet()) {
			list.add(getMap(name));
		}
		return list;
	}
	
	public static GameMap getRandomMap() {
		Random r = new Random();
		int value;
		
		do {
			value = r.nextInt(getMaps().size());
		}
		while (isInUse(getMaps().get(value)));
		
		return getMaps().get(value);
	}
	
	public static boolean isInUse(GameMap map) {
		for (Arena arena : getArenas()) {
			if (arena.getMap() == map) {
				return true;
			}
		}
		return false;
	}
	
	public static void removeMap(GameMap map) {
		if (arenas.containsKey(map.getName())) {
			arenas.remove(map);
		}
	}
	
	public static void addPlayer(Player player, Arena arena) {
		if (!players.containsKey(player.getUniqueId())) {
			players.put(player.getUniqueId(), arena);
		}
	}
	
	public static Arena getArena(Player player) {
		if (players.containsKey(player.getUniqueId())) {
			return players.get(player.getUniqueId());
		}
		return null;
	}
	
	public static List<UUID> getPlayers() {
		List<UUID> list = new ArrayList<UUID>();
		for (UUID uuid : players.keySet()) {
			list.add(uuid);
		}
		return list;
	}
	
	public static boolean isPlaying(Player player) {
		return players.containsKey(player.getUniqueId());
	}
	
	public static void removePlayer(Player player) {
		if (players.containsKey(player.getUniqueId())) {
			players.remove(player.getUniqueId());
		}
	}
}