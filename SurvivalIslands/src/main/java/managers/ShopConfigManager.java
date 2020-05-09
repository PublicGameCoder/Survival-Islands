package managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import survivalislands.SurvivalIslands;

public class ShopConfigManager {

	private static ShopConfigManager instance;
	private File configFile;
	private Map<String, List<String>> shopItems;
	
	public static ShopConfigManager getManager() {
		if (instance == null) {
			instance = new ShopConfigManager();
		}
		return instance;
	}
	
	private ShopConfigManager() {
		if (!SurvivalIslands.getInstance().getDataFolder().exists()) {
			SurvivalIslands.getInstance().getDataFolder().mkdirs();
		}
		
		configFile = new File(SurvivalIslands.getInstance().getDataFolder(), "shopData.yml");
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			setupConfigDefaults();
		}
		loadShops();
	}

	private void setupConfigDefaults() {
		// TODO create configSetup implementation
		FileConfiguration configuration = getConfig();
		
		List<String> shopCategories = new ArrayList<String>();
		shopCategories.add("Building_Blocks");
		configuration.set("ShopCategories", shopCategories);
		
		List<String> standardContent = new ArrayList<String>();
		standardContent.add(CreateItemString(Material.WOOD, 2, 1, 5.0, 2.5));//Stone item amount( 2 ) data( 1 ) buy( 5.0 ) sell( 2.5 );
		
		for (String string : shopCategories) {
			ConfigurationSection section = configuration.createSection(string);
			ConfigurationSection settings = section.createSection("Settings");
			settings.set("DisplayName", "&3"+string);
			settings.set("DisplayItem", Material.GRASS.toString());
			settings.set("DisplayData", (int) 0);
			settings.set("Highlight", true);
			section.set("Contents", standardContent);
		}
		
		List<String> blockItems = new ArrayList<String>();
		blockItems.add(CreateItemString(Material.DIRT, 1, 0, 50, 5));
		blockItems.add(CreateItemString(Material.GRASS, 1, 0, 200, 20));
		blockItems.add(CreateItemString(Material.COBBLESTONE, 1, 0, 10, 5));
		blockItems.add(CreateItemString(Material.STONE,1,0,25,10));
		blockItems.add(CreateItemString(Material.WOOD,1,0,10,5));
		blockItems.add(CreateItemString(Material.LOG,1,0,40,20));
		blockItems.add(CreateItemString(Material.QUARTZ_BLOCK,1,0,500,50));
		blockItems.add(CreateItemString(Material.STONE,1,1,5,2.50));
		blockItems.add(CreateItemString(Material.STONE,1,2,20,10));
		blockItems.add(CreateItemString(Material.STONE,1,3,10,5));
		blockItems.add(CreateItemString(Material.STONE,1,4,40,20));
		blockItems.add(CreateItemString(Material.STONE,1,5,20,10));
		blockItems.add(CreateItemString(Material.STONE,1,6,80,40));
		blockItems.add(CreateItemString(Material.SAND,1,0,50,25));
		blockItems.add(CreateItemString(Material.SANDSTONE,1,0,200,100));
		blockItems.add(CreateItemString(Material.SAND,1,1,100,25));
		blockItems.add(CreateItemString(Material.RED_SANDSTONE,1,0,400,100));
		blockItems.add(CreateItemString(Material.GRAVEL,1,0,50,10));
		blockItems.add(CreateItemString(Material.SMOOTH_BRICK,1,0,100,40));
		blockItems.add(CreateItemString(Material.GLASS,1,0,50,25));
		blockItems.add(CreateItemString(Material.CLAY,1,0,25,5));
		blockItems.add(CreateItemString(Material.CLAY_BRICK,1,0,100,20));
		blockItems.add(CreateItemString(Material.HARD_CLAY,1,0,500,50));
		blockItems.add(CreateItemString(Material.SEA_LANTERN,1,0,900,450));
		blockItems.add(CreateItemString(Material.WOOL,1,0,20,10));
		blockItems.add(CreateItemString(Material.ICE,1,0,120,30));
		blockItems.add(CreateItemString(Material.PACKED_ICE,1,0,240,60));
		blockItems.add(CreateItemString(Material.SNOW_BLOCK,1,0,50,10));
		blockItems.add(CreateItemString(Material.NETHERRACK,1,0,10,5));
		blockItems.add(CreateItemString(Material.NETHER_BRICK,1,0,100,50));
		blockItems.add(CreateItemString(Material.SOUL_SAND,1,0,500,50));
		blockItems.add(CreateItemString(Material.GLOWSTONE,1,0,500,50));
		blockItems.add(CreateItemString(Material.ENDER_STONE,1,0,150,25));
		blockItems.add(CreateItemString(Material.PRISMARINE,1,0,400,200));
		blockItems.add(CreateItemString(Material.PRISMARINE,1,1,900,450));
		blockItems.add(CreateItemString(Material.PRISMARINE,1,2,1000,500));
		
		configuration.set("Building_Blocks.Contents", blockItems);
		
		saveConfig(configuration);
	}
	
	private String CreateItemString(Material wood, int amount, int data, double buy, double sell) {
		String itemString = wood.toString()+":";
		itemString += amount+":";
		itemString += data+":";
		itemString += buy+":";
		itemString += sell;
		return itemString;
	}
	
	private void loadShops() {
		shopItems = new HashMap<String, List<String>>();
		FileConfiguration config = getConfig();
		List<String> shops = config.getStringList("ShopCategories");
		for (String shop : shops) {
			List<String> contents = config.getStringList(shop+".Contents");
			shopItems.put(shop, contents);
		}
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
	
	public Map<String, List<String>> getShopCategories() {
		return shopItems;
	}
	
	void reloadShops() {
		loadShops();
	}

	public ItemStack getSelectionItem(String categoryName) {
		FileConfiguration config = getConfig();
		ConfigurationSection section = config.getConfigurationSection(categoryName);
		ConfigurationSection settings = section.getConfigurationSection("Settings");
		String displayName = ChatColor.translateAlternateColorCodes('&', settings.getString("DisplayName"));
		displayName = displayName.replaceAll("_", " ");
		Material mat = Material.getMaterial(settings.getString("DisplayItem"));
		int data = settings.getInt("DisplayData");
		boolean highlight = settings.getBoolean("Highlight");
		int shopItemsAmount = section.getStringList("Contents").size();
		
		ItemStack selectionItem = new ItemStack(mat,1,(byte) data);
		
		if (highlight) {
			selectionItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		
		ItemMeta meta = selectionItem.getItemMeta();
		meta.setDisplayName(displayName);
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Available items: &6"+shopItemsAmount));
		meta.setLore(lore);
		selectionItem.setItemMeta(meta);
		
		return selectionItem;
	}
}
