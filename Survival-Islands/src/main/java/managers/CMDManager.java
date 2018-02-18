package managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import utilities.chatUtil;

public class CMDManager implements CommandExecutor {

	private static CMDManager instance;

	public static CMDManager getManager() {
		if (instance == null) {
			instance = new CMDManager();
		}
		return instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You need to be a player to execute this command!");
			return false;
		}
		Player p = (Player) sender;
		
		printHelp(p);
		return false;
	}

	private void printHelp(Player p) {
		String[] messages = null;
		if (p.hasPermission("SurvivalIsland.User"))
		chatUtil.sendMessage(p,messages, true);
	}

}
