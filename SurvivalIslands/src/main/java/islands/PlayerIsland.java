package islands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.AsyncWorldEditBukkit;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacer;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.api.blockPlacer.IJobEntryListener;
import org.primesoft.asyncworldedit.api.blockPlacer.entries.IJobEntry;
import org.primesoft.asyncworldedit.api.blockPlacer.entries.JobStatus;
import org.primesoft.asyncworldedit.api.playerManager.IPlayerEntry;
import org.primesoft.asyncworldedit.api.playerManager.IPlayerManager;
import org.primesoft.asyncworldedit.api.utils.IFuncParamEx;
import org.primesoft.asyncworldedit.api.worldedit.IAsyncEditSessionFactory;
import org.primesoft.asyncworldedit.api.worldedit.ICancelabeEditSession;
import org.primesoft.asyncworldedit.api.worldedit.IThreadSafeEditSession;

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
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.patterns.Pattern;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;

import managers.ConfigManager;
import managers.IslandsManager;
import managers.PasteAction;
import managers.PlayerStatsManager;
import net.citizensnpcs.api.event.DespawnReason;
import utilities.ValuedMaterial;
import utilities.chatUtil;

@SuppressWarnings("deprecation")
public class PlayerIsland{
	
	private UUID ownerUUID;
	private Location center;
	private Location npcLocation;
	private WorkerNPC wnpc;
	private String islandGenerateJobName;
	private boolean _isGenerated;
	private float islandLevel;
	
