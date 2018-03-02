package managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import survivalislands.SurvivalIslands;

public class PlayerStatsManager {

	private static PlayerStatsManager instance;
	private File playerdataFolder;
	private File islandSchematicsFolder;
	private File playerSchematicsFolder;
	
	public static PlayerStatsManager getManager() {
		if (instance == null) {
			instance = new PlayerStatsManager();
		}
		return instance;
	}
	
	private PlayerStatsManager() {
		
		if (!SurvivalIslands.getInstance().getDataFolder().exists()) {
			SurvivalIslands.getInstance().getDataFolder().mkdirs();
		}
		
		playerdataFolder = new File(SurvivalIslands.getInstance().getDataFolder(), "playerdata");
		if (!SurvivalIslands.getInstance().getDataFolder().exists()) {
			playerdataFolder.mkdir();
		}
		
		islandSchematicsFolder = new File(SurvivalIslands.getInstance().getDataFolder(), "islandSchematics");
		if (!islandSchematicsFolder.exists()) {
			islandSchematicsFolder.mkdir();
		}
		
		playerSchematicsFolder = new File(SurvivalIslands.getInstance().getDataFolder(), "playerIslands");
		if (!playerSchematicsFolder.exists()) {
			playerSchematicsFolder.mkdir();
		}
	}
	
	private void setupPlayerFileDefaults(Player p) {
		FileConfiguration playerData = getPlayerData(p);
		
		ConfigurationSection detailsSection = playerData.createSection("Details");
		
		detailsSection.set("PlayerName", p.getName());
		detailsSection.set("UUID", p.getUniqueId());
		
		playerData.set("islandNames", new ArrayList<String>());
		
		playerData.createSection("Islands");
		
		savePlayerdata(p, playerData);
	}
	
	public FileConfiguration getPlayerData(Player p) {
		File playerFile = getPlayerFile(p);
		return YamlConfiguration.loadConfiguration(playerFile);
	}
	
	public File getPlayerIsland(String schematicName) {
		File file = new File(playerSchematicsFolder, schematicName + ".schematic");
		if (!file.exists()) {
			File srcFile = getBasicSchematicFile();
			try {
				FileUtils.copyFile(srcFile, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	private File getBasicSchematicFile() {
		String schematicName = ConfigManager.getManager().getConfig().getString("DefaultSchematicFile") + ".schematic";
		File file = new File(islandSchematicsFolder, schematicName);
		if (!file.exists()) {
			System.out.println("ERROR! Basic island schematic not found with name: '"+schematicName + "' in folder: '"+islandSchematicsFolder.getAbsolutePath()+"'.");
			return null;
		}
		return file;
	}
	
	private File getPlayerFile(Player p) {
		String uuid = p.getUniqueId().toString();
		File playerFile = new File(playerdataFolder, uuid);
		
		if (!playerFile.exists()) {
			try {
				playerFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setupPlayerFileDefaults(p);
		}
		
		return playerFile;
	}

	public void savePlayerdata(Player p,FileConfiguration playerData) {
		File playerFile = getPlayerFile(p);
		try {
			playerData.save(playerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
