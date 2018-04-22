package managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import managers.PickaxeLevelingManager.pickaxeType;
import survivalislands.SurvivalIslands;

public class ConfigManager {

	private static ConfigManager instance;
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
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			setupConfigDefaults();
		}
		
		pickaxeChances = new HashMap<Material, Map<Material,Float>>();
	}
	
	private void setupConfigDefaults() {
		// TODO create configSetup implementation
		FileConfiguration configuration = getConfig();
		
		configuration.set("DefaultSchematicFile", "basicIsland");
		
		for (pickaxeType type : pickaxeType.values()) {
			ConfigurationSection pickaxeSection = configuration.createSection("pickaxeDropChances."+type.toString());
			
			pickaxeSection.set(Material.COAL_ORE.toString(), (float) 70.0f);
			pickaxeSection.set(Material.IRON_ORE.toString(), (float) 50.0f);
			pickaxeSection.set(Material.GOLD_ORE.toString(), (float) 40.0f);
			pickaxeSection.set(Material.DIAMOND_ORE.toString(), (float) 30.0f);
			pickaxeSection.set(Material.EMERALD_ORE.toString(), (float) 20.0f);
			pickaxeSection.set(Material.LAPIS_ORE.toString(), (float) 60.0f);
			pickaxeSection.set(Material.REDSTONE_ORE.toString(), (float) 90.0f);
			pickaxeSection.set(Material.GLOWING_REDSTONE_ORE.toString(), (float) 90.0f);
			pickaxeSection.set(Material.QUARTZ_ORE.toString(), (float) 80.0f);
		}
		
		saveConfig(configuration);
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
	
	private Map<Material, Map<Material, Float>> pickaxeChances;
	public Map<Material, Map<Material, Float>> getPickaxeChances() {
		if (pickaxeChances.isEmpty()) {
			loadPickaxeChances();
		}
		return pickaxeChances;
	}

	private void loadPickaxeChances() {
		FileConfiguration config = getConfig();
		
		for (pickaxeType type : pickaxeType.values()) {
			ConfigurationSection pickaxeSection = config.getConfigurationSection("pickaxeDropChances."+type.toString());
			
			Map<Material, Float> chances = new HashMap<Material, Float>();
			
			chances.put(Material.COAL_ORE, Float.parseFloat(pickaxeSection.getString(Material.COAL_ORE.toString())));
			chances.put(Material.IRON_ORE, Float.parseFloat(pickaxeSection.getString(Material.IRON_ORE.toString())));
			chances.put(Material.GOLD_ORE, Float.parseFloat(pickaxeSection.getString(Material.GOLD_ORE.toString())));
			chances.put(Material.DIAMOND_ORE, Float.parseFloat(pickaxeSection.getString(Material.DIAMOND_ORE.toString())));
			chances.put(Material.EMERALD_ORE, Float.parseFloat(pickaxeSection.getString(Material.EMERALD_ORE.toString())));
			chances.put(Material.LAPIS_ORE, Float.parseFloat(pickaxeSection.getString(Material.LAPIS_ORE.toString())));
			chances.put(Material.REDSTONE_ORE, Float.parseFloat(pickaxeSection.getString(Material.REDSTONE_ORE.toString())));
			chances.put(Material.GLOWING_REDSTONE_ORE, Float.parseFloat(pickaxeSection.getString(Material.GLOWING_REDSTONE_ORE.toString())));
			chances.put(Material.QUARTZ_ORE, Float.parseFloat(pickaxeSection.getString(Material.QUARTZ_ORE.toString())));
			
			pickaxeChances.put(type.getMaterial(), chances);
		}
	}

}
