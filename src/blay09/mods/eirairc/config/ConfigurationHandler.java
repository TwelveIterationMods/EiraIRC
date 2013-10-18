// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import blay09.mods.eirairc.irc.IRCConnection;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

public class ConfigurationHandler {

	public static final String DEFAULT_NICK = "EiraBot";
	public static final String CATEGORY_SERVERS = "servers";
	public static final String CATEGORY_CHANNELS = "channels";
	
	private static final Map<String, ServerConfig> serverConfigs = new HashMap<String, ServerConfig>();
	private static Configuration config;
	
	public static void load(File configFile) {
		config = new Configuration(configFile);
		config.removeCategory(config.getCategory(Configuration.CATEGORY_GENERAL));
		config.removeCategory(config.getCategory(Configuration.CATEGORY_BLOCK));
		config.removeCategory(config.getCategory(Configuration.CATEGORY_ITEM));
		
		GlobalConfig.nick = unquote(config.get("global", "nick", DEFAULT_NICK + (int) (Math.random() * 1000)).getString());
		GlobalConfig.enableNameColors = config.get("global", "enableNameColors", GlobalConfig.enableNameColors).getBoolean(GlobalConfig.enableNameColors);
		GlobalConfig.enableAliases = config.get("global", "enableAliases", GlobalConfig.enableAliases).getBoolean(GlobalConfig.enableAliases);
		GlobalConfig.opColor = config.get("global", "opColor", GlobalConfig.opColor).getString();
		GlobalConfig.ircColor = config.get("global", "ircColor", GlobalConfig.ircColor).getString();
		String[] colorBlackList = config.get("global", "colorBlackList", Globals.DEFAULT_COLOR_BLACKLIST).getStringList();
		for(int i = 0; i < colorBlackList.length; i++) {
			GlobalConfig.colorBlackList.add(colorBlackList[i]);
		}
		GlobalConfig.showDeathMessages = config.get("global", "showDeathMessages", GlobalConfig.showDeathMessages).getBoolean(GlobalConfig.showDeathMessages);
		GlobalConfig.showMinecraftJoinLeave = config.get("global", "showMinecraftJoinLeave", GlobalConfig.showMinecraftJoinLeave).getBoolean(GlobalConfig.showMinecraftJoinLeave);
		GlobalConfig.showIRCJoinLeave = config.get("global", "showIRCJoinLeave", GlobalConfig.showIRCJoinLeave).getBoolean(GlobalConfig.showIRCJoinLeave);
		GlobalConfig.allowPrivateMessages = config.get("global", "allowPrivateMessages", GlobalConfig.allowPrivateMessages).getBoolean(GlobalConfig.allowPrivateMessages);
		GlobalConfig.enableLinkFilter = config.get("global", "enableLinkFilter", GlobalConfig.enableLinkFilter).getBoolean(GlobalConfig.enableLinkFilter);
		GlobalConfig.showNickChanges = config.get("global", "showNickChanges", GlobalConfig.showNickChanges).getBoolean(GlobalConfig.showNickChanges);
		GlobalConfig.persistentConnection = config.get("global", "persistentConnection", GlobalConfig.persistentConnection).getBoolean(GlobalConfig.persistentConnection);
		GlobalConfig.interOp = config.get("global", "interOp", GlobalConfig.interOp).getBoolean(GlobalConfig.interOp);
		
		ConfigCategory serverCategory = config.getCategory(CATEGORY_SERVERS);
		for(String categoryName : config.getCategoryNames()) {
			ConfigCategory category = config.getCategory(categoryName);
			if(category.parent == serverCategory) {
				String host = unquote(config.get(categoryName, "host", "").getString());
				ServerConfig serverConfig = new ServerConfig(host);
				serverConfig.setNick(unquote(config.get(categoryName, "nick", "").getString()));
				serverConfig.setNickServ(unquote(config.get(categoryName, "nickServName", "").getString()), unquote(config.get(categoryName, "nickServPassword", "").getString()));
				serverConfig.setServerPassword(unquote(config.get(categoryName, "serverPassword", "").getString()));
				serverConfig.setAllowPrivateMessages(config.get(categoryName, "allowPrivateMessages", true).getBoolean(true));
				serverConfig.setAutoConnect(config.get(categoryName, "autoConnect", true).getBoolean(true));
				
				String channelsCategoryName = categoryName + Configuration.CATEGORY_SPLITTER + CATEGORY_CHANNELS;
				ConfigCategory channelsCategory = config.getCategory(channelsCategoryName);
				for(ConfigCategory channelCategory : channelsCategory.getChildren()) {
					ChannelConfig channelConfig = new ChannelConfig(config.get(channelCategory.getQualifiedName(), "name", "").getString());
					channelConfig.load(config, channelCategory);
					serverConfig.addChannelConfig(channelConfig);
				}
				serverConfigs.put(host, serverConfig);
			}
		}
		
		config.save();
	}
	
