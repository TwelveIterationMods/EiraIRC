// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import java.util.List;

public class ChannelConfig {

	private final ServerConfig serverConfig;
	private final GeneralSettings generalSettings;
	private final BotSettings botSettings;
	private final ThemeSettings theme;

	private String name = "";
	private String password = "";

	public ChannelConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		generalSettings = new GeneralSettings(serverConfig.getGeneralSettings());
		botSettings = new BotSettings(serverConfig.getBotSettings());
		theme = new ThemeSettings(serverConfig.getTheme());
	}

	public void setName(String name) {
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

	public static ChannelConfig loadFromJson(ServerConfig serverConfig, JsonObject object) {
		ChannelConfig config = new ChannelConfig(serverConfig);
		config.setName(object.get("name").getAsString());
		if(object.has("password")) {
			config.password = object.get("password").getAsString();
		}
		if(object.has("bot")) {
			config.botSettings.load(object.getAsJsonObject("bot"));
		}
		if(object.has("theme")) {
			config.theme.load(object.getAsJsonObject("theme"));
		}
		if(object.has("settings")) {
			config.generalSettings.load(object.getAsJsonObject("settings"));
		}
		return config;
	}

	public JsonObject toJsonObject() {
		JsonObject object = new JsonObject();
		object.addProperty("name", name);
		if(!password.isEmpty()) {
			object.addProperty("password", password);
		}
		JsonObject botSettingsObject = botSettings.toJsonObject();
		if(botSettingsObject != null) {
			object.add("bot", botSettingsObject);
		}
		JsonObject themeObject = theme.toJsonObject();
		if(themeObject != null) {
			object.add("theme", themeObject);
		}
		JsonObject generalSettingsObject = generalSettings.toJsonObject();
		if(generalSettingsObject != null) {
			object.add("settings", generalSettingsObject);
		}
		return object;
	}

	public void loadLegacy(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		name = Utils.unquote(config.get(categoryName, "name", "").getString());
		password = Utils.unquote(config.get(categoryName, "password", "").getString());
	}

	public void handleConfigCommand(ICommandSender sender, String key) {
		String value;
		value = generalSettings.handleConfigCommand(sender, key);
		if(value == null) {
			value = botSettings.handleConfigCommand(sender, key);
		}
		if(value == null) {
			value = theme.handleConfigCommand(sender, key);
		}
		if(value != null) {
			Utils.sendLocalizedMessage(sender, "irc.config.lookup", name, key, value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", name, key);
		}
	}

	public void handleConfigCommand(ICommandSender sender, String key, String value) {
		if(!generalSettings.handleConfigCommand(sender, key, value) && !botSettings.handleConfigCommand(sender, key, value) && !theme.handleConfigCommand(sender, key, value)) {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", name, key, value);
			return;
		}
		Utils.sendLocalizedMessage(sender, "irc.config.change", name, key, value);
		ConfigurationHandler.save();
	}

	public static void addOptionsToList(List<String> list, String option) {
		ThemeSettings.addOptionsToList(list, option);
		BotSettings.addOptionsToList(list, option);
		GeneralSettings.addOptionsToList(list, option);
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getIdentifier() {
		return serverConfig.getIdentifier() + "/" + name.toLowerCase();
	}
}
