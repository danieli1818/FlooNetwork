package me.danieli1818.floo_network.stations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.regions.Region;

import me.danieli1818.floo_network.utils.serialization.RegionSerializable;

public class FlooNetworkStation implements ConfigurationSerializable {

	private List<String> names;
	private RegionSerializable region;
	private Location spawnLocation;
	
	public FlooNetworkStation(List<String> names, Region region, Location spawnLocation) {
		this(names, new RegionSerializable(region), spawnLocation);
	}
	
	public FlooNetworkStation(List<String> names, RegionSerializable region, Location spawnLocation) {
		this(names);
		this.region = region;
		this.spawnLocation = spawnLocation;
	}
	
	public FlooNetworkStation(List<String> names) {
		if (names == null) {
			names = new ArrayList<String>();
		}
		this.names = names;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serialization = new HashMap<String, Object>();
		serialization.put("names", names);
		serialization.put("region", region);
		serialization.put("spawn_location", spawnLocation);
		return serialization;
	}
	
	public static FlooNetworkStation deserialize(Map<String, Object> serialization) {
		List<String> names = (List<String>) serialization.get("names");
		RegionSerializable region = (RegionSerializable) serialization.get("region");
		Location spawnLocation = (Location) serialization.get("spawn_location");
		return new FlooNetworkStation(names, region, spawnLocation);
	}
	
	public boolean addName(String name) {
		if (this.names.contains(name)) {
			return false;
		}
		this.names.add(name);
		return true;
	}
	
	public boolean removeName(String name) {
		return this.names.remove(name);
	}
	
	public void setRegion(Region region) {
		this.region = new RegionSerializable(region);
	}
	
	public void setSpawnLocation(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}
	
	public void setNames(List<String> names) {
		this.names = names;
	}
	
	public boolean isInRegion(Location location) {
		return this.region.contains(location);
	}
	
	public List<String> getNames() {
		return this.names;
	}
	
	public boolean teleportPlayer(Player player) {
		if (this.spawnLocation == null) {
			return false;
		}
		player.teleport(this.spawnLocation);
		return true;
	}
	
	public void onUse() {
		this.region.replace(Material.FIRE, Material.SOUL_FIRE);
	}
	
	public void onFinishedUse() {
		this.region.replace(Material.SOUL_FIRE, Material.FIRE);
	}
	
}
