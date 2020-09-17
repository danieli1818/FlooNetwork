package me.danieli1818.floo_network.commands.setcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;

import me.danieli1818.floo_network.commands.SubCommandsExecutor;
import me.danieli1818.floo_network.stations.FlooNetworkStation;
import me.danieli1818.floo_network.stations.FlooNetworkStationsManager;

public class SetCommands implements SubCommandsExecutor {

	private static WorldEditPlugin wep = (WorldEditPlugin)Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
	
	public SetCommands() {}
	
	@Override
	public boolean onCommand(CommandSender sender, String command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("Invalid Command! Try using /help for help.");
			return false;
		}
		String setOption = args[0];
		System.out.println(setOption);
		if (setOption.equals("region")) {
			return onSetRegionCommand(sender, args);
		} else if (setOption.equals("spawn")) {
			return setSpawnLocation(sender, args);
		}
		sender.sendMessage("Invalid Command! Try using /help for help.");
		return false;
	}
	
	public boolean onSetRegionCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission("floonetwork.set.region")) {
			sender.sendMessage("You don't have permissions to run this command!");
			return false;
		}
		if (args.length != 2) {
			sender.sendMessage("Invalid Command! Try using /help for help.");
			return false;
		}
		Player player = (Player)sender;
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to run this command!");
			return false;
		}
		String stationID = args[1];
		FlooNetworkStation station = FlooNetworkStationsManager.getInstance().getStation(stationID);
		if (station == null) {
			sender.sendMessage("Station doesn't exist!");
			return false;
		}
		LocalSession session = wep.getSession(player);
		try {
			Region region = session.getSelection(session.getSelectionWorld());
			station.setRegion(region);
			player.sendMessage("Successfully set region!");
		} catch (IncompleteRegionException e) {
			player.sendMessage("Error! Invalid Region Selected!");
			return false;
		}
		return true;
	}
	
	public boolean setSpawnLocation(CommandSender sender, String[] args) {
		if (!sender.hasPermission("floonetwork.set.spawnlocation")) {
			sender.sendMessage("You don't have permissions to run this command!");
			return false;
		}
		if (args.length != 2) {
			sender.sendMessage("Invalid Command! Try using /help for help.");
			return false;
		}
		Player player = (Player)sender;
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to run this command!");
			return false;
		}
		String stationID = args[1];
		FlooNetworkStation station = FlooNetworkStationsManager.getInstance().getStation(stationID);
		if (station == null) {
			sender.sendMessage("Station doesn't exist!");
			return false;
		}
		station.setSpawnLocation(player.getLocation());
		player.sendMessage("Successfully set spawn location!");
		return true;
	}
	
}
