package fr.uniqsky.itchibot.listeners.listeners;

import java.awt.Color;

import org.bukkit.Bukkit;

import fr.uniqsky.itchibot.DiscordUtil;
import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.listeners.DiscordListenerAdapter;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public class NewUserListener extends DiscordListenerAdapter implements DiscordUtil {

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		// Send welcome message
		TextChannel tc = getTextChannel(ItchiBot.get().getConfigManager().getNewChannel());
		if (tc == null) {
			Bukkit.getLogger().warning(
					"NewUserListener: Cannot access channel " + ItchiBot.get().getConfigManager().getNewChannel());
			return;
		}
		tc.sendMessage(createEmbededMessageWithFields(Color.white, ItchiBot.get().getConfigManager().getNewTitle(), "",
				ItchiBot.get().getConfigManager().getNewFooter()
						.replace("%number%", "" + e.getGuild().getMemberCount()),
				e.getMember().getUser().getAvatarUrl(),
				new String[] { ItchiBot.get().getConfigManager().getNewName().replace("%user%",
						e.getMember().getUser().getAsMention()) },
				new String[] { ItchiBot.get().getConfigManager().getNewValue().replace("%user%",
						e.getMember().getUser().getAsMention()) }))
				.queue();
	}
}
