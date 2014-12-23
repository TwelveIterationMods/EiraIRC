package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blay09 on 29.09.2014.
 */
public class SharedGlobalConfig {

	public static final String GENERAL = "general";
	public static final String NETWORK = "network";
	public static final String THEME = "theme";
	public static final String BOT = "bot";
	public static final String SETTINGS = "settings";

	public static Configuration thisConfig;

	// General
	public static boolean enablePlayerAliases = false;
	public static boolean enablePlayerColors = true;
	public static final List<String> colorBlacklist = new ArrayList<String>();
	public static boolean registerShortCommands = true;
	public static boolean hidePlayerTags = false;
	public static boolean debugMode = false;

	// Network Settings
	public static String bindIP = "";
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

	public static void load(File configDir) {
		if(thisConfig == null) {
			thisConfig = new Configuration(new File(configDir, "shared.cfg"));
		}

		// General
		enablePlayerAliases = thisConfig.getBoolean("enablePlayerAliases", GENERAL, enablePlayerAliases, "", "eirairc:config.property.enablePlayerAliases");
		enablePlayerColors = thisConfig.getBoolean("enablePlayerColors", GENERAL, enablePlayerColors, "", "eirairc:config.property.enablePlayerColors");
		String[] colorBlacklistArray = thisConfig.getStringList("colorBlacklist", GENERAL, Globals.DEFAULT_COLOR_BLACKLIST, "", null, "eirairc:config.property.colorBlacklist");
		for(String entry : colorBlacklistArray) {
			colorBlacklist.add(entry);
		}
		registerShortCommands = thisConfig.getBoolean("registerShortCommands", GENERAL, registerShortCommands, "", "eirairc:config.property.registerShortCommands");
		hidePlayerTags = thisConfig.getBoolean("hidePlayerTags", GENERAL, hidePlayerTags, "", "eirairc:config.property.hidePlayerTags");
		debugMode = thisConfig.getBoolean("debugMode", GENERAL, debugMode, "", "eirairc:config.property.debugMode");

		// Network
		bindIP = thisConfig.getString("bindIP", NETWORK, bindIP, "", "eirairc:config.property.bindIP");
		sslTrustAllCerts = thisConfig.getBoolean("sslTrustAllCerts", NETWORK, sslTrustAllCerts, "", "eirairc:config.property.sslTrustAllCerts");
		sslCustomTrustStore = thisConfig.getString("sslCustomTrustStore", NETWORK, sslCustomTrustStore, "", "eirairc:config.property.sslCustomTrustStore");
		sslDisableDiffieHellman = thisConfig.getBoolean("sslDisableDiffieHellman", NETWORK, sslDisableDiffieHellman, "", "eirairc:config.property.sslDisableDiffieHellman");
		proxyHost = thisConfig.getString("proxyHost", NETWORK, proxyHost, "", "eirairc:config.property.proxyHost");
		proxyUsername = thisConfig.getString("proxyUsername", NETWORK, proxyUsername, "", "eirairc:config.property.proxyUsername");
		proxyPassword = thisConfig.getString("proxyPassword", NETWORK, proxyPassword, "", "eirairc:config.property.proxyPassword");

		// Default Settings
		theme.load(thisConfig, THEME, true);
		botSettings.load(thisConfig, BOT, true);
		generalSettings.load(thisConfig, SETTINGS, true);
	}

	public static void save() {
		// Category Comments
		thisConfig.setCategoryComment(GENERAL, I18n.format("eirairc:config.category.general.tooltip"));
		thisConfig.setCategoryComment(NETWORK, I18n.format("eirairc:config.category.network.tooltip"));
		thisConfig.setCategoryComment(THEME, I18n.format("eirairc:config.category.theme.tooltip"));
		thisConfig.setCategoryComment(BOT, I18n.format("eirairc:config.category.bot.tooltip"));
		thisConfig.setCategoryComment(SETTINGS, I18n.format("eirairc:config.category.settings.tooltip"));

		// General
		thisConfig.get(GENERAL, "enablePlayerAliases", false, I18n.format("eirairc:config.property.enablePlayerAliases.tooltip")).set(enablePlayerAliases);
		thisConfig.get(GENERAL, "enablePlayerColors", false, I18n.format("eirairc:config.property.enablePlayerColors.tooltip")).set(enablePlayerColors);
		thisConfig.get(GENERAL, "colorBlacklist", new String[0], I18n.format("eirairc:config.property.colorBlacklist.tooltip")).set(colorBlacklist.toArray(new String[colorBlacklist.size()]));
		thisConfig.get(GENERAL, "registerShortCommands", false, I18n.format("eirairc:config.property.registerShortCommands.tooltip")).set(registerShortCommands);
		thisConfig.get(GENERAL, "hidePlayerTags", false, I18n.format("eirairc:config.property.hidePlayerTags.tooltip")).set(hidePlayerTags);
		thisConfig.get(GENERAL, "debugMode", false, I18n.format("eirairc:config.property.debugMode.tooltip")).set(debugMode);

		// Network
		thisConfig.get(NETWORK, "bindIP", "", I18n.format("eirairc:config.property.bindIP.toooltip")).set(bindIP);
		thisConfig.get(NETWORK, "sslTrustAllCerts", false, I18n.format("eirairc:config.property.sslTrustAllCerts.tooltip")).set(sslTrustAllCerts);
		thisConfig.get(NETWORK, "sslCustomTrustStore", I18n.format("eirairc:config.property.sslCustomTrustStore.tooltip")).set(sslCustomTrustStore);
		thisConfig.get(NETWORK, "sslDisableDiffieHellman", false, I18n.format("eirairc:config.property.sslDisableDiffieHellman.tooltip")).set(sslDisableDiffieHellman);
		thisConfig.get(NETWORK, "proxyHost", "", I18n.format("eirairc:config.property.proxyHost.tooltip")).set(proxyHost);
		thisConfig.get(NETWORK, "proxyUsername", "", I18n.format("eirairc:config.property.proxyUsername.tooltip")).set(proxyUsername);
		thisConfig.get(NETWORK, "proxyPassword", "", I18n.format("eirairc:config.property.proxyPassword.tooltip")).set(proxyPassword);

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
		for(String entry : colorBlacklistArray) {
			colorBlacklist.add(Utils.unquote(entry));
		}
		registerShortCommands = legacyConfig.getBoolean("registerShortCommands", "global", registerShortCommands, "");
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
	}
}
