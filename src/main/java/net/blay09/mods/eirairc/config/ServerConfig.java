// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.blay09.mods.eirairc.api.IRCChannel;
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
import org.jetbrains.annotations.NotNull;

public class ServerConfig {

	private final Map<String, ChannelConfig> channels = new HashMap<String, ChannelConfig>();
	private final GeneralSettings generalSettings = new GeneralSettings(SharedGlobalConfig.generalSettings);
	private final BotSettings botSettings = new BotSettings(SharedGlobalConfig.botSettings);
	private final ThemeSettings theme = new ThemeSettings(SharedGlobalConfig.theme);

	private final String address;
	private String charset = "UTF-8";
	private String nick = "";
	private String serverPassword = "";
	private String nickServName = "";
	private String nickServPassword = "";
	private boolean isSSL = false;

	private String botProfile = ""; // bot

	public ServerConfig(String address) {
		this.address = address;
	}

	public void useDefaults(boolean serverSide) {
		if(address.equals(Globals.TWITCH_SERVER)) {
			botProfile = BotProfileImpl.DEFAULT_TWITCH;
		} else if(serverSide) {
			botProfile = BotProfileImpl.DEFAULT_SERVER;
		} else {
			botProfile = BotProfileImpl.DEFAULT_CLIENT;
		}
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
	
	public ChannelConfig getChannelConfig(String channelName) {
		ChannelConfig channelConfig = channels.get(channelName.toLowerCase());
		if(channelConfig == null) {
			channelConfig = new ChannelConfig(this, channelName);
			channelConfig.useDefaults(Utils.isServerSide());
			channels.put(channelConfig.getName().toLowerCase(), channelConfig);
			ConfigurationHandler.save();
		}
		return channelConfig;
	}
	
	public ChannelConfig getChannelConfig(IRCChannel channel) {
		return getChannelConfig(channel.getName());
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

	public void loadLegacy(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		nick = Utils.unquote(config.get(categoryName, "nick", "").getString());
		nickServName = Utils.unquote(config.get(categoryName, "nickServName", "").getString());
		nickServPassword = Utils.unquote(config.get(categoryName, "nickServPassword", "").getString());
		serverPassword = Utils.unquote(config.get(categoryName, "serverPassword", "").getString());
		botProfile = Utils.unquote(config.get(categoryName, "botProfile", "").getString());
		isSSL = config.get(categoryName, "secureConnection", isSSL).getBoolean(isSSL);
		
		String channelsCategoryName = categoryName + Configuration.CATEGORY_SPLITTER + ConfigurationHandler.CATEGORY_CHANNELS;
		ConfigCategory channelsCategory = config.getCategory(channelsCategoryName);
		for(ConfigCategory channelCategory : channelsCategory.getChildren()) {
			ChannelConfig channelConfig = new ChannelConfig(this, Utils.unquote(config.get(channelCategory.getQualifiedName(), "name", "").getString()));
			channelConfig.loadLegacy(config, channelCategory);
			addChannelConfig(channelConfig);
		}
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
		list.add("autoConnect");
	}

	public static void addValuesToList(List<String> list, String option) {
		if(option.equals("autoConnect")) {
			Utils.addBooleansToList(list);
		}
	}

	public String getBotProfile() {
		return botProfile;
	}

	public void setBotProfile(@NotNull String botProfile) {
		this.botProfile = botProfile;
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
