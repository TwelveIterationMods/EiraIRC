// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import blay09.mods.eirairc.config.DisplayConfig;
import blay09.mods.eirairc.config.DisplayFormatConfig;
import blay09.mods.eirairc.config.ScreenshotConfig;
import blay09.mods.eirairc.util.Globals;

public class GuiDisplaySettings extends GuiScreen {

	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;
	
	private GuiButton btnMessageDisplayMode;
	private GuiButton btnRecordingHUD;
	private GuiButton btnBack;
	
	private Iterator<DisplayFormatConfig> displayFormatIterator;
	
	@Override
	public void initGui() {
		int leftX = width /  2 - 152;
		int rightX = width / 2 + 2;
		
		btnMessageDisplayMode = new GuiButton(1, leftX, height / 2 - 90, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnMessageDisplayMode);
		
		btnRecordingHUD = new GuiButton(2, rightX, height / 2 - 90, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnRecordingHUD);
		
		btnBack = new GuiButton(0, width / 2 - 100, height / 2 + 90, 200, 20, "Back");
		buttonList.add(btnBack);
		
		displayFormatIterator = DisplayConfig.displayFormates.values().iterator();
		updateButtonText();
	}
	
	public void updateButtonText() {
		btnRecordingHUD.displayString = "Show Recording HUD: " + (DisplayConfig.hudRecState ? "Yes" : "No");
		btnMessageDisplayMode.displayString = "Message Display: " + DisplayConfig.displayMode;
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(null);
		} else if(button == btnRecordingHUD) {
			DisplayConfig.hudRecState = !DisplayConfig.hudRecState;
			updateButtonText();
		} else if(button == btnMessageDisplayMode) {
			if(!displayFormatIterator.hasNext()) {
				displayFormatIterator = DisplayConfig.displayFormates.values().iterator();
			}
			DisplayConfig.displayMode = displayFormatIterator.next().getName();
			updateButtonText();
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		this.drawCenteredString(fontRenderer, "EiraIRC - Display Settings", width / 2, height / 2 - 110, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
}
