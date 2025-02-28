/**
 * 
 */
package fr.uniqsky.itchibot.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;

@Getter
public class ConfigManager {
	private Config config;
	private String token;
	private String id;
	private String prefix;
	private String activity;
	private String noPerm;

	// Reaction
	private String reactionValid;
	private String reactionInvalid;

	// Help
	private boolean helpActive;
	private String helpCommand;
	private String helpTitle;
	private String helpFooter;
	private String[] helpShowTitle;
	private String[] helpShowValues;

	// Suggests
	private boolean suggestActive;
	private List<String> suggestChannels;
	private String suggestTitle;
	private String suggestMessage;
	private String suggestFooter;

	// Accept
	private boolean acceptActive;
	private String acceptCommand;
	private List<String> acceptAllowUsers;
	private String acceptChannel;
	private String acceptNotFound;
	private String acceptFooter;

	// Deny
	private boolean denyActive;
	private String denyCommand;
	private List<String> denyAllowUsers;
	private String denyChannel;
	private String denyNotFound;
	private String denyFooter;

	// New
	private boolean newActive;
	private String newTitle;
	private String newChannel;
	private String newName;
	private String newValue;
	private String newFooter;

	// Member
	private boolean memberActive;
	private String memberMessage;
	private String memberReaction;
	private String memberRole;

	// Role
	private boolean roleActive;
	private String roleCommand;
	private String roleNotConnected;
	private HashMap<String, String> roleRoles;
	private String roleLog;

	// PlayerNumber
	private boolean playerNumberActive;
	private boolean playerNumberUseBungeecord;
	private String playerNumberChannel;
	private String playerNumberMessage;

	// Chat
	private boolean chatActive;
	private String chatChannel;
	private String chatFormat;
	private String chatMc;
	private HashMap<String, String> chatUsers;

	// Clear
	private boolean clearActive;
	private String clearCommand;
	private List<String> clearAllowUsers;

	public ConfigManager() {
		reload();
	}

	public void reload() {
		config = new Config(new File("plugins" + File.separator + "ItchiBot" + File.separator + "config.yml"));

		prefix = config.getString("prefix", false);
		id = config.getString("id", false);
		token = config.getString("token", false);
		activity = config.getString("activity", false);
		noPerm = config.getString("noPerm", false);

		// Reaction
		reactionValid = config.getString("reaction.valid", false);
		reactionInvalid = config.getString("reaction.invalid", false);

		// Help
		helpActive = config.getBoolean("help.active");
		if (helpActive) {
			helpCommand = config.getString("help.command", false);
			helpTitle = config.getString("help.title", false);
			helpFooter = config.getString("help.footer", false);
			List<String> helpShowKeys = config.getKeys("help.show");
			helpShowTitle = new String[helpShowKeys.size()];
			helpShowValues = new String[helpShowKeys.size()];
			for (int i = 0; i < helpShowKeys.size(); i++) {
				String k = "help.show." + helpShowKeys.get(i);
				helpShowTitle[i] = config.getString(k + ".title", false);
				helpShowValues[i] = String.join("\n", config.getStringList(k + ".values", false));
			}
		}

		// Suggests
		suggestActive = config.getBoolean("suggests.active");
		if (suggestActive) {
			suggestChannels = config.getStringList("suggests.channels", false);
			suggestTitle = config.getString("suggests.title", false);
			suggestMessage = config.getString("suggests.message", false);
			suggestFooter = config.getString("suggests.footer", false);
		}

		// Accept
		acceptActive = config.getBoolean("accept.active");
		if (acceptActive) {
			acceptCommand = config.getString("accept.command", false);
			acceptAllowUsers = config.getStringList("accept.allowUsers", false);
			acceptChannel = config.getString("accept.channel", false);
			acceptNotFound = config.getString("accept.notfound", false);
			acceptFooter = config.getString("accept.footer", false);
		}

		// Deny
		denyActive = config.getBoolean("deny.active");
		if (denyActive) {
			denyCommand = config.getString("deny.command", false);
			denyAllowUsers = config.getStringList("deny.allowUsers", false);
			denyChannel = config.getString("deny.channel", false);
			denyNotFound = config.getString("deny.notfound", false);
			denyFooter = config.getString("deny.footer", false);
		}

		// New
		newActive = config.getBoolean("new.active");
		if (newActive) {
			newTitle = config.getString("new.title", false);
			newChannel = config.getString("new.channel", false);
			newName = config.getString("new.name", false);
			newValue = config.getString("new.value", false);
			newFooter = config.getString("new.footer", false);
		}

		// Member
		memberActive = config.getBoolean("member.active");
		if (memberActive) {
			memberMessage = config.getString("member.message", false);
			memberReaction = config.getString("member.reaction", false);
			memberRole = config.getString("member.role", false);
		}

		// Role
		roleActive = config.getBoolean("role.active");
		if (roleActive) {
			roleCommand = config.getString("role.command", false);
			roleNotConnected = config.getString("role.notConnected", false);
			roleRoles = new HashMap<>();
			for (String key : config.getKeys("role.roles")) {
				String k = "role.roles." + key;
				String permission = config.getString(k + ".permission", false);
				String discord = config.getString(k + ".discord", false);
				roleRoles.put(discord, permission);
			}
			roleLog = config.getString("role.log", false);
		}

		// PlayerNumber
		playerNumberActive = config.getBoolean("playernumber.active");
		if (playerNumberActive) {
			playerNumberUseBungeecord = config.getBoolean("playernumber.usebungeecord");
			playerNumberChannel = config.getString("playernumber.channel", false);
			playerNumberMessage = config.getString("playernumber.message", false);
		}

		// Chat
		chatActive = config.getBoolean("chat.active");
		if (chatActive) {
			chatChannel = config.getString("chat.channel", false);
			chatFormat = config.getString("chat.format", false);
			chatMc = config.getString("chat.mc");
			chatUsers = new HashMap<>();
			for (String key : config.getKeys("chat.users")) {
				String k = "chat.users." + key;
				String id = config.getString(k + ".id", false);
				String user = config.getString(k + ".user");
				chatUsers.put(id, user);
			}
		}

		// Clear
		clearActive = config.getBoolean("clear.active");
		if (clearActive) {
			clearCommand = config.getString("clear.command", false);
			clearAllowUsers = config.getStringList("clear.allowUsers", false);
		}
	}
}
