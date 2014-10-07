package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.tab.DummyTabPage;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabPage;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiServerConfigContainer extends GuiTabContainer {

	public GuiServerConfigContainer(GuiScreen parentScreen) {
		super(parentScreen);

		for(ServerConfig config : ConfigurationHandler.getServerConfigs()) {
			pages.add(new GuiServerConfig(this, config));
		}

		pages.add(new DummyTabPage(this, "+") {
			@Override
			public void tabClicked() {
				for(int i = 0; i < pages.size(); i++) {
					GuiTabPage tabPage = pages.get(i);
					if(tabPage instanceof GuiServerConfig) {
						if(((GuiServerConfig) tabPage).isNew()) {
							setCurrentTab(tabPage, false);
							return;
						}
					}
				}
				GuiServerConfig newTab = new GuiServerConfig(GuiServerConfigContainer.this);
				pages.add(pages.size() - 1, newTab);
				buildHeaders();
				setCurrentTab(newTab, false);
			}
		});

		currentTab = pages.get(0);
	}

	@Override
	public void initGui() {
		super.initGui();

		buildHeaders();
	}

}
