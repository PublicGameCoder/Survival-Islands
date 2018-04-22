package managers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import islands.PlayerIsland;
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
		if (label.equalsIgnoreCase("SIM")) {
			String[] newArgs = new String[(args.length == 0)? 1 : args.length];
			newArgs[0] = "Manager";
			for (int i = 1; i <= args.length; i++) {
				newArgs[i] = args[i-1];
			}
			args = newArgs;
		}
		if (label.equalsIgnoreCase("SIR")) {
			String[] newArgs = new String[(args.length == 0)? 1 : args.length];
			newArgs[0] = "Reload";
			for (int i = 1; i <= args.length; i++) {
				newArgs[i] = args[i-1];
			}
			args = newArgs;
		}
		label = "SurvivalIsland";
		_label = label;
		Player p = (Player) sender;
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("Create") && (p.hasPermission("SurvivalIsland.All") || p.hasPermission("SurvivalIsland.Admin") || p.hasPermission("SurvivalIsland.Anywhere") || p.hasPermission("SurvivalIsland.User"))) {
				IslandsManager.getManager().loadIsland(p, false);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("Home") && (p.hasPermission("SurvivalIsland.All") || p.hasPermission("SurvivalIsland.Admin") || p.hasPermission("SurvivalIsland.Anywhere") || p.hasPermission("SurvivalIsland.User"))) {
				PlayerIsland island = IslandsManager.getManager().getIslandOf(p);
				boolean success = true;
				boolean firstTime = false;
				if (island == null) {
					success = IslandsManager.getManager().loadIsland(p, true);
					island = IslandsManager.getManager().getIslandOf(p);
					firstTime = true;
				}
				if (!success) {
					chatUtil.sendMessage(p, ChatColor.GRAY+"Something went wrong. please contact a staff!", true);
				}else {
					if (!firstTime) {
						island.homing(p);
					}
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("Manager") && (p.hasPermission("SurvivalIsland.All") || p.hasPermission("SurvivalIsland.Admin") || p.hasPermission("SurvivalIsland.Anywhere"))) {
				IslandsManager.getManager().openIslandManager(p);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("Reload") && (p.hasPermission("SurvivalIsland.All") || p.hasPermission("SurvivalIsland.Admin"))) {
				IslandsManager.getManager().unloadAll();
				ShopManager.getManager().reload();
				return true;
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
					"/"+_label+" Home | Go to your Survival Island Home.",
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
					"/"+_label+" Home | Go to your Survival Island Home.",
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
					"/"+_label+" Home | Go to your Survival Island Home.",
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
					"/"+_label+" Home | Go to your Survival Island Home.",
					"&8==========================================================="
			};
			
			chatUtil.sendMessage(p,messages);
			return;
		}
	}

}
