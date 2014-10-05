package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.tab.DummyTabPage;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiServerConfigContainer extends GuiTabContainer {

	public GuiServerConfigContainer(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	public void initGui() {
		super.initGui();

		for(ServerConfig config : ConfigurationHandler.getServerConfigs()) {
			pages.add(new GuiServerConfig(config));
		}

		pages.add(new DummyTabPage("+") {
			@Override
			public void tabClicked(GuiTabContainer tabContainer) {
				GuiServerConfig newTab = new GuiServerConfig();
				pages.add(pages.size() - 1, newTab);
				buildHeaders();
				setCurrentTab(newTab);
			}
		});

		buildHeaders();
	}

}
