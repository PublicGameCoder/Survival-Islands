package islands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import managers.ShopManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.milkbowl.vault.economy.Economy;
import survivalislands.SurvivalIslands;

public class WorkerNPC implements Listener {
	private PlayerIsland island;
	private NPC npc;
	private Inventory GUI_Main;
	private Map<Selection, ItemStack> selections;
	
	public enum Selection {
		SHOP,
		STONEREGEN,
		INFO
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
		
		//StoneRegenItem
		ItemStack stoneRegenItem = new ItemStack(Material.STONE,1);
		meta = stoneRegenItem.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Regenerate");
		List<String> lores = new ArrayList<String>();
		
		lores.add("");
		lores.add(ChatColor.translateAlternateColorCodes('&', "&7Buy to regenerate specific layer(s) on your island!"));
		lores.add("");
		
		meta.setLore(lores);
		stoneRegenItem.setItemMeta(meta);
		
		selections.put(Selection.STONEREGEN, stoneRegenItem);
		
		//IslandInfo
		ItemStack islandInfo = new ItemStack(Material.BOOK,1);
		meta = islandInfo.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Info");
		lores = new ArrayList<String>();
		
		lores.add("");
		lores.add(ChatColor.translateAlternateColorCodes('&', "&7Level:&6 "+getIsland().getIslandLevel()));
		lores.add("");
		
		meta.setLore(lores);
		islandInfo.setItemMeta(meta);
		
		selections.put(Selection.INFO, islandInfo);
	}

	private void setupGUIs() {
		GUI_Main = Bukkit.createInventory(null, 1 * 9, "Manager");
		GUI_Main.setItem(1, getSelection(Selection.SHOP));
		GUI_Main.setItem(7, getSelection(Selection.STONEREGEN));
		GUI_Main.setItem(4, getSelection(Selection.INFO));
	}

	private ItemStack getSelection(Selection shop) {
		return selections.get(shop);
	}
	private void setSelection(Selection shop, ItemStack item) {
		selections.replace(shop, item);
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
	public void onGUIOpen(InventoryOpenEvent e) {
		if (e.getInventory() == null || !e.getInventory().getTitle().equals(GUI_Main.getTitle()))return;
		update();
	}
	
	private void update() {
		Player player = getIsland().getPlayer();
		List<String> lores = new ArrayList<String>();
		
		
		//Update Info
		ItemStack info = getSelection(Selection.INFO);
		ItemMeta meta = info.getItemMeta();
		lores = new ArrayList<String>();
		
		lores.add("");
		lores.add(ChatColor.translateAlternateColorCodes('&', "&7Owner:&6 "+player.getName()));
		lores.add("");
		Economy econ = SurvivalIslands.getEconomy();
		lores.add(ChatColor.translateAlternateColorCodes('&', "&7Your balance:&6 "+econ.getBalance(player)));
		lores.add("");
		lores.add(ChatColor.translateAlternateColorCodes('&', "&7Island level:&6 "+getIsland().getIslandLevel()));
		lores.add("");
		meta.setLore(lores);
		info.setItemMeta(meta);
		setSelection(Selection.INFO, info);
		GUI_Main.setItem(4, getSelection(Selection.INFO));
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
		
		if (item.equals(getSelection(Selection.STONEREGEN))) {
			ShopManager.getManager().openRegenShop(player);
		}
	}
	
	public PlayerIsland getIsland() {
		return this.island;
	}
	
	public NPC getNPC() {
		return this.npc;
	}
}
