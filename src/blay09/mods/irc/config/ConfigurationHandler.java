// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.config;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

public class ConfigurationHandler {

	public static final String DEFAULT_NICK = "EiraBot";
	public static final String CATEGORY_SERVERS = "servers.";
	public static final String FLAGS_PREFIX = "flags.";
	
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
		String[] colorBlackList = config.get("global", "colorBlackList", new String[0]).getStringList();
		for(int i = 0; i < colorBlackList.length; i++) {
			GlobalConfig.colorBlackList.add(colorBlackList[i]);
		}
		GlobalConfig.showDeathMessages = config.get("global", "showDeathMessages", GlobalConfig.showDeathMessages).getBoolean(GlobalConfig.showDeathMessages);
		GlobalConfig.showMinecraftJoinLeave = config.get("global", "showMinecraftJoinLeave", GlobalConfig.showMinecraftJoinLeave).getBoolean(GlobalConfig.showMinecraftJoinLeave);
		GlobalConfig.showIRCJoinLeave = config.get("global", "showIRCJoinLeave", GlobalConfig.showIRCJoinLeave).getBoolean(GlobalConfig.showIRCJoinLeave);
		GlobalConfig.allowPrivateMessages = config.get("global", "allowPrivateMessages", GlobalConfig.allowPrivateMessages).getBoolean(GlobalConfig.allowPrivateMessages);
		
		for(String category : config.getCategoryNames()) {
			if(!category.startsWith(CATEGORY_SERVERS)) {
				continue;
			}
			String nick = config.get(category, "nick", GlobalConfig.nick).getString();
			ServerConfig serverConfig = new ServerConfig(category.substring(CATEGORY_SERVERS.length()).replaceAll("_", "."), nick);
			String[] channels = config.get(category, "channels", new String[0]).getStringList();
			for(int i = 0; i < channels.length; i++) {
				serverConfig.channels.add(channels[i]);
				if(config.hasKey(category, FLAGS_PREFIX + channels[i])) {
					serverConfig.channelFlags.put(channels[i], config.get(category, FLAGS_PREFIX + channels[i], "").getString());
				}				
			}
			serverConfig.nickServName = config.get(category, "nickServName", serverConfig.nickServName).getString();
			serverConfig.nickServPassword = config.get(category, "nickServPassword", serverConfig.nickServPassword).getString();
			serverConfig.allowPrivateMessages = config.get(category, "allowPrivateMessages", true).getBoolean(true);
			serverConfigs.put(serverConfig.host, serverConfig);
		}
		
		config.save();
	}
	
	public static void save() {
		for(ServerConfig serverConfig : serverConfigs.values()) {
			String category = CATEGORY_SERVERS + serverConfig.host.replaceAll("\\.", "_");
			config.get(category, "nick", GlobalConfig.nick).set(serverConfig.nick);
			config.get(category, "channels", new String[0]).set(serverConfig.channels.toArray(new String[serverConfig.channels.size()]));
			for(Entry<String, String> entry : serverConfig.channelFlags.entrySet()) {
				if(serverConfig.channels.contains(entry.getKey())) {
					config.get(category, FLAGS_PREFIX + entry.getKey(), entry.getValue()).set(entry.getValue());
				}
			}
			config.get(category, "nickServName", serverConfig.nickServName).set(serverConfig.nickServName);
			config.get(category, "nickServPassword", serverConfig.nickServPassword).set(serverConfig.nickServPassword);
		}
		
		config.save();
	}
	
	public static ServerConfig getServerConfig(String host) {
		ServerConfig serverConfig = serverConfigs.get(host);
		if(serverConfig == null) {
			serverConfig = new ServerConfig(host, DEFAULT_NICK + (int) (Math.random() * 10000));
			serverConfigs.put(host, serverConfig);
		}
		return serverConfig;
	}

	public static Collection<ServerConfig> getServerConfigs() {
		return serverConfigs.values();
	}

}
