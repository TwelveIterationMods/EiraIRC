// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.config.base.BotProfileImpl;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.BotStringComponent;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import org.jetbrains.annotations.NotNull;

public class ServerConfig {

	private final Map<String, ChannelConfig> channels = new HashMap<String, ChannelConfig>();
	private final GeneralSettings generalSettings = new GeneralSettings(SharedGlobalConfig.generalSettings);
	private final BotSettings botSettings = new BotSettings(SharedGlobalConfig.botSettings);
	private final ThemeSettings theme = new ThemeSettings(SharedGlobalConfig.theme);

	private String address = "";
	private String charset = Globals.DEFAULT_CHARSET;
	private String nick = Globals.DEFAULT_NICK;
	private String serverPassword = "";
	private String nickServName = "";
	private String nickServPassword = "";
	private boolean isSSL = false;

	public ServerConfig() {
	}

	public ServerConfig(String address) {
		this.address = address;
	}

	public void useDefaults(boolean serverSide) {
		if(address.equals(Globals.TWITCH_SERVER)) {
			botSettings.setString(BotStringComponent.BotProfile, BotProfileImpl.DEFAULT_TWITCH);
		} else if(serverSide) {
			botSettings.setString(BotStringComponent.BotProfile, BotProfileImpl.DEFAULT_SERVER);
		} else {
			botSettings.setString(BotStringComponent.BotProfile, BotProfileImpl.DEFAULT_CLIENT);
		}
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setNick(@NotNull String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

	public String getServerPassword() {
		return serverPassword;
	}

	public void setServerPassword(@NotNull String serverPassword) {
		this.serverPassword = serverPassword;
	}

	public String getNickServName() {
		return nickServName;
	}

	public String getNickServPassword() {
		return nickServPassword;
	}

	public ChannelConfig getOrCreateChannelConfig(String channelName) {
		ChannelConfig channelConfig = channels.get(channelName.toLowerCase());
		if(channelConfig == null) {
			channelConfig = new ChannelConfig(this, channelName);
			channels.put(channelConfig.getName().toLowerCase(), channelConfig);
			ConfigurationHandler.save();
		}
		return channelConfig;
	}

	public ChannelConfig getOrCreateChannelConfig(IRCChannel channel) {
		return getOrCreateChannelConfig(channel.getName());
	}

	public void setNickServ(@NotNull String nickServName, @NotNull String nickServPassword) {
		this.nickServName = nickServName;
		this.nickServPassword = nickServPassword;
	}

	public void addChannelConfig(@NotNull ChannelConfig channelConfig) {
		channels.put(channelConfig.getName().toLowerCase(), channelConfig);
	}

	public void removeChannelConfig(@NotNull String channelName) {
		channels.remove(channelName.toLowerCase());
	}

	public boolean hasChannelConfig(@NotNull String channelName) {
		return channels.containsKey(channelName.toLowerCase());
	}

	public Collection<ChannelConfig> getChannelConfigs() {
		return channels.values();
	}

	public void loadLegacy(Configuration legacyConfig, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		nick = Utils.unquote(legacyConfig.get(categoryName, "nick", "").getString());
		if(nick.isEmpty()) {
			nick = Utils.unquote(legacyConfig.get("global", "nick", Globals.DEFAULT_NICK).getString());
		}
		nickServName = Utils.unquote(legacyConfig.get(categoryName, "nickServName", "").getString());
		nickServPassword = Utils.unquote(legacyConfig.get(categoryName, "nickServPassword", "").getString());
		serverPassword = Utils.unquote(legacyConfig.get(categoryName, "serverPassword", "").getString());
		isSSL = legacyConfig.get(categoryName, "secureConnection", isSSL).getBoolean(isSSL);
		charset = Utils.unquote(legacyConfig.get("global", "charset", charset).getString());

		String channelsCategoryName = categoryName + Configuration.CATEGORY_SPLITTER + "channels";
		ConfigCategory channelsCategory = legacyConfig.getCategory(channelsCategoryName);
		for(ConfigCategory channelCategory : channelsCategory.getChildren()) {
			ChannelConfig channelConfig = new ChannelConfig(this, Utils.unquote(legacyConfig.get(channelCategory.getQualifiedName(), "name", "").getString()));
			channelConfig.loadLegacy(legacyConfig, channelCategory);
			addChannelConfig(channelConfig);
		}

		theme.loadLegacy(legacyConfig, categoryName);
		botSettings.loadLegacy(legacyConfig, categoryName);
		generalSettings.loadLegacy(legacyConfig, categoryName);
	}

	public static ServerConfig loadFromJson(JsonObject object) {
		ServerConfig config = new ServerConfig(object.get("address").getAsString());
		if(object.has("nick")) {
			config.nick = object.get("nick").getAsString();
		}
		if(object.has("serverPassword")) {
			config.serverPassword = object.get("serverPassword").getAsString();
		}
		if(object.has("charset")) {
			config.charset = object.get("charset").getAsString();
		}
		if(object.has("isSSL")) {
			config.isSSL = object.get("isSSL").getAsBoolean();
		}
		if(object.has("nickserv")) {
			JsonObject nickServObject = object.getAsJsonObject("nickserv");
			config.nickServName = nickServObject.get("username").getAsString();
			config.nickServPassword = nickServObject.get("password").getAsString();
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
		if(object.has("channels")) {
			JsonArray channelArray = object.getAsJsonArray("channels");
			for(int i = 0; i < channelArray.size(); i++) {
				config.addChannelConfig(ChannelConfig.loadFromJson(config, channelArray.get(i).getAsJsonObject()));
			}
		}
		return config;
	}

	public JsonObject toJsonObject() {
		JsonObject object = new JsonObject();
		object.addProperty("address", address);
		object.addProperty("nick", nick);
		if(!serverPassword.isEmpty()) {
			object.addProperty("serverPassword", serverPassword);
		}
		if(!charset.equals(Globals.DEFAULT_CHARSET)) {
			object.addProperty("charset", charset);
		}
		if(isSSL) {
			object.addProperty("isSSL", true);
		}
		if(!nickServName.isEmpty() || !nickServPassword.isEmpty()) {
			JsonObject nickServObject = new JsonObject();
			nickServObject.addProperty("username", nickServName);
			nickServObject.addProperty("password", nickServPassword);
			object.add("nickserv", nickServObject);
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
		JsonArray channelArray = new JsonArray();
		for(ChannelConfig channelConfig : channels.values()) {
			channelArray.add(channelConfig.toJsonObject());
		}
		object.add("channels", channelArray);
		return object;
	}

	public void handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(value != null) {
			Utils.sendLocalizedMessage(sender, "irc.config.lookup", address, key, value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", address, key);
		}
	}

	public void handleConfigCommand(ICommandSender sender, String key, String value) {
		if(true) {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", address, key, value);
			return;
		}
		Utils.sendLocalizedMessage(sender, "irc.config.change", address, key, value);
		ConfigurationHandler.save();
	}

	public static void addOptionstoList(List<String> list) {
	}

	public static void addValuesToList(List<String> list, String option) {
	}

	public boolean isSSL() {
		return isSSL;
	}

	public String getCharset() {
		return charset;
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
