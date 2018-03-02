package managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.MaxChangedBlocksException;

import islands.PlayerIsland;
import utilities.chatUtil;

public class IslandsManager {

	private static IslandsManager instance;
	private List<PlayerIsland> islands;
	private int padding = 30;
	private int spacing = 1;
	private int diameter = 10;
	private int YValue = 100;
	private int swapLength = 2;
	
	public static IslandsManager getManager() {
		if (instance == null) {
			instance = new IslandsManager();
		}
		return instance;
	}
	
	private IslandsManager() {
		islands = new ArrayList<PlayerIsland>();
	}
	
	public boolean loadIsland(Player p) {
		System.out.println("Loading island from: "+ p.getName());
		chatUtil.sendMessage(p, ChatColor.GREEN+"Loading island...", true);
		Location spawnLocation = getPasteLocation();
		PlayerIsland island = new PlayerIsland(p, spawnLocation, spawnLocation);
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
		
		return new Location(w,x,YValue,z);
	}

	public Location getSpawnOf(Player p) {
		for (PlayerIsland playerIsland : islands) {
			if (playerIsland.getPlayer().getName().equalsIgnoreCase(p.getName())) {
				return playerIsland.getSpawnLocation();
			}
		}
		return null;
	}

	public void unloadAll() {
		for (PlayerIsland playerIsland : islands) {
			playerIsland.unloadIsland();
		}
	}

}
