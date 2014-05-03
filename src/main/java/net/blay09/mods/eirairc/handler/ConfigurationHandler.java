// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.blay09.mods.eirairc.config.BotProfile;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.CompatibilityConfig;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.KeyConfig;
import net.blay09.mods.eirairc.config.NotificationConfig;
import net.blay09.mods.eirairc.config.ScreenshotConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.ServiceConfig;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {

	private static String CONFIG_VERSION = "2";
	
	public static final String CATEGORY_GLOBAL = "global";
	public static final String CATEGORY_DISPLAY = "display";
	public static final String CATEGORY_FORMATS = "formats";
	public static final String CATEGORY_SERVERONLY = "serveronly";
	public static final String CATEGORY_CLIENTONLY = "clientonly";
	public static final String CATEGORY_SERVERS = "servers";
	public static final String CATEGORY_CHANNELS = "channels";
	public static final String CATEGORY_COMPAT = "compatibility";
	public static final String CATEGORY_NOTIFICATIONS = "notifications";
	public static final String PREFIX_SERVER = "server";
	public static final String PREFIX_CHANNEL = "channel";
	
	private static final Map<String, ServerConfig> serverConfigs = new HashMap<String, ServerConfig>();

	private static Configuration config;
	private static Map<String, BotProfile> botProfiles = new HashMap<String, BotProfile>();
	private static BotProfile defaultBotProfile;
	
	public static void loadBotProfiles(File profileDir) {
		if(!profileDir.exists()) {
			profileDir.mkdirs();
		}
		File[] files = profileDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith(".cfg");
			}
		});
		for(int i = 0; i < files.length; i++) {
			BotProfile botProfile = new BotProfile(files[i]);
			botProfile.loadCommands();
			botProfiles.put(botProfile.getName(), botProfile);
		}
		if(!botProfiles.containsKey(BotProfile.DEFAULT_CLIENT)) {
			BotProfile botProfile = new BotProfile(new File(profileDir, BotProfile.DEFAULT_CLIENT + ".cfg"));
			botProfile.defaultClient();
			botProfile.save();
			botProfile.loadCommands();
			botProfiles.put(botProfile.getName(), botProfile);
		}
		if(!botProfiles.containsKey(BotProfile.DEFAULT_SERVER)) {
			BotProfile botProfile = new BotProfile(new File(profileDir, BotProfile.DEFAULT_SERVER + ".cfg"));
			botProfile.defaultServer();
			botProfile.save();
			botProfile.loadCommands();
			botProfiles.put(botProfile.getName(), botProfile);
		}
		if(!botProfiles.containsKey(BotProfile.DEFAULT_TWITCH)) {
			BotProfile botProfile = new BotProfile(new File(profileDir, BotProfile.DEFAULT_TWITCH + ".cfg"));
			botProfile.defaultTwitch();
			botProfile.save();
			botProfile.loadCommands();
			botProfiles.put(botProfile.getName(), botProfile);
		}
		defaultBotProfile = botProfiles.get(BotProfile.DEFAULT_CLIENT);
	}
	
	public static void load(File configFile) {
		boolean newConfigFile = !configFile.exists();
		config = new Configuration(configFile);
		if(newConfigFile) {
			config.get(CATEGORY_GLOBAL, "isNewConfigFormat", true, "Do not change this, it'll reset your config file.").set(true);
		} else if(!config.get(CATEGORY_GLOBAL, "isNewConfigFormat", false, "Do not change this, it'll reset your config file.").getBoolean(false)) {
			resetConfig();
			config.get(CATEGORY_GLOBAL, "isNewConfigFormat", true, "Do not change this, it'll reset your config file.").set(true);
		}
		config.removeCategory(config.getCategory(Configuration.CATEGORY_GENERAL));
		
		GlobalConfig.load(config);
		KeyConfig.load(config);
		NotificationConfig.load(config);
		ScreenshotConfig.load(config);
		DisplayConfig.load(config);
		CompatibilityConfig.load(config);
		ServiceConfig.load(config);
		
		config.save();
	}
	
	public static void save() {
		GlobalConfig.save(config);
		KeyConfig.save(config);
		NotificationConfig.save(config);
		ScreenshotConfig.save(config);
		DisplayConfig.save(config);
		CompatibilityConfig.save(config);
	}
	
	public static void resetConfig() {
		for(String categoryName : config.getCategoryNames()) {
			ConfigCategory category = config.getCategory(categoryName);
			if(category.parent == null) {
				config.removeCategory(category);
			}
		}
	}
	
	public static ServerConfig getServerConfig(String host) {
		ServerConfig serverConfig = serverConfigs.get(host.toLowerCase());
		if(serverConfig == null) {
			serverConfig = new ServerConfig(host);
		}
		return serverConfig;
	}

	public static Collection<ServerConfig> getServerConfigs() {
		return serverConfigs.values();
	}

	public static void addServerConfig(ServerConfig serverConfig) {
		serverConfigs.put(serverConfig.getHost().toLowerCase(), serverConfig);
	}
	
	public static void removeServerConfig(String host) {
		serverConfigs.remove(host.toLowerCase());
	}

	public static boolean hasServerConfig(String host) {
		return serverConfigs.containsKey(host.toLowerCase());
	}

	public static void handleConfigCommand(ICommandSender sender, String target, String key, String value) {
		if(target.equals("global")) {
			boolean result = false;
			result = GlobalConfig.handleConfigCommand(sender, key, value);
			if(!result) result = NotificationConfig.handleConfigCommand(sender, key, value);
			if(!result) result = ScreenshotConfig.handleConfigCommand(sender, key, value);
			if(!result) result = DisplayConfig.handleConfigCommand(sender, key, value);
			if(!result) result = CompatibilityConfig.handleConfigCommand(sender, key, value);
			if(result) {
				Utils.sendLocalizedMessage(sender, "irc.config.change", "Global", key, value);
				ConfigurationHandler.save();
			} else {
				Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", "Global", key);
			}
		} else {
			ChannelConfig channelConfig = IRCResolver.resolveChannelConfig(target, IRCResolver.FLAGS_NONE);
			if(channelConfig != null) {
				channelConfig.handleConfigCommand(sender, key, value);
			} else {
				ServerConfig serverConfig = IRCResolver.resolveServerConfig(target, IRCResolver.FLAGS_NONE);
				if(serverConfig != null) {
					serverConfig.handleConfigCommand(sender, key, value);
				} else {
					Utils.sendLocalizedMessage(sender, "irc.target.targetNotFound", target);
				}
			}
		}
	}
	
	public static void handleConfigCommand(ICommandSender sender, String target, String key) {
		if(target.equals("global")) {
			String result = null;
			result = GlobalConfig.handleConfigCommand(sender, key);
			if(result == null) result = NotificationConfig.handleConfigCommand(sender, key);
			if(result == null) result = ScreenshotConfig.handleConfigCommand(sender, key);
			if(result == null) result = DisplayConfig.handleConfigCommand(sender, key);
			if(result == null) result = CompatibilityConfig.handleConfigCommand(sender, key);
			if(result != null) {
				Utils.sendLocalizedMessage(sender, "irc.config.lookup", "Global", key, result);
			} else {
				Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", "Global", key);
			}
		} else {
			ChannelConfig channelConfig = IRCResolver.resolveChannelConfig(target, IRCResolver.FLAGS_NONE);
			if(channelConfig != null) {
				channelConfig.handleConfigCommand(sender, key);
			} else {
				ServerConfig serverConfig = IRCResolver.resolveServerConfig(target, IRCResolver.FLAGS_NONE);
				if(serverConfig != null) {
					serverConfig.handleConfigCommand(sender, key);
				} else {
					Utils.sendLocalizedMessage(sender, "irc.target.targetNotFound", target);
				}
			}
		}
	}

	public static ServerConfig getDefaultServerConfig() {
		Iterator<ServerConfig> it = serverConfigs.values().iterator();
		if(it.hasNext()) {
			return it.next();
		}
		return null;
	}

	public static void addOptionsToList(List<String> list) {
		GlobalConfig.addOptionsToList(list);
		DisplayConfig.addOptionsToList(list);
		NotificationConfig.addOptionsToList(list);
		ScreenshotConfig.addOptionsToList(list);
		CompatibilityConfig.addOptionsToList(list);
	}

	public static void addValuesToList(List<String> list, String option) {
		GlobalConfig.addValuesToList(list, option);
		DisplayConfig.addValuesToList(list, option);
		NotificationConfig.addValuesToList(list, option);
		ScreenshotConfig.addValuesToList(list, option);
		CompatibilityConfig.addValuesToList(list, option);
	}

	public static BotProfile getBotProfile(String name) {
		BotProfile botProfile = botProfiles.get(name);
		if(botProfile == null) {
			return defaultBotProfile;
		}
		return botProfile;
	}

}
