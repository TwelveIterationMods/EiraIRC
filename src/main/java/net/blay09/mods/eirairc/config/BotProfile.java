// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.io.File;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.bot.IBotProfile;
import net.minecraftforge.common.config.Configuration;

public class BotProfile implements IBotProfile {

	private static final String CATEGORY_SETTINGS = "settings";
	private static final String CATEGORY_COMMANDS = "commands";
	private static final String CATEGORY_MACROS = "macros";
	
	public static final String DEFAULT_CLIENT = "default_client";
	public static final String DEFAULT_SERVER = "default_server";
	public static final String DEFAULT_TWITCH = "default_twitch";
	
	private final Configuration config;
	
	private String name;
	private boolean muted;
	private boolean readOnly;
	
	public BotProfile(File file) {
		config = new Configuration(file);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultVal) {
		return config.get(CATEGORY_SETTINGS, key, defaultVal).getBoolean(defaultVal);
	}

	@Override
	public boolean isMuted() {
		return muted;
	}
	
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	public String getName() {
		return name;
	}

	public void save() {
		config.save();
	}

	public void defaultClient() {
		config.get(CATEGORY_SETTINGS, KEY_ALLOWPRIVMSG, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_AUTOWHO, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_RELAYIRCJOINLEAVE, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYNICKCHANGES, true).set(true);
	}
	
	public void defaultServer() {
		config.get(CATEGORY_SETTINGS, KEY_ALLOWPRIVMSG, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_AUTOWHO, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYIRCJOINLEAVE, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYNICKCHANGES, true).set(true);
	}
	
	public void defaultTwitch() {
		config.get(CATEGORY_SETTINGS, KEY_ALLOWPRIVMSG, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_AUTOWHO, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_RELAYIRCJOINLEAVE, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_RELAYNICKCHANGES, false).set(false);
	}
}
