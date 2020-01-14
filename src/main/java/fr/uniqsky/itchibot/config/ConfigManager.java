/**
 * 
 */
package fr.uniqsky.itchibot.config;

import java.io.File;
import java.util.List;

import lombok.Getter;

@Getter
public class ConfigManager {
	private Config config;
	private String token;
	private String prefix;
	private String activity;
	private String noPerm;

	// Reaction
	private String reactionValid;
	private String reactionInvalid;

	// Suggests
	private List<String> suggestChannels;
	private String suggestTitle;
	private String suggestMessage;
	private String suggestFooter;

	// Accept
	private String acceptCommand;
	private List<String> acceptAllowUsers;
	private String acceptChannel;
	private String acceptNotFound;
	private String acceptFooter;

	// Deny
	private String denyCommand;
	private List<String> denyAllowUsers;
	private String denyChannel;
	private String denyNotFound;
	private String denyFooter;

	// New
	private String newTitle;
	private String newChannel;
	private String newName;
	private String newValue;
	private String newFooter;

	public ConfigManager() {
		config = new Config(new File("plugins" + File.separator + "ItchiBot" + File.separator + "config.yml"));
		token = config.getString("token", false);
		prefix = config.getString("prefix", false);
		activity = config.getString("activity", false);
		noPerm = config.getString("noPerm", false);
		// Reaction
		reactionValid = config.getString("reaction.valid", false);
		reactionInvalid = config.getString("reaction.invalid", false);
		// Suggests
		suggestChannels = config.getStringList("suggests.channels", false);
		suggestTitle = config.getString("suggests.title", false);
		suggestMessage = config.getString("suggests.message", false);
		suggestFooter = config.getString("suggests.footer", false);
		// Accept
		acceptCommand = config.getString("accept.command", false);
		acceptAllowUsers = config.getStringList("accept.allowUsers", false);
		acceptChannel = config.getString("accept.channel", false);
		acceptNotFound = config.getString("accept.notfound", false);
		acceptFooter = config.getString("accept.footer", false);
		// Deny
		denyCommand = config.getString("deny.command", false);
		denyAllowUsers = config.getStringList("deny.allowUsers", false);
		denyChannel = config.getString("deny.channel", false);
		denyNotFound = config.getString("deny.notfound", false);
		denyFooter = config.getString("deny.footer", false);
		// New
		newTitle = config.getString("new.title", false);
		newChannel = config.getString("new.channel", false);
		newName = config.getString("new.name", false);
		newValue = config.getString("new.value", false);
		newFooter = config.getString("new.footer", false);
	}
}
