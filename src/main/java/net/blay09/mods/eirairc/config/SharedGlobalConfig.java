package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SharedGlobalConfig {

	public static final String GENERAL = "general";
	public static final String NETWORK = "network";
	public static final String THEME = "theme";
	public static final String BOT = "bot";
	public static final String SETTINGS = "settings";

	public static Configuration thisConfig;

	// General
	public static String defaultChat = "Minecraft";
	public static boolean autoResetChat = false;
	public static boolean enablePlayerAliases = false;
	public static boolean enablePlayerColors = true;
	public static final List<String> colorBlacklist = new ArrayList<String>();
	public static boolean hidePlayerTags = false;
	public static boolean debugMode = false;
	public static boolean preventUserPing = false;
	public static boolean twitchNameColors = true;

	// Network Settings
	public static String bindIP = "";
	public static int antiFloodTime = 500;
	public static boolean sslTrustAllCerts = false;
	public static String sslCustomTrustStore = "";
	public static boolean sslDisableDiffieHellman = true;
	public static String proxyHost = "";
	public static String proxyUsername = "";
	public static String proxyPassword = "";

	// Default Settings
	public static final ThemeSettings theme = new ThemeSettings(null);
	public static final BotSettings botSettings = new BotSettings(null);
	public static final GeneralSettings generalSettings = new GeneralSettings(null);

	public static void load(File configDir, boolean reloadFile) {
		if(thisConfig == null || reloadFile) {
			thisConfig = new Configuration(new File(configDir, "shared.cfg"));
		}

		// General
		defaultChat = thisConfig.getString("defaultChat", GENERAL, defaultChat, I19n.format("eirairc:config.property.defaultChat.tooltip"), "eirairc:config.property.defaultChat");
		enablePlayerAliases = thisConfig.getBoolean("enablePlayerAliases", GENERAL, enablePlayerAliases, I19n.format("eirairc:config.property.enablePlayerAliases.tooltip"), "eirairc:config.property.enablePlayerAliases");
		enablePlayerColors = thisConfig.getBoolean("enablePlayerColors", GENERAL, enablePlayerColors, I19n.format("eirairc:config.property.enablePlayerColors.tooltip"), "eirairc:config.property.enablePlayerColors");
		String[] colorBlacklistArray = thisConfig.getStringList("colorBlacklist", GENERAL, Globals.DEFAULT_COLOR_BLACKLIST, I19n.format("eirairc:config.property.colorBlacklist.tooltip"), null, "eirairc:config.property.colorBlacklist");
		colorBlacklist.clear();
		Collections.addAll(colorBlacklist, colorBlacklistArray);
		hidePlayerTags = thisConfig.getBoolean("hidePlayerTags", GENERAL, hidePlayerTags, I19n.format("eirairc:config.property.hidePlayerTags.tooltip"), "eirairc:config.property.hidePlayerTags");
		preventUserPing = thisConfig.getBoolean("preventUserPing", GENERAL, preventUserPing, I19n.format("eirairc:config.property.preventUserPing.tooltip"), "eirairc:config.property.preventUserPing");
		twitchNameColors = thisConfig.getBoolean("twitchNameColors", GENERAL, twitchNameColors, I19n.format("eirairc:config.property.twitchNameColors.tooltip"), "eirairc:config.property.twitchNameColors");
		debugMode = thisConfig.getBoolean("debugMode", GENERAL, debugMode, I19n.format("eirairc:config.property.debugMode.tooltip"), "eirairc:config.property.debugMode");

		// Network
		bindIP = thisConfig.getString("bindIP", NETWORK, bindIP, I19n.format("eirairc:config.property.bindIP.tooltip"), "eirairc:config.property.bindIP");
		antiFloodTime = thisConfig.getInt("antiFloodTime", NETWORK, antiFloodTime, 0, 10000, I19n.format("eirairc:config.property.antiFloodTime.tooltip"), "eirairc:config.property.antiFloodTime");
		sslTrustAllCerts = thisConfig.getBoolean("sslTrustAllCerts", NETWORK, sslTrustAllCerts, I19n.format("eirairc:config.property.sslTrustAllCerts.tooltip"), "eirairc:config.property.sslTrustAllCerts");
		sslCustomTrustStore = thisConfig.getString("sslCustomTrustStore", NETWORK, sslCustomTrustStore, I19n.format("eirairc:config.property.sslCustomTrustStore.tooltip"), "eirairc:config.property.sslCustomTrustStore");
		sslDisableDiffieHellman = thisConfig.getBoolean("sslDisableDiffieHellman", NETWORK, sslDisableDiffieHellman, I19n.format("eirairc:config.property.sslDisableDiffieHellman.tooltip"), "eirairc:config.property.sslDisableDiffieHellman");
		proxyHost = thisConfig.getString("proxyHost", NETWORK, proxyHost, I19n.format("eirairc:config.property.proxyHost.tooltip"), "eirairc:config.property.proxyHost");
		proxyUsername = thisConfig.getString("proxyUsername", NETWORK, proxyUsername, I19n.format("eirairc:config.property.proxyUsername.tooltip"), "eirairc:config.property.proxyUsername");
		proxyPassword = thisConfig.getString("proxyPassword", NETWORK, proxyPassword, I19n.format("eirairc:config.property.proxyPassword.tooltip"), "eirairc:config.property.proxyPassword");

		// Default Settings
		theme.load(thisConfig, THEME, true);
		botSettings.load(thisConfig, BOT, true);
		generalSettings.load(thisConfig, SETTINGS, true);

		save();
	}

	public static void save() {
		// Category Comments
		thisConfig.setCategoryComment(GENERAL, I19n.format("eirairc:config.category.general.tooltip"));
		thisConfig.setCategoryComment(NETWORK, I19n.format("eirairc:config.category.network.tooltip"));
		thisConfig.setCategoryComment(THEME, I19n.format("eirairc:config.category.theme.tooltip"));
		thisConfig.setCategoryComment(BOT, I19n.format("eirairc:config.category.bot.tooltip"));
		thisConfig.setCategoryComment(SETTINGS, I19n.format("eirairc:config.category.settings.tooltip"));

		// General
		thisConfig.get(GENERAL, "defaultChat", "", I19n.format("eirairc:config.property.defaultChat")).set(defaultChat);
		thisConfig.get(GENERAL, "enablePlayerAliases", false, I19n.format("eirairc:config.property.enablePlayerAliases.tooltip")).set(enablePlayerAliases);
		thisConfig.get(GENERAL, "enablePlayerColors", false, I19n.format("eirairc:config.property.enablePlayerColors.tooltip")).set(enablePlayerColors);
		thisConfig.get(GENERAL, "colorBlacklist", new String[0], I19n.format("eirairc:config.property.colorBlacklist.tooltip")).set(colorBlacklist.toArray(new String[colorBlacklist.size()]));
		thisConfig.get(GENERAL, "hidePlayerTags", false, I19n.format("eirairc:config.property.hidePlayerTags.tooltip")).set(hidePlayerTags);
		thisConfig.get(GENERAL, "preventUserPing", false, I19n.format("eirairc:config.property.preventUserPing.tooltip")).set(preventUserPing);
		thisConfig.get(GENERAL, "twitchNameColors", false, I19n.format("eirairc:config.property.twitchNameColors.tooltip")).set(twitchNameColors);
		thisConfig.get(GENERAL, "debugMode", false, I19n.format("eirairc:config.property.debugMode.tooltip")).set(debugMode);

		// Network
		thisConfig.get(NETWORK, "bindIP", "", I19n.format("eirairc:config.property.bindIP.tooltip")).set(bindIP);
		thisConfig.get(NETWORK, "antiFloodTime", "", I19n.format("eirairc:config.property.antiFloodTime.tooltip")).set(antiFloodTime);
		thisConfig.get(NETWORK, "sslTrustAllCerts", false, I19n.format("eirairc:config.property.sslTrustAllCerts.tooltip")).set(sslTrustAllCerts);
		thisConfig.get(NETWORK, "sslCustomTrustStore", I19n.format("eirairc:config.property.sslCustomTrustStore.tooltip")).set(sslCustomTrustStore);
		thisConfig.get(NETWORK, "sslDisableDiffieHellman", false, I19n.format("eirairc:config.property.sslDisableDiffieHellman.tooltip")).set(sslDisableDiffieHellman);
		thisConfig.get(NETWORK, "proxyHost", "", I19n.format("eirairc:config.property.proxyHost.tooltip")).set(proxyHost);
		thisConfig.get(NETWORK, "proxyUsername", "", I19n.format("eirairc:config.property.proxyUsername.tooltip")).set(proxyUsername);
		thisConfig.get(NETWORK, "proxyPassword", "", I19n.format("eirairc:config.property.proxyPassword.tooltip")).set(proxyPassword);

		// Default Settings
		theme.save(thisConfig, THEME);
		botSettings.save(thisConfig, BOT);
		generalSettings.save(thisConfig, SETTINGS);

		thisConfig.save();
	}

	public static void loadLegacy(File configDir, Configuration legacyConfig) {
		thisConfig = new Configuration(new File(configDir, "shared.cfg"));

		// General
		enablePlayerAliases = legacyConfig.getBoolean("enableAliases", "serveronly", enablePlayerAliases, "");
		enablePlayerColors = legacyConfig.getBoolean("enableNameColors", "display", enablePlayerColors, "");
		String[] colorBlacklistArray = legacyConfig.getStringList("colorBlackList", "serveronly", new String[0], "");
		colorBlacklist.clear();
		for(String entry : colorBlacklistArray) {
			colorBlacklist.add(Utils.unquote(entry));
		}
		debugMode = legacyConfig.getBoolean("debugMode", "global", debugMode, "");
		hidePlayerTags = legacyConfig.getBoolean("hidePlayerTags", "display", hidePlayerTags, "");

		// Network
		sslTrustAllCerts = legacyConfig.getBoolean("sslTrustAllCerts", "network", sslTrustAllCerts, "");
		sslCustomTrustStore = Utils.unquote(legacyConfig.getString("sslCustomTrustStore", "network", sslCustomTrustStore, ""));
		sslDisableDiffieHellman = legacyConfig.getBoolean("sslDisableDiffieHellman", "network", sslDisableDiffieHellman, "");
		proxyHost = Utils.unquote(legacyConfig.getString("proxyHost", "network", proxyHost, ""));
		proxyUsername = Utils.unquote(legacyConfig.getString("proxyUsername", "network", proxyUsername, ""));
		proxyPassword = Utils.unquote(legacyConfig.getString("proxyPassword", "network", proxyPassword, ""));

		// Theme
		theme.load(thisConfig, THEME, true);
		theme.loadLegacy(legacyConfig, null);
		botSettings.load(thisConfig, BOT, true);
		botSettings.loadLegacy(legacyConfig, null);
		generalSettings.load(thisConfig, SETTINGS, true);
		generalSettings.loadLegacy(legacyConfig, null);

		save();
	}

	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		boolean result = true;
		if(key.equals("defaultChat")) {
			defaultChat = value;
		} else if(key.equals("enablePlayerColors")) {
			enablePlayerColors = Boolean.parseBoolean(value);
		} else if(key.equals("preventUserPing")) {
			preventUserPing = Boolean.parseBoolean(value);
		} else if(key.equals("enablePlayerAliases")) {
			enablePlayerAliases = Boolean.parseBoolean(value);
		} else if(key.equals("hidePlayerTags")) {
			hidePlayerTags = Boolean.parseBoolean(value);
		} else if(key.equals("twitchNameColors")) {
			twitchNameColors = Boolean.parseBoolean(value);
		} else if(key.equals("debugMode")) {
			debugMode = Boolean.parseBoolean(value);
		} else if(key.equals("antiFloodTime")) {
			antiFloodTime = Integer.parseInt(value);
		} else if(key.equals("bindIP")) {
			bindIP = value;
		} else if(key.equals("sslTrustAllCerts")) {
			sslTrustAllCerts = Boolean.parseBoolean(value);
		} else if(key.equals("sslDisableDiffieHellman")) {
			sslDisableDiffieHellman = Boolean.parseBoolean(value);
		} else if (key.equals("sslCustomTrustStore")) {
			sslCustomTrustStore = value;
		} else if(!theme.handleConfigCommand(sender, key, value) && !botSettings.handleConfigCommand(sender, key, value) && !generalSettings.handleConfigCommand(sender, key, value)) {
			result = false;
		}
		return result;
	}

	public static String handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("defaultChat")) {
			value = defaultChat;
		} else if(key.equals("enablePlayerColors")) {
			value = String.valueOf(enablePlayerColors);
		} else if(key.equals("enablePlayerAliases")) {
			value = String.valueOf(enablePlayerAliases);
		} else if(key.equals("preventUserPing")) {
			value = String.valueOf(preventUserPing);
		} else if(key.equals("twitchNameColors")) {
			value = String.valueOf(twitchNameColors);
		} else if(key.equals("hidePlayerTags")) {
			value = String.valueOf(hidePlayerTags);
		} else if(key.equals("twitchNameColors")) {
			value = String.valueOf(twitchNameColors);
		} else if(key.equals("debugMode")) {
			value = String.valueOf(debugMode);
		} else if(key.equals("antiFloodTime")) {
			value = String.valueOf(antiFloodTime);
		} else if(key.equals("bindIP")) {
			value = bindIP;
		} else if(key.equals("sslTrustAllCerts")) {
			value = String.valueOf(sslTrustAllCerts);
		} else if(key.equals("sslDisableDiffieHellman")) {
			value = String.valueOf(sslDisableDiffieHellman);
		} else if(key.equals("sslCustomTrustStore")) {
			value = sslCustomTrustStore;
		}
		if(value == null) {
			value = theme.handleConfigCommand(sender, key);
		}
		if(value == null) {
			value = botSettings.handleConfigCommand(sender, key);
		}
		if(value == null) {
			value = generalSettings.handleConfigCommand(sender, key);
		}
		return value;
	}

	public static void addOptionsToList(List<String> list, String option) {
		if(option == null) {
			list.add("defaultChat");
			list.add("enablePlayerColors");
			list.add("preventUserPing");
			list.add("hidePlayerTags");
			list.add("twitchNameColors");
			list.add("debugMode");
			list.add("bindIP");
			list.add("antiFloodTime");
			list.add("sslCustomTrustStore");
			list.add("sslTrustAllCerts");
			list.add("sslDisableDiffieHellman");
		} else if(option.equals("enablePlayerColors") || option.equals("registerShortCommands") || option.equals("hidePlayerTags") || option.equals("sslTrustAllCerts") || option.equals("sslDisableDiffieHellman") || option.equals("preventUserPing") || option.equals("twitchNameColors")) {
			Utils.addBooleansToList(list);
		}
		ThemeSettings.addOptionsToList(list, option);
		GeneralSettings.addOptionsToList(list, option);
		BotSettings.addOptionsToList(list, option);
	}

}
