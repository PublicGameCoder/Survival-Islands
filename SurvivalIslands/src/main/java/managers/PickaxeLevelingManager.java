package managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import survivalislands.SurvivalIslands;

public class PickaxeLevelingManager implements Listener {
	
	private static PickaxeLevelingManager instance;

	public static PickaxeLevelingManager getManager() {
		if (instance == null) {
			instance = new PickaxeLevelingManager();
		}
		return instance;
	}
	
	private Map<Material, Map<Integer, Map<Material, Float>>> chancesMap;
	Random chanceCalculator;
	private int blocksBrokenToLvlUp = 75;
	
	public enum pickaxeType {
		Wood_Pickaxe(Material.WOOD_PICKAXE),
		Stone_Pickaxe(Material.STONE_PICKAXE),
		Iron_Pickaxe(Material.IRON_PICKAXE),
		Gold_Pickaxe(Material.GOLD_PICKAXE),
		Diamond_Pickaxe(Material.DIAMOND_PICKAXE);
		
		private Material _material;
		
		pickaxeType(Material material) {
			this._material = material;
		}
		
		public Material getMaterial() {
			return _material;
		}
	}
	
	private PickaxeLevelingManager() {
		chancesMap = ConfigManager.getManager().getPickaxeChances();
		chanceCalculator = new Random();
		
		SurvivalIslands.getInstance().getServer().getPluginManager().registerEvents(this, SurvivalIslands.getInstance());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onOreBreak(BlockBreakEvent e) {
		ItemStack pickaxe = e.getPlayer().getItemInHand();
		boolean valid = false;
		for (pickaxeType pickaxeType : pickaxeType.values()) {
			if (pickaxe.getType() == pickaxeType.getMaterial()) {
				valid = true;
				break;
			}
		}
		if (!valid)return;
		Block block = e.getBlock();
		Material oreType = block.getType();
		
		if (oreType != Material.COAL_ORE
			&& oreType != Material.IRON_ORE
			&& oreType != Material.GOLD_ORE
			&& oreType != Material.DIAMOND_ORE
			&& oreType != Material.EMERALD_ORE
			&& oreType != Material.LAPIS_ORE
			&& oreType != Material.REDSTONE_ORE
			&& oreType != Material.GLOWING_REDSTONE_ORE
			&& oreType != Material.QUARTZ_ORE
			&& oreType != Material.STONE
			)return;
		
		Player p = e.getPlayer();
		World w = p.getLocation().getWorld();
		
		e.setCancelled(true);
		int pickaxeLvl = getCurrentLvl(pickaxe);
		int brokenblocks = getCurrentBlocksBroken(pickaxe);
		brokenblocks++;
		setCurrentBlocksBroken(pickaxe,brokenblocks);
		
		block.setType(Material.AIR);
		
		Map<Integer, Map<Material, Float>> chances = chancesMap.get(pickaxe.getType());
		if (chances == null || chances.isEmpty() || !pickaxe.hasItemMeta() || !pickaxe.getItemMeta().hasLore()) {
			w.dropItem(block.getLocation(), new ItemStack(Material.COBBLESTONE,1));
			return;
		}
		
		Map<Material, Float> ores = chances.get(pickaxeLvl);
		if (ores == null || ores.isEmpty()) {
			w.dropItem(block.getLocation(), new ItemStack(Material.COBBLESTONE,1));
			return;
		}
		
		float luck = chanceCalculator.nextFloat() * 100;
		Material lowest = Material.STONE;
		float lowestChance = 100.0f;
		for (Entry<Material, Float> entry : ores.entrySet()) {
			if (entry.getValue() > 0.0f && lowestChance >= entry.getValue() && entry.getValue() >= luck) {
				lowestChance = entry.getValue();
				lowest = entry.getKey();
			}
		}
		
		switch (lowest) {
		case COAL_ORE:
			w.dropItem(block.getLocation(), new ItemStack(Material.COAL,1));
			break;
		case IRON_ORE:
			w.dropItem(block.getLocation(), new ItemStack(Material.IRON_ORE,1));
			break;
		case GOLD_ORE:
			w.dropItem(block.getLocation(), new ItemStack(Material.GOLD_ORE,1));
			break;
		case DIAMOND_ORE:
			w.dropItem(block.getLocation(), new ItemStack(Material.DIAMOND,1));
			break;
		case EMERALD_ORE:
			w.dropItem(block.getLocation(), new ItemStack(Material.EMERALD,1));
			break;
		case LAPIS_ORE:
			w.dropItem(block.getLocation(), new ItemStack(Material.INK_SACK,1,(short) 0,(byte) 4));
			break;
		case REDSTONE_ORE:
			w.dropItem(block.getLocation(), new ItemStack(Material.REDSTONE,1));
			break;
		case GLOWING_REDSTONE_ORE:
			w.dropItem(block.getLocation(), new ItemStack(Material.REDSTONE,1));
			break;
		case QUARTZ_ORE:
			w.dropItem(block.getLocation(), new ItemStack(Material.QUARTZ,1));
			break;
		default:
			w.dropItem(block.getLocation(), new ItemStack(Material.COBBLESTONE,1));
			break;
		}
	}

	private int getCurrentBlocksBroken(ItemStack pickaxe) {
		ItemMeta meta = pickaxe.getItemMeta();
		List<String> lore = new ArrayList<String>();
		if (meta.hasLore()) {
			lore = meta.getLore();
		}
		if (lore == null) {
			setCurrentLvl(pickaxe, 0);
			meta = pickaxe.getItemMeta();
		}
		int amount = -1;
		for (int i = 0; i < lore.size(); i++) {
			String line = lore.get(i);
			if (line.contains("Blocks Broken:")) {
				amount = Integer.parseInt(ChatColor.stripColor(line.split("Blocks Broken: ")[1]));
				break;
			}
		}
		if (amount == -1) {
			setCurrentBlocksBroken(pickaxe, 0);
			amount = 0;
		}
		return amount;
	}

	
	private void setCurrentBlocksBroken(ItemStack pickaxe, int amount) {
		ItemMeta meta = pickaxe.getItemMeta();
		List<String> lore = new ArrayList<String>();
		if (meta.hasLore()) {
			lore = meta.getLore();
		}
		List<String> newLore = new ArrayList<String>();
		boolean exists = false;
		for (int i = 0; i < lore.size(); i++) {
			String line = lore.get(i);
			if (line.contains("Blocks Broken:")) {
				line = ChatColor.translateAlternateColorCodes('&', "&7Blocks Broken: &6"+amount);
				exists = true;
			}
			newLore.add(line);
		}
		if (!exists) {
			newLore.add(ChatColor.translateAlternateColorCodes('&', "&7Blocks Broken: &6"+amount));
		}
		meta.setLore(newLore);
		pickaxe.setItemMeta(meta);
		
		setCurrentLvl(pickaxe, (int) amount / blocksBrokenToLvlUp);
	}
	
	private int getCurrentLvl(ItemStack pickaxe) {
		ItemMeta meta = pickaxe.getItemMeta();
		List<String> lore = new ArrayList<String>();
		if (meta.hasLore()) {
			lore = meta.getLore();
		}
		int level = -1;
		for (int i = 0; i < lore.size(); i++) {
			String line = lore.get(i);
			if (line.contains("Level:")) {
				level = Integer.parseInt(ChatColor.stripColor(line.split("Level: ")[1]));
				break;
			}
		}
		if (level == -1) {
			setCurrentLvl(pickaxe, 0);
			level = 0;
		}
		return level;
	}

	private void setCurrentLvl(ItemStack pickaxe, int lvl) {
		int maxLvl = 0;
		Material type = pickaxe.getType();
		maxLvl = (type == pickaxeType.Wood_Pickaxe.getMaterial())? 3 : maxLvl;
		maxLvl = (type == pickaxeType.Stone_Pickaxe.getMaterial())? 5 : maxLvl;
		maxLvl = (type == pickaxeType.Iron_Pickaxe.getMaterial())? 10 : maxLvl;
		maxLvl = (type == pickaxeType.Gold_Pickaxe.getMaterial())? 25 : maxLvl;
		maxLvl = (type == pickaxeType.Diamond_Pickaxe.getMaterial())? 50 : maxLvl;
		
		if (lvl > maxLvl) {
			lvl = maxLvl;
		}
		
		ItemMeta meta = pickaxe.getItemMeta();
		List<String> lore = new ArrayList<String>();
		if (meta.hasLore()) {
			lore = meta.getLore();
		}
		List<String> newLore = new ArrayList<String>(lore.size());
		boolean exists = false;
		for (int i = 0; i < lore.size(); i++) {
			String line = lore.get(i);
			if (line.contains("Level:")) {
				line = ChatColor.translateAlternateColorCodes('&', "&7Level: &6"+lvl);
				exists = true;
			}
			newLore.add(line);
		}
		if (!exists) {
			newLore.add(ChatColor.translateAlternateColorCodes('&', "&7Level: &6"+lvl));
		}
		meta.setLore(newLore);
		pickaxe.setItemMeta(meta);
	}

}
