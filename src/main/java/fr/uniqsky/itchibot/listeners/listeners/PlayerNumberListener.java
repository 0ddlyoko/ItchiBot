package fr.uniqsky.itchibot.listeners.listeners;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import fr.uniqsky.itchibot.DiscordUtil;
import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.listeners.DiscordListenerAdapter;
import net.dv8tion.jda.api.entities.GuildChannel;

public class PlayerNumberListener extends DiscordListenerAdapter implements DiscordUtil {
	private BukkitTask playerNumberScheduler;
	private GuildChannel playerNumberChannel;
	private int playerSize = -1;
	private int maxPlayerSize = -1;

	public PlayerNumberListener() {
		playerNumberChannel = getChannel(ItchiBot.get().getConfigManager().getPlayerNumberChannel());
		if (playerNumberChannel == null) {
			Bukkit.getLogger().warning("PlayerNumberListener: Cannot access channel "
					+ ItchiBot.get().getConfigManager().getPlayerNumberChannel());
			return;
		}
		// Start the scheduler
		playerNumberScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(ItchiBot.get(), () -> {
			int playerSize = Bukkit.getOnlinePlayers().size();
			int maxPlayerSize = Bukkit.getMaxPlayers();
			// Update ONLY if it changes
			if (this.playerSize != playerSize || this.maxPlayerSize != maxPlayerSize)
				playerNumberChannel.getManager()
						.setName(ItchiBot.get().getConfigManager().getPlayerNumberMessage()
								.replace("%number%", "" + playerSize).replace("%maxnumber%", "" + maxPlayerSize))
						.queue();
		}, 1L, 100L);
	}

	@Override
	public void stop() {
		if (playerNumberScheduler != null)
			Bukkit.getScheduler().cancelTask(playerNumberScheduler.getTaskId());
		if (playerNumberChannel != null) {
			playerNumberChannel.getManager().setName(ItchiBot.get().getConfigManager().getPlayerNumberMessage()
					.replace("%number%", "???").replace("%maxnumber%", "???")).queue();
		}
		super.stop();
	}
}
