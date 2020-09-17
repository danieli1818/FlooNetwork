package me.danieli1818.floo_network.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;

import me.danieli1818.floo_network.commands.setcommands.SetCommands;
import me.danieli1818.floo_network.stations.FlooNetworkStationsManager;

public class FlooNetworkCommands implements CommandExecutor {

	private static WorldEditPlugin wep = (WorldEditPlugin)Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
	
	private static SetCommands setCommands;
	
	private static FlooNetworkCommands instance;
	
	private FlooNetworkCommands() {
		this.setCommands = new SetCommands();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			onHelpCommand(sender, 1);
			return true;
		}
		String subCommand = args[0];
		if (subCommand.equals("create")) {
			onCreateCommand(sender, args);
		} else if (subCommand.equals("set")) {
			String[] arguments = new String[args.length - 1];
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = args[i + 1];
			}
			return setCommands.onCommand(sender, subCommand, subCommand, arguments);
		}
		return false;
	}
	
	public void onHelpCommand(CommandSender sender, int page) {
		
	}
	
	public boolean onCreateCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission("floonetwork.create")) {
			sender.sendMessage("You don't have permission to run this command!");
			return false;
		}
		if (args.length < 3) {
			sender.sendMessage("Error, Invalid Arguments! Try using the help command for help.");
			return false;
		}
		String id = args[1];
		String stationName = args[2];
		for (int i = 3; i < args.length; i++) {
			stationName += " " + args[i];
		}
		List<String> stationNames = new ArrayList<String>();
		stationNames.add(stationName);
		if (FlooNetworkStationsManager.getInstance().createStation(id, stationNames)) {
			sender.sendMessage("Successfully created floo station!");
		} else {
			sender.sendMessage("Error! A station with this id already exists!");
			return false;
		}
		return true;
	}
	
	public static FlooNetworkCommands getInstance() {
		if (instance == null) {
			instance = new FlooNetworkCommands();
		}
		return instance;
	}

}
