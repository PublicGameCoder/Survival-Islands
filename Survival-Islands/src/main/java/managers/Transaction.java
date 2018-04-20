package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import survivalislands.SurvivalIslands;
import utilities.chatUtil;

public class Transaction implements InventoryHolder, Listener {

	private static int ID;
	
	private boolean _finished;
	private int _id;
	private Inventory _GUI;
	private TransactionType _type;
	private UUID uuid;
	private ItemStack item;
	private double _costEach;
	private String _title;
	
	private ItemStack addOne, addFive, addThirdyTwo, addSixtyFour; 
	private ItemStack removeOne, removeFive, removeThirdyTwo, removeSixtyFour;
	private ItemStack info, cancel, finish;
	
	private Inventory _from;
	
	private int amount;
	
	public enum TransactionType {
		BUY,
		SELL
	}
	
	public Transaction(TransactionType type, Player player, ItemStack shopItem, Inventory from) {
		this._finished = false;
		this._id = (ID++);
		this._type = type;
		this.uuid = player.getUniqueId();
		this.item = shopItem;
		this.amount = 1;
		this._from = from;
		
		String costs = getItemCosts(item);
		if (costs == null) {
			this._finished = true;
			return;
		}
		String[] args = costs.split(":");
		this._costEach = 0;
		if (type == TransactionType.BUY) {
			this._costEach = Double.parseDouble(args[0]);
			this._title = ChatColor.translateAlternateColorCodes('&', "&7Transaction: &aBuy");
		}else if (type == TransactionType.SELL) {
			this._costEach = Double.parseDouble(args[1]);
			this._title = ChatColor.translateAlternateColorCodes('&', "&7Transaction: &cSell");
		}
		
		createGUI();
		
		player.openInventory(this._GUI);
		SurvivalIslands.getInstance().getServer().getPluginManager().registerEvents(this, SurvivalIslands.getInstance());
	}
	
	private void createGUI() {
		createItems();
		this._GUI = Bukkit.createInventory(this, 6 * 9, this._title);
		
		this._GUI.setItem(18, addOne);
		this._GUI.setItem(19, addFive);
		this._GUI.setItem(20, addThirdyTwo);
		this._GUI.setItem(21, addSixtyFour);
		
		this._GUI.setItem(13, info);
		this._GUI.setItem(45, cancel);
		this._GUI.setItem(53, finish);
		
		this._GUI.setItem(23, removeOne);
		this._GUI.setItem(24, removeFive);
		this._GUI.setItem(25, removeThirdyTwo);
		this._GUI.setItem(26, removeSixtyFour);
		
		updateInfo();
	}

	@SuppressWarnings("deprecation")
	private void createItems() {
		addOne = new ItemStack(Material.STAINED_CLAY, 1);
		ItemMeta meta = addOne.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a+1"));
		addOne.setItemMeta(meta);
		
		addFive = new ItemStack(Material.IRON_BLOCK, 1);
		meta = addFive.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a+5"));
		addFive.setItemMeta(meta);
		
		addThirdyTwo = new ItemStack(Material.GOLD_BLOCK, 1);
		meta = addThirdyTwo.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a+32"));
		addThirdyTwo.setItemMeta(meta);
		
		addSixtyFour = new ItemStack(Material.DIAMOND_BLOCK, 1);
		meta = addSixtyFour.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a+64"));
		addSixtyFour.setItemMeta(meta);
		
		info = item.clone();
		meta = info.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7Info"));
		meta.setLore(new ArrayList<String>());
		info.setItemMeta(meta);
		
		cancel = new ItemStack(Material.WOOL, 1, (short) 0, (byte) 14);
		meta = cancel.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cCancel"));
		cancel.setItemMeta(meta);
		
		finish = new ItemStack(Material.WOOL, 1, (short) 0, (byte) 5);
		meta = finish.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Finish"));
		finish.setItemMeta(meta);
		
		removeOne = new ItemStack(Material.STAINED_CLAY, 1);
		meta = removeOne.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c-1"));
		removeOne.setItemMeta(meta);
		
		removeFive = new ItemStack(Material.IRON_BLOCK, 1);
		meta = removeFive.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c-5"));
		removeFive.setItemMeta(meta);
		
		removeThirdyTwo = new ItemStack(Material.GOLD_BLOCK, 1);
		meta = removeThirdyTwo.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c-32"));
		removeThirdyTwo.setItemMeta(meta);
		
		removeSixtyFour = new ItemStack(Material.DIAMOND_BLOCK, 1);
		meta = removeSixtyFour.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c-64"));
		removeSixtyFour.setItemMeta(meta);
		
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (!e.getInventory().getTitle().equalsIgnoreCase(this._GUI.getTitle()))return;
		this._finished = true;
	}
	
