package net.blay09.mods.eirairc.client.gui.overlay;

import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Blay09 on 19.02.2015.
 */
public class GuiOverlay extends EiraGuiScreen {

	public GuiOverlay(GuiScreen parentScreen) {
		super(parentScreen);
		allowSideClickClose = false;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(mouseX < menuX || mouseX >= menuX + menuWidth || mouseY < menuY || mouseY >= menuY + menuHeight) {
			gotoPrevious();
			return;
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void gotoPrevious() {
		((EiraGuiScreen) parentScreen).setOverlay(null);
	}
}
