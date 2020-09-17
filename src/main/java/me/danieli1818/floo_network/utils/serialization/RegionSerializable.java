package me.danieli1818.floo_network.utils.serialization;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;

public class RegionSerializable implements ConfigurationSerializable {

	private Region region;

	public RegionSerializable(final Region region) {
		this.region = region;
		
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serialization = new HashMap<String, Object>();
		BlockVector3 minimumPoint = getMinimumPoint();
		BlockVector3 maximumPoint = getMaximumPoint();
		serialization.put("world", getWorld().getName());
		serialization.put("min_x", minimumPoint.getBlockX());
		serialization.put("min_y", minimumPoint.getBlockY());
		serialization.put("min_z", minimumPoint.getBlockZ());
		serialization.put("max_x", maximumPoint.getBlockX());
		serialization.put("max_y", maximumPoint.getBlockY());
		serialization.put("max_z", maximumPoint.getBlockZ());
		return serialization;
	}
	
	public static RegionSerializable deserialize(Map<String, Object> serialization) {
		String worldName = (String) serialization.get("world");
		int min_x = (Integer) serialization.get("min_x");
		int min_y = (Integer) serialization.get("min_y");
		int min_z = (Integer) serialization.get("min_z");
		int max_x = (Integer) serialization.get("max_x");
		int max_y = (Integer) serialization.get("max_y");
		int max_z = (Integer) serialization.get("max_z");
		World world = BukkitAdapter.adapt(Bukkit.getWorld(worldName));
		BlockVector3 minimumPoint = BlockVector3.at(min_x, min_y, min_z);
		BlockVector3 maximumPoint = BlockVector3.at(max_x, max_y, max_z);
		Region region = new CuboidRegion(world, minimumPoint, maximumPoint);
		return new RegionSerializable(region);
	}
	
	public BlockVector3 getMinimumPoint() {
		return this.region.getMinimumPoint();
	}
	
	public BlockVector3 getMaximumPoint() {
		return this.region.getMaximumPoint();
	}
	
	public World getWorld() {
		return this.region.getWorld();
	}
	
	public boolean contains(BlockVector3 vector) {
		return this.region.contains(vector);
	}
	
	public boolean contains(Location location) {
		BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
		return (this.region.getWorld().getName() == location.getWorld().getName()) && contains(vector);
	}
	
	public void replace(final Material prevMaterial, final Material newMaterial) {
		this.region.forEach(new Consumer<BlockVector3>() {

			@Override
			public void accept(BlockVector3 t) {
				Location location = new Location(BukkitAdapter.adapt(region.getWorld()), t.getBlockX(), t.getBlockY(), t.getBlockZ());
				
				if (location.getBlock().getType() == prevMaterial) {
					location.getBlock().setType(newMaterial);
				}
				
				
			}
			
		});
	}
	
}
