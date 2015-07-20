// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.config.property.ConfigProperty;
import net.blay09.mods.eirairc.config.property.ConfigManager;
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
import java.util.List;

public class SharedGlobalConfig {

    public static final String GENERAL = "general";
    public static final String NETWORK = "network";
    public static final String THEME = "theme";
    public static final String BOT = "bot";
    public static final String SETTINGS = "settings";

    public static Configuration thisConfig;
    private static final ConfigManager manager = new ConfigManager();

    // General
    public static final ConfigProperty<String> defaultChat = new ConfigProperty<>(manager, GENERAL, "defaultChat", "Minecraft");
    public static final ConfigProperty<Boolean> enablePlayerColors = new ConfigProperty<>(manager, GENERAL, "enablePlayerColors", true);
    public static final ConfigProperty<Boolean> hidePlayerTags = new ConfigProperty<>(manager, GENERAL, "hidePlayerTags", false);
    public static final ConfigProperty<Boolean> debugMode = new ConfigProperty<>(manager, GENERAL, "debugMode", false);
    public static final ConfigProperty<Boolean> preventUserPing = new ConfigProperty<>(manager, GENERAL, "preventUserPing", false);
    public static final ConfigProperty<Boolean> twitchNameColors = new ConfigProperty<>(manager, GENERAL, "twitchNameColors", true);
    public static final ConfigProperty<Boolean> twitchNameBadges = new ConfigProperty<>(manager, GENERAL, "twitchNameBadges", true);
    public static final ConfigProperty<String> ircCommandPrefix = new ConfigProperty<>(manager, GENERAL, "ircCommandPrefix", "!");

    public static final List<String> colorBlacklist = new ArrayList<String>();

    // Network Settings
    public static final ConfigProperty<String> bindIP = new ConfigProperty<>(manager, NETWORK, "bindIP", "");
    public static final ConfigProperty<Integer> antiFloodTime = new ConfigProperty<>(manager, NETWORK, "antiFloodTime", 500);
    public static final ConfigProperty<Boolean> sslTrustAllCerts = new ConfigProperty<>(manager, NETWORK, "sslTrustAllCerts", false);
    public static final ConfigProperty<String> sslCustomTrustStore = new ConfigProperty<>(manager, NETWORK, "sslCustomTrustStore", "");
    public static final ConfigProperty<Boolean> sslDisableDiffieHellman = new ConfigProperty<>(manager, NETWORK, "sslDisableDiffieHellman", true);
    public static final ConfigProperty<String> proxyHost = new ConfigProperty<>(manager, NETWORK, "proxyHost", "");
    public static final ConfigProperty<String> proxyUsername = new ConfigProperty<>(manager, NETWORK, "proxyUsername", "");
    public static final ConfigProperty<String> proxyPassword = new ConfigProperty<>(manager, NETWORK, "proxyPassword", "");

    // Default Settings
    public static final ThemeSettings theme = new ThemeSettings(null);
    public static final BotSettings botSettings = new BotSettings(null);
    public static final GeneralSettings generalSettings = new GeneralSettings(null);

    public static void load(File configDir, boolean reloadFile) {
        if (thisConfig == null || reloadFile) {
            thisConfig = new Configuration(new File(configDir, "shared.cfg"));
        }

        manager.load(thisConfig);

        String[] colorBlacklistArray = thisConfig.getStringList("colorBlacklist", GENERAL, Globals.DEFAULT_COLOR_BLACKLIST, I19n.format("eirairc:config.property.colorBlacklist.tooltip"), null, "eirairc:config.property.colorBlacklist");
        colorBlacklist.clear();
        for (String entry : colorBlacklistArray) {
            colorBlacklist.add(entry);
        }

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

        manager.save(thisConfig);
        thisConfig.get(GENERAL, "colorBlacklist", new String[0], I19n.format("eirairc:config.property.colorBlacklist.tooltip")).set(colorBlacklist.toArray(new String[colorBlacklist.size()]));

        // Default Settings
        theme.save(thisConfig, THEME);
        botSettings.save(thisConfig, BOT);
        generalSettings.save(thisConfig, SETTINGS);

        thisConfig.save();
    }

