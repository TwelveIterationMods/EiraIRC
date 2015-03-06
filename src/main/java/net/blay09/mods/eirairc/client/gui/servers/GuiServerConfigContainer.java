package net.blay09.mods.eirairc.client.gui.servers;

import net.blay09.mods.eirairc.client.gui.base.tab.DummyTabPage;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabPage;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiServerConfigContainer extends GuiTabContainer {

	public GuiServerConfigContainer(GuiScreen parentScreen) {
		super(parentScreen);

		// Create a tab page for every server configuration
		for(ServerConfig config : ConfigurationHandler.getServerConfigs()) {
			pages.add(new GuiServerConfig(this, config));
		}

		// If no servers are set up yet, start on an empty server form by default
		if(pages.size() == 0) {
			GuiServerConfig newTab = new GuiServerConfig(this);
			pages.add(newTab);
		}

		// Add a dummy tab that allows adding of new server configurations
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

		// Select the first tab on startup
		currentTab = pages.get(0);
	}

	@Override
	public void initGui() {
		super.initGui();

		buildHeaders();
	}

}
