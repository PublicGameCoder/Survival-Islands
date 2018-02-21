package managers;

import java.io.File;

import survivalislands.SurvivalIslands;

public class WorldProcessInteractor {

	private static WorldProcessInteractor instance;
	@SuppressWarnings("unused")
	private File schematicFolder;
	
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
	}

}
