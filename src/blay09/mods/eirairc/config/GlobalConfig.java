// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import blay09.mods.eirairc.Utils;

public class GlobalConfig {

	public static final String DEFAULT_NICK = "EiraBot";
	
	public static String nick = DEFAULT_NICK;
	public static final List<String> colorBlackList = new ArrayList<String>();
	public static final Map<String, DisplayFormatConfig> displayFormates = new HashMap<String, DisplayFormatConfig>();
	public static String opColor = "red";
	public static String ircColor = "gray";
	public static String emoteColor = "purple";
	public static String quitMessage = "Leaving.";
	public static String displayMode = "S-Light";
	public static boolean enableNameColors = true;
	public static boolean enableAliases = true;
	public static boolean relayDeathMessages = true;
	public static boolean relayMinecraftJoinLeave = true;
	public static boolean relayIRCJoinLeave = true;
	public static boolean relayNickChanges = true;
	public static boolean allowPrivateMessages = true;
	public static boolean persistentConnection = true;
	public static boolean saveCredentials = false;
	public static boolean enableLinkFilter = true;
	public static boolean registerShortCommands = true;
	public static boolean interOp = false;
	
	public static void handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("opColor")) {
			if(Utils.isValidColor(value)) {
				opColor = value;
			} else {
				Utils.sendLocalizedMessage(sender, "irc.color.invalid", value);
				return;
			}
		} else if(key.equals("ircColor")) {
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
		} else if(key.equals("enableNameColors")){
			enableNameColors = Boolean.parseBoolean(value);
		} else if(key.equals("relayDeathMessages")){
			relayDeathMessages = Boolean.parseBoolean(value);
		} else if(key.equals("relayMinecraftJoinLeave")){
			relayMinecraftJoinLeave = Boolean.parseBoolean(value);
		} else if(key.equals("relayIRCJoinLeave")){
			relayIRCJoinLeave = Boolean.parseBoolean(value);
		} else if(key.equals("relayNickChanges")){
			relayNickChanges = Boolean.parseBoolean(value);
		} else if(key.equals("allowPrivateMessages")){
			allowPrivateMessages = Boolean.parseBoolean(value);
		} else if(key.equals("persistentConnection")){
			persistentConnection = Boolean.parseBoolean(value);
		} else if(key.equals("saveCredentials")){
			saveCredentials = Boolean.parseBoolean(value);
		} else if(key.equals("enableLinkFilter")){
			enableLinkFilter = Boolean.parseBoolean(value);
		} else if(key.equals("registerShortCommands")){
			registerShortCommands = Boolean.parseBoolean(value);
			Utils.sendLocalizedMessage(sender, "irc.config.requiresRestart");
		} else if(key.equals("interOp") || key.equals("enableAliases")) {
			Utils.sendLocalizedMessage(sender, "irc.config.noAbuse");
			return;
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", "Global", key);
			return;
		}
		Utils.sendLocalizedMessage(sender, "irc.config.change", "Global", key, value);
		ConfigurationHandler.save();
	}
	
	public static void load(Configuration config) {
		/*
		 * Global Config
		 */
		GlobalConfig.nick = Utils.unquote(config.get(ConfigurationHandler.CATEGORY_GLOBAL, "nick", DEFAULT_NICK + (int) (Math.random() * 10000)).getString());
		GlobalConfig.allowPrivateMessages = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "allowPrivateMessages", GlobalConfig.allowPrivateMessages).getBoolean(GlobalConfig.allowPrivateMessages);
		GlobalConfig.enableLinkFilter = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "enableLinkFilter", GlobalConfig.enableLinkFilter).getBoolean(GlobalConfig.enableLinkFilter);
		GlobalConfig.saveCredentials = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "saveCredentials", GlobalConfig.saveCredentials).getBoolean(GlobalConfig.saveCredentials);
		GlobalConfig.registerShortCommands = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "registerShortCommands", GlobalConfig.registerShortCommands).getBoolean(GlobalConfig.registerShortCommands);
		config.getCategory(ConfigurationHandler.CATEGORY_GLOBAL).setComment("These are settings that are applied on all servers and channels.");
		
		/*
		 * Display Config
		 */
		GlobalConfig.ircColor = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "ircColor", GlobalConfig.ircColor).getString();
		GlobalConfig.emoteColor = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "emoteColor", GlobalConfig.emoteColor).getString();
		GlobalConfig.relayDeathMessages = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "showDeathMessages", GlobalConfig.relayDeathMessages).getBoolean(GlobalConfig.relayDeathMessages);
		GlobalConfig.relayMinecraftJoinLeave = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "showMinecraftJoinLeave", GlobalConfig.relayMinecraftJoinLeave).getBoolean(GlobalConfig.relayMinecraftJoinLeave);
		GlobalConfig.relayIRCJoinLeave = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "showIRCJoinLeave", GlobalConfig.relayIRCJoinLeave).getBoolean(GlobalConfig.relayIRCJoinLeave);
		GlobalConfig.relayNickChanges = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "showNickChanges", GlobalConfig.relayNickChanges).getBoolean(GlobalConfig.relayNickChanges);
		ConfigCategory displayCategory = config.getCategory(ConfigurationHandler.CATEGORY_DISPLAY);
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
		GlobalConfig.persistentConnection = config.get(ConfigurationHandler.CATEGORY_CLIENTONLY, "persistentConnection", GlobalConfig.persistentConnection).getBoolean(GlobalConfig.persistentConnection);
		config.getCategory(ConfigurationHandler.CATEGORY_CLIENTONLY).setComment("These options are only important in the client version as they either have no function on servers or aren't really intended to be server-side.");
		
		/*
		 * ServerOnly Config
		 */
		GlobalConfig.enableNameColors = config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "enableNameColors", GlobalConfig.enableNameColors).getBoolean(GlobalConfig.enableNameColors);
		GlobalConfig.enableAliases = config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "enableAliases", GlobalConfig.enableAliases).getBoolean(GlobalConfig.enableAliases);
		GlobalConfig.opColor = config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "opColor", GlobalConfig.opColor).getString();
		String[] colorBlackList = config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "colorBlackList", Globals.DEFAULT_COLOR_BLACKLIST).getStringList();
		for(int i = 0; i < colorBlackList.length; i++) {
			GlobalConfig.colorBlackList.add(colorBlackList[i]);
		}
		GlobalConfig.interOp = config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "interOp", GlobalConfig.interOp).getBoolean(GlobalConfig.interOp);
		config.getCategory(ConfigurationHandler.CATEGORY_SERVERONLY).setComment("These options are only important in the server version as they either have no function on clients or aren't really intended to be client-side.");
		
		/*
		 * Servers Config
		 */
		ConfigCategory serverCategory = config.getCategory(ConfigurationHandler.CATEGORY_SERVERS);
		for(String categoryName : config.getCategoryNames()) {
			ConfigCategory category = config.getCategory(categoryName);
			if(category.parent == serverCategory) {
				String host = Utils.unquote(config.get(categoryName, "host", "").getString());
				ServerConfig serverConfig = new ServerConfig(host);
				serverConfig.load(config, category);
				ConfigurationHandler.addServerConfig(serverConfig);
			}
		}
		config.getCategory(ConfigurationHandler.CATEGORY_SERVERS).setComment("The following is a list of IRC server configs along with their channels.");
	}
	
	public static void save(Configuration config) {
		/*
		 * Global Config
		 */
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "nick", "").set(Utils.quote(GlobalConfig.nick));
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "allowPrivateMessages", GlobalConfig.allowPrivateMessages).set(GlobalConfig.allowPrivateMessages);
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "enableLinkFilter", GlobalConfig.enableLinkFilter).set(GlobalConfig.enableLinkFilter);
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "saveCredentials", GlobalConfig.saveCredentials).set(GlobalConfig.saveCredentials);
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "quitMessage", GlobalConfig.quitMessage).set(GlobalConfig.quitMessage);
		
		/*
		 * Display Config
		 */
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "ircColor", GlobalConfig.ircColor).set(GlobalConfig.ircColor);
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "emoteColor", GlobalConfig.emoteColor).set(GlobalConfig.emoteColor);
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "showDeathMessages", GlobalConfig.relayDeathMessages).set(GlobalConfig.relayDeathMessages);
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "showMinecraftJoinLeave", GlobalConfig.relayMinecraftJoinLeave).set(GlobalConfig.relayMinecraftJoinLeave);
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "showIRCJoinLeave", GlobalConfig.relayIRCJoinLeave).set(GlobalConfig.relayIRCJoinLeave);
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "showNickChanges", GlobalConfig.relayNickChanges).set(GlobalConfig.relayNickChanges);
		
		/*
		 * ClientOnly Config
		 */
		config.get(ConfigurationHandler.CATEGORY_CLIENTONLY, "persistentConnection", GlobalConfig.persistentConnection).set(GlobalConfig.persistentConnection);
		
		/*
		 * ServerOnly Config
		 */
		config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "enableNameColors", GlobalConfig.enableNameColors).set(GlobalConfig.enableNameColors);
		config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "enableAliases", GlobalConfig.enableAliases).set(GlobalConfig.enableAliases);
		config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "opColor", GlobalConfig.opColor).set(GlobalConfig.opColor);
		config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "colorBlackList", new String[0]).set(GlobalConfig.colorBlackList.toArray(new String[GlobalConfig.colorBlackList.size()]));
		config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "interOp", GlobalConfig.interOp).set(GlobalConfig.interOp);
		
		int c = 0;
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			String category = ConfigurationHandler.CATEGORY_SERVERS + Configuration.CATEGORY_SPLITTER + ConfigurationHandler.CATEGORY_SERVER_PREFIX + c;
			serverConfig.save(config, config.getCategory(category));
			c++;
		}
		
		config.save();
	}
}
