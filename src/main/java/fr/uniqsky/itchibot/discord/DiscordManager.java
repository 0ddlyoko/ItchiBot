package fr.uniqsky.itchibot.discord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import fr.uniqsky.itchibot.DiscordUtil;
import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.commands.discord.commands.AcceptCmd;
import fr.uniqsky.itchibot.commands.discord.commands.ClearCmd;
import fr.uniqsky.itchibot.commands.discord.commands.DenyCmd;
import fr.uniqsky.itchibot.commands.discord.commands.HelpCmd;
import fr.uniqsky.itchibot.commands.discord.commands.RoleCmd;
import fr.uniqsky.itchibot.listeners.DiscordListenerAdapter;
import fr.uniqsky.itchibot.listeners.StopListener;
import fr.uniqsky.itchibot.listeners.listeners.ChatListener;
import fr.uniqsky.itchibot.listeners.listeners.MemberListener;
import fr.uniqsky.itchibot.listeners.listeners.NewUserListener;
import fr.uniqsky.itchibot.listeners.listeners.PlayerNumberListener;
import fr.uniqsky.itchibot.listeners.listeners.SuggestListener;
import lombok.Getter;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordManager extends ListenerAdapter implements DiscordUtil {
	@Getter
	private JDA jda;
	@Getter
	private Guild guild;
	private List<StopListener> stops;
	@Getter
	private RoleCmd roleCmd;

	public DiscordManager() {
		stops = new ArrayList<>();
		Bukkit.getScheduler().runTaskAsynchronously(ItchiBot.get(), () -> {
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
		});
	}

	public void addEventListener(DiscordListenerAdapter listener) {
		jda.addEventListener(listener);
		addStopListener(listener);
	}

	public void removeEventListener(DiscordListenerAdapter listener) {
		jda.removeEventListener(listener);
		removeStopListener(listener);
	}

	public void addStopListener(StopListener listener) {
		stops.add(listener);
	}

	public void removeStopListener(StopListener listener) {
		stops.remove(listener);
	}

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("API is ready!");
		guild = jda.getGuildById(ItchiBot.get().getConfigManager().getId());

		// Commands
		if (ItchiBot.get().getConfigManager().isAcceptActive())
			ItchiBot.get().getCommandManager().registerCommand(new AcceptCmd());
		if (ItchiBot.get().getConfigManager().isClearActive())
			ItchiBot.get().getCommandManager().registerCommand(new ClearCmd());
		if (ItchiBot.get().getConfigManager().isDenyActive())
			ItchiBot.get().getCommandManager().registerCommand(new DenyCmd());
		if (ItchiBot.get().getConfigManager().isHelpActive())
			ItchiBot.get().getCommandManager().registerCommand(new HelpCmd());
		if (ItchiBot.get().getConfigManager().isRoleActive())
			ItchiBot.get().getCommandManager().registerCommand(roleCmd = new RoleCmd());
		// Listeners
		if (ItchiBot.get().getConfigManager().isChatActive())
			addEventListener(new ChatListener());
		if (ItchiBot.get().getConfigManager().isMemberActive())
			addEventListener(new MemberListener());
		if (ItchiBot.get().getConfigManager().isNewActive())
			addEventListener(new NewUserListener());
		if (ItchiBot.get().getConfigManager().isPlayerNumberActive())
			addEventListener(new PlayerNumberListener());
		if (ItchiBot.get().getConfigManager().isSuggestActive())
			addEventListener(new SuggestListener());
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// Do not accept private messages
		if (!e.isFromGuild() || e.isFromType(ChannelType.PRIVATE))
			return;
		// Self bot
		if (e.getAuthor().equals(e.getJDA().getSelfUser()))
			return;
		// Check command
		if (ItchiBot.get().getCommandManager().onChat(e))
			return;
	}

	public void stop() {
		for (StopListener l : new ArrayList<>(stops))
			l.stop();
		// Stop the app
		try {
			if (jda != null) {
				CompletableFuture<Void> shutdownTask = new CompletableFuture<>();
				jda.addEventListener(new ListenerAdapter() {
					@Override
					public void onShutdown(ShutdownEvent event) {
						shutdownTask.complete(null);
					}
				});
				jda.shutdown();
				try {
					shutdownTask.get(15, TimeUnit.SECONDS);
				} catch (TimeoutException e) {
					Bukkit.getLogger().warning("JDA took too long to shut down, skipping");
				}
				jda.shutdownNow();
			}
			jda = null;
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "", ex);
		}
	}
}
