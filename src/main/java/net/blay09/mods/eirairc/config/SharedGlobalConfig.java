// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.config.property.ConfigManager;
import net.blay09.mods.eirairc.config.property.ConfigProperty;
import net.blay09.mods.eirairc.api.config.StringList;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.List;

public class SharedGlobalConfig {

    public static final String GENERAL = "general";
    public static final String NETWORK = "network";
    public static final String THEME = "theme";
    public static final String BOT = "bot";
    public static final String SETTINGS = "settings";
    public static final String ADDONS = "addons";

    public static Configuration thisConfig;
    public static final ConfigManager manager = new ConfigManager();

    // General
    public static final ConfigProperty<String> defaultChat = new ConfigProperty<>(manager, GENERAL, "defaultChat", "Minecraft");
    public static final ConfigProperty<Boolean> enablePlayerColors = new ConfigProperty<>(manager, GENERAL, "enablePlayerColors", true);
    public static final ConfigProperty<Boolean> hidePlayerTags = new ConfigProperty<>(manager, GENERAL, "hidePlayerTags", false);
    public static final ConfigProperty<Boolean> debugMode = new ConfigProperty<>(manager, GENERAL, "debugMode", false);
    public static final ConfigProperty<Boolean> preventUserPing = new ConfigProperty<>(manager, GENERAL, "preventUserPing", false);
    public static final ConfigProperty<Boolean> twitchNameColors = new ConfigProperty<>(manager, GENERAL, "twitchNameColors", true);
    public static final ConfigProperty<String> ircCommandPrefix = new ConfigProperty<>(manager, GENERAL, "ircCommandPrefix", "!");
    public static final ConfigProperty<StringList> colorBlacklist = new ConfigProperty<>(manager, GENERAL, "colorBlacklist", new StringList(Globals.DEFAULT_COLOR_BLACKLIST));

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
            manager.setParentConfig(thisConfig);
            generalSettings.manager.setParentConfig(thisConfig);
            botSettings.manager.setParentConfig(thisConfig);
            theme.manager.setParentConfig(thisConfig);
        }

        manager.load(thisConfig);

        // Default Settings
        theme.load(thisConfig, false);
        botSettings.load(thisConfig, false);
        generalSettings.load(thisConfig, false);

        save();
    }

    public static void save() {
        // Category Comments
        thisConfig.setCategoryComment(GENERAL, I19n.format("eirairc:config.category.general.tooltip"));
        thisConfig.setCategoryComment(NETWORK, I19n.format("eirairc:config.category.network.tooltip"));
        thisConfig.setCategoryComment(THEME, I19n.format("eirairc:config.category.theme.tooltip"));
        thisConfig.setCategoryComment(BOT, I19n.format("eirairc:config.category.bot.tooltip"));
        thisConfig.setCategoryComment(SETTINGS, I19n.format("eirairc:config.category.settings.tooltip"));
        thisConfig.setCategoryComment(ADDONS, I19n.format("eirairc:config.category.addons.tooltip"));

        manager.save(thisConfig);

        // Default Settings
        theme.save(thisConfig);
        botSettings.save(thisConfig);
        generalSettings.save(thisConfig);

        thisConfig.save();
    }

    public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
        if(manager.setFromString(key, value)) {
        } else if (theme.handleConfigCommand(sender, key, value)) {
        } else if (botSettings.handleConfigCommand(sender, key, value)) {
        } else if (generalSettings.handleConfigCommand(sender, key, value)) {
        } else {
            return false;
        }
        return true;
    }

    public static String handleConfigCommand(ICommandSender sender, String key) {
        String value = manager.getAsString(key);
        if(value == null) {
            value = theme.handleConfigCommand(sender, key);
        }
        if (value == null) {
            value = botSettings.handleConfigCommand(sender, key);
        }
        if (value == null) {
            value = generalSettings.handleConfigCommand(sender, key);
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
                list.add("true");
                list.add("false");
            }
        }
        theme.addOptionsToList(list, option, autoCompleteOption);
        generalSettings.addOptionsToList(list, option, autoCompleteOption);
        botSettings.addOptionsToList(list, option, autoCompleteOption);
    }

}
