package fr.uniqsky.itchibot.discord;

import java.awt.Color;
import java.util.logging.Level;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import fr.uniqsky.itchibot.ItchiBot;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordManager extends ListenerAdapter {
	private JDA jda;

	public DiscordManager() {
		try {
			jda = new JDABuilder(AccountType.BOT).setToken(ItchiBot.get().getConfigManager().getToken()).build();
			jda.addEventListener(this);
			jda.getPresence().setActivity(Activity.playing(ItchiBot.get().getConfigManager().getActivity()));
			jda.getPresence().setStatus(OnlineStatus.ONLINE);
		} catch (LoginException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "LoginException while registering bot", ex);
			Bukkit.getPluginManager().disablePlugin(ItchiBot.get());
			return;
		}
	}

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("API is ready!");
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (e.getAuthor().equals(e.getJDA().getSelfUser())) {
			// Self bot
			return;
		}
		String message = e.getMessage().getContentRaw();
		// Test if it's in suggest channel
		if (ItchiBot.get().getConfigManager().getSuggestChannels().contains(e.getTextChannel().getId())) {
			// Empty message, delete it
			if (message.trim().equalsIgnoreCase("")) {
				e.getMessage().delete().queue();
				return;
			}
			EmbedBuilder em = new EmbedBuilder();
			em.setThumbnail(e.getAuthor().getAvatarUrl());
			em.setColor(Color.white);
			em.setTitle(ItchiBot.get().getConfigManager().getSuggestTitle().replace("%user%", e.getAuthor().getName()));
			em.setDescription(ItchiBot.get().getConfigManager().getSuggestMessage().replace("%message%", message));
			em.setFooter(ItchiBot.get().getConfigManager().getSuggestFooter());
			// Delete old message
			e.getMessage().delete().queue();
			// Send new message
			e.getChannel().sendMessage(em.build()).queue(msg -> {
				msg.addReaction(ItchiBot.get().getConfigManager().getReactionValid()).queue(success -> {
					msg.addReaction(ItchiBot.get().getConfigManager().getReactionInvalid()).queue();
				});
			});
			return;
		}
		if (message.startsWith(ItchiBot.get().getConfigManager().getAcceptPrefix())) {
			if (ItchiBot.get().getConfigManager().getAcceptAllowUsers().contains(e.getAuthor().getId())) {
				// Accept suggest
				String id = message.substring(ItchiBot.get().getConfigManager().getAcceptPrefix().length() + 1);
				try {
					Long.parseLong(id);
				} catch (Exception ex) {
					e.getChannel().sendMessage("Please enter a correct id").queue();
					return;
				}
				// Try to search in which channel the message is
				Message m = null;
				for (String channelId : ItchiBot.get().getConfigManager().getSuggestChannels()) {
					try {
						TextChannel tc = jda.getTextChannelById(channelId);
						m = tc.retrieveMessageById(id).complete();
						if (m != null)
							break;
					} catch (InsufficientPermissionException ex) {
						Bukkit.getLogger().log(Level.SEVERE, "No permission to read in channel " + channelId, ex);
					} catch (ErrorResponseException ex) {
					} catch (Exception ex) {
						Bukkit.getLogger().log(Level.WARNING, "Exception while retrieving message from " + channelId,
								ex);
					}
				}
				if (m == null) {
					// Message not found
					e.getMessage().addReaction(ItchiBot.get().getConfigManager().getAcceptNotFound()).queue();
					return;
				}
				// Get raw message
				String msg = ItchiBot.get().getConfigManager().getSuggestMessage().replace("%message%",
						m.getContentRaw());
				String imageUrl = m.getAuthor().getAvatarUrl();
				String title = ItchiBot.get().getConfigManager().getSuggestTitle().replace("%user%",
						m.getAuthor().getName());
				if (msg == null || "".equalsIgnoreCase(msg)) {
					// Test embeded messag
					if (m.getEmbeds().size() >= 1) {
						// Get first embed
						MessageEmbed me = m.getEmbeds().get(0);
						imageUrl = me.getThumbnail() == null ? null : me.getThumbnail().getUrl();
						title = me.getTitle();
						for (Field f : me.getFields()) {
							msg = f.getValue();
							if (msg != null && !"".equalsIgnoreCase(msg))
								break;
						}
						if (msg == null || "".equalsIgnoreCase(msg))
							msg = me.getDescription();
					}
				}
				if (msg == null || "".equalsIgnoreCase(msg.trim())) {
					// Unknown message or empty message. WTF ?
					e.getMessage().addReaction(ItchiBot.get().getConfigManager().getAcceptNotFound()).queue();
					return;
				}
				EmbedBuilder em = new EmbedBuilder();
				em.setThumbnail(imageUrl);
				em.setColor(Color.white);
				em.setTitle(title);
				em.setDescription(ItchiBot.get().getConfigManager().getSuggestMessage().replace("%message%", msg));
				em.setFooter(ItchiBot.get().getConfigManager().getAcceptFooter());
				// Delete old message
				e.getMessage().delete().queue();
				// Send new message

				try {
					TextChannel tc = jda.getTextChannelById(ItchiBot.get().getConfigManager().getAcceptChannel());
					tc.sendMessage(em.build()).queue();
					m.addReaction(ItchiBot.get().getConfigManager().getAcceptEmojiValid()).queue();
				} catch (InsufficientPermissionException ex) {
					Bukkit.getLogger().log(Level.SEVERE,
							"No permission to read in channel " + ItchiBot.get().getConfigManager().getAcceptChannel(),
							ex);
				} catch (ErrorResponseException ex) {
				} catch (Exception ex) {
					Bukkit.getLogger().log(Level.WARNING, "Exception while accept / write in channel "
							+ ItchiBot.get().getConfigManager().getAcceptChannel(), ex);
				}

				return;
			} else {
				e.getMessage().addReaction(ItchiBot.get().getConfigManager().getNoPerm()).queue();
				return;
			}
		}
	}

	public void stop() {
		// Stop the app
		if (jda != null)
			jda.shutdownNow();
	}
}
