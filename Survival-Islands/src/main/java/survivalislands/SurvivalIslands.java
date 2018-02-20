package survivalislands;

import org.bukkit.plugin.java.JavaPlugin;

import managers.CMDManager;

public class SurvivalIslands extends JavaPlugin {

	@Override
	public void onLoad() {
		// TODO Loading configuration
	}
	
	@Override
	public void onEnable() {
		// TODO Setting configuration
		getCommand("SurvivalIsland").setExecutor(CMDManager.getManager());
		getCommand("SI").setExecutor(CMDManager.getManager());
		getCommand("SIM").setExecutor(CMDManager.getManager());
		getCommand("SIR").setExecutor(CMDManager.getManager());
	}
	
	@Override
	public void onDisable() {
		// TODO Saving configuration
	}
}