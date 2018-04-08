package islands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import managers.ShopManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import survivalislands.SurvivalIslands;

public class WorkerNPC implements Listener {
	private PlayerIsland island;
	private NPC npc;
	private Inventory GUI_Main;
	private Map<Selection, ItemStack> selections;
	
	public enum Selection {
		SHOP
	}
	
	public WorkerNPC(PlayerIsland island) {
		this.island = island;
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		npc = registry.createNPC(EntityType.PLAYER, "Manager");
		npc.setProtected(true);
		npc.setFlyable(false);
		
		setupSelections();
		setupGUIs();
		
		SurvivalIslands.getInstance().getServer().getPluginManager().registerEvents(this, SurvivalIslands.getInstance());
	}
	
	private void setupSelections() {
		selections = new HashMap<Selection, ItemStack>();
		
		//ShopsItem
		ItemStack shopItem = new ItemStack(Material.GOLD_INGOT,1);
		ItemMeta meta = shopItem.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Shops");
		shopItem.setItemMeta(meta);
		
		selections.put(Selection.SHOP, shopItem);
	}

	private void setupGUIs() {
		GUI_Main = Bukkit.createInventory(null, 1 * 9, "Manager");
		GUI_Main.setItem(1, getSelection(Selection.SHOP));
	}

	private ItemStack getSelection(Selection shop) {
		return selections.get(shop);
	}

	public boolean spawn() {
		if (npc.isSpawned()) {
			npc.despawn(DespawnReason.PENDING_RESPAWN);
		}
		boolean success = npc.spawn(island.getNPCLocation());
		if (success) {
			npc.getEntity().setCustomNameVisible(true);
		}
		return success;
	}
	
	@EventHandler
	public void onRightClick(NPCRightClickEvent e) {
		if (getIsland() == null || getIsland().getPlayer() == null ||!e.getClicker().getName().equalsIgnoreCase(getIsland().getPlayer().getName()) || e.getNPC().getId() != npc.getId())return;
		Player p = e.getClicker();
		openMenu(p);
	}
	
	public void openMenu(Player p) {
		p.openInventory(GUI_Main);
	}
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent e) {
		if (e.getInventory() == null || !e.getInventory().getTitle().equals(GUI_Main.getTitle()))return;
		ItemStack item = e.getCurrentItem();
		if (item == null)return;
		Player player = (Player) e.getWhoClicked();
		e.setCancelled(true);
		
		if (item.equals(getSelection(Selection.SHOP))) {
			ShopManager.getManager().openShop(player);
		}
	}
	
	public PlayerIsland getIsland() {
		return this.island;
	}
	
	public NPC getNPC() {
		return this.npc;
	}
}
