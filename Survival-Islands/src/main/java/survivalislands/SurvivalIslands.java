package survivalislands;

import org.bukkit.plugin.java.JavaPlugin;

import managers.CMDManager;
import managers.ConfigManager;
import managers.PlayerStatsManager;
import managers.WorldProcessInteractor;

public class SurvivalIslands extends JavaPlugin {

	private static SurvivalIslands instance;
	
	@Override
	public void onLoad() {
		// TODO Loading configuration
	}
	
	@Override
	public void onEnable() {
		// TODO Setting configuration
		instance = this;
		
		getCommand("SurvivalIsland").setExecutor(CMDManager.getManager());
		getCommand("SI").setExecutor(CMDManager.getManager());
		getCommand("SIM").setExecutor(CMDManager.getManager());
		getCommand("SIR").setExecutor(CMDManager.getManager());
		
		//loading manager(s)
		ConfigManager.getManager();
		PlayerStatsManager.getManager();
		WorldProcessInteractor.getManager();
	}
	
	@Override
	public void onDisable() {
		// TODO Saving configuration
	}
	
	public static SurvivalIslands getInstance() {
		return instance;
	}
}