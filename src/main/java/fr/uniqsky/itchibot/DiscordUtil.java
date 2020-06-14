package fr.uniqsky.itchibot;

import java.awt.Color;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public interface DiscordUtil {

	public default TextChannel getTextChannel(String id) {
		try {
			return ItchiBot.get().getDiscordManager().getJda().getTextChannelById(id);
		} catch (InsufficientPermissionException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "No permission to read text channel " + id, ex);
		} catch (ErrorResponseException ex) {
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, "Exception while accept / write in channel " + id, ex);
		}
		return null;
	}

	public default VoiceChannel getVoiceChannel(String id) {
		try {
			return ItchiBot.get().getDiscordManager().getJda().getVoiceChannelById(id);
		} catch (InsufficientPermissionException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "No permission to read voice channel " + id, ex);
		} catch (ErrorResponseException ex) {
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, "Exception while accept / write in channel " + id, ex);
		}
		return null;
	}

	public default GuildChannel getChannel(String id) {
		try {
			return ItchiBot.get().getDiscordManager().getJda().getGuildChannelById(id);
		} catch (InsufficientPermissionException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "No permission to read voice channel " + id, ex);
		} catch (ErrorResponseException ex) {
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, "Exception while accept / write in channel " + id, ex);
		}
		return null;
	}

	public default MessageEmbed createEmbededMessage(Color color, String title, String description, String footer,
			String thumbnail) {
		EmbedBuilder em = new EmbedBuilder();
		em.setThumbnail(thumbnail);
		em.setColor(Color.white);
		em.setTitle(title);
		em.setDescription(description);
		em.setFooter(footer);
		return em.build();
	}

	public default MessageEmbed createEmbededMessageWithFields(Color color, String title, String description,
			String footer, String thumbnail, String fieldNames[], String fieldValues[]) {
		EmbedBuilder em = new EmbedBuilder();
		em.setThumbnail(thumbnail);
		em.setColor(Color.white);
		em.setTitle(title);
		em.setDescription(description);
		em.setFooter(footer);
		for (int i = 0; i < Math.min(fieldNames.length, fieldValues.length); i++) {
			em.addField(fieldNames[i], fieldValues[i], false);
		}
		return em.build();
	}

	/**
	 * Get the title, the message and the image of specific Message.
	 * <ul>
	 * <li>If the contentRaw of the message returns something, msg will be the
	 * rawMessage formatted with msgFormat</li>
	 * <li>If the contentRaw of the message return nothings (so the message is a
	 * MessageEmbed), msg if either the first value of first non-empty field
	 * otherwise the description</li>
	 * <li>The imageUrl is either the avatar url of the author of the message or the
	 * thumbnail of the MessageEmbed if it's a MessageEmbed</li>
	 * <li>The title is either the author name formatted with titleFormat or the
	 * actual title of the MessageEmbed
	 * <li>
	 * </ul>
	 * 
	 * @param m
	 *                        The Message
	 * @param msgFormat
	 *                        the format to use for message
	 * @param titleFormat
	 *                        the format to use for title
	 * @return
	 */
	public default String[] getTitleMsgImg(Message m, String msgFormat, String titleFormat) {
		// Get raw message
		String msg = m.getContentRaw();
		String imageUrl = m.getAuthor().getAvatarUrl();
		String title = titleFormat.replace("%user%", m.getAuthor().getName());
		if (msg == null || "".equalsIgnoreCase(msg)) {
			// Test embeded message
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
		} else
			msg = msgFormat.replace("%message%", msg);
		return new String[] { title, msg, imageUrl };
	}

	public default Message getMessageInChannels(String messageId, List<String> channelsId) {
		Message m = null;
		for (String channelId : ItchiBot.get().getConfigManager().getSuggestChannels()) {
			try {
				TextChannel tc = getTextChannel(channelId);
				m = tc.retrieveMessageById(messageId).complete();
				if (m != null)
					break;
			} catch (InsufficientPermissionException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "No permission to read messages in channel " + channelId, ex);
			} catch (ErrorResponseException ex) {
			} catch (Exception ex) {
				Bukkit.getLogger().log(Level.WARNING, "Exception while retrieving message from " + channelId, ex);
			}
		}
		return m;
	}
}
