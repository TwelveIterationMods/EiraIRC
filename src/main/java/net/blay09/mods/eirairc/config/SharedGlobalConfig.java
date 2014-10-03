package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blay09 on 29.09.2014.
 */
public class SharedGlobalConfig {

	private static final String GENERAL = "general";
	private static final String NETWORK = "network";
	private static final String THEME = "theme";
	private static final String BOT = "bot";
	private static final String SETTINGS = "settings";

	private static Configuration thisConfig;

	// General
	public static boolean enablePlayerAliases = false;
	public static boolean enablePlayerColors = true;
	public static final List<String> colorBlacklist = new ArrayList<String>();
	public static boolean registerShortCommands = true;
	public static boolean hidePlayerTags = false;
	public static boolean debugMode = false;

	// Network Settings
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
		thisConfig = new Configuration(new File(configDir, "shared.cfg"));

		// General
		enablePlayerAliases = thisConfig.getBoolean("enablePlayerAliases", GENERAL, enablePlayerAliases, "[Deprecated] If set to true, OPs can assign an alias for a MC nick that will be used instead.");
		enablePlayerColors = thisConfig.getBoolean("enablePlayerColors", GENERAL, enablePlayerColors, "If set to true, players can use the '/irc color' command to set a color for their MC nick. See also: colorBlackList");
		String[] colorBlacklistArray = thisConfig.getStringList("colorBlackList", GENERAL, Globals.DEFAULT_COLOR_BLACKLIST, "A list of colors that players are not allowed to use as name colors when using the '/irc color' command.");
		for(String entry : colorBlacklistArray) {
			colorBlacklist.add(entry);
		}
		registerShortCommands = thisConfig.getBoolean("registerShortCommands", GENERAL, registerShortCommands, "If set to true, EiraIRC will link commands such as /join, /msg or /nick to it's /irc <command> variants for quicker usage.");
		hidePlayerTags = thisConfig.getBoolean("hidePlayerTags", GENERAL, hidePlayerTags, "If set to true, EiraIRC will attempt to strip player name tags such as [Admin] (that were added by other mods) when sending to IRC.");
		debugMode = thisConfig.getBoolean("debugMode", GENERAL, debugMode, "[Advanced] If set to true, raw IRC messages will be printed into the log for investigation purposes.");

		// Network
		sslTrustAllCerts = thisConfig.getBoolean("sslTrustAllCerts", NETWORK, sslTrustAllCerts, "[Advanced] If set to true, EiraIRC will accept all SSL certificates without checking the truststore.");
		sslCustomTrustStore = thisConfig.getString("sslCustomTrustStore", NETWORK, sslCustomTrustStore, "[Advanced] The path to a custom SSL truststore.");
		sslDisableDiffieHellman = thisConfig.getBoolean("sslDisableDiffieHellman", NETWORK, sslDisableDiffieHellman, "[Advanced] If set to true, disables DiffieHellman encryption for SSL connections to fix a Java issue.");
		proxyHost = thisConfig.getString("proxyHost", NETWORK, proxyHost, "[Advanced] The address to a proxy you want connections to go through.");
		proxyUsername = thisConfig.getString("proxyUsername", NETWORK, proxyUsername, "[Advanced] The username to authenticate with the proxy, if necessary.");
		proxyPassword = thisConfig.getString("proxyPassword", NETWORK, proxyPassword, "[Advanced] The password to authenticate with the proxy, if necessary.");

		// Default Settings
		theme.load(thisConfig, THEME, true);
		botSettings.load(thisConfig, BOT, true);
		generalSettings.load(thisConfig, SETTINGS, true);
	}

	public static void save() {
		// General
		thisConfig.get(GENERAL, "enablePlayerAliases", false).set(enablePlayerAliases);
		thisConfig.get(GENERAL, "enablePlayerColors", false).set(enablePlayerColors);
		thisConfig.get(GENERAL, "colorBlackList", new String[0]).set(colorBlacklist.toArray(new String[colorBlacklist.size()]));
		thisConfig.get(GENERAL, "registerShortCommands", false).set(registerShortCommands);
		thisConfig.get(GENERAL, "hidePlayerTags", false).set(hidePlayerTags);
		thisConfig.get(GENERAL, "debugMode", false).set(debugMode);

		// Network
		thisConfig.get(NETWORK, "sslTrustAllCerts", false).set(sslTrustAllCerts);
		thisConfig.get(NETWORK, "sslCustomTrustStore", "").set(sslCustomTrustStore);
		thisConfig.get(NETWORK, "sslDisableDiffieHellman", false).set(sslDisableDiffieHellman);
		thisConfig.get(NETWORK, "proxyHost", "").set(proxyHost);
		thisConfig.get(NETWORK, "proxyUsername", "").set(proxyUsername);
		thisConfig.get(NETWORK, "proxyPassword", "").set(proxyPassword);

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
		theme.loadLegacy(legacyConfig);
		botSettings.loadLegacy(legacyConfig, null);
		generalSettings.loadLegacy(legacyConfig, null);
	}
}
