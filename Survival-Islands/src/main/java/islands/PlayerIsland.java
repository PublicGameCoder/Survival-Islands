package islands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
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

import managers.IslandsManager;
import managers.PlayerStatsManager;
import net.citizensnpcs.api.event.DespawnReason;
import utilities.chatUtil;

public class PlayerIsland {
	
	private UUID ownerUUID;
	private Location center;
	private Location npcLocation;
	private WorkerNPC wnpc;
	
	public PlayerIsland(Player p, Location spawnLocation, Location npcLocation) {
		this.ownerUUID = p.getUniqueId();
		this.center = spawnLocation;
		this.npcLocation = npcLocation;
		this.wnpc = new WorkerNPC(this);
	}
	
	public void unloadIsland() {
		Player owner = getPlayer();
		if (owner != null) {
			if (owner.getWorld().getName().equalsIgnoreCase(center.getWorld().getName())) {
				chatUtil.sendMessage(owner, "Saving island..", true);
				owner.teleport(IslandsManager.getManager().getLobbyLocation());
			}
		}
		saveIsland();
		getNPC().getNPC().despawn(DespawnReason.WORLD_UNLOAD);
		clearIsland();
	}
	
	private void clearIsland() {
		Vector centervector = new Vector(center.getX(), center.getY(), center.getZ());
		World weWorld = new BukkitWorld(center.getWorld());
		CylinderRegion weRegion = new CylinderRegion(weWorld,centervector, new Vector2D(21, 21), 60, 254);
		
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1);
		try {
		    editSession.setBlocks(weRegion, new BaseBlock(BlockID.AIR));
		} catch (MaxChangedBlocksException e) {
		    // As of the blocks are unlimited this should not be called
		}
	}

	private void saveIsland() {
		Player p = getPlayer();
		if (p == null)return;
		String schematicName = p.getUniqueId().toString();
		
		File file = PlayerStatsManager.getManager().getPlayerIsland(schematicName, null);
		
		Vector centervector = new Vector(center.getX(), center.getY(), center.getZ());
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
		AtomicBoolean isNew = new AtomicBoolean(false);
		File file = PlayerStatsManager.getManager().getPlayerIsland(schematicName, isNew);
		
		org.bukkit.util.Vector originVector = center.toVector();
        Vector to;
        if (isNew.get()) {
        	to = new Vector(originVector.getBlockX() - 21,60,originVector.getBlockZ() -21);//to = new Vector(originVector.getBlockX(),originVector.getBlockY(),originVector.getBlockZ());
        	//Vector borderCenter = new Vector(to.getX(),to.getY()+2,to.getZ());
        	//generateBorder(borderCenter,21);
        }else {
        	to = new Vector(originVector.getBlockX() - 21,60,originVector.getBlockZ() -21);
        }
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
	private void generateBorder(Vector borderCenter, double radius) {
		World world = new BukkitWorld(center.getWorld());
		EditSession es = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
	    try {
	         es.enableQueue();
	         es.makeCylinder(borderCenter, new SingleBlockPattern(new BaseBlock(BlockID.BARRIER)), radius , (254 - borderCenter.getBlockY()), false);
	         es.makeCylinder(new Vector(borderCenter.getBlockX(), 254, borderCenter.getBlockZ()), new SingleBlockPattern(new BaseBlock(BlockID.BARRIER)), radius , 1, true);
	         es.flushQueue();
	    } catch(MaxChangedBlocksException ignored) {
	        // We have no limit, this should never be hit
	    }
	}*/

	public Player getPlayer() {
		return Bukkit.getPlayer(ownerUUID);
	}

	public Location getSpawnLocation() {
		return this.center;
	}
	
	public Location getNPCLocation() {
		return npcLocation;
	}
	
	public WorkerNPC getNPC() {
		return wnpc;
	}

	public void homing(final Player p) {
		p.teleport(getSpawnLocation());
		getNPC().spawn();
	}
}
