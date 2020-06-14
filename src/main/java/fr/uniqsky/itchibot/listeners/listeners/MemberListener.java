package fr.uniqsky.itchibot.listeners.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.__;
import fr.uniqsky.itchibot.listeners.DiscordListenerAdapter;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public class MemberListener extends DiscordListenerAdapter {

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		// Only accept from guilds
		if (!e.isFromGuild())
			return;
		if (!ItchiBot.get().getConfigManager().getMemberMessage().equalsIgnoreCase(e.getMessageId()))
			return;
		if (!ItchiBot.get().getConfigManager().getMemberReaction().equalsIgnoreCase(e.getReactionEmote().getId()))
			return;
		// Add role
		Role r = e.getGuild().getRoleById(ItchiBot.get().getConfigManager().getMemberRole());
		if (r == null) {
			Bukkit.getLogger()
					.warning(__.PREFIX + ChatColor.RED + "Cannot update role for user " + e.getUser().getAsTag()
							+ " : role " + ItchiBot.get().getConfigManager().getMemberRole() + " doesn't exist");
			return;
		}
		e.getGuild().addRoleToMember(e.getMember(), r).queue();
	}

	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		// Only accept from guilds
		if (!e.isFromGuild())
			return;
		if (!ItchiBot.get().getConfigManager().getMemberMessage().equalsIgnoreCase(e.getMessageId()))
			return;
		if (!ItchiBot.get().getConfigManager().getMemberReaction().equalsIgnoreCase(e.getReactionEmote().getId()))
			return;
		if (!ItchiBot.get().getConfigManager().getMemberReaction().equalsIgnoreCase(e.getReactionEmote().getId()))
			return;
		// Remove role
		Role r = e.getGuild().getRoleById(ItchiBot.get().getConfigManager().getMemberRole());
		if (r == null) {
			Bukkit.getLogger()
					.warning(__.PREFIX + ChatColor.RED + "Cannot update role for user " + e.getUser().getAsTag()
							+ " : role " + ItchiBot.get().getConfigManager().getMemberRole() + " doesn't exist");
			return;
		}
		e.getGuild().removeRoleFromMember(e.getMember(), r).queue();
	}
}
