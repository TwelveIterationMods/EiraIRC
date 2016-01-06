package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class GuiEiraIRCConfig extends GuiConfig {

    public GuiEiraIRCConfig(GuiScreen parentScreen) {
        super(parentScreen, getCategories(), Globals.MOD_ID, "global", false, false, I19n.format("eirairc:gui.config"));
    }

    private static List<IConfigElement> getCategories() {
        List<IConfigElement> list = new ArrayList<>();
        list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.general.shared"), "eirairc:config.category.general", getCombinedConfigElements(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.GENERAL), ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.GENERAL))));
        list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.screenshots"), "eirairc:config.category.screenshots", new ConfigElement(ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.SCREENSHOTS)).getChildElements()));
        list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.notifications"), "eirairc:config.category.notifications", new ConfigElement(ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.NOTIFICATIONS)).getChildElements()));
        list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.theme"), "eirairc:config.category.theme", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.THEME)).getChildElements()));
        list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.bot"), "eirairc:config.category.bot", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.BOT)).getChildElements()));
        list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.settings"), "eirairc:config.category.settings", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.SETTINGS)).getChildElements()));
        list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.compatibility"), "eirairc:config.category.compatibility", new ConfigElement(ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.COMPATIBILITY)).getChildElements()));
        list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.network"), "eirairc:config.category.network", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.NETWORK)).getChildElements()));
        list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.addons"), "eirairc:config.category.addons", getCombinedConfigElements(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.ADDONS), ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.ADDONS))));
        return list;
    }

    public static List<IConfigElement> getCombinedConfigElements(ConfigCategory... categories) {
        List<IConfigElement> list = new ArrayList<>();
        for (ConfigCategory category : categories) {
            list.addAll(new ConfigElement(category).getChildElements());
        }
        return list;
    }

    public static List<IConfigElement> getAllConfigElements(Configuration config) {
        List<IConfigElement> list = new ArrayList<>();
        for (String category : config.getCategoryNames()) {
            list.addAll(new ConfigElement(config.getCategory(category)).getChildElements());
        }
        return list;
    }

}
