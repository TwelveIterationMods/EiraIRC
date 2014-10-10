package net.blay09.mods.eirairc.client.gui.base.list;

import net.minecraft.client.gui.Gui;

public abstract class GuiListEntry extends Gui {

	private boolean selected;

	public void setSelected(boolean selected){
		this.selected = selected;
		if(selected) {
			onSelected();
		}
	}

	public void onSelected() {}
	public void onDoubleClick() {}
	public abstract void drawEntry(int x, int y);
	
}
