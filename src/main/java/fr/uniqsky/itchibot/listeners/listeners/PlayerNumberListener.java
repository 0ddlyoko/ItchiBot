package fr.uniqsky.itchibot.listeners.listeners;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.uniqsky.itchibot.DiscordUtil;
import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.listeners.DiscordListenerAdapter;
import net.dv8tion.jda.api.entities.GuildChannel;

public class PlayerNumberListener extends DiscordListenerAdapter implements DiscordUtil, PluginMessageListener {
	private BukkitTask playerNumberScheduler;
	private GuildChannel playerNumberChannel;
	private int playerSize = -1;
	private int newPlayerSize = -1;
	private int maxPlayerSize = -1;
	private int newMaxPlayerSize = -1;

	public PlayerNumberListener() {
		playerNumberChannel = getChannel(ItchiBot.get().getConfigManager().getPlayerNumberChannel());
		if (playerNumberChannel == null) {
			Bukkit.getLogger().warning("PlayerNumberListener: Cannot access channel "
					+ ItchiBot.get().getConfigManager().getPlayerNumberChannel());
			return;
		}
		// Start the scheduler
		playerNumberScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(ItchiBot.get(), () -> {
			newMaxPlayerSize = Bukkit.getMaxPlayers();
			if (ItchiBot.get().getConfigManager().isPlayerNumberUseBungeecord()) {
				// Ask
				try {
					if (Bukkit.getOnlinePlayers().size() != 0)
						getCount(Bukkit.getOnlinePlayers().iterator().next());
					else
						newPlayerSize = -1;
				} catch (Exception ex) {
					Bukkit.getLogger().log(Level.SEVERE, "Cannot send PlayerCount request", ex);
				}
			} else {
				newPlayerSize = Bukkit.getOnlinePlayers().size();
			}
			// Update ONLY if it changes
			if (playerSize != newPlayerSize || maxPlayerSize != newMaxPlayerSize) {
				playerSize = newPlayerSize;
				maxPlayerSize = newMaxPlayerSize;
				playerNumberChannel.getManager()
						.setName(ItchiBot.get().getConfigManager().getPlayerNumberMessage()
								.replace("%number%", "" + playerSize).replace("%maxnumber%", "" + maxPlayerSize))
						.queue();

			}
		}, 1L, 100L);
		Bukkit.getMessenger().registerOutgoingPluginChannel(ItchiBot.get(), "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(ItchiBot.get(), "BungeeCord", this);
	}

	@Override
	public void stop() {
		if (playerNumberScheduler != null)
			Bukkit.getScheduler().cancelTask(playerNumberScheduler.getTaskId());
		if (playerNumberChannel != null) {
			playerNumberChannel.getManager().setName(ItchiBot.get().getConfigManager().getPlayerNumberMessage()
					.replace("%number%", "???").replace("%maxnumber%", "???")).queue();
		}
		Bukkit.getMessenger().unregisterOutgoingPluginChannel(ItchiBot.get(), "BungeeCord");
		Bukkit.getMessenger().unregisterIncomingPluginChannel(ItchiBot.get(), "BungeeCord", this);
		super.stop();
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		try {
			if (!"BungeeCord".equalsIgnoreCase(channel))
				return;

			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();

			if ("PlayerCount".equalsIgnoreCase(subchannel)) {
				String server = in.readUTF();
				if ("ALL".equalsIgnoreCase(server))
					this.newPlayerSize = in.readInt();
			}
		} catch (Exception ex) {
		}
	}

	public void getCount(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerCount");
		out.writeUTF("ALL");

		player.sendPluginMessage(ItchiBot.get(), "BungeeCord", out.toByteArray());
	}
}
