package fr.uniqsky.itchibot.discord;

import java.awt.Color;
import java.util.List;
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
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
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
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		// Send welcome message
		try {
			TextChannel tc = jda.getTextChannelById(ItchiBot.get().getConfigManager().getNewChannel());
			tc.sendMessage(
					createEmbededMessageWithFields(Color.white, ItchiBot.get().getConfigManager().getNewTitle(), "",
							ItchiBot.get().getConfigManager().getNewFooter()
									.replace("%number%", "" + e.getGuild().getMemberCount()),
							e.getMember().getUser().getAvatarUrl(),
							new String[] { ItchiBot.get().getConfigManager().getNewName().replace("%user%",
									e.getMember().getUser().getAsMention()) },
							new String[] { ItchiBot.get().getConfigManager().getNewValue().replace("%user%",
									e.getMember().getUser().getAsMention()) }))
					.queue();
		} catch (InsufficientPermissionException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"No permission to read in channel " + ItchiBot.get().getConfigManager().getNewChannel(), ex);
		} catch (ErrorResponseException ex) {
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING,
					"Exception while deny / write in channel " + ItchiBot.get().getConfigManager().getNewChannel(), ex);
		}
		System.out.println(e.getMember().getUser().getAsMention());
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// Do not accept private message
		if (e.isFromType(ChannelType.PRIVATE))
			return;
		if (e.getAuthor().equals(e.getJDA().getSelfUser())) {
			// Self bot
			return;
		}
		String message = e.getMessage().getContentRaw();
		// Test if it's in suggest channel
		if (ItchiBot.get().getConfigManager().getSuggestChannels().contains(e.getTextChannel().getId())) {
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
							ItchiBot.get().getConfigManager().getSuggestTitle().replace("%user%",
									e.getAuthor().getName()),
							ItchiBot.get().getConfigManager().getSuggestMessage().replace("%message%", message),
							ItchiBot.get().getConfigManager().getSuggestFooter(), e.getAuthor().getAvatarUrl()))
					.queue(msg -> {
						msg.addReaction(ItchiBot.get().getConfigManager().getReactionValid()).queue(success -> {
							msg.addReaction(ItchiBot.get().getConfigManager().getReactionInvalid()).queue();
						});
					});
			return;
		} else if (message.startsWith(ItchiBot.get().getConfigManager().getAcceptCommand() + " ")) {
			if (ItchiBot.get().getConfigManager().getAcceptAllowUsers().contains(e.getAuthor().getId())) {
				// Accept suggest
				String id = message.substring(ItchiBot.get().getConfigManager().getAcceptCommand().length() + 1);
				try {
					Long.parseLong(id);
				} catch (Exception ex) {
					e.getChannel().sendMessage("Please enter a correct id").queue();
					return;
				}
				// Try to search in which channel the message is
				Message m = getMessageInChannels(id, ItchiBot.get().getConfigManager().getSuggestChannels());
				if (m == null) {
					// Message not found
					e.getMessage().addReaction(ItchiBot.get().getConfigManager().getAcceptNotFound()).queue();
					return;
				}
				// Get raw message
				String[] all = getTitleMsgImg(m, ItchiBot.get().getConfigManager().getSuggestMessage(),
						ItchiBot.get().getConfigManager().getSuggestTitle());
				String title = all[0];
				String msg = all[1];
				String imageUrl = all[2];
				if (msg == null || "".equalsIgnoreCase(msg.trim())) {
					// Unknown message or empty message. WTF ?
					e.getMessage().addReaction(ItchiBot.get().getConfigManager().getAcceptNotFound()).queue();
					return;
				}
				// Delete old message
				e.getMessage().delete().queue();

				// Send new message
				try {
					TextChannel tc = jda.getTextChannelById(ItchiBot.get().getConfigManager().getAcceptChannel());
					tc.sendMessage(createEmbededMessage(Color.white, title,
							ItchiBot.get().getConfigManager().getSuggestMessage().replace("%message%", msg),
							ItchiBot.get().getConfigManager().getAcceptFooter(), imageUrl)).queue();
					// Delete
					m.delete().queue();
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
		} else if (message.startsWith(ItchiBot.get().getConfigManager().getDenyCommand() + " ")) {
			if (ItchiBot.get().getConfigManager().getDenyAllowUsers().contains(e.getAuthor().getId())) {
				// Deny suggest
				String id = message.substring(ItchiBot.get().getConfigManager().getDenyCommand().length() + 1);
				try {
					Long.parseLong(id);
				} catch (Exception ex) {
					e.getChannel().sendMessage("Please enter a correct id").queue();
					return;
				}
				// Try to search in which channel the message is
				Message m = getMessageInChannels(id, ItchiBot.get().getConfigManager().getSuggestChannels());
				if (m == null) {
					// Message not found
					e.getMessage().addReaction(ItchiBot.get().getConfigManager().getDenyNotFound()).queue();
					return;
				}
				// Get raw message
				String[] all = getTitleMsgImg(m, ItchiBot.get().getConfigManager().getSuggestMessage(),
						ItchiBot.get().getConfigManager().getSuggestTitle());
				String title = all[0];
				String msg = all[1];
				String imageUrl = all[2];
				if (msg == null || "".equalsIgnoreCase(msg.trim())) {
					// Unknown message or empty message. WTF ?
					e.getMessage().addReaction(ItchiBot.get().getConfigManager().getDenyNotFound()).queue();
					return;
				}
				// Delete old message
				e.getMessage().delete().queue();

				// Send new message
				try {
					TextChannel tc = jda.getTextChannelById(ItchiBot.get().getConfigManager().getDenyChannel());
					tc.sendMessage(createEmbededMessage(Color.white, title,
							ItchiBot.get().getConfigManager().getSuggestMessage().replace("%message%", msg),
							ItchiBot.get().getConfigManager().getDenyFooter(), imageUrl)).queue();
					// Delete
					m.delete().queue();
				} catch (InsufficientPermissionException ex) {
					Bukkit.getLogger().log(Level.SEVERE,
							"No permission to read in channel " + ItchiBot.get().getConfigManager().getDenyChannel(),
							ex);
				} catch (ErrorResponseException ex) {
				} catch (Exception ex) {
					Bukkit.getLogger().log(Level.WARNING, "Exception while deny / write in channel "
							+ ItchiBot.get().getConfigManager().getDenyChannel(), ex);
				}

				return;
			} else {
				e.getMessage().addReaction(ItchiBot.get().getConfigManager().getNoPerm()).queue();
				return;
			}
		} else if (message.trim().equalsIgnoreCase(ItchiBot.get().getConfigManager().getHelpCommand())) {
			e.getChannel()
					.sendMessage(createEmbededMessageWithFields(Color.white,
							ItchiBot.get().getConfigManager().getHelpTitle(), null,
							ItchiBot.get().getConfigManager().getHelpFooter(), null,
							ItchiBot.get().getConfigManager().getHelpShowTitle(),
							ItchiBot.get().getConfigManager().getHelpShowValues()))
					.queue();
		}
		
		String msg= e.getMessage().getContentRaw();
	 	String[] split = msg.split(" ");
	 	if(split[0].equalsIgnoreCase("&clear") && split.length==2) { 
	 		if(!isInteger(split[1])) {
	 			e.getTextChannel().sendMessage("Veuillez entrer une valeur correct").complete();
	 			return;
	 		}
	 		int clear = Integer.parseInt(split[1]);
	 		if(clear>100) {
	 			e.getTextChannel().sendMessage("Oulah tu veux me tuer ? entre une valeur inférieur à 100 s'il te plait !").complete();
	 			return;
	 		}
	 		
	 		MessageHistory History = e.getChannel().getHistoryBefore(e.getMessage() , clear).complete();
	 		for(Message  m :History.getRetrievedHistory()) {
	 			m.delete().queue();
	 		}
				}
	 	return;
		
	}
		


public static boolean isInteger(String s) {
return isInteger(s,10);
}

public static boolean isInteger(String s, int radix) {
if(s.isEmpty()) return false;
for(int i = 0; i < s.length(); i++) {
if(i == 0 && s.charAt(i) == '-') {
    if(s.length() == 1) return false;
    else continue;
}
if(Character.digit(s.charAt(i),radix) < 0) return false;
}
return true;
}

	private Message getMessageInChannels(String messageId, List<String> channelsId) {
		Message m = null;
		for (String channelId : ItchiBot.get().getConfigManager().getSuggestChannels()) {
			try {
				TextChannel tc = jda.getTextChannelById(channelId);
				m = tc.retrieveMessageById(messageId).complete();
				if (m != null)
					break;
			} catch (InsufficientPermissionException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "No permission to read in channel " + channelId, ex);
			} catch (ErrorResponseException ex) {
			} catch (Exception ex) {
				Bukkit.getLogger().log(Level.WARNING, "Exception while retrieving message from " + channelId, ex);
			}
		}
		return m;
	}

	private MessageEmbed createEmbededMessage(Color color, String title, String description, String footer,
			String thumbnail) {
		EmbedBuilder em = new EmbedBuilder();
		em.setThumbnail(thumbnail);
		em.setColor(Color.white);
		em.setTitle(title);
		em.setDescription(description);
		em.setFooter(footer);
		return em.build();
	}

	private MessageEmbed createEmbededMessageWithFields(Color color, String title, String description, String footer,
			String thumbnail, String fieldNames[], String fieldValues[]) {
		EmbedBuilder em = new EmbedBuilder();
		em.setThumbnail(thumbnail);
		em.setColor(Color.white);
		em.setTitle(title);
		em.setDescription(description);
		em.setFooter(footer);
		for (int i = 0; i < Math.min(fieldNames.length, fieldValues.length); i++) {
			em.addField(fieldNames[i], fieldValues[i], true);
		}
		return em.build();
	}

	/**
	 * Get the title, the message and the image of specific Message.<br />
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
	private String[] getTitleMsgImg(Message m, String msgFormat, String titleFormat) {
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

	public void stop() {
		// Stop the app
		if (jda != null)
			jda.shutdownNow();
	}
}
