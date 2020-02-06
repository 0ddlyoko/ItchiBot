package fr.uniqsky.itchibot.commands.discord.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.uniqsky.itchibot.ItchiBot;
import fr.uniqsky.itchibot.__;
import fr.uniqsky.itchibot.commands.discord.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RoleCmd implements Command, Listener {
	// An HashMap containing the UUID of player as key and the discord user's id as
	// value
	private HashMap<UUID, String> players;
	private Map<Role, String> roles;

	public RoleCmd() {
		players = new HashMap<>();
		Bukkit.getPluginManager().registerEvents(this, ItchiBot.get());
		roles = new HashMap<>();
		for (Entry<String, String> role : ItchiBot.get().getConfigManager().getRoleRoles().entrySet()) {
			Role r = ItchiBot.get().getDiscordManager().getGuild().getRoleById(role.getKey());
			if (r == null) {
				Bukkit.getLogger().severe(__.PREFIX + ChatColor.RED + "Role " + role + " not found");
				continue;
			}
			roles.put(r, role.getValue());
		}
	}

	@Override
	public String getCmd() {
		return ItchiBot.get().getConfigManager().getRoleCommand();
	}

	@Override
	public boolean onCommand(String label, String[] args, MessageReceivedEvent e) {
		if (args.length != 1)
			return true;
		String username = args[0];
		if (username == null || "".equalsIgnoreCase(username.trim()))
			return true;
		Player p = Bukkit.getPlayer(username);
		if (p == null || !p.isOnline()) {
			e.getTextChannel().sendMessage(ItchiBot.get().getConfigManager().getRoleNotConnected()).queue();
			return true;
		}
		// Player is connected
		// Add to list
		players.put(p.getUniqueId(), e.getAuthor().getId());
		// Send message
		e.getAuthor().openPrivateChannel().flatMap(c -> c.sendMessage("/itchibot link " + e.getAuthor().getId()))
				.queue();
		return true;
	}

	public void onPlayerCommand(Player p, String id) {
		String value = players.get(p.getUniqueId());
		if (value == null || "".equalsIgnoreCase(value.trim())) {
			// Not in list
			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
			return;
		}
		if (!value.equals(id)) {
			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			return;
		}
		// Get player
		Member member = ItchiBot.get().getDiscordManager().getGuild().getMemberById(id);
		if (member == null) {
			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1, 1);
			return;
		}
		// Update roles

		List<Role> roles = new ArrayList<>();
		// Update roles
		for (Entry<Role, String> r : this.roles.entrySet())
			if (p.hasPermission(r.getValue()))
				roles.add(r.getKey());
		// Add roles
		ItchiBot.get().getDiscordManager().getGuild().modifyMemberRoles(member, roles, null).queue();
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		players.remove(e.getPlayer().getUniqueId());
	}
}