	public static String unquote(String s) {
		return s.startsWith("\"") ? s.substring(1, s.length() - 1) : s;
	}
	
	public static String quote(String s) {
		return "\"" + s + "\"";
	}
	
	public static void save() {
//		config.get("global", "nick", "").set(quote(GlobalConfig.nick));
//		config.get("global", "enableNameColors", GlobalConfig.enableNameColors).set(GlobalConfig.enableNameColors);
//		config.get("global", "opColor", GlobalConfig.opColor).set(GlobalConfig.opColor);
//		config.get("global", "ircColor", GlobalConfig.ircColor).set(GlobalConfig.ircColor);
//		config.get("global", "colorBlackList", new String[0]).set(GlobalConfig.colorBlackList.toArray(new String[GlobalConfig.colorBlackList.size()]));
//		config.get("global", "showDeathMessages", GlobalConfig.showDeathMessages).getBoolean(GlobalConfig.showDeathMessages);
//		config.get("global", "showMinecraftJoinLeave", GlobalConfig.showMinecraftJoinLeave).getBoolean(GlobalConfig.showMinecraftJoinLeave);
//		config.get("global", "showIRCJoinLeave", GlobalConfig.showIRCJoinLeave).getBoolean(GlobalConfig.showIRCJoinLeave);
//		config.get("global", "allowPrivateMessages", GlobalConfig.allowPrivateMessages).getBoolean(GlobalConfig.allowPrivateMessages);
//		config.get("global", "enableLinkFilter", GlobalConfig.enableLinkFilter).getBoolean(GlobalConfig.enableLinkFilter);
//		config.get("global", "showNickChanges", GlobalConfig.showNickChanges).getBoolean(GlobalConfig.showNickChanges);
//		config.get("global", "persistentConnection", GlobalConfig.persistentConnection).getBoolean(GlobalConfig.persistentConnection);
//		config.get("global", "interOp", GlobalConfig.interOp).getBoolean(GlobalConfig.interOp);
//		
//		for(ServerConfig serverConfig : serverConfigs.values()) {
//			String category = CATEGORY_SERVERS + serverConfig.host.replaceAll("\\.", "_");
//			config.get(category, "nick", "").set(quote(serverConfig.nick));
//			String[] channels = serverConfig.channels.toArray(new String[serverConfig.channels.size()]);
//			for(int i = 0; i < channels.length; i++) {
//				channels[i] = quote(channels[i]);
//			}
//			config.get(category, "channels", channels).set(channels);
//			for(Entry<String, String> entry : serverConfig.channelFlags.entrySet()) {
//				if(serverConfig.channels.contains(entry.getKey())) {
//					config.get(category + CATEGORY_FLAGS_SUFFIX, quote(entry.getKey()), "").set(quote(entry.getValue()));
//				}
//			}
//			if(GlobalConfig.saveCredentials) {
//				for(Entry<String, String> entry : serverConfig.channelPasswords.entrySet()) {
//					if(serverConfig.channels.contains(entry.getKey())) {
//						config.get(category + CATEGORY_CHANPW_SUFFIX, quote(entry.getKey()), "").set(quote(entry.getValue()));
//					}
//				}
//				config.get(category, "nickServName", "").set(quote(serverConfig.nickServName));
//				config.get(category, "nickServPassword", "").set(quote(serverConfig.nickServPassword));
//				config.get(category, "serverPassword", "").set(quote(serverConfig.serverPassword));
//			}
//			config.get(category, "allowPrivateMessages", false).set(serverConfig.allowPrivateMessages);
//			config.get(category, "autoConnect", false).set(serverConfig.autoConnect);
//		}
//		
//		config.save();
	}
	
	public static ServerConfig getServerConfig(String host) {
		ServerConfig serverConfig = serverConfigs.get(host);
		if(serverConfig == null) {
			serverConfig = new ServerConfig(host);
		}
		return serverConfig;
	}

	public static Collection<ServerConfig> getServerConfigs() {
		return serverConfigs.values();
	}

	public static void addServerConfig(ServerConfig serverConfig) {
		serverConfigs.put(serverConfig.getHost(), serverConfig);
	}
	
	public static void removeServerConfig(String host) {
		serverConfigs.remove(host);
	}

	public static boolean hasServerConfig(String host) {
		return serverConfigs.containsKey(host);
	}

}
