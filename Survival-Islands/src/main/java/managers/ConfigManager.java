package managers;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import survivalislands.SurvivalIslands;

public class ConfigManager {

	private static ConfigManager instance;
	@SuppressWarnings("unused")
	private File configFile;
	
	public static ConfigManager getManager() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}
	
	private ConfigManager() {
		
		if (!SurvivalIslands.getInstance().getDataFolder().exists()) {
			SurvivalIslands.getInstance().getDataFolder().mkdirs();
		}
		
		configFile = new File(SurvivalIslands.getInstance().getDataFolder(), "config.yml");
		if (!SurvivalIslands.getInstance().getDataFolder().exists()) {
			try {
				SurvivalIslands.getInstance().getDataFolder().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			setupConfigDefaults();
		}
	}

	private void setupConfigDefaults() {
		// TODO create configSetup implementation
	}
	
	public FileConfiguration getConfig() {
		return YamlConfiguration.loadConfiguration(configFile);
	}
	
	public void saveConfig(FileConfiguration config) {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