	@EventHandler
	public void onGUI(InventoryClickEvent e) {
		if (!e.getInventory().getTitle().equalsIgnoreCase(this._GUI.getTitle()))return;
		ItemStack selected = e.getCurrentItem();
		if (selected == null || selected.getType() == Material.AIR)return;
		e.setCancelled(true);
		
		
		if (selected.getItemMeta().getDisplayName().equalsIgnoreCase(finish.getItemMeta().getDisplayName())) {
			finish();
			Player player = getPlayer();
			if (player == null) {
				this._finished = true;
				return;
			}
			player.openInventory(this._from);
		}
		
		if (selected.getItemMeta().toString().equalsIgnoreCase(cancel.getItemMeta().toString())) {
			Player player = getPlayer();
			if (player == null) {
				this._finished = true;
				return;
			}
			player.openInventory(this._from);
		}
		
		if (selected.getItemMeta().getDisplayName().equalsIgnoreCase(addOne.getItemMeta().getDisplayName())) {
			amount += 1;
			if (amount < 1) {
				amount = 1;
			}
			updateInfo();
		}
		
		if (selected.getItemMeta().getDisplayName().equalsIgnoreCase(addFive.getItemMeta().getDisplayName())) {
			amount += 5;
			if (amount < 1) {
				amount = 1;
			}
			updateInfo();
		}
		
		if (selected.getItemMeta().getDisplayName().equalsIgnoreCase(addThirdyTwo.getItemMeta().getDisplayName())) {
			amount += 32;
			if (amount < 1) {
				amount = 1;
			}
			updateInfo();
		}
		
		if (selected.getItemMeta().getDisplayName().equalsIgnoreCase(addSixtyFour.getItemMeta().getDisplayName())) {
			amount += 64;
			if (amount < 1) {
				amount = 1;
			}
			updateInfo();
		}
		//
		if (selected.getItemMeta().getDisplayName().equalsIgnoreCase(removeOne.getItemMeta().getDisplayName())) {
			amount -= 1;
			if (amount < 1) {
				amount = 1;
			}
			updateInfo();
		}
		
		if (selected.getItemMeta().getDisplayName().equalsIgnoreCase(removeFive.getItemMeta().getDisplayName())) {
			amount -= 5;
			if (amount < 1) {
				amount = 1;
			}
			updateInfo();
		}
		
		if (selected.getItemMeta().getDisplayName().equalsIgnoreCase(removeThirdyTwo.getItemMeta().getDisplayName())) {
			amount -= 32;
			if (amount < 1) {
				amount = 1;
			}
			updateInfo();
		}
		
		if (selected.getItemMeta().getDisplayName().equalsIgnoreCase(removeSixtyFour.getItemMeta().getDisplayName())) {
			amount -= 64;
			if (amount < 1) {
				amount = 1;
			}
			updateInfo();
		}
	}

	private void updateInfo() {
		List<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Amount: &6"+this.amount));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Cost&8: &6"+this._costEach+"&7/each"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Total: &6"+(this.amount * this._costEach)));
		lore.add("");
		ItemMeta meta = info.getItemMeta();
		meta.setLore(lore);
		info.setItemMeta(meta);
		this._GUI.setItem(13, info);
	}

	public boolean finish() {
		
		Economy econ = SurvivalIslands.getEconomy();
		Player player = getPlayer();
		if (player == null) {
			this._finished = true;
			return false;
		}
		
		double totalCost = this._costEach * this.amount;
		
		if (this._type == TransactionType.BUY) {
			EconomyResponse transactionPaper = econ.withdrawPlayer(player, totalCost);
			if (transactionPaper.transactionSuccess()) {
				chatUtil.sendMessage(player, "&a"+totalCost+"$ withdraw successful!", true);
				ItemStack invItem = getRawItem(this.item);
				for (int i = 0; i < this.amount; i++) {
					player.getInventory().addItem(invItem);
				}
				return true;
			}else {
				chatUtil.sendMessage(player, "&cYou can't afford this item!", true);
			}
		}else if (this._type == TransactionType.SELL) {
			ItemStack invItem = getRawItem(this.item);
			if (player.getInventory().contains(invItem.getType(), this.amount)) {
				Map<Integer, ItemStack> totalRests = new HashMap<Integer, ItemStack>();
				
				for (int i = 0; i < this.amount; i++) {
					Map<Integer, ItemStack> rests = player.getInventory().removeItem(invItem);
					for (Entry<Integer, ItemStack> entry : rests.entrySet()) {
						totalRests.put(entry.getKey(), entry.getValue());
					}
				}
				
				if (totalRests.isEmpty()) {
					EconomyResponse transactionPaper = econ.depositPlayer(player, totalCost);
					if (transactionPaper.transactionSuccess()) {
						chatUtil.sendMessage(player, "&a"+totalCost+"$ deposit successful!", true);
						return true;
					}else {
						chatUtil.sendMessage(player, "&cDeposit failure! please contact staff to deposit "+ totalCost +" to your account!", true);
					}
				}else {
					chatUtil.sendMessage(player, "&cSomething went wrong when trying to remove the selling item from your inventory!", true);
					chatUtil.sendMessage(player, "Please contact a staff member to report this problem", true);
				}
			}else {
				chatUtil.sendMessage(player, "You don't have enough of this item to sell it!", true);
			}
		}
		player.closeInventory();
		this._finished = true;
		return false;
	}
	
	private Player getPlayer() {
		return Bukkit.getPlayer(this.uuid);
	}

	@SuppressWarnings("deprecation")
	private String getItemCosts(ItemStack shopItem) {
		for (Entry<ItemStack, String> entry : ShopManager.getManager().shopItemPrices.entrySet()) {
			ItemStack key = entry.getKey();
			Material mat = key.getType();
			int amount = key.getAmount();
			byte data = key.getData().getData();
			if (shopItem.getType() == mat && shopItem.getAmount() == amount && shopItem.getData().getData() == data) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack getRawItem(ItemStack shopItem) {
		for (Entry<ItemStack, String> entry : ShopManager.getManager().shopItemPrices.entrySet()) {
			ItemStack key = entry.getKey();
			Material mat = key.getType();
			int amount = key.getAmount();
			byte data = key.getData().getData();
			if (shopItem.getType() == mat && shopItem.getAmount() == amount && shopItem.getData().getData() == data) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	@Override
	public Inventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getID() {
		return this._id;
	}
	
	public boolean isFinished() {
		return _finished;
	}

}
