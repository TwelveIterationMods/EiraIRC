// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.CompatibilityConfig;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.KeyConfig;
import net.blay09.mods.eirairc.config.ServiceConfig;
import net.blay09.mods.eirairc.config.NotificationConfig;
import net.blay09.mods.eirairc.config.ScreenshotConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.IRCTargetError;
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
	public static final String PREFIX_SERVER = "server";
	public static final String PREFIX_CHANNEL = "channel";
	
	private static final Map<String, ServerConfig> serverConfigs = new HashMap<String, ServerConfig>();
	private static Configuration config;
	
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
			Object rt = Utils.resolveIRCTarget(target, true, false, true, false, false, false);
			if(rt instanceof IRCTargetError) {
				switch((IRCTargetError) rt) {
				case ChannelNotFound: Utils.sendLocalizedMessage(sender, "irc.target.channelNotFound", target);
					break;
				case InvalidTarget: Utils.sendLocalizedMessage(sender, "irc.target.invalid");
					break;
				case ServerNotFound: Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", target);
					break;
				case SpecifyServer: Utils.sendLocalizedMessage(sender, "irc.target.unknown");
					break;
				default: Utils.sendLocalizedMessage(sender, "irc.target.unknown");
					break;
				}
			} else if(rt instanceof ServerConfig) {
				((ServerConfig) rt).handleConfigCommand(sender, key, value);
			} else if(rt instanceof ChannelConfig) {
				((ChannelConfig) rt).handleConfigCommand(sender, key, value);
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
			Object rt = Utils.resolveIRCTarget(target, true, false, true, false, false, false);
			if(rt instanceof IRCTargetError) {
				switch((IRCTargetError) rt) {
				case ChannelNotFound: Utils.sendLocalizedMessage(sender, "irc.target.channelNotFound", target);
					break;
				case InvalidTarget: Utils.sendLocalizedMessage(sender, "irc.target.invalid");
					break;
				case ServerNotFound: Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", target);
					break;
				case SpecifyServer: Utils.sendLocalizedMessage(sender, "irc.target.unknown");
					break;
				default: Utils.sendLocalizedMessage(sender, "irc.target.unknown");
					break;
				}
			} else if(rt instanceof ServerConfig) {
				((ServerConfig) rt).handleConfigCommand(sender, key);
			} else if(rt instanceof ChannelConfig) {
				((ChannelConfig) rt).handleConfigCommand(sender, key);
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

}
