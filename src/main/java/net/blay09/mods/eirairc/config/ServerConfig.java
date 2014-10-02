// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.config.base.BotProfileImpl;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import org.jetbrains.annotations.NotNull;

public class ServerConfig {

	private final Map<String, ChannelConfig> channels = new HashMap<String, ChannelConfig>();

	private final String address;
	private String charset = "UTF-8";
	private String nick = "";
	private String serverPassword = "";
	private String nickServName = "";
	private String nickServPassword = "";
	private boolean isSSL = false;

	private boolean autoConnect = true; // channelsettings: autoJoin
	private String botProfile = ""; // bot
	private String ident = Globals.DEFAULT_IDENT; // bot
	private String description = Globals.DEFAULT_DESCRIPTION; // bot
	private String quitMessage = ""; // bot
	private String ircColor = ""; // theme
	private String emoteColor = ""; // theme

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
	
	public void setIdent(@NotNull String ident) {
		this.ident = ident;
	}
	
	public String getIdent() {
		return ident;
	}
	
	public void setDescription(@NotNull String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isAutoConnect() {
		return autoConnect;
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

	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
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

	public String getQuitMessage() {
		return quitMessage;
	}
	
	public Collection<ChannelConfig> getChannelConfigs() {
		return channels.values();
	}

	public void load(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		nick = Utils.unquote(config.get(categoryName, "nick", "").getString());
		ident = Utils.unquote(config.get(categoryName, "ident", Globals.DEFAULT_IDENT).getString());
		description = Utils.unquote(config.get(categoryName, "description", Globals.DEFAULT_DESCRIPTION).getString());
		ircColor = Utils.unquote(config.get(categoryName, "ircColor", "").getString());
		emoteColor = Utils.unquote(config.get(categoryName, "emoteColor", emoteColor).getString());
		quitMessage = Utils.unquote(config.get(categoryName, "quitMessage", "").getString());
		nickServName = Utils.unquote(config.get(categoryName, "nickServName", "").getString());
		nickServPassword = Utils.unquote(config.get(categoryName, "nickServPassword", "").getString());
		serverPassword = Utils.unquote(config.get(categoryName, "serverPassword", "").getString());
		autoConnect = config.get(categoryName, "autoConnect", autoConnect).getBoolean(autoConnect);
		botProfile = Utils.unquote(config.get(categoryName, "botProfile", "").getString());
		isSSL = config.get(categoryName, "secureConnection", isSSL).getBoolean(isSSL);
		
		String channelsCategoryName = categoryName + Configuration.CATEGORY_SPLITTER + ConfigurationHandler.CATEGORY_CHANNELS;
		ConfigCategory channelsCategory = config.getCategory(channelsCategoryName);
		for(ConfigCategory channelCategory : channelsCategory.getChildren()) {
			ChannelConfig channelConfig = new ChannelConfig(this, Utils.unquote(config.get(channelCategory.getQualifiedName(), "name", "").getString()));
			channelConfig.load(config, channelCategory);
			addChannelConfig(channelConfig);
		}
	}

	public void save(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		config.get(categoryName, "host", "").set(Utils.quote(address));
		config.get(categoryName, "nick", "").set(Utils.quote(nick != null ? nick : ""));
		config.get(categoryName, "ident", "").set(Utils.quote(ident != null ? ident : Globals.DEFAULT_IDENT));
		config.get(categoryName, "description", "").set(Utils.quote(description != null ? description : Globals.DEFAULT_DESCRIPTION));
		config.get(categoryName, "ircColor", "").set(Utils.quote(ircColor != null ? ircColor : ""));
		config.get(categoryName, "emoteColor", "").set(Utils.quote(emoteColor));
		config.get(categoryName, "quitMessage", "").set(Utils.quote(quitMessage != null ? quitMessage : ""));
		config.get(categoryName, "nickServName", "").set(Utils.quote(nickServName != null ? nickServName : ""));
		config.get(categoryName, "nickServPassword", "").set(Utils.quote(nickServPassword != null ? nickServPassword : ""));
		config.get(categoryName, "serverPassword", "").set(Utils.quote(serverPassword != null ? serverPassword : ""));
		config.get(categoryName, "autoConnect", autoConnect).set(autoConnect);
		config.get(categoryName, "botProfile", "").set(Utils.quote(botProfile != null ? botProfile : ""));
		config.get(categoryName, "secureConnection", isSSL).set(isSSL);
		
		String channelsCategoryName = categoryName + Configuration.CATEGORY_SPLITTER + ConfigurationHandler.CATEGORY_CHANNELS;
		int c = 0;
		for(ChannelConfig channelConfig : channels.values()) {
			String channelCategoryName = channelsCategoryName + Configuration.CATEGORY_SPLITTER + ConfigurationHandler.PREFIX_CHANNEL + c;
			channelConfig.save(config, config.getCategory(channelCategoryName));
			c++;
		}
	}

	public String getIRCColor() {
		return ircColor;
	}
	
	public String getEmoteColor() {
		return emoteColor;
	}

	public void handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("ircColor")) value = ircColor;
		else if(key.equals("emoteColor")) value = emoteColor;
		else if(key.equals("quitMessage")) value = quitMessage;
		else if(key.equals("autoConnect")) value = String.valueOf(autoConnect);
		if(value != null) {
			Utils.sendLocalizedMessage(sender, "irc.config.lookup", address, key, value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", address, key);
		}
	}
	
	public void handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("ircColor")) {
			if(Utils.isValidColor(value)) {
				ircColor = value;
			} else {
				Utils.sendLocalizedMessage(sender, "irc.color.invalid", value);
				return;
			}
		} else if(key.equals("emoteColor")) {
			if(Utils.isValidColor(value)) {
				emoteColor = value;
			} else {
				Utils.sendLocalizedMessage(sender, "irc.color.invalid", value);
				return;
			}
		} else if(key.equals("quitMessage")) {
			quitMessage = value;
		} else if(key.equals("autoConnect")) {
			autoConnect = Boolean.parseBoolean(value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", address, key, value);
			return;
		}
		Utils.sendLocalizedMessage(sender, "irc.config.change", address, key, value);
		ConfigurationHandler.save();
	}
	
	public static void addOptionstoList(List<String> list) {
		list.add("ircColor");
		list.add("emoteColor");
		list.add("quitMessage");
		list.add("autoConnect");
	}

	public static void addValuesToList(List<String> list, String option) {
		if(option.equals("ircColor") || option.equals("emoteColor")) {
			Utils.addValidColorsToList(list);
		} else if(option.equals("autoConnect")) {
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
}
