package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.config.property.ConfigProperty;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class LocalConfig {

    private static final String GENERAL = "general";

    public static Configuration thisConfig;
    public static final ConfigManager manager = new ConfigManager();

    public static final ConfigProperty<Boolean> disableModpackConfirmation = new ConfigProperty<>(manager, GENERAL, "disableModpackConfirmation", false);
    public static final ConfigProperty<Boolean> disableModpackIRC = new ConfigProperty<>(manager, GENERAL, "disableModpackIRC", false);
    public static final ConfigProperty<Boolean> disableWelcomeScreen = new ConfigProperty<>(manager, GENERAL, "disableWelcomeScreen", false);

    public static void load(File configDir, boolean reloadFile) {
        if(thisConfig == null || reloadFile) {
            thisConfig = new Configuration(new File(configDir, "local.cfg"));
            manager.setParentConfig(thisConfig);
        }

        manager.load(thisConfig);

        save();
    }

    public static void save() {
        thisConfig.getCategory(GENERAL).setComment("This file contains user-specific settings and is separate to prevent value resets during modpack updates.\nShipping this file in your modpack would be a pretty dumb idea.");

        manager.save(thisConfig);

        thisConfig.save();
    }
}
