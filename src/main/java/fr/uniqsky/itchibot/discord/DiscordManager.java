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
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class DiscordManager implements EventListener {
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
	public void onEvent(GenericEvent e) {
		if (e instanceof ReadyEvent)
			System.out.println("API is ready!");
		if (e instanceof MessageReceivedEvent)
			onMessage((MessageReceivedEvent) e);
	}

	private void onMessage(MessageReceivedEvent e) {
		if (e.getAuthor().equals(e.getJDA().getSelfUser())) {
			// Self bot
			return;
		}
		// Test if it's in suggest channel
		if (ItchiBot.get().getConfigManager().getSuggestChannels().contains(e.getTextChannel().getId())) {
			String suggest = e.getMessage().getContentRaw();
			// Empty message, delete it
			if (suggest.trim().equalsIgnoreCase("")) {
				e.getMessage().delete().queue();
				return;
			}
			EmbedBuilder em = new EmbedBuilder();
			// em.setAuthor(e.getAuthor().getName());
			em.setThumbnail(e.getAuthor().getAvatarUrl());
			em.setColor(Color.white);
			em.setTitle(ItchiBot.get().getConfigManager().getSuggestTitle().replace("%user%", e.getAuthor().getName()));
			em.addField("", ItchiBot.get().getConfigManager().getSuggestMessage().replace("%message%", suggest), false);
			em.setFooter(ItchiBot.get().getConfigManager().getSuggestFooter());
			// Delete old message
			e.getMessage().delete().queue();
			// Send new message
			e.getChannel().sendMessage(em.build()).queue(msg -> {
				msg.addReaction(ItchiBot.get().getConfigManager().getReactionValid()).queue(success -> {
					msg.addReaction(ItchiBot.get().getConfigManager().getReactionInvalid()).queue();
				});
			});
		}
	}

	public void stop() {
		// Stop the app
		if (jda != null)
			jda.shutdownNow();
	}
}
