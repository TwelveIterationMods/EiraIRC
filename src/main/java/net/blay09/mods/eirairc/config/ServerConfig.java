// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerConfig {

	private final Map<String, ChannelConfig> channels = new HashMap<String, ChannelConfig>();
	private final GeneralSettings generalSettings = new GeneralSettings(SharedGlobalConfig.generalSettings);
	private final BotSettings botSettings = new BotSettings(SharedGlobalConfig.botSettings);
	private final ThemeSettings theme = new ThemeSettings(SharedGlobalConfig.theme);

	private String address = "";
	private String charset = Globals.DEFAULT_CHARSET;
	private String nick = Globals.DEFAULT_NICK;
	private boolean isRedirect;
	private boolean isSSL = false;
	private boolean isRemote = false;

	public ServerConfig() {
	}

	public ServerConfig(String address) {
		this.address = address;
		if(address.equals(Globals.TWITCH_SERVER)) {
			nick = "";
		}
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setNick(String nick) {
		if(address.equals(Globals.TWITCH_SERVER)) {
			this.nick = nick.toLowerCase();
		} else {
			this.nick = nick;
		}
	}

	public String getNick() {
		return nick;
	}

	public ChannelConfig getOrCreateChannelConfig(String channelName) {
		ChannelConfig channelConfig = channels.get(channelName.toLowerCase());
		if(channelConfig == null) {
			channelConfig = new ChannelConfig(this);
			channelConfig.setName(channelName);
			channels.put(channelConfig.getName().toLowerCase(), channelConfig);
			ConfigurationHandler.save();
		}
		return channelConfig;
	}

	public ChannelConfig getOrCreateChannelConfig(IRCChannel channel) {
		return getOrCreateChannelConfig(channel.getName());
	}

	public void addChannelConfig(ChannelConfig channelConfig) {
		channels.put(channelConfig.getName().toLowerCase(), channelConfig);
	}

	public ChannelConfig removeChannelConfig(String channelName) {
		return channels.remove(channelName.toLowerCase());
	}

	public boolean hasChannelConfig(String channelName) {
		return channels.containsKey(channelName.toLowerCase());
	}

	public ChannelConfig getChannelConfig(String channelName) {
		return channels.get(channelName.toLowerCase());
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
		String nickServName = Utils.unquote(legacyConfig.get(categoryName, "nickServName", "").getString());
		String nickServPassword = Utils.unquote(legacyConfig.get(categoryName, "nickServPassword", "").getString());
		if(nickServName != null && !nickServName.isEmpty() && nickServPassword != null && !nickServPassword.isEmpty()) {
			AuthManager.putNickServData(getIdentifier(), nickServName, nickServPassword);
		}
		String serverPassword = Utils.unquote(legacyConfig.get(categoryName, "serverPassword", "").getString());
		if(serverPassword != null && !serverPassword.isEmpty()) {
			AuthManager.putServerPassword(getIdentifier(), serverPassword);
		}
		isSSL = legacyConfig.get(categoryName, "secureConnection", isSSL).getBoolean(isSSL);
		charset = Utils.unquote(legacyConfig.get("global", "charset", charset).getString());

		String channelsCategoryName = categoryName + Configuration.CATEGORY_SPLITTER + "channels";
		ConfigCategory channelsCategory = legacyConfig.getCategory(channelsCategoryName);
		for(ConfigCategory channelCategory : channelsCategory.getChildren()) {
			ChannelConfig channelConfig = new ChannelConfig(this);
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
			config.setNick(object.get("nick").getAsString());
		}
		if(object.has("serverPassword")) {
			AuthManager.putServerPassword(config.getIdentifier(), object.get("serverPassword").getAsString());
		}
		if(object.has("charset")) {
			config.charset = object.get("charset").getAsString();
		}
		if(object.has("isRedirect")) {
			config.isRedirect = object.get("isRedirect").getAsBoolean();
		}
		if(object.has("isSSL")) {
			config.isSSL = object.get("isSSL").getAsBoolean();
		}
		if(object.has("nickserv")) {
			JsonObject nickServObject = object.getAsJsonObject("nickserv");
			AuthManager.putNickServData(config.getIdentifier(), nickServObject.get("username").getAsString(), nickServObject.get("password").getAsString());
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
		if(!charset.equals(Globals.DEFAULT_CHARSET)) {
			object.addProperty("charset", charset);
		}
		if(isSSL) {
			object.addProperty("isSSL", true);
		}
		if(isRedirect) {
			object.addProperty("isRedirect", true);
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
		String value;
		value = generalSettings.handleConfigCommand(sender, key);
		if(value == null) {
			value = botSettings.handleConfigCommand(sender, key);
		}
		if(value == null) {
			value = theme.handleConfigCommand(sender, key);
		}
		if(value != null) {
			Utils.sendLocalizedMessage(sender, "commands.config.lookup", address, key, value);
		} else {
			Utils.sendLocalizedMessage(sender, "commands.config.invalidOption", address, key);
		}
	}

	public void handleConfigCommand(ICommandSender sender, String key, String value) {
		if(!generalSettings.handleConfigCommand(sender, key, value) && !botSettings.handleConfigCommand(sender, key, value) && !theme.handleConfigCommand(sender, key, value)) {
			Utils.sendLocalizedMessage(sender, "commands.config.invalidOption", address, key, value);
			return;
		}
		Utils.sendLocalizedMessage(sender, "commands.config.change", address, key, value);
		ConfigurationHandler.save();
	}

	public void addOptionsToList(List<String> list, String option, boolean autoCompleteOption) {
		generalSettings.addOptionsToList(list, option, autoCompleteOption);
		botSettings.addOptionsToList(list, option, autoCompleteOption);
		theme.addOptionsToList(list, option, autoCompleteOption);
	}

	public boolean isRemote() {
		return isRemote;
	}

	public void setIsRemote(boolean isRemote) {
		this.isRemote = isRemote;
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

	public void setIsSSL(boolean isSSL) {
		this.isSSL = isSSL;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public boolean isRedirect() {
		return isRedirect;
	}

	public String getIdentifier() {
		return Utils.extractHost(address);
	}
}
