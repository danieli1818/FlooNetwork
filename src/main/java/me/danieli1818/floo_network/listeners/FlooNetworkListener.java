package me.danieli1818.floo_network.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.danieli1818.floo_network.stations.FlooNetworkStation;
import me.danieli1818.floo_network.stations.FlooNetworkStationsManager;
import me.danieli1818.floo_network.utils.scheduler.Scheduler;

public class FlooNetworkListener implements Listener {
	
	private Map<Player, FlooNetworkStation> playersUsingFlooStation;
	private Map<Player, Integer> playersTasks;
	
	public FlooNetworkListener() {
		this.playersUsingFlooStation = new ConcurrentHashMap<Player, FlooNetworkStation>();
		this.playersTasks = new ConcurrentHashMap<Player, Integer>();
	}

	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		Block block = event.getClickedBlock();
		if (block == null || item == null) {
			return;
		}
		Material clickedMaterial = block.getType(); // TODO change the block to the one you click and not where you stand.
		if ((item.getType() == Material.GUNPOWDER) && (clickedMaterial == Material.FIRE || clickedMaterial == Material.SOUL_FIRE)) {
			final Player player = event.getPlayer();
			final FlooNetworkStation station = FlooNetworkStationsManager.getInstance().getStation(block.getLocation());
			if (station == null) {
				return;
			}
			if (this.playersUsingFlooStation.containsKey(player)) {
				event.setCancelled(true);
				return;
			}
			this.playersUsingFlooStation.put(player, station);
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 1));
			Scheduler.getInstance().scheduleSyncTask(new Runnable() {
				
				@Override
				public void run() {
					station.onUse();
					
				}
			});
			player.sendMessage("Type Where You Want To Go! (Or Cancel To Cancel).");
			ItemStack holdingItem = player.getInventory().getItemInMainHand();
			holdingItem.setAmount(holdingItem.getAmount() - 1);
			this.playersTasks.put(player, Scheduler.getInstance().scheduleSyncTask(new Runnable() {
				
				@Override
				public void run() {
					FlooNetworkStation station = playersUsingFlooStation.remove(player);
					
					if (station != null) {
						if (!playersUsingFlooStation.containsValue(station)) {
							station.onFinishedUse();
						}
					}
					
				}
			}, 600));
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent e) {
	    final Player player = e.getPlayer();
	    if (this.playersUsingFlooStation.containsKey(player) && this.playersUsingFlooStation.get(player).isInRegion(player.getLocation()) 
	    		&& (player.getFireTicks() >= (player.getMaxFireTicks() - 1))) {
	    	if (this.playersTasks.containsKey(player)) {
	    		Scheduler.getInstance().cancelTask(this.playersTasks.get(player));
	    	}
	    	final FlooNetworkStation fromStation = this.playersUsingFlooStation.remove(player);
	    	String message = e.getMessage();
	    	message = message.toLowerCase();
	    	if (!message.equals("cancel")) {
	    		FlooNetworkStation station = FlooNetworkStationsManager.getInstance().getStationByName(message);
	    		String teleportMessage = ChatColor.GREEN + "Swooooooooosh!";
	    		if (station == null) {
	    			teleportMessage += " Where have you been spawned?";
	    			List<FlooNetworkStation> stations = new ArrayList<FlooNetworkStation>(FlooNetworkStationsManager.getInstance().getStations());
	    			if (stations.size() == 1) {
	    				station = stations.iterator().next();
	    			} else {
	    				Collections.shuffle(stations);
	    				for (FlooNetworkStation current_station : stations) {
	    					if (current_station != fromStation) {
	    						station = current_station;
	    						break;
	    					}
	    				}
	    			}
	    		}
    			teleportPlayerToStation(fromStation, station, player, teleportMessage);
	    	} else {
	    		player.getInventory().addItem(new ItemStack(Material.GUNPOWDER, 1));
    			Scheduler.getInstance().scheduleSyncTask(new Runnable() {
    				
    				@Override
    				public void run() {
    					player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
    					if (!playersUsingFlooStation.containsValue(fromStation)) {
    						fromStation.onFinishedUse();
    					}
    				}
    			});
	    	}
	    	player.setFireTicks(0);
	    	e.setCancelled(true);
	    }
	}
	
	private void teleportPlayerToStation(final FlooNetworkStation fromStation, final FlooNetworkStation toStation, final Player player, final String message) {
		Scheduler.getInstance().scheduleSyncTask(new Runnable() {
			
			@Override
			public void run() {
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 1));
				toStation.teleportPlayer(player);
				player.sendMessage(message);
				player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
				if (!playersUsingFlooStation.containsValue(fromStation)) {
					fromStation.onFinishedUse();
				}
			}
		});
	}
	
}
