package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.api.bot.IBotProfile;
import net.minecraftforge.common.config.Configuration;

public class BotProfile implements IBotProfile {

	private static final String CATEGORY_SETTINGS = "settings";
	private static final String CATEGORY_COMMANDS = "commands";
	
	private final Configuration config = new Configuration();
	
	private String name;
	
	public BotProfile(String name) {
		this.name = name;
	}

	@Override
	public boolean getBoolean(String key, boolean defaultVal) {
		return config.get(CATEGORY_SETTINGS, key, defaultVal).getBoolean(defaultVal);
	}

	@Override
	public boolean isMuted() {
		return false;
	}
	
	@Override
	public boolean isReadOnly() {
		return false;
	}
}
