package me.danieli1818.floo_network.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface SubCommandsExecutor {

	public boolean onCommand(CommandSender sender, String command, String label, String[] args);
	
}
