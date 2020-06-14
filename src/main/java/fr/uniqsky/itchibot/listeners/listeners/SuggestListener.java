package fr.uniqsky.itchibot.listeners.listeners;

import java.awt.Color;

import fr.uniqsky.itchibot.DiscordUtil;
import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.listeners.DiscordListenerAdapter;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SuggestListener extends DiscordListenerAdapter implements DiscordUtil {

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// Do not accept private messages
		if (!e.isFromGuild() || e.isFromType(ChannelType.PRIVATE))
			return;
		// Self bot
		if (e.getAuthor().equals(e.getJDA().getSelfUser()))
			return;
		String message = e.getMessage().getContentRaw();
		// Test if it's in suggest channel
		if (!ItchiBot.get().getConfigManager().getSuggestChannels().contains(e.getTextChannel().getId()))
			return;
		// Empty message, delete it
		if ("".equalsIgnoreCase(message.trim())) {
			e.getMessage().delete().queue();
			return;
		}
		// Delete old message
		e.getMessage().delete().queue();
		// Send new message
		e.getChannel()
				.sendMessage(createEmbededMessage(Color.white,
						ItchiBot.get().getConfigManager().getSuggestTitle().replace("%user%", e.getAuthor().getName()),
						ItchiBot.get().getConfigManager().getSuggestMessage().replace("%message%", message),
						ItchiBot.get().getConfigManager().getSuggestFooter(), e.getAuthor().getAvatarUrl()))
				.queue(msg -> {
					msg.addReaction(ItchiBot.get().getConfigManager().getReactionValid()).queue(success -> {
						msg.addReaction(ItchiBot.get().getConfigManager().getReactionInvalid()).queue();
					});
				});
	}
}
