package fr.uniqsky.itchibot.listeners;

import fr.uniqsky.itchibot.ItchiBot;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class DiscordListenerAdapter extends ListenerAdapter implements StopListener {

	@Override
	public void stop() {
		// Unregister event
		ItchiBot.get().getDiscordManager().removeEventListener(this);
	}
}
