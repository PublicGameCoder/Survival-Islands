package survivalislands;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import managers.CMDManager;
import managers.ConfigManager;
import managers.IslandsManager;
import managers.PickaxeLevelingManager;
import managers.PlayerStatsManager;
import managers.ShopConfigManager;
import managers.ShopManager;
import managers.WorldProcessInteractor;
import net.citizensnpcs.api.CitizensPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import utilities.TeleportFix;

public class SurvivalIslands extends JavaPlugin {

	private static SurvivalIslands instance;
	private static WorldEditPlugin worldEdit;
	private static CitizensPlugin citizens;
	
	private static final Logger log = Logger.getLogger("Minecraft");
	private static Economy econ = null;
    private static Permission perms = null;
    private static boolean permsActive = false;
    
	@Override
	public void onEnable() {
		instance = this;
		
		if (!setupWorldedit() ) {
            log.severe(String.format("[%s] - Disabled due to no WorldEdit dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		if (!setupCitizens() ) {
            log.severe(String.format("[%s] - Disabled due to no Citizens dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		setupPermissions();
		permsActive = perms.hasGroupSupport();
		
        getCommand("SurvivalIsland").setExecutor(CMDManager.getManager());
		getCommand("SI").setExecutor(CMDManager.getManager());
		getCommand("SIM").setExecutor(CMDManager.getManager());
		getCommand("SIR").setExecutor(CMDManager.getManager());
		
		//loading manager(s)
		ConfigManager.getManager();
		PlayerStatsManager.getManager();
		WorldProcessInteractor.getManager();
		IslandsManager.getManager();
		ShopConfigManager.getManager();
		ShopManager.getManager();
		PickaxeLevelingManager.getManager();
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new TeleportFix(getInstance(), getServer()), getInstance());
		log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}

	private boolean setupWorldedit() {
		if ((worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit")) == null) {
            return false;
        }
		return true;
	}
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	private boolean setupCitizens() {
		if ((citizens = (CitizensPlugin) getServer().getPluginManager().getPlugin("Citizens")) == null) {
            return false;
        }
		return true;
	}
	
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return (perms != null);
    }
	
	@Override
	public void onDisable() {
		IslandsManager.getManager().unloadAll();
		WorldProcessInteractor.getManager().deleteByDisable();
		log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}
	
	public static SurvivalIslands getInstance() {
		return instance;
	}
	
	public static WorldEditPlugin getWorldEdit() {
		return worldEdit;
	}
	
	public static CitizensPlugin getCitizens() {
		return citizens;
	}
	
	public static Economy getEconomy() {
        return econ;
    }
    
    public static Permission getPermissions() {
        return perms;
    }

	public static boolean isPermissionsActive() {
		return permsActive;
	}
}