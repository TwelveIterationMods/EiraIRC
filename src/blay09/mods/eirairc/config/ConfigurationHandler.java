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
import blay09.mods.eirairc.IRCTargetError;
import blay09.mods.eirairc.Utils;

public class ConfigurationHandler {

	public static final String DEFAULT_NICK = "EiraBot";
	public static final String CATEGORY_GLOBAL = "global";
	public static final String CATEGORY_DISPLAY = "display";
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
		
		/*
		 * Global Config
		 */
		GlobalConfig.nick = Utils.unquote(config.get(CATEGORY_GLOBAL, "nick", DEFAULT_NICK + (int) (Math.random() * 10000)).getString());
		GlobalConfig.allowPrivateMessages = config.get(CATEGORY_GLOBAL, "allowPrivateMessages", GlobalConfig.allowPrivateMessages).getBoolean(GlobalConfig.allowPrivateMessages);
		GlobalConfig.enableLinkFilter = config.get(CATEGORY_GLOBAL, "enableLinkFilter", GlobalConfig.enableLinkFilter).getBoolean(GlobalConfig.enableLinkFilter);
		GlobalConfig.saveCredentials = config.get(CATEGORY_GLOBAL, "saveCredentials", GlobalConfig.saveCredentials).getBoolean(GlobalConfig.saveCredentials);
		GlobalConfig.registerShortCommands = config.get(CATEGORY_GLOBAL, "registerShortCommands", GlobalConfig.registerShortCommands).getBoolean(GlobalConfig.registerShortCommands);
		config.getCategory(CATEGORY_GLOBAL).setComment("These are settings that are applied on all servers and channels.");
		
		/*
		 * Display Config
		 */
		GlobalConfig.ircColor = config.get(CATEGORY_DISPLAY, "ircColor", GlobalConfig.ircColor).getString();
		GlobalConfig.emoteColor = config.get(CATEGORY_DISPLAY, "emoteColor", GlobalConfig.emoteColor).getString();
		GlobalConfig.relayDeathMessages = config.get(CATEGORY_DISPLAY, "showDeathMessages", GlobalConfig.relayDeathMessages).getBoolean(GlobalConfig.relayDeathMessages);
		GlobalConfig.relayMinecraftJoinLeave = config.get(CATEGORY_DISPLAY, "showMinecraftJoinLeave", GlobalConfig.relayMinecraftJoinLeave).getBoolean(GlobalConfig.relayMinecraftJoinLeave);
		GlobalConfig.relayIRCJoinLeave = config.get(CATEGORY_DISPLAY, "showIRCJoinLeave", GlobalConfig.relayIRCJoinLeave).getBoolean(GlobalConfig.relayIRCJoinLeave);
		GlobalConfig.relayNickChanges = config.get(CATEGORY_DISPLAY, "showNickChanges", GlobalConfig.relayNickChanges).getBoolean(GlobalConfig.relayNickChanges);
		ConfigCategory displayCategory = config.getCategory(CATEGORY_DISPLAY);
		DisplayFormatConfig.defaultConfig(config, displayCategory);
		for(ConfigCategory category : displayCategory.getChildren()) {
			DisplayFormatConfig dfc = new DisplayFormatConfig(category);
			dfc.load(config);
			GlobalConfig.displayFormates.put(dfc.getName(), dfc);
		}
		displayCategory.setComment("These options determine how the chat is displayed and what gets sent / received to and from IRC.");
		
		/*
		 * ClientOnly Config
		 */
		GlobalConfig.persistentConnection = config.get(CATEGORY_CLIENTONLY, "persistentConnection", GlobalConfig.persistentConnection).getBoolean(GlobalConfig.persistentConnection);
		config.getCategory(CATEGORY_CLIENTONLY).setComment("These options are only important in the client version as they either have no function on servers or aren't really intended to be server-side.");
		
		/*
		 * ServerOnly Config
		 */
		GlobalConfig.enableNameColors = config.get(CATEGORY_SERVERONLY, "enableNameColors", GlobalConfig.enableNameColors).getBoolean(GlobalConfig.enableNameColors);
		GlobalConfig.enableAliases = config.get(CATEGORY_SERVERONLY, "enableAliases", GlobalConfig.enableAliases).getBoolean(GlobalConfig.enableAliases);
		GlobalConfig.opColor = config.get(CATEGORY_SERVERONLY, "opColor", GlobalConfig.opColor).getString();
		String[] colorBlackList = config.get(CATEGORY_SERVERONLY, "colorBlackList", Globals.DEFAULT_COLOR_BLACKLIST).getStringList();
		for(int i = 0; i < colorBlackList.length; i++) {
			GlobalConfig.colorBlackList.add(colorBlackList[i]);
		}
		GlobalConfig.interOp = config.get(CATEGORY_SERVERONLY, "interOp", GlobalConfig.interOp).getBoolean(GlobalConfig.interOp);
		config.getCategory(CATEGORY_SERVERONLY).setComment("These options are only important in the server version as they either have no function on clients or aren't really intended to be client-side.");
		
