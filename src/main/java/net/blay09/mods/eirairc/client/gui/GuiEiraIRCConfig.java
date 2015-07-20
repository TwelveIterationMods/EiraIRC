// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.gui;

import cpw.mods.fml.client.config.ConfigGuiType;
import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;

import java.util.ArrayList;
import java.util.List;

public class GuiEiraIRCConfig extends GuiConfig {

	public GuiEiraIRCConfig(GuiScreen parentScreen) {
		super(parentScreen, getCategories(), Globals.MOD_ID, "global", false, false, "EiraIRC Config");
	}

	@SuppressWarnings("unchecked")
	private static List<IConfigElement> getCategories() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.general.shared"), "eirairc:config.category.general", getGeneralConfigElements(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.GENERAL), ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.GENERAL))));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.screenshots"), "eirairc:config.category.screenshots", new ConfigElement(ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.SCREENSHOTS)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.notifications"), "eirairc:config.category.notifications", new ConfigElement(ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.NOTIFICATIONS)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.theme"), "eirairc:config.category.theme", getThemeConfigElements(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.THEME), true)));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.bot"), "eirairc:config.category.bot", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.BOT)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.settings"), "eirairc:config.category.settings", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.SETTINGS)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.compatibility"), "eirairc:config.category.compatibility", new ConfigElement(ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.COMPATIBILITY)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.network"), "eirairc:config.category.network", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.NETWORK)).getChildElements()));
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<IConfigElement> getGeneralConfigElements(ConfigCategory ctgyShared, ConfigCategory ctgyClient) {
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.addAll(new ConfigElement(ctgyShared).getChildElements());
		list.addAll(new ConfigElement(ctgyClient).getChildElements());
		return list;
	}

	public static List<IConfigElement> getThemeConfigElements(ConfigCategory ctgy, boolean isGlobal) {
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		if(isGlobal) {
			list.add(new ConfigColorElement(ctgy.get("mcNameColor")));
			list.add(new ConfigColorElement(ctgy.get("mcOpNameColor")));
		}
		list.add(new ConfigColorElement(ctgy.get("ircNameColor")));
		list.add(new ConfigColorElement(ctgy.get("ircOpNameColor")));
		list.add(new ConfigColorElement(ctgy.get("ircVoiceNameColor")));
		list.add(new ConfigColorElement(ctgy.get("ircPrivateNameColor")));
		list.add(new ConfigColorElement(ctgy.get("ircNoticeTextColor")));
		list.add(new ConfigColorElement(ctgy.get("emoteTextColor")));
		return list;
	}

	private static class ConfigColorElement extends ConfigElement<String> {

		public ConfigColorElement(Property prop) {
			super(prop);
		}

		@Override
		public ConfigGuiType getType() {
			return ConfigGuiType.COLOR;
		}

	}

}
