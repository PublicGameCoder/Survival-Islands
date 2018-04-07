package managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.sk89q.worldedit.MaxChangedBlocksException;

import islands.PlayerIsland;
import survivalislands.SurvivalIslands;
import utilities.chatUtil;

public class IslandsManager implements Listener {

	private static IslandsManager instance;
	private List<PlayerIsland> islands;
	private int padding = 30;
	private int spacing = 1;
	private int diameter = 21;
	private int YValue = 100;
	private int swapLength = 2;
	
	private Location lobbySpawn;
	
	public static IslandsManager getManager() {
		if (instance == null) {
			instance = new IslandsManager();
		}
		return instance;
	}
	
	private IslandsManager() {
		islands = new ArrayList<PlayerIsland>();
		
		lobbySpawn = Bukkit.getWorlds().get(0).getSpawnLocation();
		
		SurvivalIslands.getInstance().getServer().getPluginManager().registerEvents(this, SurvivalIslands.getInstance());
	}
	
	public boolean loadIsland(Player p) {
		System.out.println("Loading island from: "+ p.getName());
		chatUtil.sendMessage(p, ChatColor.GREEN+"Loading island...", true);
		Location spawnLocation = getPasteLocation();
		PlayerIsland island = new PlayerIsland(p, spawnLocation, spawnLocation.clone().subtract(-0.5, -6, 2.5));
		try {
			island.loadIsland();
		} catch (IOException | MaxChangedBlocksException e) {
			System.out.println("island Loading failed!");
			chatUtil.sendMessage(p, ChatColor.RED+"Island loading failed.", true);
			chatUtil.sendMessage(p, ChatColor.RED+"Please report this bugg to our staff.", true);
			e.printStackTrace();
			return false;
		}
		System.out.println("island Loading success!");
		chatUtil.sendMessage(p, ChatColor.GREEN+"Island loading finished successfully!", true);
		islands.add(island);
		return true;
	}
	
	private Location getPasteLocation() {
		World w = WorldProcessInteractor.getManager().getIslandsWorld();
		int size = islands.size();
		
		float totalX = diameter + padding + spacing;
		float totalY = diameter + padding + spacing;
		    
		float x = totalX * (size % swapLength);
		float z = (size/swapLength) * totalY;
		
		Location pos = new Location(w,x,YValue,z);
		return pos;
	}

	public Location getSpawnOf(Player p) {
		if (p == null) return null;
		PlayerIsland island = getIslandOf(p);
		if (island == null) {
			return null;
		}else {
			return island.getSpawnLocation();
		}
	}
	
	public boolean hasIsland(Player p) {
		if (p == null) return false;
		String name = p.getName();
		for (PlayerIsland island : islands) {
			if (island.getPlayer() == null)continue;
			if (island.getPlayer().getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public PlayerIsland getIslandOf(Player p) {
		if (p == null) return null;
		for (PlayerIsland playerIsland : islands) {
			if (playerIsland.getPlayer() == null) continue;
			if (playerIsland.getPlayer().getName().equalsIgnoreCase(p.getName())) {
				return playerIsland;
			}
		}
		return null;
	}

	public void unloadAll() {
		for (PlayerIsland playerIsland : islands) {
			playerIsland.unloadIsland();
		}
		islands = new ArrayList<PlayerIsland>();
	}
	
	public Location getLobbyLocation() {
		return lobbySpawn;
	}

}
