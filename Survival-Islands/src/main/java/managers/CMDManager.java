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

	private String _label;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You need to be a player to execute this command!");
			return false;
		}
		_label = label;
		Player p = (Player) sender;
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("Create") && (p.hasPermission("SurvivalIsland.All") || p.hasPermission("SurvivalIsland.Admin") || p.hasPermission("SurvivalIsland.Anywhere") || p.hasPermission("SurvivalIsland.User"))) {
				// TODO Implement Island Creation Functionality.
				
			}
			
			if (args[0].equalsIgnoreCase("Manager") && (p.hasPermission("SurvivalIsland.All") || p.hasPermission("SurvivalIsland.Admin") || p.hasPermission("SurvivalIsland.Anywhere"))) {
				// TODO Implement External Island Manager Access Functionality.
				
			}
			
			if (args[0].equalsIgnoreCase("Reload") && (p.hasPermission("SurvivalIsland.All") || p.hasPermission("SurvivalIsland.Admin"))) {
				// TODO Implement Reload Functionality.
				
			}
		}
		
		printHelp(p);
		return false;
	}

	private void printHelp(Player p) {
		String[] messages = null;
		if (p.hasPermission("SurvivalIsland.All")) {
			messages = new String[] {
					"&8=========[&6Survival&7-&6Islands &cHelp&8]=======",
					"/"+_label+" Create | Creates your Survival Island.",
					"/"+_label+" Manager | Access your manager from anywhere.",
					"/"+_label+" Reload  | Reloads all of the configs.",
					"&8==========================================================="
			};
			
			chatUtil.sendMessage(p,messages);
			return;
		}
		
		if (p.hasPermission("SurvivalIsland.Admin")) {
			messages = new String[] {
					"&8=========[&6Survival&7-&6Islands &cHelp&8]=======",
					"/"+_label+" Create | Creates your Survival Island.",
					"/"+_label+" Manager | Access your manager from anywhere.",
					"/"+_label+" Reload  | Reloads all of the configs.",
					"&8==========================================================="
			};
			
			chatUtil.sendMessage(p,messages);
			return;
		}
		
		if (p.hasPermission("SurvivalIsland.Manager.Anywhere")) {
			messages = new String[] {
					"&8=========[&6Survival&7-&6Islands &cHelp&8]=======",
					"/"+_label+" Create | Creates your Survival Island.",
					"/"+_label+" Manager | Access your manager from anywhere.",
					"&8==========================================================="
			};
			
			chatUtil.sendMessage(p,messages);
			return;
		}
		
		if (p.hasPermission("SurvivalIsland.User")) {
			
			messages = new String[] {
					"&8=========[&6Survival&7-&6Islands &cHelp&8]=======",
					"/"+_label+" Create | Creates your Survival Island.",
					"&8==========================================================="
			};
			
			chatUtil.sendMessage(p,messages);
			return;
		}
	}

}
