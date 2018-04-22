package managers;

import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import survivalislands.SurvivalIslands;

public class PickaxeLevelingManager implements Listener {
	
	private static PickaxeLevelingManager instance;

	public static PickaxeLevelingManager getManager() {
		if (instance == null) {
			instance = new PickaxeLevelingManager();
		}
		return instance;
	}
	
	private Map<Material, Map<Material,Float>> chancesMap;
	Random chanceCalculator;
	
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
			)return;
		
		Map<Material,Float> chances = chancesMap.get(pickaxe.getType());
		if (chances == null || chances.isEmpty()) {
			e.setCancelled(true);
			block.setType(Material.AIR);
			return;
		}
		Float chance = chances.get(oreType);
		if (chance == null || chance <= 0.0f) {
			e.setCancelled(true);
			block.setType(Material.AIR);
			return;
		}
		
		e.setCancelled(true);
		block.setType(Material.AIR);
		
		float luck = chanceCalculator.nextFloat() * 100;
		if (chance < luck) {
			return;
		}
		Player p = e.getPlayer();
		World w = p.getLocation().getWorld();
		switch (oreType) {
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
			break;
		}
	}

}
