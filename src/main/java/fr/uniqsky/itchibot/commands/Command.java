package fr.uniqsky.itchibot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Command {
	public String getCmd();

	public boolean onCommand(String label, String[] args, MessageReceivedEvent e);
}
