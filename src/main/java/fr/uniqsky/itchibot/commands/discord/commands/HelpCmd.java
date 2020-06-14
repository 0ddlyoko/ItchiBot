package fr.uniqsky.itchibot.commands.discord.commands;

import java.awt.Color;

import fr.uniqsky.itchibot.DiscordUtil;
import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.commands.discord.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCmd implements Command, DiscordUtil {

	@Override
	public String getCmd() {
		return ItchiBot.get().getConfigManager().getHelpCommand();
	}

	@Override
	public boolean onCommand(String label, String[] args, MessageReceivedEvent e) {
		e.getChannel()
				.sendMessage(
						createEmbededMessageWithFields(Color.white, ItchiBot.get().getConfigManager().getHelpTitle(),
								null, ItchiBot.get().getConfigManager().getHelpFooter(), null,
								ItchiBot.get().getConfigManager().getHelpShowTitle(),
								ItchiBot.get().getConfigManager().getHelpShowValues()))
				.queue();
		return true;
	}
}
