package net.blay09.mods.eirairc.client.gui.base.tab;

import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.minecraft.client.gui.GuiScreen;


public abstract class GuiTabPage extends EiraGuiScreen {

	protected String title;
	protected GuiTabContainer tabContainer;

	public GuiTabPage(GuiTabContainer tabContainer, String title) {
		this.title = title;
		this.tabContainer = tabContainer;
	}

	public GuiTabPage(GuiTabContainer tabContainer, GuiTabPage parentTab) {
		super(parentTab);
		this.title = parentTab.getTitle();
		this.tabContainer = tabContainer;
	}

	public boolean requestClose() {
		return true;
	}

	public void tabClicked() {
		tabContainer.setCurrentTab(this, false);
	}

	public String getTitle() {
		return title;
	}

	public GuiScreen getParentScreen() {
		return parentScreen;
	}
}
