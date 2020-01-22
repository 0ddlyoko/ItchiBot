package fr.uniqsky.itchibot.commands;

import java.util.HashMap;
import java.util.regex.Pattern;

import fr.uniqsky.itchibot.ItchiBot;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandManager {
	private HashMap<String, Command> commands;

	public CommandManager() {
		commands = new HashMap<>();
	}

	public void registerCommand(Command cmd) {
		commands.put(cmd.getCmd(), cmd);
	}

	public void unregisterCommand(String cmd) {
		commands.remove(cmd);
	}

	private Pattern pattern = Pattern.compile(" ");

	public boolean onChat(MessageReceivedEvent e) {
		// Do not accept private messages
		if (!e.isFromGuild() || e.isFromType(ChannelType.PRIVATE))
			return false;
		// Self bot
		if (e.getAuthor().equals(e.getJDA().getSelfUser()))
			return false;
		String msg = e.getMessage().getContentRaw();
		if (msg.startsWith(ItchiBot.get().getConfigManager().getPrefix())) {
			String[] split = pattern.split(msg);
			String label = split[0].substring(ItchiBot.get().getConfigManager().getPrefix().length());
			String[] args = new String[split.length - 1];
			if (split.length >= 2)
				for (int i = 1; i < split.length; i++)
					args[i - 1] = split[i];
			Command cmd = commands.get(label);
			if (cmd == null)
				return false;
			return cmd.onCommand(label, args, e);
		}
		return false;
	}
}
