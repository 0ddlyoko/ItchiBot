package fr.uniqsky.itchibot.commands.discord.commands;

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
			MessageHistory history = e.getChannel().getHistoryBefore(e.getMessage(), copyNumber).complete();
			for (Message m : history.getRetrievedHistory())
				m.delete().queue();
		});
		return true;
	}
}
