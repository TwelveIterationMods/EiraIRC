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

	public void tabClicked() {

	}

	public String getTitle() {
		return title;
	}
}
