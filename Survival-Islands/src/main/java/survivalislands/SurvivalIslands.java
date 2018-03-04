package survivalislands;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import managers.CMDManager;
import managers.ConfigManager;
import managers.IslandsManager;
import managers.PlayerStatsManager;
import managers.WorldProcessInteractor;
import utilities.TeleportFix;

public class SurvivalIslands extends JavaPlugin {

	private static SurvivalIslands instance;
	private static WorldEditPlugin worldEdit;
	
	@Override
	public void onLoad() {
		worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
	}
	
	@Override
	public void onEnable() {
		instance = this;
		
		getCommand("SurvivalIsland").setExecutor(CMDManager.getManager());
		getCommand("SI").setExecutor(CMDManager.getManager());
		getCommand("SIM").setExecutor(CMDManager.getManager());
		getCommand("SIR").setExecutor(CMDManager.getManager());
		
		//loading manager(s)
		ConfigManager.getManager();
		PlayerStatsManager.getManager();
		WorldProcessInteractor.getManager();
		IslandsManager.getManager();
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new TeleportFix(getInstance(), getServer()), getInstance());
		
	}
	
	@Override
	public void onDisable() {
		IslandsManager.getManager().unloadAll();
		WorldProcessInteractor.getManager().deleteByDisable();
	}
	
	public static SurvivalIslands getInstance() {
		return instance;
	}

	public static WorldEditPlugin getWorldEdit() {
		return worldEdit;
	}
}