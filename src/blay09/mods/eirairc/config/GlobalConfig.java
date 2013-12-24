// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import blay09.mods.eirairc.Utils;

public class GlobalConfig {

	public static String nick = Globals.DEFAULT_NICK;
	public static final List<String> colorBlackList = new ArrayList<String>();
	public static final Map<String, DisplayFormatConfig> displayFormates = new HashMap<String, DisplayFormatConfig>();
	public static String opColor = "red";
	public static String ircColor = "gray";
	public static String emoteColor = "gold";
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
	public static List<String> interOpAuthList = new ArrayList<String>();
	public static String charset = "UTF-8";
	
	public static void handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("opColor")) value = opColor;
		else if(key.equals("ircColor")) value = ircColor;
		else if(key.equals("emoteColor")) value = emoteColor;
		else if(key.equals("quitMessage")) value = quitMessage;
		else if(key.equals("enableNameColors")) value = String.valueOf(enableNameColors);
		else if(key.equals("relayMinecraftJoinLeave")) value = String.valueOf(relayMinecraftJoinLeave);
		else if(key.equals("relayIRCJoinLeave")) value = String.valueOf(relayIRCJoinLeave);
		else if(key.equals("relayDeathMessages")) value = String.valueOf(relayDeathMessages);
		else if(key.equals("relayNickChanges")) value = String.valueOf(relayNickChanges);
		else if(key.equals("persistentConnection")) value = String.valueOf(persistentConnection);
		else if(key.equals("saveCredentials")) value = String.valueOf(saveCredentials);
		else if(key.equals("enableLinkFilter")) value = String.valueOf(enableLinkFilter);
		else if(key.equals("registerShortCommands")) value = String.valueOf(registerShortCommands);
		else if(key.equals("interOp")) value = String.valueOf(interOp);
		else if(key.equals("enableAliases")) value = String.valueOf(enableAliases);
		else if(key.equals("displayMode")) value = displayMode;
		else if(key.equals("charset")) value = charset;
		if(value != null) {
			Utils.sendLocalizedMessage(sender, "irc.config.lookup", "Global", key, value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", "Global", key);
		}
	}
	
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
		} else if(key.equals("displayMode")) {
			if(GlobalConfig.displayFormates.containsKey(value)) {
				Utils.sendLocalizedMessage(sender, "irc.config.invalidDisplayMode", value);
				return;
			}
			displayMode = value;
		} else if(key.equals("charset")) {
			charset = value;
			Utils.sendLocalizedMessage(sender, "irc.config.requiresRestart");
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
	
	public static void addOptionsToList(List<String> list) {
		list.add("opColor");
		list.add("ircColor");
		list.add("emoteColor");
		list.add("quitMessage");
		list.add("enableNameColors");
		list.add("relayDeathMessages");
		list.add("relayMinecraftJoinLeave");
		list.add("relayIRCJoinLeave");
		list.add("relayNickChanges");
		list.add("allowPrivateMessages");
		list.add("persistentConnection");
		list.add("saveCredentials");
		list.add("enableLinkFilter");
		list.add("registerShortCommands");
		list.add("displayMode");
		list.add("charset");
	}
	
	public static void load(Configuration config) {
		/*
		 * Global Config
		 */
		GlobalConfig.nick = Utils.unquote(config.get(ConfigurationHandler.CATEGORY_GLOBAL, "nick", Globals.DEFAULT_NICK).getString());
		GlobalConfig.allowPrivateMessages = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "allowPrivateMessages", GlobalConfig.allowPrivateMessages).getBoolean(GlobalConfig.allowPrivateMessages);
		GlobalConfig.enableLinkFilter = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "enableLinkFilter", GlobalConfig.enableLinkFilter).getBoolean(GlobalConfig.enableLinkFilter);
		GlobalConfig.saveCredentials = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "saveCredentials", GlobalConfig.saveCredentials).getBoolean(GlobalConfig.saveCredentials);
		GlobalConfig.registerShortCommands = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "registerShortCommands", GlobalConfig.registerShortCommands).getBoolean(GlobalConfig.registerShortCommands);
		GlobalConfig.charset = Utils.unquote(config.get(ConfigurationHandler.CATEGORY_GLOBAL, "charset", GlobalConfig.charset).getString());
		config.getCategory(ConfigurationHandler.CATEGORY_GLOBAL).setComment("These are settings that are applied on all servers and channels.");
		
		/*
		 * Display Config
		 */
		GlobalConfig.displayMode = Utils.unquote(config.get(ConfigurationHandler.CATEGORY_DISPLAY, "displayMode", GlobalConfig.displayMode).getString());
		GlobalConfig.ircColor = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "ircColor", GlobalConfig.ircColor).getString();
		GlobalConfig.emoteColor = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "emoteColor", GlobalConfig.emoteColor).getString();
		GlobalConfig.relayDeathMessages = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "relayDeathMessages", GlobalConfig.relayDeathMessages).getBoolean(GlobalConfig.relayDeathMessages);
		GlobalConfig.relayMinecraftJoinLeave = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "relayMinecraftJoinLeave", GlobalConfig.relayMinecraftJoinLeave).getBoolean(GlobalConfig.relayMinecraftJoinLeave);
		GlobalConfig.relayIRCJoinLeave = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "relayIRCJoinLeave", GlobalConfig.relayIRCJoinLeave).getBoolean(GlobalConfig.relayIRCJoinLeave);
		GlobalConfig.relayNickChanges = config.get(ConfigurationHandler.CATEGORY_DISPLAY, "relayNickChanges", GlobalConfig.relayNickChanges).getBoolean(GlobalConfig.relayNickChanges);
		ConfigCategory displayFormatCategory = config.getCategory(ConfigurationHandler.CATEGORY_DISPLAY + Configuration.CATEGORY_SPLITTER + ConfigurationHandler.CATEGORY_FORMATS);
		DisplayFormatConfig.defaultConfig(config, displayFormatCategory);
		for(ConfigCategory category : displayFormatCategory.getChildren()) {
			DisplayFormatConfig dfc = new DisplayFormatConfig(category);
			dfc.load(config);
			GlobalConfig.displayFormates.put(dfc.getName(), dfc);
		}
		config.getCategory(ConfigurationHandler.CATEGORY_DISPLAY).setComment("These options determine how the chat is displayed and what gets sent / received to and from IRC.");
		
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
		String[] interOpAuthList = config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "interOpAuthList", new String[0]).getStringList();
		for(int i = 0; i < interOpAuthList.length; i++) {
			GlobalConfig.interOpAuthList.add(interOpAuthList[i]);
		}
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
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "quitMessage", "").set(Utils.quote(GlobalConfig.quitMessage));
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "charset", "").set(Utils.quote(GlobalConfig.charset));
		
		/*
		 * Display Config
		 */
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "displayMode", "").set(Utils.quote(GlobalConfig.displayMode));
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "ircColor", "").set(GlobalConfig.ircColor);
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "emoteColor", "").set(GlobalConfig.emoteColor);
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "relayDeathMessages", GlobalConfig.relayDeathMessages).set(GlobalConfig.relayDeathMessages);
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "relayMinecraftJoinLeave", GlobalConfig.relayMinecraftJoinLeave).set(GlobalConfig.relayMinecraftJoinLeave);
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "relayIRCJoinLeave", GlobalConfig.relayIRCJoinLeave).set(GlobalConfig.relayIRCJoinLeave);
		config.get(ConfigurationHandler.CATEGORY_DISPLAY, "relayNickChanges", GlobalConfig.relayNickChanges).set(GlobalConfig.relayNickChanges);
		
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
		
		config.removeCategory(config.getCategory(ConfigurationHandler.CATEGORY_SERVERS));
		int c = 0;
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			String category = ConfigurationHandler.CATEGORY_SERVERS + Configuration.CATEGORY_SPLITTER + ConfigurationHandler.CATEGORY_SERVER_PREFIX + c;
			serverConfig.save(config, config.getCategory(category));
			c++;
		}
		
		config.save();
	}

	public static void addValuesToList(List<String> list, String option) {
		if(option.equals("ircColor") || option.equals("emoteColor") || option.equals("opColor")) {
			Utils.addValidColorsToList(list);
		} else if(option.startsWith("enable") || option.startsWith("show") || option.startsWith("allow") || option.equals("saveCredentials") || option.equals("persistentConnection")) {
			Utils.addBooleansToList(list);
		} else if(option.equals("charset")) {
			for(String cs : Charset.availableCharsets().keySet()) {
				list.add(cs);
			}
		} else if(option.equals("displayMode")) {
			for(String dm : displayFormates.keySet()) {
				list.add(dm);
			}
		}
	}
}
