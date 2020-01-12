package fr.uniqsky.itchibot;

import org.bukkit.plugin.java.JavaPlugin;

import fr.uniqsky.itchibot.config.ConfigManager;
import fr.uniqsky.itchibot.discord.DiscordManager;
import lombok.Getter;

public class ItchiBot extends JavaPlugin {
	private static ItchiBot INSTANCE;
	@Getter
	private ConfigManager configManager;
	@Getter
	private DiscordManager discordManager;

	public ItchiBot() {
		INSTANCE = this;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		configManager = new ConfigManager();
		discordManager = new DiscordManager();
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
