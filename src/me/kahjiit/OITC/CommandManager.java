package me.kahjiit.OITC;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kahjiit.OITC.commands.AddSpawn;
import me.kahjiit.OITC.commands.CreateArena;
import me.kahjiit.OITC.commands.CreateMap;
import me.kahjiit.OITC.commands.Join;
import me.kahjiit.OITC.commands.Leave;
import me.kahjiit.OITC.commands.Reload;
import me.kahjiit.OITC.commands.SetLobby;
import me.kahjiit.OITC.commands.SetMainLobby;
import me.kahjiit.OITC.commands.Start;
import me.kahjiit.OITC.commands.SubCommand;
import me.kahjiit.OITC.commands.Update;
import me.kahjiit.OITC.commands.Version;

public class CommandManager implements CommandExecutor {

	private static CommandManager instance = new CommandManager();

	public static CommandManager getInstance() {
		setup();
		return instance;
	}
	
	private static List<SubCommand> commands = new ArrayList<SubCommand>();
	
	private static void setup() {
		commands.add(new AddSpawn());
		commands.add(new CreateArena());
		commands.add(new CreateMap());
		commands.add(new Join());
		commands.add(new Leave());
		commands.add(new Reload());
		commands.add(new SetLobby());
		commands.add(new SetMainLobby());
		commands.add(new Start());
		commands.add(new Update());
		commands.add(new Version());
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players may use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (label.equalsIgnoreCase("oitc")) {
			if (args.length == 0) {
				player.sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "---" + ChatColor.RESET
						+ ChatColor.GOLD + ChatColor.BOLD.toString() + " OITC " + ChatColor.RESET
						+ ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "---");
				
				player.sendMessage(ChatColor.YELLOW + " /oitc join <arena>: " + ChatColor.GRAY + getCommand("join").description());
				player.sendMessage(ChatColor.YELLOW + " /oitc leave: " + ChatColor.GRAY + getCommand("leave").description());
				player.sendMessage(ChatColor.YELLOW + " /oitc version: " + ChatColor.GRAY + getCommand("version").description());
				if (player.hasPermission("oitc.admin")) {
					player.sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "-" + ChatColor.RESET
							+ ChatColor.GOLD + ChatColor.BOLD.toString() + " Admin Commands");
					
					player.sendMessage(ChatColor.YELLOW + " /oitc addspawn <map>: " + ChatColor.GRAY + getCommand("addspawn").description());
					player.sendMessage(ChatColor.YELLOW + " /oitc createarena <id>: " + ChatColor.GRAY + getCommand("createarena").description());
					player.sendMessage(ChatColor.YELLOW + " /oitc createmap <name>: " + ChatColor.GRAY + getCommand("createmap").description());
					player.sendMessage(ChatColor.YELLOW + " /oitc reload: " + ChatColor.GRAY + getCommand("reload").description());
					player.sendMessage(ChatColor.YELLOW + " /oitc setlobby <arena>: " + ChatColor.GRAY + getCommand("setlobby").description());
					player.sendMessage(ChatColor.YELLOW + " /oitc setmainlobby: " + ChatColor.GRAY + getCommand("setmainlobby").description());
					player.sendMessage(ChatColor.YELLOW + " /oitc start <arena>: " + ChatColor.GRAY + getCommand("start").description());
					player.sendMessage(ChatColor.YELLOW + " /oitc update: " + ChatColor.GRAY + getCommand("update").description());
				}
				player.sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "----------------");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("help")) {
				Bukkit.dispatchCommand(player, "oitc");
				return true;
			}
			
			SubCommand cmd = getCommand(args[0]);
			
			if (cmd == null) {
				return true;
			}
			
			try {
				cmd.onCommand(player, args);
			}
			catch (Exception exception) {
				Messages.sendPlayerMessage(player, ChatColor.RED + "An error has occured: " + exception.getCause());
				exception.printStackTrace();
			}
		}
		
		if (label.equalsIgnoreCase("leave")) {
			Bukkit.dispatchCommand(player, "oitc leave");
		}
		
		return true;
	}
	
	private SubCommand getCommand(String name) {
		for (SubCommand sc : commands) {
			if (sc.name().equalsIgnoreCase(name)) {
				return sc;
			}
		}
		return null;
	}
}