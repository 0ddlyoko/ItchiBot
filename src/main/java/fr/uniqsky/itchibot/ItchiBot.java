package fr.uniqsky.itchibot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.uniqsky.itchibot.commands.discord.CommandManager;
import fr.uniqsky.itchibot.commands.spigot.ItchiBotCommand;
import fr.uniqsky.itchibot.config.ConfigManager;
import fr.uniqsky.itchibot.discord.DiscordManager;
import lombok.Getter;

public class ItchiBot extends JavaPlugin {
	private static ItchiBot INSTANCE;
	@Getter
	private ConfigManager configManager;
	@Getter
	private DiscordManager discordManager;
	@Getter
	private CommandManager commandManager;

	public ItchiBot() {
		INSTANCE = this;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		configManager = new ConfigManager();
		discordManager = new DiscordManager();
		commandManager = new CommandManager();
		Bukkit.getPluginCommand("itchibot").setExecutor(new ItchiBotCommand());
	}

	@Override
	public void onDisable() {
		if (discordManager != null)
			discordManager.stop();
	}

	public static ItchiBot get() {
		return INSTANCE;
	}
}