    public static void loadLegacy(File configDir, Configuration legacyConfig) {
        thisConfig = new Configuration(new File(configDir, "shared.cfg"));

        // General
        enablePlayerColors.set(legacyConfig.getBoolean("enableNameColors", "display", enablePlayerColors.getDefaultValue(), ""));
        String[] colorBlacklistArray = legacyConfig.getStringList("colorBlackList", "serveronly", new String[0], "");
        colorBlacklist.clear();
        for (String entry : colorBlacklistArray) {
            colorBlacklist.add(Utils.unquote(entry));
        }
        debugMode.set(legacyConfig.getBoolean("debugMode", "global", debugMode.getDefaultValue(), ""));
        hidePlayerTags.set(legacyConfig.getBoolean("hidePlayerTags", "display", hidePlayerTags.getDefaultValue(), ""));

        // Network
        sslTrustAllCerts.set(legacyConfig.getBoolean("sslTrustAllCerts", "network", sslTrustAllCerts.getDefaultValue(), ""));
        sslCustomTrustStore.set(Utils.unquote(legacyConfig.getString("sslCustomTrustStore", "network", sslCustomTrustStore.getDefaultValue(), "")));
        sslDisableDiffieHellman.set(legacyConfig.getBoolean("sslDisableDiffieHellman", "network", sslDisableDiffieHellman.getDefaultValue(), ""));
        proxyHost.set(Utils.unquote(legacyConfig.getString("proxyHost", "network", proxyHost.getDefaultValue(), "")));
        proxyUsername.set(Utils.unquote(legacyConfig.getString("proxyUsername", "network", proxyUsername.getDefaultValue(), "")));
        proxyPassword.set(Utils.unquote(legacyConfig.getString("proxyPassword", "network", proxyPassword.getDefaultValue(), "")));

        // Theme
        theme.load(thisConfig, THEME, true);
        theme.loadLegacy(legacyConfig, null);
        botSettings.load(thisConfig, BOT, true);
        botSettings.loadLegacy(legacyConfig, null);
        generalSettings.load(thisConfig, SETTINGS, true);
        generalSettings.loadLegacy(legacyConfig, null);

        save();
    }

    @SuppressWarnings("unchecked")
    public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
        ConfigProperty property = manager.getProperty(key);
        if (property != null) {
            Object type = property.get();
            if (type.getClass() == String.class) {
                property.set(value);
            } else if (type.getClass() == Boolean.class) {
                property.set(Boolean.parseBoolean(value));
            } else if (type.getClass() == Integer.class) {
                property.set(Integer.parseInt(value));
            }
        } else if (theme.handleConfigCommand(sender, key, value)) {
        } else if (botSettings.handleConfigCommand(sender, key, value)) {
        } else if (generalSettings.handleConfigCommand(sender, key, value)) {
        } else {
            return false;
        }
        return true;
    }

    public static String handleConfigCommand(ICommandSender sender, String key) {
        ConfigProperty property = manager.getProperty(key);
        String value;
        if(property != null) {
            value = String.valueOf(property.get());
        } else {
            value = theme.handleConfigCommand(sender, key);
            if (value == null) {
                value = botSettings.handleConfigCommand(sender, key);
            }
            if (value == null) {
                value = generalSettings.handleConfigCommand(sender, key);
            }
        }
        return value;
    }

    public static void addOptionsToList(List<String> list, String option, boolean autoCompleteOption) {
        if (autoCompleteOption) {
            for(ConfigProperty property : manager.getProperties()) {
                if(property.getName().startsWith(option)) {
                    list.add(property.getName());
                }
            }
        } else {
            ConfigProperty property = manager.getProperty(option);
            if(property != null && property.get().getClass() == Boolean.class) {
                Utils.addBooleansToList(list);
            }
        }
        ThemeSettings.addOptionsToList(list, option, autoCompleteOption);
        GeneralSettings.addOptionsToList(list, option, autoCompleteOption);
        BotSettings.addOptionsToList(list, option, autoCompleteOption);
    }

}
