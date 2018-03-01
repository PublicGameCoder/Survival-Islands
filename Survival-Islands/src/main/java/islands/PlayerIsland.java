package islands;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.DataException;

import managers.PlayerStatsManager;

public class PlayerIsland {
	
	private UUID ownerUUID;
	private Location center;
	private Location npcLocation;
	
	public PlayerIsland(Player p, Location spawnLocation, Location npcLocation) {
		this.ownerUUID = p.getUniqueId();
		this.center = spawnLocation;
		this.npcLocation = npcLocation;
	}
	
	public void unloadIsland() {
		
	}
	
	private void saveIsland() {
		
	}
	
	@SuppressWarnings("deprecation")
	public void loadIsland() throws DataException, IOException, MaxChangedBlocksException {
		String schematicName = getPlayer().getUniqueId().toString();
		File file = PlayerStatsManager.getManager().getPlayerIsland(schematicName);
		
	    EditSession es = new EditSession(new BukkitWorld(center.getWorld()), 999999999);
	    CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
	    org.bukkit.util.Vector originVector = center.toVector();
	    cc.paste(es, new Vector(originVector.getBlockX(),originVector.getBlockY(),originVector.getBlockZ()), false);
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(ownerUUID);
	}

	public Location getSpawnLocation() {
		return this.center;
	}
}
