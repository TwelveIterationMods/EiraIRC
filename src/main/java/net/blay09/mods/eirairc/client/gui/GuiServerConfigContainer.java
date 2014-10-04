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
			public void tabClicked() {
				System.out.println("Adding new server");
			}
		});

		buildHeaders();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
//		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		super.drawScreen(mouseX, mouseY, p_73863_3_);
	}
}
