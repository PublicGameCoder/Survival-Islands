package managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
	private int swapLength = 20;
	
	private Location lobbySpawn;
	
	public static IslandsManager getManager() {
		if (instance == null) {
			instance = new IslandsManager();
		}
		return instance;
	}
	
	private IslandsManager() {
		islands = new ArrayList<PlayerIsland>();
		
		padding = ConfigManager.getManager().getPadding();
		
		lobbySpawn = Bukkit.getWorlds().get(0).getSpawnLocation();
		
		SurvivalIslands.getInstance().getServer().getPluginManager().registerEvents(this, SurvivalIslands.getInstance());
		
		final PotionEffect nightvision = new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 1);
		
new BukkitRunnable() {
			
			@Override
			public void run() {
				for (PlayerIsland playerIsland : islands) {
					Player p = playerIsland.getPlayer();
					if (p == null)continue;
					Location loc = p.getLocation();
					Location spawnLoc = playerIsland.getSpawnLocation();
					double xDist = loc.getX() - spawnLoc.getX();
					double zDist = loc.getZ() - spawnLoc.getZ();
					double xzDist = Math.sqrt((xDist * xDist) + (zDist * zDist));
					if (loc.getWorld() == spawnLoc.getWorld() && loc.getBlockY() < (spawnLoc.getBlockY() - 5) && xzDist <= diameter) {
						p.addPotionEffect(nightvision);
					}else {
						p.removePotionEffect(PotionEffectType.NIGHT_VISION);
					}
					
					if (playerIsland.isGenerated()) {
						playerIsland.countLevel();
					}
				}
			}
		}.runTaskTimer(SurvivalIslands.getInstance(), 0, 20);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for (PlayerIsland playerIsland : islands) {					
					if (playerIsland.isGenerated()) {
						playerIsland.countLevel();
					}
				}
			}
		}.runTaskTimer(SurvivalIslands.getInstance(), 0, 100);
	}
	
	public boolean loadIsland(Player p, boolean forceEnter) {
		if (hasIsland(p)) {
			chatUtil.sendMessage(p, ChatColor.GREEN+"Island is already loaded!", true);
			return true;
		}
		System.out.println("Loading island from: "+ p.getName());
		chatUtil.sendMessage(p, ChatColor.GREEN+"Loading island...", true);
		Location spawnLocation = getPasteLocation();
		PlayerIsland island = new PlayerIsland(p, spawnLocation, spawnLocation.clone().subtract(-0.5, -6, 2.5));
		try {
			island.loadIsland(forceEnter);
		} catch (IOException | MaxChangedBlocksException e) {
			System.out.println("island Loading failed!");
			chatUtil.sendMessage(p, ChatColor.RED+"Island loading failed.", true);
			chatUtil.sendMessage(p, ChatColor.RED+"Please report this bugg to our staff.", true);
			e.printStackTrace();
			return false;
		}
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
	
	public void openIslandManager(Player player) {
		PlayerIsland island = IslandsManager.getManager().getIslandOf(player);
		if (island == null) {
			player.closeInventory();
			chatUtil.sendMessage(player, "&cPlease create an island before trying to access an island menu!");
		}else {
			island.getNPC().openMenu(player);
		}
	}

	public void unloadAll() {
		for (PlayerIsland playerIsland : islands) {
			playerIsland.unloadIsland();
		}
		islands = new ArrayList<PlayerIsland>();
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		PlayerIsland island = getIslandOf(e.getPlayer());
		String level = "";
		String group = "";
		
		if (island != null && island.isGenerated()) {
			level = "&8[&7Lvl "+island.getIslandLevel()+"&8] ";
		}
		
		if (SurvivalIslands.isPermissionsActive()) {
			group = "&8[&7"+SurvivalIslands.getPermissions().getPrimaryGroup(e.getPlayer())+"&8] ";
		}
		
		String player = e.getPlayer().getDisplayName();
		String message = e.getMessage();
		e.setCancelled(true);
		e.setMessage("");
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', level+group+"&7"+player+"&8: &7")+message);
	}
	
	public Location getLobbyLocation() {
		return lobbySpawn;
	}

}
