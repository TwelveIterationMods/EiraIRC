// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.Utils;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.DisplayFormatConfig;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.Globals;
import blay09.mods.eirairc.config.NotificationConfig;

public class GuiIRCScreenshots extends GuiScreen {
	
	private static final int BUTTON_WIDTH = 120;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 5;
	private static final int CUSTOM_GAP = 1;
	
	private GuiButton btnScreenshotList;
	private GuiButton btnUploadService;
	private GuiButton btnCustomUpload;
	private GuiButton btnScreenshotAction;
	
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		int leftX = width / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = width / 2 + BUTTON_GAP;
		
		btnScreenshotList = new GuiButton(1, leftX, height / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.screenshotList"));
		buttonList.add(btnScreenshotList);
		
		btnUploadService = new GuiButton(2, rightX, height / 2 - 64, BUTTON_WIDTH - BUTTON_HEIGHT - CUSTOM_GAP, BUTTON_HEIGHT, "");
		buttonList.add(btnUploadService);
		
		btnCustomUpload = new GuiButton(3, rightX + BUTTON_WIDTH - BUTTON_HEIGHT, height / 2 - 64, BUTTON_HEIGHT, BUTTON_HEIGHT, "...");
		btnCustomUpload.enabled = false;
		buttonList.add(btnCustomUpload);
		
		btnScreenshotAction = new GuiButton(4, leftX, height / 2 - 48, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		
		btnBack = new GuiButton(0, leftX, height / 2, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		updateButtonText();
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnScreenshotList) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCScreenshotList(this));
		} else if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCSettings());
		}
		updateButtonText();
	}
	
	public void updateButtonText() {
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.screenshots"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
	
}
