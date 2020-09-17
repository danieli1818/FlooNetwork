package me.danieli1818.floo_network.stations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import me.danieli1818.floo_network.FlooNetwork;
import me.danieli1818.floo_network.utils.serialization.SavingAndLoadingUtils;


public class FlooNetworkStationsManager implements ConfigurationSerializable {

	private static FlooNetworkStationsManager instance;
	
	private static FlooNetwork plugin = FlooNetwork.getPlugin(FlooNetwork.class);

	private static FileConfiguration stationsConfig = plugin.getFlooStationsConfig();
	
	private static File stationsConfigFile = plugin.getFlooStationsConfigFile();
	
	private Map<String, FlooNetworkStation> flooNetworkStations;
	
	private FlooNetworkStationsManager() {
		this.flooNetworkStations = new ConcurrentHashMap<String, FlooNetworkStation>();
	}
	
	private FlooNetworkStationsManager(Map<String, FlooNetworkStation> stations) {
		this.flooNetworkStations = new ConcurrentHashMap<String, FlooNetworkStation>();
		this.flooNetworkStations.putAll(stations);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serialization = new HashMap<String, Object>();
		serialization.putAll(this.flooNetworkStations);
		return serialization;
	}
	
	public static FlooNetworkStationsManager deserialize(Map<String, Object> serialization) {
		Map<String, FlooNetworkStation> stations = new HashMap<String, FlooNetworkStation>();
		for (Map.Entry<String, Object> entry : serialization.entrySet()) {
			if (entry.getValue() instanceof String) {
				continue;
			}
			stations.put(entry.getKey(), (FlooNetworkStation)entry.getValue());
		}
		return new FlooNetworkStationsManager(stations);
	}
	
	public synchronized static FlooNetworkStationsManager getInstance() {
		if (FlooNetworkStationsManager.instance == null) {
			FlooNetworkStationsManager.instance = new FlooNetworkStationsManager();
		}
		return FlooNetworkStationsManager.instance;
	}
	
	public void reloadStations() {

		this.flooNetworkStations.clear();
		
		try {
			stationsConfigFile = plugin.getFlooStationsConfigFile();
			this.stationsConfig = new YamlConfiguration();
			this.stationsConfig.load(stationsConfigFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		FlooNetworkStationsManager manager = this.stationsConfig.getSerializable("stations", FlooNetworkStationsManager.class);
		
		if (manager != null) {
			this.flooNetworkStations = manager.flooNetworkStations;
		}
		
		
	}
	
	public void saveStations() {
		try {
			SavingAndLoadingUtils.saveSerializable(this, this.stationsConfig, this.stationsConfigFile, "stations");
		} catch (IOException e) {
			System.out.println("Error In Saving Stations!");
			e.printStackTrace();
		}
	}
	
	public boolean createStation(String stationID, List<String> stationNames) {
		if (this.flooNetworkStations.containsKey(stationID)) {
			return false;
		}
		List<String> stationNamesLowerCase = new ArrayList<String>();
		for (String stationName : stationNames) {
			stationNamesLowerCase.add(stationName.toLowerCase());
		}
		this.flooNetworkStations.put(stationID, new FlooNetworkStation(stationNamesLowerCase));
		return true;
	}
	
	public boolean removeStation(String stationName) {
		if (this.flooNetworkStations.containsKey(stationName)) {
			this.flooNetworkStations.remove(stationName);
			return true;
		}
		return false;
	}
	
	public FlooNetworkStation getStation(String stationID) {
		return this.flooNetworkStations.get(stationID);
	}
	
	public FlooNetworkStation getStation(Location location) {
		for (FlooNetworkStation station : this.flooNetworkStations.values()) {
			if (station.isInRegion(location)) {
				return station;
			}
		}
		return null;
	}
	
	public FlooNetworkStation getStationByName(String name) {
		name = name.toLowerCase();
		for (FlooNetworkStation station : this.flooNetworkStations.values()) {
			if (station.getNames().contains(name)) {
				return station;
			}
		}
		return null;
	}
	
	public Collection<FlooNetworkStation> getStations() {
		return this.flooNetworkStations.values();
	}
	
}
