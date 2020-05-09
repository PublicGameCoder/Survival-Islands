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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import managers.Transaction.TransactionType;
import survivalislands.SurvivalIslands;

public class ShopManager implements Listener{
	private static ShopManager instance;
	private Inventory Shop_Main;
	private int mainTotalRows;
	private int categoryTotalRows;
	private Map<ItemStack, List<ItemStack>> shopsContent;
	public Map<ItemStack, String> shopItemPrices;
	private Map<ItemStack, List<Inventory>> shops;
	private List<Transaction> pending;;
	
	private ItemStack nextPageItem;
	private ItemStack prevPageItem;
	private ItemStack prevMenuItem;
	private ItemStack backToNPCMenu;
	
	public static ShopManager getManager() {
		if (instance == null) {
			instance = new ShopManager();
		}
		return instance;
	}
	
	private ShopManager() {
		pending = new ArrayList<Transaction>();
		createCustomItems();
		mainTotalRows = 2;
		categoryTotalRows = 6;
		Shop_Main = Bukkit.createInventory(null, mainTotalRows * 9, ChatColor.GOLD+"Shop");
		loadShopCategories();
		
		Shop_Main.setItem((mainTotalRows * 9) - 9, backToNPCMenu);
		for (ItemStack itemStack : shops.keySet()) {
			Shop_Main.addItem(itemStack);
		}
		
		SurvivalIslands.getInstance().getServer().getPluginManager().registerEvents(this, SurvivalIslands.getInstance());
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Iterator<Transaction> it = pending.iterator();
				
				while (it.hasNext()) {
					Transaction transaction = it.next();
					if (transaction.isFinished()) {
						InventoryClickEvent.getHandlerList().unregister(transaction);
						InventoryCloseEvent.getHandlerList().unregister(transaction);
						it.remove();
					}
				}
			}
		}.runTaskTimer(SurvivalIslands.getInstance(), 0, 40);
	}
	
	@SuppressWarnings("deprecation")
	private void createCustomItems() {
		nextPageItem = new ItemStack(Material.SKULL_ITEM, 1,(short) 0, (byte)3);
		SkullMeta meta1 = (SkullMeta) nextPageItem.getItemMeta();
		meta1.setOwner("MHF_ArrowRight");
		meta1.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3Next page &7-->"));
		nextPageItem.setItemMeta(meta1);
		
		prevPageItem = new ItemStack(Material.SKULL_ITEM, 1,(short) 0, (byte)3);
		SkullMeta meta2 = (SkullMeta) prevPageItem.getItemMeta();
		meta2.setOwner("MHF_ArrowLeft");
		meta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7<-- &3Previous page"));
		prevPageItem.setItemMeta(meta2);
		
		prevMenuItem = new ItemStack(Material.BARRIER, 1);
		ItemMeta meta3 = prevMenuItem.getItemMeta();
		meta3.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3Back to shop menu"));
		prevMenuItem.setItemMeta(meta3);
		
		backToNPCMenu = new ItemStack(Material.BARRIER, 1);
		ItemMeta meta4 = prevMenuItem.getItemMeta();
		meta4.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3Back to island menu"));
		backToNPCMenu.setItemMeta(meta4);
	}

	private void loadShopCategories() {
		shopItemPrices = new HashMap<ItemStack, String>();
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
				Inventory page = null;
				try {
					page = Bukkit.createInventory(null, categoryTotalRows * 9, displayName);
				}catch (Exception e) {
					System.out.println("Display name is too long: "+displayName);
					e.printStackTrace();
					continue;
				}
				
				page.setItem(0, prevMenuItem);
				
				pages.add(page);
			}
			
			for (int i = 0; i < pages.size(); i++) {
				if (i+1 < pages.size() && i+1 >= 0 && pages.get(i+1) != null) {
					pages.get(i).setItem((categoryTotalRows * 9) - 1, nextPageItem);
				}
				
				if (i-1 < pages.size() && i-1 >= 0 && pages.get(i-1) != null) {
					pages.get(i).setItem((categoryTotalRows * 9) - 9, prevPageItem);
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
		
		shopItemPrices.put(item.clone(), buyPrice+":"+sellPrice);//Add it to shopItemPrices before lore has been set.
		
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
	
	public void openRegenShop(Player player) {
		Transaction transaction = new Transaction(TransactionType.BUYLAYERREGEN, player, null, null);
		pending.add(transaction);
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
		
		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())return;
		if (item.getItemMeta().getDisplayName().equalsIgnoreCase(backToNPCMenu.getItemMeta().getDisplayName())) {
			IslandsManager.getManager().openIslandManager(player);
		}
		
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
		if (item == null || item.getType() == Material.AIR)return;
		
		ItemStack selectorItem = getSelectorItemFromPage(e.getInventory());
		if (selectorItem == null)return;
		
		e.setCancelled(true);
		Player player = (Player) e.getWhoClicked();
		
		int currentPageIndex = getPageIndex(selectorItem, e.getInventory());
		
		if (item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(nextPageItem.getItemMeta().getDisplayName()) && hasNext(selectorItem, currentPageIndex)) {
			Inventory next = getNext(selectorItem, currentPageIndex);
			if (next != null) {
				player.openInventory(next);
			}
		}
		
		if (item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(prevPageItem.getItemMeta().getDisplayName()) && hasPrev(selectorItem, currentPageIndex)) {
			Inventory prev = getPrev(selectorItem, currentPageIndex);
			if (prev != null) {
				player.openInventory(prev);
			}
		}
		
		if (item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(prevMenuItem.getItemMeta().getDisplayName())) {
			player.openInventory(Shop_Main);
		}
		
		for (ItemStack shopItem : shopsContent.get(selectorItem)) {
			if (item.getItemMeta().toString().equalsIgnoreCase(shopItem.getItemMeta().toString())) {
				ClickType clickType = e.getClick();
				if (clickType == ClickType.LEFT) {
					Transaction transaction = new Transaction(TransactionType.BUYITEM, player, shopItem, e.getInventory());
					this.pending.add(transaction);
				}else if (clickType == ClickType.RIGHT) {
					Transaction transaction = new Transaction(TransactionType.SELLITEM, player, shopItem, e.getInventory());
					this.pending.add(transaction);
				}
				break;
			}
		}
	}

	private Inventory getNext(ItemStack selectorItem, int currentPageIndex) {
		List<Inventory> pages = shops.get(selectorItem);
		if (pages == null || pages.isEmpty()) return null;
		int next = currentPageIndex + 1;
		return pages.get(next);
	}
	
	private Inventory getPrev(ItemStack selectorItem, int currentPageIndex) {
		List<Inventory> pages = shops.get(selectorItem);
		if (pages == null || pages.isEmpty()) return null;
		int prev = currentPageIndex - 1;
		return pages.get(prev);
	}

	private boolean hasNext(ItemStack selectorItem, int currentPageIndex) {
		List<Inventory> pages = shops.get(selectorItem);
		if (pages == null || pages.isEmpty()) return false;
		int next = currentPageIndex + 1;
		return (next < pages.size() && next >= 0 && pages.get(next) != null);
	}
	
	private boolean hasPrev(ItemStack selectorItem, int currentPageIndex) {
		List<Inventory> pages = shops.get(selectorItem);
		if (pages == null || pages.isEmpty()) return false;
		int prev = currentPageIndex - 1;
		return (prev < pages.size() && prev >= 0 && pages.get(prev) != null);
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
