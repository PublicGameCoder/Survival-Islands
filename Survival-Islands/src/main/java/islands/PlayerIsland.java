package islands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;

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
		saveIsland();
	}
	
	private void saveIsland() {
		String schematicName = getPlayer().getUniqueId().toString();
		File file = PlayerStatsManager.getManager().getPlayerIsland(schematicName);
		
		Vector centervector = new Vector(center.getBlockX(), center.getBlockY(), center.getBlockZ());
		CylinderRegion region = new CylinderRegion(centervector, new Vector2D(21, 21), 60, 254);
		
		World weWorld = new BukkitWorld(center.getWorld());
        WorldData worldData = weWorld.getWorldData();
        Vector pos1 = region.getMinimumPoint();
        Vector pos2 = region.getMaximumPoint();
        CuboidRegion cReg = new CuboidRegion(weWorld, pos1, pos2);
        try {
            BlockArrayClipboard clipboard = new BlockArrayClipboard(cReg);      
            Extent source = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1);
            Extent destination = clipboard;
            ForwardExtentCopy copy = new ForwardExtentCopy(source, cReg, clipboard.getOrigin(), destination, pos1);
            copy.setSourceMask(new ExistingBlockMask(source));
            Operations.completeLegacy(copy);
            FileOutputStream out = new FileOutputStream(file);
            ClipboardWriter writer = ClipboardFormat.SCHEMATIC.getWriter(out);
            writer.write(clipboard, worldData);
            writer.close();
            
        } catch (IOException | MaxChangedBlocksException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
	}
	
	public void loadIsland() throws FileNotFoundException, IOException, MaxChangedBlocksException {
		String schematicName = getPlayer().getUniqueId().toString();
		File file = PlayerStatsManager.getManager().getPlayerIsland(schematicName);
		
		org.bukkit.util.Vector originVector = center.toVector();
        Vector to = new Vector(originVector.getBlockX(),originVector.getBlockY(),originVector.getBlockZ());
        World weWorld = new BukkitWorld(center.getWorld());
        WorldData worldData = weWorld.getWorldData();	
        FileInputStream input = new FileInputStream(file);
        ClipboardReader reader = ClipboardFormat.SCHEMATIC.getReader(input);
        Clipboard clipboard = reader.read(worldData);
        input.close();
        Extent source = clipboard;
        Extent destination = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1);
        ForwardExtentCopy copy = new ForwardExtentCopy(source, clipboard.getRegion(), clipboard.getOrigin(), destination, to);
        copy.setSourceMask(new ExistingBlockMask(clipboard));
        Operations.completeLegacy(copy);
	}
	
	/*
	@SuppressWarnings("deprecation")
	public void loadIsland() throws DataException, IOException, MaxChangedBlocksException {
		String schematicName = getPlayer().getUniqueId().toString();
		File file = PlayerStatsManager.getManager().getPlayerIsland(schematicName);
		
	    EditSession es = new EditSession(new BukkitWorld(center.getWorld()), 999999999);
	    CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
	    org.bukkit.util.Vector originVector = center.toVector();
	    cc.paste(es, new Vector(originVector.getBlockX(),originVector.getBlockY(),originVector.getBlockZ()), false);
	}*/

	public Player getPlayer() {
		return Bukkit.getPlayer(ownerUUID);
	}

	public Location getSpawnLocation() {
		return this.center;
	}
}
