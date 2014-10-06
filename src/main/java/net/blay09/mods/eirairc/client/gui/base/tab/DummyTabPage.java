package net.blay09.mods.eirairc.client.gui.base.tab;

/**
 * Created by Blay09 on 04.10.2014.
 */
public abstract class DummyTabPage extends GuiTabPage {

	public DummyTabPage(GuiTabContainer tabContainer, String title) {
		super(tabContainer, title);
	}

	@Override
	public abstract void tabClicked();

}
