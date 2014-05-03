package net.blay09.mods.eirairc.config;

import net.minecraftforge.common.config.Configuration;

public class BotProfile {

	private static final String CATEGORY_SETTINGS = "settings";
	private static final String CATEGORY_COMMANDS = "commands";
	
	private Configuration config;
	
	private String name;
	
	public BotProfile(String name) {
		this.name = name;
	}

	public boolean getBoolean(String key, boolean defaultVal) {
		return config.get(CATEGORY_SETTINGS, key, defaultVal).getBoolean(defaultVal);
	}
	
}
