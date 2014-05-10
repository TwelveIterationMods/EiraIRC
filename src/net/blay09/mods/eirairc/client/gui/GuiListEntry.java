package net.blay09.mods.eirairc.client.gui;

import net.minecraft.client.gui.Gui;

public abstract class GuiListEntry extends Gui {

	public abstract void setSelected(boolean selected);
	public abstract void drawEntry(int x, int y);
	
}
