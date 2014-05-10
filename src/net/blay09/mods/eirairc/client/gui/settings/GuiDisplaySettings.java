// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import java.util.Iterator;

import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.DisplayFormatConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiDisplaySettings extends GuiScreen {

	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;
	
	private GuiButton btnRecordingHUD;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		int leftX = width /  2 - 152;
		int rightX = width / 2 + 2;
		
		btnRecordingHUD = new GuiButton(2, leftX, height / 2 - 90, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnRecordingHUD);
		
		btnBack = new GuiButton(0, width / 2 - 100, height / 2 + 90, 200, 20, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		updateButtonText();
	}
	
	public void updateButtonText() {
		btnRecordingHUD.displayString = Utils.getLocalizedMessage("irc.gui.displaySettings.recordingHud", Utils.getLocalizedMessage((DisplayConfig.hudRecState ? "irc.gui.yes" : "irc.gui.no")));
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
		} else if(button == btnRecordingHUD) {
			DisplayConfig.hudRecState = !DisplayConfig.hudRecState;
			updateButtonText();
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		this.drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.displaySettings"), width / 2, height / 2 - 110, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
}
