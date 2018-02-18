package utilities;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class chatUtil {
	
	private static String prefix = "&8[&6Survival&7-&6Islands&8] &7";

	public static void sendMessage(Player p, String message, boolean usePrefix) {
		message = "&7"+message;
		if (usePrefix) {
			message = prefix + message;
		}
		message = ChatColor.translateAlternateColorCodes('&', message);
		p.sendMessage(message);
	}

	public static void sendMessage(Player p, String[] messages, boolean usePrefix) {
		for (String message : messages) {
			sendMessage(p,message, usePrefix);
		}
	}
	
	public static void sendMessage(Player p, String message) {
		sendMessage(p, message, false);
	}
	
	public static void sendMessage(Player p, String[] messages) {
		sendMessage(p,messages, false);
	}
}
