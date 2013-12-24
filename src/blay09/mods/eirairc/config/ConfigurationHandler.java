// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import blay09.mods.eirairc.util.IRCTargetError;
import blay09.mods.eirairc.util.Utils;

public class ConfigurationHandler {

	public static final String CATEGORY_GLOBAL = "global";
	public static final String CATEGORY_DISPLAY = "display";
	public static final String CATEGORY_FORMATS = "formats";
	public static final String CATEGORY_SERVERONLY = "serveronly";
	public static final String CATEGORY_CLIENTONLY = "clientonly";
	public static final String CATEGORY_SERVERS = "servers";
	public static final String CATEGORY_CHANNELS = "channels";
	public static final String CATEGORY_SERVER_PREFIX = "server";
	public static final String CATEGORY_CHANNEL_PREFIX = "channel";
	
	private static final Map<String, ServerConfig> serverConfigs = new HashMap<String, ServerConfig>();
	private static Configuration config;
	
	public static void load(File configFile) {
		config = new Configuration(configFile);
		if(!config.get(CATEGORY_GLOBAL, "isNewConfigFormat", false, "Do not change this, it'll reset your config file.").getBoolean(false)) {
			resetConfig();
			config.get(CATEGORY_GLOBAL, "isNewConfigFormat", true, "Do not change this, it'll reset your config file.").set(true);
		}
		config.removeCategory(config.getCategory(Configuration.CATEGORY_GENERAL));
		config.removeCategory(config.getCategory(Configuration.CATEGORY_BLOCK));
		config.removeCategory(config.getCategory(Configuration.CATEGORY_ITEM));
		
		GlobalConfig.load(config);
		
		config.save();
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
			GlobalConfig.handleConfigCommand(sender, key, value);
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

	public static ServerConfig getDefaultServerConfig() {
		Iterator<ServerConfig> it = serverConfigs.values().iterator();
		if(it.hasNext()) {
			return it.next();
		}
		return null;
	}

	public static void save() {
		GlobalConfig.save(config);
	}

	public static void handleConfigCommand(ICommandSender sender, String target, String key) {
		if(target.equals("global")) {
			GlobalConfig.handleConfigCommand(sender, key);
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

}
