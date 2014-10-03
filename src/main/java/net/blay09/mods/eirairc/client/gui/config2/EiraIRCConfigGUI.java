package net.blay09.mods.eirairc.client.gui.config2;

import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blay09 on 03.10.2014.
 */
public class EiraIRCConfigGUI extends GuiConfig {

	public EiraIRCConfigGUI(GuiScreen parentScreen) {
		super(parentScreen, getCategories(), Globals.MOD_ID, false, false, "EiraIRC Config");
	}

	@SuppressWarnings("unchecked") // why would he even complain here? o_O
	private static List<IConfigElement> getCategories() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.general.shared"), "eirairc:config.category.general.shared", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.GENERAL)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.general.client"), "eirairc:config.category.general.client", new ConfigElement(ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.GENERAL)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.network"), "eirairc:config.category.network", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.NETWORK)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.screenshots"), "eirairc:config.category.screenshots", new ConfigElement(ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.SCREENSHOTS)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.notifications"), "eirairc:config.category.notifications", new ConfigElement(ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.NOTIFICATIONS)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.compatibility"), "eirairc:config.category.compatibility", new ConfigElement(ClientGlobalConfig.thisConfig.getCategory(ClientGlobalConfig.COMPATIBILITY)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.theme"), "eirairc:config.category.theme", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.THEME)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.bot"), "eirairc:config.category.bot", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.BOT)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement(I18n.format("eirairc:config.category.settings"), "eirairc:config.category.settings", new ConfigElement(SharedGlobalConfig.thisConfig.getCategory(SharedGlobalConfig.SETTINGS)).getChildElements()));
		return list;
	}


}