		/*
		 * Servers Config
		 */
		ConfigCategory serverCategory = config.getCategory(CATEGORY_SERVERS);
		for(String categoryName : config.getCategoryNames()) {
			ConfigCategory category = config.getCategory(categoryName);
			if(category.parent == serverCategory) {
				String host = Utils.unquote(config.get(categoryName, "host", "").getString());
				ServerConfig serverConfig = new ServerConfig(host);
				serverConfig.load(config, category);
				serverConfigs.put(host, serverConfig);
			}
		}
		config.getCategory(CATEGORY_SERVERS).setComment("The following is a list of IRC server configs along with their channels.");
		
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
	
	public static void save() {
		/*
		 * Global Config
		 */
		config.get(CATEGORY_GLOBAL, "nick", "").set(Utils.quote(GlobalConfig.nick));
		config.get(CATEGORY_GLOBAL, "allowPrivateMessages", GlobalConfig.allowPrivateMessages).set(GlobalConfig.allowPrivateMessages);
		config.get(CATEGORY_GLOBAL, "enableLinkFilter", GlobalConfig.enableLinkFilter).set(GlobalConfig.enableLinkFilter);
		config.get(CATEGORY_GLOBAL, "saveCredentials", GlobalConfig.saveCredentials).set(GlobalConfig.saveCredentials);
		config.get(CATEGORY_GLOBAL, "quitMessage", GlobalConfig.quitMessage).set(GlobalConfig.quitMessage);
		
		/*
		 * Display Config
		 */
		config.get(CATEGORY_DISPLAY, "ircColor", GlobalConfig.ircColor).set(GlobalConfig.ircColor);
		config.get(CATEGORY_DISPLAY, "emoteColor", GlobalConfig.emoteColor).set(GlobalConfig.emoteColor);
		config.get(CATEGORY_DISPLAY, "showDeathMessages", GlobalConfig.relayDeathMessages).set(GlobalConfig.relayDeathMessages);
		config.get(CATEGORY_DISPLAY, "showMinecraftJoinLeave", GlobalConfig.relayMinecraftJoinLeave).set(GlobalConfig.relayMinecraftJoinLeave);
		config.get(CATEGORY_DISPLAY, "showIRCJoinLeave", GlobalConfig.relayIRCJoinLeave).set(GlobalConfig.relayIRCJoinLeave);
		config.get(CATEGORY_DISPLAY, "showNickChanges", GlobalConfig.relayNickChanges).set(GlobalConfig.relayNickChanges);
		
		/*
		 * ClientOnly Config
		 */
		config.get(CATEGORY_CLIENTONLY, "persistentConnection", GlobalConfig.persistentConnection).set(GlobalConfig.persistentConnection);
		
		/*
		 * ServerOnly Config
		 */
		config.get(CATEGORY_SERVERONLY, "enableNameColors", GlobalConfig.enableNameColors).set(GlobalConfig.enableNameColors);
		config.get(CATEGORY_SERVERONLY, "enableAliases", GlobalConfig.enableAliases).set(GlobalConfig.enableAliases);
		config.get(CATEGORY_SERVERONLY, "opColor", GlobalConfig.opColor).set(GlobalConfig.opColor);
		config.get(CATEGORY_SERVERONLY, "colorBlackList", new String[0]).set(GlobalConfig.colorBlackList.toArray(new String[GlobalConfig.colorBlackList.size()]));
		config.get(CATEGORY_SERVERONLY, "interOp", GlobalConfig.interOp).set(GlobalConfig.interOp);
		
		int c = 0;
		for(ServerConfig serverConfig : serverConfigs.values()) {
			String category = CATEGORY_SERVERS + Configuration.CATEGORY_SPLITTER + CATEGORY_SERVER_PREFIX + c;
			serverConfig.save(config, config.getCategory(category));
			c++;
		}
		
		config.save();
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

}
