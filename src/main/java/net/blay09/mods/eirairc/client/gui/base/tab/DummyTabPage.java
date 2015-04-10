package net.blay09.mods.eirairc.client.gui.base.tab;


public abstract class DummyTabPage extends GuiTabPage {

	public DummyTabPage(GuiTabContainer tabContainer, String title) {
		super(tabContainer, title);
	}

	@Override
	public abstract void tabClicked();

}
