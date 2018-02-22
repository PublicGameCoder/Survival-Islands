package managers;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import survivalislands.SurvivalIslands;

public class WorldProcessInteractor {

	private static WorldProcessInteractor instance;
	@SuppressWarnings("unused")
	private File schematicFolder;
	private World voidWorldBackup;
	private World islandWorld;
	
	public static WorldProcessInteractor getManager() {
		if (instance == null) {
			instance = new WorldProcessInteractor();
		}
		return instance;
	}
	
	private WorldProcessInteractor() {
		
		if (!SurvivalIslands.getInstance().getDataFolder().exists()) {
			SurvivalIslands.getInstance().getDataFolder().mkdirs();
		}
		
		schematicFolder = new File(SurvivalIslands.getInstance().getDataFolder(), "schematics");
		if (!SurvivalIslands.getInstance().getDataFolder().exists()) {
			SurvivalIslands.getInstance().getDataFolder().mkdir();
		}
		
		loadNewVoidWorld("basicIslandsWorld");
	}
	
	private void loadNewVoidWorld(String worldName) {
		if (voidWorldBackup == null) {
			voidWorldBackup = Bukkit.createWorld(new WorldCreator("VoidWorldBackup"));
		}
		
		islandWorld = Bukkit.createWorld(new WorldCreator(worldName).copy(voidWorldBackup));
	}
	
	public World getIslandsWorld() {
		return this.islandWorld;
	}

}
