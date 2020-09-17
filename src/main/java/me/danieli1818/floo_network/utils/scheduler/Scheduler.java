package me.danieli1818.floo_network.utils.scheduler;

import me.danieli1818.floo_network.FlooNetwork;

public class Scheduler {

	private static Scheduler instance;
	
	private FlooNetwork plugin;
	
	private Scheduler(FlooNetwork plugin) {
		this.plugin = plugin;
	}
	
	public static Scheduler getInstance(FlooNetwork plugin) {
		if (instance == null) {
			instance = new Scheduler(plugin);
		}
		return instance;
	}
	
	public static Scheduler getInstance() {
		return instance;
	}
	
	public int scheduleSyncTask(Runnable task) {
		return this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task);
	}
	
	public int scheduleSyncTask(Runnable task, long delay) {
		return this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, delay);
	}
	
	public int scheduleAsyncTask(Runnable task, long delay) {
		return this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, task, delay);
	}
	
	public void cancelTask(int taskID) {
		this.plugin.getServer().getScheduler().cancelTask(taskID);
	}
	
}
