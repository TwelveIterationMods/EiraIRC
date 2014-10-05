package net.blay09.mods.eirairc.client.gui.base.tab;

import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;

/**
 * Created by Blay09 on 04.10.2014.
 */
public abstract class GuiTabPage extends EiraGuiScreen {

	private String title;

	public GuiTabPage(String title) {
		this.title = title;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void tabClicked(GuiTabContainer tabContainer) {
		tabContainer.setCurrentTab(this);
	}

	public String getTitle() {
		return title;
	}
}
