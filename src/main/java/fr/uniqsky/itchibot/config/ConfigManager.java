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

	// SuggestAccept
	private String acceptPrefix;
	private List<String> acceptAllowUsers;
	private String acceptChannel;
	private String acceptNotFound;
	private String acceptFooter;
	private String acceptEmojiValid;
	private String acceptEmojiInvalid;

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
		// SuggestAccept
		acceptPrefix = config.getString("accept.prefix", false);
		acceptAllowUsers = config.getStringList("accept.allowUsers", false);
		acceptChannel = config.getString("accept.channel", false);
		acceptNotFound = config.getString("accept.notfound", false);
		acceptFooter = config.getString("accept.footer", false);
		acceptEmojiValid = config.getString("accept.emoji.valid", false);
		acceptEmojiInvalid = config.getString("accept.emoji.invalid", false);
	}
}
