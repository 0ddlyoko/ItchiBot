package fr.uniqsky.itchibot.commands.spigot;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.__;

public class ItchiBotCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("itchibot".equalsIgnoreCase(command.getLabel())) {
			if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
				// Help
				sender.sendMessage(ChatColor.YELLOW + "-----------[" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW
						+ "]-----------");
				sender.sendMessage(
						ChatColor.AQUA + "- /itchibot info" + ChatColor.YELLOW + " : Get informations about ItchiBot");
				sender.sendMessage(ChatColor.AQUA + "- /itchibot help" + ChatColor.YELLOW + " : Show this message");
				if (sender.hasPermission("itchibot.reload"))
					sender.sendMessage(
							ChatColor.AQUA + "- /itchibot reload" + ChatColor.YELLOW + " : Reload config file");
			} else if ("info".equalsIgnoreCase(args[0])) {
				sender.sendMessage(ChatColor.YELLOW + "-----------[" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW
						+ "]-----------");
				sender.sendMessage(ChatColor.AQUA + "Created by 0ddlyoko & Grandoz_");
				sender.sendMessage(ChatColor.GREEN + "v" + ItchiBot.get().getDescription().getVersion());
				sender.sendMessage(ChatColor.AQUA + "https://www.0ddlyoko.be");
				sender.sendMessage(ChatColor.AQUA + "https://www.github.com/0ddlyoko");
			} else if ("reload".equalsIgnoreCase(args[0])) {
				if (sender.hasPermission("itchibot.reload")) {
					// Reload
					sender.sendMessage(__.PREFIX + ChatColor.GREEN + "Reloading ...");
					ItchiBot.get().getConfigManager().reload();
					sender.sendMessage(__.PREFIX + ChatColor.GREEN + "Reloaded");
				}
			}
		}
		return false;
	}
}
