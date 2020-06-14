package fr.uniqsky.itchibot.commands.discord.commands;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import fr.uniqsky.itchibot.DiscordUtil;
import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.commands.discord.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearCmd implements Command, DiscordUtil {

	@Override
	public String getCmd() {
		return ItchiBot.get().getConfigManager().getClearCommand();
	}

	@Override
	public boolean onCommand(String label, String[] args, MessageReceivedEvent e) {
		if (!ItchiBot.get().getConfigManager().getClearAllowUsers().contains(e.getAuthor().getId())) {
			e.getMessage().addReaction(ItchiBot.get().getConfigManager().getNoPerm()).queue();
			return true;
		}
		if (args.length == 0) {
			e.getMessage().delete().queue();
			return true;
		}
		MessageHistory history = new MessageHistory(e.getChannel());
		if ("all".equalsIgnoreCase(args[0])) {
			// Clear all messages
			Bukkit.getScheduler().runTaskAsynchronously(ItchiBot.get(), () -> {
				try {
					List<Message> msgs;
					while (true) {
						msgs = history.retrievePast(50).complete();
						if (msgs.size() == 0)
							break;
						e.getTextChannel().deleteMessages(msgs).complete();
					}
				} catch (Exception ex) {
					Bukkit.getLogger().log(Level.SEVERE, "Exception while retrieving ALL history: ", ex);
				}
			});
			return true;
		} else if ("until".equalsIgnoreCase(args[0])) {
			if (args.length == 1) {
				e.getMessage().delete().queue();
				return true;
			}
			Bukkit.getScheduler().runTaskAsynchronously(ItchiBot.get(), () -> {
				try {
					List<Message> msgs;
					while (true) {
						msgs = history.retrievePast(1).complete();
						msgs.get(0).delete().complete();
						if (msgs.get(0).getId().equalsIgnoreCase(args[1]))
							break;
					}
				} catch (Exception ex) {
					Bukkit.getLogger().log(Level.SEVERE, "Exception while retrieving UNTIL history: ", ex);
				}
			});
			return true;
		}
		int number = 1;
		try {
			number = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			number = 1;
		}
		if (number <= 0)
			number = 1;
		else if (number >= 100)
			number = 100;
		e.getMessage().delete().queue();
		if (number == 1)
			return true;
		// JAVA ...
		final int copyNumber = number - 1;
		Bukkit.getScheduler().runTaskAsynchronously(ItchiBot.get(), () -> {
			try {
				List<Message> msgs = history.retrievePast(copyNumber).complete();
				e.getTextChannel().deleteMessages(msgs).complete();
			} catch (Exception ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Exception while retrieving history: ", ex);

			}
		});
		return true;
	}
}
