package managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import survivalislands.SurvivalIslands;

public class PlayerStatsManager {

	private static PlayerStatsManager instance;
	@SuppressWarnings("unused")
	private File playerdataFolder;
	
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
			SurvivalIslands.getInstance().getDataFolder().mkdir();
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
