package me.kahjiit.OITC;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.kahjiit.OITC.arena.Arena;

public class Utils {
	
	public static void openPlayerHeadsInventory(Player player, Arena arena) {
		Inventory inv = Bukkit.createInventory(null, 18, "Living Players");
		
		for (UUID uuid : arena.getAlivePlayers()) {
			Player p = Bukkit.getPlayer(uuid);
			
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta headMeta = (SkullMeta) head.getItemMeta();
			
			headMeta.setDisplayName(ChatColor.YELLOW + p.getName());
			headMeta.setOwner(p.getName());
			
			head.setItemMeta(headMeta);
			
			inv.addItem(head);
		}
		
		player.openInventory(inv);
	}
	
	public static void shootRandomFirework(Location location) {
		Firework fw = (Firework) location.getWorld().spawn(location, Firework.class);
		FireworkMeta meta = fw.getFireworkMeta();
		Random random = new Random();
		int type = random.nextInt(5) + 1;
		Type fireworkType = null;
		
		switch (type) {
		case 1:
			fireworkType = Type.BALL;
			break;
		case 2:
			fireworkType = Type.CREEPER;
			break;
		case 3:
			fireworkType = Type.STAR;
			break;
		case 4:
			fireworkType = Type.BURST;
			break;
		case 5:
			fireworkType = Type.BALL_LARGE;
		}
		
		int color = random.nextInt(6) + 1;
		Color fireworkColor = null;
		
		switch (color) {
		case 1:
			fireworkColor = Color.RED;
			break;
		case 2:
			fireworkColor = Color.BLUE;
			break;
		case 3: 
			fireworkColor = Color.GREEN;
			break;
		case 4:
			fireworkColor = Color.YELLOW;
			break;
		case 5:
			fireworkColor = Color.ORANGE;
			break;
		case 6:
			fireworkColor = Color.PURPLE;
			break;
		}
		
		meta.addEffect(FireworkEffect.builder().flicker(random.nextBoolean()).trail(random.nextBoolean())
				.withColor(fireworkColor).with(fireworkType).build());
		fw.setFireworkMeta(meta);
	}
}