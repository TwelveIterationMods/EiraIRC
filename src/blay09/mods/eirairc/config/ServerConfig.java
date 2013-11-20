// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import blay09.mods.eirairc.Utils;
import blay09.mods.eirairc.irc.IRCChannel;

public class ServerConfig {
	
	private final String host;
	private String nick;
	private String serverPassword;
	private String nickServName;
	private String nickServPassword;
	private final Map<String, ChannelConfig> channels = new HashMap<String, ChannelConfig>();
	private boolean serverSide;
	private boolean allowPrivateMessages = true;
	private boolean autoConnect = true;
	private String quitMessage;
	private String ircColor;
	private String emoteColor;
	
	public ServerConfig(String host) {
		this.host = host;
		if(MinecraftServer.getServer() != null) {
			if(MinecraftServer.getServer().isSinglePlayer()) {
				serverSide = false;
			} else {
				serverSide = true;
			}
		} else {
			serverSide = false;
		}
	}
	
	public String getHost() {
		return host;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getServerPassword() {
		return serverPassword;
	}
	
	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}
	
	public String getNickServName() {
		return nickServName;
	}
	
	public String getNickServPassword() {
		return nickServPassword;
	}
	
	public boolean isClientSide() {
		return !serverSide;
	}
	
	public boolean isAutoConnect() {
		return autoConnect;
	}
	
	public boolean allowsPrivateMessages() {
		return allowPrivateMessages;
	}
	
	public ChannelConfig getChannelConfig(String channelName) {
		ChannelConfig channelConfig = channels.get(channelName.toLowerCase());
		if(channelConfig == null) {
			channelConfig = new ChannelConfig(this, channelName);
			if(host.equals(Globals.TWITCH_SERVER)) {
				channelConfig.defaultTwitch();
			} else if(serverSide) {
				channelConfig.defaultServer();
			} else {
				channelConfig.defaultClient();
			}
			channels.put(channelName.toLowerCase(), channelConfig);
			ConfigurationHandler.save();
		}
		return channelConfig;
	}
	
	public ChannelConfig getChannelConfig(IRCChannel channel) {
		return getChannelConfig(channel.getName().toLowerCase());
	}

	public void setNickServ(String nickServName, String nickServPassword) {
		this.nickServName = nickServName;
		this.nickServPassword = nickServPassword;
	}

	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
	}

	public void setAllowPrivateMessages(boolean allowPrivateMessages) {
		this.allowPrivateMessages = allowPrivateMessages;
	}

	public void addChannelConfig(ChannelConfig channelConfig) {
		channels.put(channelConfig.getName().toLowerCase(), channelConfig);
	}
	
	public void removeChannelConfig(String channelName) {
		channels.remove(channelName.toLowerCase());
	}

	public boolean hasChannelConfig(String channelName) {
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
		ircColor = Utils.unquote(config.get(categoryName, "ircColor", "").getString());
		emoteColor = Utils.unquote(config.get(categoryName, "emoteColor", "").getString());
		quitMessage = Utils.unquote(config.get(categoryName, "quitMessage", "").getString());
		nickServName = Utils.unquote(config.get(categoryName, "nickServName", "").getString());
		nickServPassword = Utils.unquote(config.get(categoryName, "nickServPassword", "").getString());
		serverPassword = Utils.unquote(config.get(categoryName, "serverPassword", "").getString());
		allowPrivateMessages = config.get(categoryName, "allowPrivateMessages", allowPrivateMessages).getBoolean(allowPrivateMessages);
		autoConnect = config.get(categoryName, "autoConnect", autoConnect).getBoolean(autoConnect);
		
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
		config.get(categoryName, "host", "").set(Utils.quote(host));
		config.get(categoryName, "nick", "").set(Utils.quote(nick != null ? nick : ""));
		config.get(categoryName, "ircColor", "").set(Utils.quote(ircColor != null ? ircColor : ""));
		config.get(categoryName, "emoteColor", "").set(Utils.quote(emoteColor != null ? emoteColor : ""));
		config.get(categoryName, "quitMessage", "").set(Utils.quote(quitMessage != null ? quitMessage : ""));
		config.get(categoryName, "nickServName", "").set(Utils.quote(GlobalConfig.saveCredentials && nickServName != null ? nickServName : ""));
		config.get(categoryName, "nickServPassword", "").set(Utils.quote(GlobalConfig.saveCredentials && nickServPassword != null ? nickServPassword : ""));
		config.get(categoryName, "serverPassword", "").set(Utils.quote(GlobalConfig.saveCredentials && serverPassword != null ? serverPassword : ""));
		config.get(categoryName, "allowPrivateMessages", allowPrivateMessages).set(allowPrivateMessages);
		config.get(categoryName, "autoConnect", autoConnect).set(autoConnect);
		
		String channelsCategoryName = categoryName + Configuration.CATEGORY_SPLITTER + ConfigurationHandler.CATEGORY_CHANNELS;
		int c = 0;
		for(ChannelConfig channelConfig : channels.values()) {
			String channelCategoryName = channelsCategoryName + Configuration.CATEGORY_SPLITTER + ConfigurationHandler.CATEGORY_CHANNEL_PREFIX + c;
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
		else if(key.equals("allowPrivateMessages")) value = String.valueOf(allowPrivateMessages);
		else if(key.equals("autoConnect")) value = String.valueOf(autoConnect);
		if(value != null) {
			Utils.sendLocalizedMessage(sender, "irc.config.lookup", host, key, value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", host, key);
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
		} else if(key.equals("allowPrivateMessages")) {
			allowPrivateMessages = Boolean.parseBoolean(value);
		} else if(key.equals("autoConnect")) {
			autoConnect = Boolean.parseBoolean(value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", host, key, value);
			return;
		}
		Utils.sendLocalizedMessage(sender, "irc.config.change", host, key, value);
		ConfigurationHandler.save();
	}
	
	public static void addOptionstoList(List<String> list) {
		list.add("ircColor");
		list.add("emoteColor");
		list.add("quitMessage");
		list.add("allowPrivateMessages");
		list.add("autoConnect");
	}

	public static void addValuesToList(List<String> list, String option) {
		if(option.equals("ircColor") || option.equals("emoteColor")) {
			Utils.addValidColorsToList(list);
		} else if(option.equals("allowPrivateMessages") || option.equals("autoConnect")) {
			Utils.addBooleansToList(list);
		}
	}
}
