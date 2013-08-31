// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.config;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import blay09.mods.irc.IRCConnection;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

public class ConfigurationHandler {

	public static final String DEFAULT_NICK = "EiraBot";
	public static final String CATEGORY_SERVERS = "servers.";
	public static final String CATEGORY_FLAGS_SUFFIX = ".flags";
	
	private static final Map<String, ServerConfig> serverConfigs = new HashMap<String, ServerConfig>();
	private static Configuration config;
	
	public static void load(File configFile) {
		config = new Configuration(configFile);
		config.removeCategory(config.getCategory(Configuration.CATEGORY_GENERAL));
		config.removeCategory(config.getCategory(Configuration.CATEGORY_BLOCK));
		config.removeCategory(config.getCategory(Configuration.CATEGORY_ITEM));
		
		GlobalConfig.nick = config.get("global", "nick", DEFAULT_NICK).getString();
		GlobalConfig.enableNameColors = config.get("global", "enableNameColors", GlobalConfig.enableNameColors).getBoolean(GlobalConfig.enableNameColors);
		GlobalConfig.enableAliases = config.get("global", "enableAliases", GlobalConfig.enableAliases).getBoolean(GlobalConfig.enableAliases);
		GlobalConfig.opColor = config.get("global", "opColor", GlobalConfig.opColor).getString();
		GlobalConfig.ircColor = config.get("global", "ircColor", GlobalConfig.ircColor).getString();
		String[] colorBlackList = config.get("global", "colorBlackList", new String[] { "black" }).getStringList();
		for(int i = 0; i < colorBlackList.length; i++) {
			GlobalConfig.colorBlackList.add(colorBlackList[i]);
		}
		GlobalConfig.showDeathMessages = config.get("global", "showDeathMessages", GlobalConfig.showDeathMessages).getBoolean(GlobalConfig.showDeathMessages);
		GlobalConfig.showMinecraftJoinLeave = config.get("global", "showMinecraftJoinLeave", GlobalConfig.showMinecraftJoinLeave).getBoolean(GlobalConfig.showMinecraftJoinLeave);
		GlobalConfig.showIRCJoinLeave = config.get("global", "showIRCJoinLeave", GlobalConfig.showIRCJoinLeave).getBoolean(GlobalConfig.showIRCJoinLeave);
		GlobalConfig.allowPrivateMessages = config.get("global", "allowPrivateMessages", GlobalConfig.allowPrivateMessages).getBoolean(GlobalConfig.allowPrivateMessages);
		
		for(String category : config.getCategoryNames()) {
			if(!category.startsWith(CATEGORY_SERVERS) || category.endsWith(CATEGORY_FLAGS_SUFFIX)) {
				continue;
			}
			String nick = config.get(category, "nick", GlobalConfig.nick).getString();
			ServerConfig serverConfig = new ServerConfig(category.substring(CATEGORY_SERVERS.length()).replaceAll("_", "."), nick);
			String[] channels = config.get(category, "channels", new String[] { "Bread" }).getStringList();
			for(int i = 0; i < channels.length; i++) {
				serverConfig.channels.add("#" + channels[i]);
				if(config.hasKey(category + CATEGORY_FLAGS_SUFFIX, channels[i])) {
					serverConfig.channelFlags.put("#" + channels[i], config.get(category + CATEGORY_FLAGS_SUFFIX, channels[i], "").getString());
				}				
			}
			serverConfig.nickServName = config.get(category, "nickServName", serverConfig.nickServName).getString();
			serverConfig.nickServPassword = config.get(category, "nickServPassword", serverConfig.nickServPassword).getString();
			serverConfig.serverPassword = config.get(category, "serverPassword", serverConfig.serverPassword).getString();
			serverConfig.saveCredentials = config.get(category, "saveCredentials", false).getBoolean(false);
			serverConfig.allowPrivateMessages = config.get(category, "allowPrivateMessages", true).getBoolean(true);
			serverConfig.autoConnect = config.get(category, "autoConnect", true).getBoolean(true);
			serverConfigs.put(serverConfig.host, serverConfig);
		}
		
		config.save();
	}
	
	public static void save() {
		for(ServerConfig serverConfig : serverConfigs.values()) {
			String category = CATEGORY_SERVERS + serverConfig.host.replaceAll("\\.", "_");
			config.get(category, "nick", GlobalConfig.nick).set(serverConfig.nick);
			String[] channels = serverConfig.channels.toArray(new String[serverConfig.channels.size()]);
			for(int i = 0; i < channels.length; i++) {
				channels[i] = channels[i].substring(1);
			}
			config.get(category, "channels", channels).set(channels);
			for(Entry<String, String> entry : serverConfig.channelFlags.entrySet()) {
				if(serverConfig.channels.contains(entry.getKey())) {
					config.get(category + CATEGORY_FLAGS_SUFFIX, entry.getKey().substring(1), entry.getValue()).set(entry.getValue());
				}
			}
			if(serverConfig.saveCredentials) {
				config.get(category, "nickServName", serverConfig.nickServName).set(serverConfig.nickServName);
				config.get(category, "nickServPassword", serverConfig.nickServPassword).set(serverConfig.nickServPassword);
				config.get(category, "serverPassword", serverConfig.serverPassword).set(serverConfig.serverPassword);
			}
			config.get(category, "allowPrivateMessages", serverConfig.allowPrivateMessages).set(serverConfig.allowPrivateMessages);
			config.get(category, "autoConnect", serverConfig.autoConnect).set(serverConfig.autoConnect);
			config.get(category, "saveCredentials", serverConfig.saveCredentials).set(serverConfig.saveCredentials);
		}
		
		config.save();
	}
	
	public static ServerConfig getServerConfig(String host) {
		ServerConfig serverConfig = serverConfigs.get(host);
		if(serverConfig == null) {
			serverConfig = new ServerConfig(host, "");
			serverConfigs.put(host, serverConfig);
		}
		return serverConfig;
	}

	public static Collection<ServerConfig> getServerConfigs() {
		return serverConfigs.values();
	}

	public static void removeServerConfig(String host) {
		serverConfigs.remove(host);
	}

	public static boolean hasServerConfig(String host) {
		return serverConfigs.containsKey(host);
	}

}
