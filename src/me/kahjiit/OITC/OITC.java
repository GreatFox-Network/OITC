package me.kahjiit.OITC;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.kahjiit.OITC.arena.Arena;
import me.kahjiit.OITC.events.BlockEvent;
import me.kahjiit.OITC.events.ChatEvent;
import me.kahjiit.OITC.events.InventoryEvent;
import me.kahjiit.OITC.events.PlayerEvent;
import me.kahjiit.OITC.events.SignEvent;
import me.kahjiit.OITC.stats.Leaderboards;

public class OITC extends JavaPlugin {

	public void onEnable() {
		Settings.getInstance().init(getPlugin());
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new BlockEvent(), getPlugin());
		pm.registerEvents(new ChatEvent(), getPlugin());
		pm.registerEvents(new InventoryEvent(), getPlugin());
		pm.registerEvents(new PlayerEvent(), getPlugin());
		pm.registerEvents(new SignEvent(), getPlugin());
		
		getCommand("leave").setExecutor(CommandManager.getInstance());
		getCommand("oitc").setExecutor(CommandManager.getInstance());
		
		autoUpdate();
	}
	
	public void onDisable() {
		for (Arena arena : Manager.getArenas()) {
			arena.getTimer().stop();
		}
		
		Settings.getInstance().save();
	}
	
	public static Plugin getPlugin() {
		return Bukkit.getServer().getPluginManager().getPlugin("OITC");
	}
	
	public void autoUpdate() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(OITC.getPlugin(), new Runnable() {
			public void run() {
				Settings.getInstance().load();
				new Leaderboards(Settings.getInstance().getPlayerScores()).updateLeaderboards();
			}
		}, 0, 6000);
	}
}