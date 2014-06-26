// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class GlobalConfig {

	public static String nick = "%USERNAME%";
	public static String nickPrefix = "";
	public static String nickSuffix = "";
	public static boolean enableAliases = false;
	public static boolean persistentConnection = true;
	public static boolean saveCredentials = true;
	public static String charset = "UTF-8";
	public static final List<String> colorBlackList = new ArrayList<String>();
	public static boolean registerShortCommands = true;
	public static boolean hideNotices = false;
	public static boolean debugMode = false;

	public static void load(Configuration config) {
		nick = Utils.unquote(config.get(ConfigurationHandler.CATEGORY_GLOBAL, "nick", nick).getString());
		saveCredentials = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "saveCredentials", saveCredentials).getBoolean(saveCredentials);
		registerShortCommands = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "registerShortCommands", registerShortCommands).getBoolean(registerShortCommands);
		charset = Utils.unquote(config.get(ConfigurationHandler.CATEGORY_GLOBAL, "charset", charset).getString());
		hideNotices = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "hideNotices", hideNotices).getBoolean(hideNotices);
		debugMode = config.get(ConfigurationHandler.CATEGORY_GLOBAL, "debugMode", debugMode).getBoolean(debugMode);
		config.getCategory(ConfigurationHandler.CATEGORY_GLOBAL).setComment("These are settings that are applied on all servers and channels.");
		
		persistentConnection = config.get(ConfigurationHandler.CATEGORY_CLIENTONLY, "persistentConnection", GlobalConfig.persistentConnection).getBoolean(GlobalConfig.persistentConnection);
		config.getCategory(ConfigurationHandler.CATEGORY_CLIENTONLY).setComment("These options are only important in the client version as they either have no function on servers or aren't really intended to be server-side.");
		
		enableAliases = config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "enableAliases", GlobalConfig.enableAliases).getBoolean(GlobalConfig.enableAliases);
		String[] colorBlackListArray = config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "colorBlackList", Globals.DEFAULT_COLOR_BLACKLIST).getStringList();
		for(int i = 0; i < colorBlackListArray.length; i++) {
			colorBlackList.add(colorBlackListArray[i]);
		}
		nickPrefix = Utils.unquote(config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "nickPrefix", nickPrefix).getString());
		nickSuffix = Utils.unquote(config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "nickSuffix", nickSuffix).getString());
		config.getCategory(ConfigurationHandler.CATEGORY_SERVERONLY).setComment("These options are only important in the server version as they either have no function on clients or aren't really intended to be client-side.");
		
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
	
	public static String handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("persistentConnection")) value = String.valueOf(persistentConnection);
		else if(key.equals("saveCredentials")) value = String.valueOf(saveCredentials);
		else if(key.equals("registerShortCommands")) value = String.valueOf(registerShortCommands);
		else if(key.equals("enableAliases")) value = String.valueOf(enableAliases);
		else if(key.equals("charset")) value = charset;
		else if(key.equals("nickPrefix")) value = nickPrefix;
		else if(key.equals("nickSuffix")) value = nickSuffix;
		else if(key.equals("hideNotices")) value = String.valueOf(hideNotices);
		else if(key.equals("debugMode")) value = String.valueOf(debugMode);
		return value;
	}
	
	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("persistentConnection")) {
			persistentConnection = Boolean.parseBoolean(value);
		} else if(key.equals("saveCredentials")) {
			saveCredentials = Boolean.parseBoolean(value);
		} else if(key.equals("hideNotices")) {
			hideNotices = Boolean.parseBoolean(value);
		} else if(key.equals("debugMode")) {
			debugMode = Boolean.parseBoolean(value);
		} else if(key.equals("registerShortCommands")) {
			registerShortCommands = Boolean.parseBoolean(value);
			Utils.sendLocalizedMessage(sender, "irc.config.requiresRestart");
		} else if(key.equals("interOp") || key.equals("enableAliases")) {
			Utils.sendLocalizedMessage(sender, "irc.config.noAbuse");
			return false;
		} else if(key.equals("charset")) {
			charset = value;
			Utils.sendLocalizedMessage(sender, "irc.config.requiresRestart");
		} else if(key.equals("nickPrefix")) {
			if(value.equals("none")) {
				value = "";
			}
			nickPrefix = value;
		} else if(key.equals("nickSuffix")) {
			if(value.equals("none")) {
				value = "";
			}
			nickSuffix = value;
		} else {
			return false;
		}
		return true;
	}
	
	public static void addOptionsToList(List<String> list) {
		list.add("persistentConnection");
		list.add("saveCredentials");
		list.add("enableLinkFilter");
		list.add("registerShortCommands");
		list.add("charset");
		list.add("nickPrefix");
		list.add("nickSuffix");
		list.add("hideNotices");
		list.add("debugMode");
	}
	
	public static void save(Configuration config) {
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "nick", "").set(Utils.quote(nick));
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "saveCredentials", saveCredentials).set(saveCredentials);
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "charset", "").set(Utils.quote(charset));
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "hideNotices", hideNotices).set(hideNotices);
		config.get(ConfigurationHandler.CATEGORY_GLOBAL, "debugMode", debugMode).set(debugMode);
		config.get(ConfigurationHandler.CATEGORY_CLIENTONLY, "persistentConnection", persistentConnection).set(persistentConnection);

		config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "colorBlackList", new String[0]).set(colorBlackList.toArray(new String[colorBlackList.size()]));
		config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "nickPrefix", "").set(Utils.quote(nickPrefix));
		config.get(ConfigurationHandler.CATEGORY_SERVERONLY, "nickSuffix", "").set(Utils.quote(nickSuffix));
		
		config.removeCategory(config.getCategory(ConfigurationHandler.CATEGORY_SERVERS));
		int c = 0;
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			String category = ConfigurationHandler.CATEGORY_SERVERS + Configuration.CATEGORY_SPLITTER + ConfigurationHandler.PREFIX_SERVER + c;
			serverConfig.save(config, config.getCategory(category));
			c++;
		}
		
		config.save();
	}

	public static void addValuesToList(List<String> list, String option) {
		if(option.startsWith("enable") || option.startsWith("allow") || option.equals("saveCredentials") || option.equals("persistentConnection") || option.equals("hideNotices")  || option.equals("debugMode") || option.equals("sslTrustAllCerts")) {
			Utils.addBooleansToList(list);
		} else if(option.equals("charset")) {
			for(String cs : Charset.availableCharsets().keySet()) {
				list.add(cs);
			}
		} else if(option.equals("nickPrefix") || option.equals("nickSuffix") || option.equals("sslCustomTrustStore")) {
			list.add("none");
		}
	}
}
