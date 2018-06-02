package managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import managers.PickaxeLevelingManager.pickaxeType;
import survivalislands.SurvivalIslands;
import utilities.ValuedMaterial;

public class ConfigManager {

	private static ConfigManager instance;
	private File configFile;
	
	private List<ValuedMaterial> valuedMats;
	
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
		
		valuedMats = new ArrayList<ValuedMaterial>();
		pickaxeChances = new HashMap<Material, Map<Integer,Map<Material,Float>>>();
	}
	
	private void setupConfigDefaults() {
		FileConfiguration configuration = getConfig();
		
		configuration.set("DefaultSchematicFile", "basicIsland");
		configuration.set("LayerRegenCostEach",(double) 200.0);
		configuration.set("IslandPadding", 30);
		
		for (pickaxeType type : pickaxeType.values()) {
			int levels = 1;
			
			levels = (type == pickaxeType.Wood_Pickaxe)? 3 : levels ;
			levels = (type == pickaxeType.Stone_Pickaxe)? 5 : levels ;
			levels = (type == pickaxeType.Iron_Pickaxe)? 10 : levels ;
			levels = (type == pickaxeType.Gold_Pickaxe)? 25 : levels ;
			levels = (type == pickaxeType.Diamond_Pickaxe)? 50 : levels ;
			
			for (int i = 1; i <= levels; i++) {
				ConfigurationSection pickaxeSection = configuration.createSection("pickaxeDropChances."+type.toString()+".LVL"+i);
				pickaxeSection.set(Material.COAL_ORE.toString(), (float) 70.0f);
				pickaxeSection.set(Material.IRON_ORE.toString(), (float) 50.0f);
				pickaxeSection.set(Material.GOLD_ORE.toString(), (float) 40.0f);
				pickaxeSection.set(Material.DIAMOND_ORE.toString(), (float) 30.0f);
				pickaxeSection.set(Material.EMERALD_ORE.toString(), (float) 20.0f);
				pickaxeSection.set(Material.LAPIS_ORE.toString(), (float) 60.0f);
				pickaxeSection.set(Material.REDSTONE_ORE.toString(), (float) 90.0f);
				pickaxeSection.set(Material.GLOWING_REDSTONE_ORE.toString(), (float) 90.0f);
				pickaxeSection.set(Material.QUARTZ_ORE.toString(), (float) 80.0f);
				pickaxeSection.set(Material.COBBLESTONE.toString(), (float) 1.0f);
			}
		}
		
		List<String> valuedBlocks = new ArrayList<String>();
		valuedBlocks.add(Material.DIAMOND_BLOCK.toString()+":"+0+":"+0.5f);
		valuedBlocks.add(Material.GOLD_BLOCK.toString()+":"+0+":"+0.2f);
		valuedBlocks.add(Material.IRON_BLOCK.toString()+":"+0+":"+0.05f);
		valuedBlocks.add(Material.EMERALD_BLOCK.toString()+":"+0+":"+0.4f);
		valuedBlocks.add(Material.STONE.toString()+":"+0+":"+0.01f);
		valuedBlocks.add(Material.STONE.toString()+":"+1+":"+0.01f);
		valuedBlocks.add(Material.STONE.toString()+":"+2+":"+0.01f);
		
		configuration.set("ValuedBlocks", valuedBlocks);
		
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
	
	private Map<Material, Map<Integer,Map<Material,Float>>> pickaxeChances;
	public Map<Material, Map<Integer,Map<Material,Float>>> getPickaxeChances() {
		if (pickaxeChances.isEmpty()) {
			loadPickaxeChances();
		}
		return pickaxeChances;
	}

	private void loadPickaxeChances() {
		FileConfiguration config = getConfig();
		
		for (pickaxeType type : pickaxeType.values()) {
			
			Map<Integer,Map<Material,Float>> levels = new HashMap<Integer,Map<Material,Float>>();
			
			int lvls = 1;
			
			lvls = (type == pickaxeType.Wood_Pickaxe)? 3 : lvls ;
			lvls = (type == pickaxeType.Stone_Pickaxe)? 5 : lvls ;
			lvls = (type == pickaxeType.Iron_Pickaxe)? 10 : lvls ;
			lvls = (type == pickaxeType.Gold_Pickaxe)? 25 : lvls ;
			lvls = (type == pickaxeType.Diamond_Pickaxe)? 50 : lvls ;
			
			for (int i = 1; i <= lvls; i++) {
				ConfigurationSection pickaxeSection = config.getConfigurationSection("pickaxeDropChances."+type.toString()+".LVL"+i);
				
				Map<Material,Float> chances = new HashMap<Material,Float>();
				
				chances.put(Material.COAL_ORE, Float.parseFloat(pickaxeSection.getString(Material.COAL_ORE.toString())));
				chances.put(Material.IRON_ORE, Float.parseFloat(pickaxeSection.getString(Material.IRON_ORE.toString())));
				chances.put(Material.GOLD_ORE, Float.parseFloat(pickaxeSection.getString(Material.GOLD_ORE.toString())));
				chances.put(Material.DIAMOND_ORE, Float.parseFloat(pickaxeSection.getString(Material.DIAMOND_ORE.toString())));
				chances.put(Material.EMERALD_ORE, Float.parseFloat(pickaxeSection.getString(Material.EMERALD_ORE.toString())));
				chances.put(Material.LAPIS_ORE, Float.parseFloat(pickaxeSection.getString(Material.LAPIS_ORE.toString())));
				chances.put(Material.REDSTONE_ORE, Float.parseFloat(pickaxeSection.getString(Material.REDSTONE_ORE.toString())));
				chances.put(Material.GLOWING_REDSTONE_ORE, Float.parseFloat(pickaxeSection.getString(Material.GLOWING_REDSTONE_ORE.toString())));
				chances.put(Material.QUARTZ_ORE, Float.parseFloat(pickaxeSection.getString(Material.QUARTZ_ORE.toString())));
				chances.put(Material.COBBLESTONE, 1.0f);
				
				levels.put(i, chances);
			}
			pickaxeChances.put(type.getMaterial(), levels);
		}
	}
	
	public List<ValuedMaterial> getValuedMaterials() {
		if (valuedMats.isEmpty()) {
			loadValuedMaterials();
		}
		return this.valuedMats;
	}

	public void loadValuedMaterials() {
		valuedMats = new ArrayList<ValuedMaterial>();
		
		FileConfiguration config = getConfig();
		
		List<String> values = config.getStringList("ValuedBlocks");
		
		for (String string : values) {
			
			String[] args = string.split(":");
			
			Material mat = Material.getMaterial(args[0]);
			int data = Integer.parseInt(args[1]);
			float value = Float.parseFloat(args[2]);
			ValuedMaterial valuedMat = new ValuedMaterial(mat, data, value);
			valuedMats.add(valuedMat);
		}
	}

	public int getPadding() {
		FileConfiguration config = getConfig();
		return config.getInt("IslandPadding");
	}

}
