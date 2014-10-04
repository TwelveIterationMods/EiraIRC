package net.blay09.mods.eirairc.client.gui.base.tab;

/**
 * Created by Blay09 on 05.10.2014.
 */
public class GuiTabHeader {

	public final GuiTabPage tabPage;
	public final int x;
	public final int y;
	public final int width;
	public final int height;

	public GuiTabHeader(GuiTabPage tabPage, int x, int y, int width, int height) {
		this.tabPage = tabPage;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

}
