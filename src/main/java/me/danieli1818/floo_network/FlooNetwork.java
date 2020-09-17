package me.danieli1818.floo_network;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import me.danieli1818.floo_network.commands.FlooNetworkCommands;
import me.danieli1818.floo_network.listeners.FlooNetworkListener;
import me.danieli1818.floo_network.stations.FlooNetworkStationsManager;
import me.danieli1818.floo_network.utils.scheduler.Scheduler;
import me.danieli1818.floo_network.utils.serialization.SavingAndLoadingUtils;

public class FlooNetwork extends JavaPlugin {

	private File flooStationsConfigFile;
	private FileConfiguration flooStationsConfig;
	
	@Override
	public void onEnable() {
		
		if (getWorldEditPlugin() == null) {
			System.out.println("WorldEdit Plugin Is Missing!");
			return;
		}
		
		Scheduler.getInstance(this);
		
		SavingAndLoadingUtils.registerConfigurationSerializables();
		
		createStationsConfig();
		
		FlooNetworkStationsManager.getInstance().reloadStations();
		
		getCommand("floonetwork").setExecutor(FlooNetworkCommands.getInstance());
		
		getServer().getPluginManager().registerEvents(new FlooNetworkListener(), this);
		
		System.out.println("Plugin has been successfully loaded!!!!");
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		super.onDisable();
		
		FlooNetworkStationsManager.getInstance().saveStations();
		
		System.out.println("Plugin has been disabled.");
	}
	
	private WorldEditPlugin getWorldEditPlugin() {
		return (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
	}
	
	private void createStationsConfig() {
		
		AbstractMap.Entry<File, FileConfiguration> arenasConfigs = createConfigurationFile("stations.yml");
		this.flooStationsConfigFile = arenasConfigs.getKey();
		this.flooStationsConfig = arenasConfigs.getValue();
		
	}
	
	private AbstractMap.Entry<File, FileConfiguration> createConfigurationFile(String name) {
		File configFile = new File(getDataFolder(), name);
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			saveResource(name, false);
		}
		
		FileConfiguration config = new YamlConfiguration();
		try {
			config.load(configFile);
			return new AbstractMap.SimpleEntry(configFile, config);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public File getFlooStationsConfigFile() {
		this.flooStationsConfigFile = new File(getDataFolder(), "stations.yml");
		return this.flooStationsConfigFile;
	}
	
	public FileConfiguration getFlooStationsConfig() {
		return this.flooStationsConfig;
	}

	
}
