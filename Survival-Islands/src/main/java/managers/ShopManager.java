package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import survivalislands.SurvivalIslands;

public class ShopManager implements Listener{
	private static ShopManager instance;
	private Inventory Shop_Main;
	private int mainTotalRows;
	private int categoryTotalRows;
	private Map<ItemStack, List<ItemStack>> shopsContent;
	private Map<ItemStack, List<Inventory>> shops;
	
	public static ShopManager getManager() {
		if (instance == null) {
			instance = new ShopManager();
		}
		return instance;
	}
	
	private ShopManager() {
		mainTotalRows = 2;
		categoryTotalRows = 6;
		Shop_Main = Bukkit.createInventory(null, mainTotalRows * 9, ChatColor.GOLD+"Shop");
		loadShopCategories();
		
		for (ItemStack itemStack : shops.keySet()) {
			Shop_Main.addItem(itemStack);
		}
		
		SurvivalIslands.getInstance().getServer().getPluginManager().registerEvents(this, SurvivalIslands.getInstance());
	}
	
	private void loadShopCategories() {
		shopsContent = new HashMap<ItemStack, List<ItemStack>>();
		Map<String, List<String>> categories = ShopConfigManager.getManager().getShopCategories();
		for (Entry<String, List<String>> entry : categories.entrySet()) {
			String categoryName = entry.getKey();
			List<String> contents = entry.getValue();
			List<ItemStack> items = new ArrayList<ItemStack>();
			for (String content : contents) {
				ItemStack item = parseItemFromString(content);
				items.add(item);
			}
			
			ItemStack categoryItem = ShopConfigManager.getManager().getSelectionItem(categoryName);
			shopsContent.put(categoryItem, items);
		}
		
		shops = new HashMap<ItemStack, List<Inventory>>();
		for (Entry<ItemStack, List<ItemStack>> entry : shopsContent.entrySet()) {
			List<Inventory> pages = new ArrayList<Inventory>();
			List<ItemStack> items = entry.getValue();
			int shopItemSlotsAmount = (categoryTotalRows - 2) * 9;
			int pageAmount = (int) Math.ceil(((float) items.size()) / ((float)(shopItemSlotsAmount)));
			
			for (int i = 0; i < pageAmount; i++) {
				String displayName = entry.getKey().getItemMeta().getDisplayName() + " "+ChatColor.GOLD+(i+1)+ChatColor.DARK_GRAY+"/"+ChatColor.GOLD+pageAmount;
				displayName = displayName.replaceAll("_", " ");
				try {
					pages.add(Bukkit.createInventory(null, categoryTotalRows * 9, displayName));
				}catch (Exception e) {
					System.out.println("Display name is too long: "+displayName);
					e.printStackTrace();
				}
			}
			
			Iterator<ItemStack> it = items.iterator();
			int itemCounter = 0;
			int pageIndex = 0;
			while(it.hasNext()) {
				itemCounter++;
				if (itemCounter > shopItemSlotsAmount) {
					itemCounter = 1;
					pageIndex++;
				}
				Inventory inv = pages.get(pageIndex);
				inv.setItem(itemCounter + 9 - 1, it.next());
			}
			shops.put(entry.getKey(), pages);
		}
		
	}

	private ItemStack parseItemFromString(String content) {
		String[] args = content.split(":");
		
		Material mat = Material.getMaterial(args[0]);
		int amount = Integer.parseInt(args[1]);
		int data = Integer.parseInt(args[2]);
		float buyPrice = Float.parseFloat(args[3]);
		float sellPrice = Float.parseFloat(args[4]);
		
		ItemStack item = new ItemStack(mat,amount,(byte)data);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Buy&8: &6"+buyPrice+"&7/each"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Sell&8: &6"+sellPrice+"&7/each"));
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}

	public void openShop(Player p) {
		p.openInventory(Shop_Main);
	}
	
	public void reload() {
		ShopConfigManager.getManager().reloadShops();
		loadShopCategories();
	}
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent e) {
		if (e.getInventory() == null || !e.getInventory().getTitle().equals(Shop_Main.getTitle()))return;
		ItemStack item = e.getCurrentItem();
		if (item == null)return;
		Player player = (Player) e.getWhoClicked();
		e.setCancelled(true);
		
		for (Entry<ItemStack, List<Inventory>> entry : shops.entrySet()) {
			if (item.getItemMeta().getDisplayName().equalsIgnoreCase(entry.getKey().getItemMeta().getDisplayName())) {
				player.openInventory(entry.getValue().get(0));
				break;
			}
		}
	}
	
	@EventHandler
	public void onPageClick(InventoryClickEvent e) {
		if (e.getInventory() == null)return;
		
		ItemStack item = e.getCurrentItem();
		if (item == null)return;
		
		ItemStack selectorItem = getSelectorItemFromPage(e.getInventory());
		if (selectorItem == null)return;
		
		e.setCancelled(true);
		
		@SuppressWarnings("unused")
		int currentPageIndex = getPageIndex(selectorItem, e.getInventory());
	}
	
	private ItemStack getSelectorItemFromPage(Inventory inv) {
		for (Entry<ItemStack, List<Inventory>> invs : shops.entrySet()) {
			for (Inventory inventory : invs.getValue()) {
				if (inv.getTitle().equalsIgnoreCase(inventory.getTitle())) {
					return invs.getKey();
				}
			}
		}
		return null;
	}
	
	private int getPageIndex(ItemStack selector,Inventory inv) {
		int pageIndex = 0;
		for (Inventory inventory : shops.get(selector)) {
			if (inv.getTitle().equalsIgnoreCase(inventory.getTitle())) {
				return pageIndex;
			}
			pageIndex++;
		}
		return -1;
	}
}
