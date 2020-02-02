package fr.uniqsky.itchibot.listeners.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.uniqsky.itchibot.DiscordUtil;
import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.listeners.DiscordListenerAdapter;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ChatListener extends DiscordListenerAdapter implements Listener, DiscordUtil {
	private TextChannel chatChannel;

	public ChatListener() {
		// Send welcome message
		chatChannel = getTextChannel(ItchiBot.get().getConfigManager().getChatChannel());
		if (chatChannel == null) {
			Bukkit.getLogger().warning(
					"ChatListener: Cannot access channel " + ItchiBot.get().getConfigManager().getChatChannel());
			return;
		}
		Bukkit.getPluginManager().registerEvents(this, ItchiBot.get());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled())
			return;
		if (chatChannel != null) {
			String msg = ChatColor.stripColor(String.format(e.getFormat(), e.getPlayer().getName(), e.getMessage()));
			// String msg =
			// ChatColor.stripColor(ItchiBot.get().getConfigManager().getChatFormat()
			// .replace("%pseudo%", e.getPlayer().getDisplayName()).replace("%message%",
			// e.getMessage()));
			chatChannel.sendMessage(msg.replaceAll("@", "[at]")).queue();
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// if (chatChannel == null)
		// return;
		// // Do not accept private messages
		// if (!e.isFromGuild() || e.isFromType(ChannelType.PRIVATE))
		// return;
		// // Self bot
		// if (e.getAuthor().equals(e.getJDA().getSelfUser()))
		// return;
		// String message = e.getMessage().getContentRaw();
		// // Test if it's in chat channel
		// if (!e.getTextChannel().getId().equalsIgnoreCase(chatChannel.getId()))
		// return;
		// String id = e.getAuthor().getId();
		// String user = ItchiBot.get().getConfigManager().getChatUsers().get(id);
		// if (user != null) {
		// // User is in the list, send message
		// String msg = ItchiBot.get().getConfigManager().getChatMc().replace("%user%",
		// user).replace("%message%",
		// message);
		// Bukkit.broadcastMessage(msg);
		// }
	}
}
