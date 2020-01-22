package fr.uniqsky.itchibot.commands.commands;

import java.awt.Color;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import fr.uniqsky.itchibot.DiscordUtil;
import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.commands.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class DenyCmd implements Command, DiscordUtil {

	@Override
	public String getCmd() {
		return ItchiBot.get().getConfigManager().getDenyCommand();
	}

	@Override
	public boolean onCommand(String label, String[] args, MessageReceivedEvent e) {
		if (!ItchiBot.get().getConfigManager().getDenyAllowUsers().contains(e.getAuthor().getId())) {
			e.getMessage().addReaction(ItchiBot.get().getConfigManager().getNoPerm()).queue();
			return true;
		}
		if (args.length == 0) {
			e.getMessage().delete().queue();
			return true;
		}
		// Deny suggest
		try {
			Long.parseLong(args[0]);
		} catch (Exception ex) {
			e.getChannel().sendMessage("Please enter a correct id").queue();
			return true;
		}
		// Try to search in which channel the message is
		Message m = getMessageInChannels(args[0], ItchiBot.get().getConfigManager().getSuggestChannels());
		if (m == null) {
			// Message not found
			e.getMessage().addReaction(ItchiBot.get().getConfigManager().getDenyNotFound()).queue();
			return true;
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
			return true;
		}
		// Delete old message
		e.getMessage().delete().queue();

		// Send new message
		try {
			TextChannel tc = getTextChannel(ItchiBot.get().getConfigManager().getDenyChannel());
			tc.sendMessage(createEmbededMessage(Color.white, title,
					ItchiBot.get().getConfigManager().getSuggestMessage().replace("%message%", msg),
					ItchiBot.get().getConfigManager().getDenyFooter(), imageUrl)).queue();
			// Delete
			m.delete().queue();
		} catch (InsufficientPermissionException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"No permission to read in channel " + ItchiBot.get().getConfigManager().getDenyChannel(), ex);
		} catch (ErrorResponseException ex) {
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING,
					"Exception while deny / write in channel " + ItchiBot.get().getConfigManager().getDenyChannel(),
					ex);
		}
		return true;
	}
}
