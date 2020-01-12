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
	private String reactionValid;
	private String reactionInvalid;
	private List<String> suggestChannels;
	private String suggestTitle;
	private String suggestMessage;
	private String suggestFooter;

	public ConfigManager() {
		config = new Config(new File("plugins" + File.separator + "ItchiBot" + File.separator + "config.yml"));
		token = config.getString("token");
		prefix = config.getString("prefix");
		activity = config.getString("activity");
		reactionValid = config.getString("reaction.valid");
		reactionInvalid = config.getString("reaction.invalid");
		suggestChannels = config.getStringList("suggests.channels");
		suggestTitle = config.getString("suggests.title");
		suggestMessage = config.getString("suggests.message");
		suggestFooter = config.getString("suggests.footer");
	}
}
