// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.util.List;

import net.blay09.mods.eirairc.config.base.BotProfileImpl;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ChannelConfig {

	private final ServerConfig serverConfig;
	private final String name;
	private final GeneralSettings generalSettings;
	private final BotSettings botSettings;
	private final ThemeSettings theme;
	private String password;
	private String botProfile; // bot

	public ChannelConfig(ServerConfig serverConfig, String name) {
		this.serverConfig = serverConfig;
		generalSettings = new GeneralSettings(serverConfig.getGeneralSettings());
		botSettings = new BotSettings(serverConfig.getBotSettings());
		theme = new ThemeSettings(serverConfig.getTheme());

		if(serverConfig.getAddress().equals(Globals.TWITCH_SERVER)) {
			this.name = name.toLowerCase();
		} else {
			this.name = name;
		}
	}

	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void loadLegacy(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		password = Utils.unquote(config.get(categoryName, "password", "").getString());
		botProfile = Utils.unquote(config.get(categoryName, "botProfile", "").getString());
	}

	public void handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(value != null) {
			Utils.sendLocalizedMessage(sender, "irc.config.lookup", name, key, value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", name, key);
		}
	}
	
	public void handleConfigCommand(ICommandSender sender, String key, String value) {
		if(true) {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", name, key, value);
			return;
		}
		Utils.sendLocalizedMessage(sender, "irc.config.change", name, key, value);
		ConfigurationHandler.save();
	}

	public static void addOptionsToList(List<String> list) {
	}
	
	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static void addValuesToList(List<String> list, String option) {
	}

	public String getBotProfile() {
		return botProfile;
	}

	public void setBotProfile(String botProfile) {
		this.botProfile = botProfile;
	}

	public void useDefaults(boolean serverSide) {
		botProfile = BotProfileImpl.INHERIT;
	}

	public ThemeSettings getTheme() {
		return theme;
	}

	public GeneralSettings getGeneralSettings() {
		return generalSettings;
	}

	public BotSettings getBotSettings() {
		return botSettings;
	}
}