	public PlayerIsland(Player p, Location spawnLocation, Location npcLocation) {
		this.ownerUUID = p.getUniqueId();
		this.center = spawnLocation;
		this.npcLocation = npcLocation;
		this.wnpc = new WorkerNPC(this);
		this._isGenerated = false;
		this.islandGenerateJobName = "GenIsland_"+p.getName();
		islandLevel = 0;
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
	
	public void loadIsland(final boolean forceEnter) throws FileNotFoundException, IOException, MaxChangedBlocksException {
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
        BukkitWorld weWorld = new BukkitWorld(center.getWorld());
        
        IAsyncEditSessionFactory ifactory = (IAsyncEditSessionFactory) WorldEdit.getInstance().getEditSessionFactory();
        IThreadSafeEditSession destination = ifactory.getThreadSafeEditSession(weWorld, -1);
        IBlockPlacer iPlacer = AsyncWorldEditBukkit.getInstance().getBlockPlacer();
        IPlayerManager iManager = AsyncWorldEditBukkit.getInstance().getAPI().getPlayerManager();
        IPlayerEntry iEntry = iManager.getPlayer(ownerUUID);
        
        AsyncWorldEditBukkit.getInstance().getAPI().getProgressDisplayManager().disableMessage(iEntry);
        
        iPlacer.addListener(new IBlockPlacerListener(){
        	private IJobEntryListener listener;
        	
            @Override
            public void jobRemoved(IJobEntry job) {
                if(!job.getName().equalsIgnoreCase(islandGenerateJobName)) return;
                if (this.listener != null) {
                	job.removeStateChangedListener(this.listener);
                }
                _isGenerated = true;
            }

            @Override
            public void jobAdded(IJobEntry job) {
                if(!job.getName().equalsIgnoreCase(islandGenerateJobName)) return;
                this.listener = new IJobEntryListener() {
					
					@Override
					public void jobStateChanged(IJobEntry j) {
						if (j.getStatus() == JobStatus.Done) {
							_isGenerated = true;
							System.out.println("island Loading success!");
							Player p = (Player) getPlayer();
							chatUtil.sendMessage(p, ChatColor.GREEN+"Island loading finished successfully!", true);
							if (forceEnter) {
								chatUtil.sendMessage(p, ChatColor.GREEN+"Entering island..", true);
								homing(p);
							}
							countLevel();
						}
					}
				};
                job.addStateChangedListener(listener);
            }
        });
        
		IFuncParamEx<Integer, ICancelabeEditSession, MaxChangedBlocksException> action = (IFuncParamEx<Integer, ICancelabeEditSession, MaxChangedBlocksException>) new PasteAction(weWorld, to, file);
        iPlacer.performAsAsyncJob(destination, iEntry, islandGenerateJobName, action);
        
        Vector centervector = new Vector(center.getX(), center.getY(), center.getZ());
		CylinderRegion region = new CylinderRegion(centervector, new Vector2D(21, 21), 60, 254);
		
		World _weWorld = new BukkitWorld(center.getWorld());
        Vector pos1 = region.getMinimumPoint();
        Vector pos2 = region.getMaximumPoint();
        privateCubiodRegion = new CuboidRegion(_weWorld, pos1, pos2);
        
        IAsyncEditSessionFactory Pifactory = (IAsyncEditSessionFactory) WorldEdit.getInstance().getEditSessionFactory();
        privateEditSession = Pifactory.getThreadSafeEditSession(_weWorld, -1);
	}
	
	private CuboidRegion privateCubiodRegion;
	private IThreadSafeEditSession privateEditSession;
	
	public void countLevel() {
        HashSet<BaseBlock> hash = null;
        
        float totalLvl = 0;
        for (ValuedMaterial valuedMat : ConfigManager.getManager().getValuedMaterials()) {
	        hash = new HashSet<BaseBlock>();
	        
	        hash.add(new BaseBlock(valuedMat.getMaterial().getId(), valuedMat.getData()));
	        totalLvl += (privateEditSession.countBlocks(privateCubiodRegion, hash) * valuedMat.getLevelValue());
        }
        islandLevel = totalLvl;
	}
	
	private void regenLayer(Location layerLoc, int layerCompression) {
    	Player p = getPlayer();
		if (p != null) {
			Location playerLoc = p.getLocation().clone();
			if (playerLoc.getWorld().getName().equalsIgnoreCase(layerLoc.getWorld().getName())
				&& playerLoc.getBlockY() <= layerLoc.getBlockY()
				&& playerLoc.getBlockY() >= layerLoc.clone().subtract(0, layerCompression + 2, 0).getBlockY()) {
				homing(p);
			}
		}
		//layerLoc = center of bottom layer to regenerate.
		//layerCompression = how much blocks need to be generated in total from bottom to upwards.
		
		org.bukkit.util.Vector originVector = layerLoc.toVector();
        Vector to = new Vector(originVector.getX(),originVector.getY(),originVector.getZ());
        
        BukkitWorld weWorld = new BukkitWorld(layerLoc.getWorld());
        
        IAsyncEditSessionFactory ifactory = (IAsyncEditSessionFactory) WorldEdit.getInstance().getEditSessionFactory();
        IThreadSafeEditSession destination = ifactory.getThreadSafeEditSession(weWorld, -1);
        
        BaseBlock blocktype = new BaseBlock(Material.STONE.getId());
        Pattern pattern = new SingleBlockPattern(blocktype);
        
        try {
			destination.makeCylinder(to, pattern, 20, layerCompression, true);
		} catch (MaxChangedBlocksException e) {
			//e.printStackTrace();
		}
	}

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
	
	public boolean isGenerated() {
		return this._isGenerated;
	}

	public void homing(final Player p) {
		if (isGenerated()) {
			chatUtil.sendMessage(p, ChatColor.GREEN+"Entering island..", true);
			p.teleport(getSpawnLocation());
			getNPC().spawn();
		}else {
			chatUtil.sendMessage(p, "The island isn't generated yet!", true);
		}
	}

	public void regenLayers(boolean[] selected, int layerCompression) {
		Location startLoc = getSpawnLocation().clone();
		startLoc.setY(startLoc.getY() - 3);
		//=============================================
		
		for (int layer = 1; layer <= selected.length; layer++) {
			Location layerLoc = startLoc.clone();
			layerLoc.subtract(0, layer * layerCompression, 0);
			if (selected[layer-1]) {
				regenLayer(layerLoc, layerCompression);
			}
		}
		
	}

	public int getIslandLevel() {
		return (int) this.islandLevel;
	}
	
	public float getRawIslandLevel() {
		return this.islandLevel;
	}
}
