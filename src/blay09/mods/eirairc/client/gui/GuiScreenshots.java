// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import blay09.mods.eirairc.client.upload.UploadHoster;
import blay09.mods.eirairc.config.DisplayFormatConfig;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.NotificationConfig;
import blay09.mods.eirairc.config.ScreenshotConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class GuiScreenshots extends GuiScreen {
	
	private static final int BUTTON_WIDTH = 160;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 5;
	private static final int CUSTOM_GAP = 1;
	
	private GuiButton btnScreenshotList;
	private GuiButton btnUploadService;
	private GuiButton btnCustomUpload;
	private GuiButton btnScreenshotAction;
	
	private GuiButton btnBack;
	
	private int hosterIdx;
	
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
		
		btnScreenshotAction = new GuiButton(4, leftX, height / 2 - 39, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnScreenshotAction);
		
		btnBack = new GuiButton(0, width / 2 - 100, height / 2, 200, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		updateButtonText();
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnScreenshotList) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshotList(this));
		} else if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
		} else if(button == btnUploadService) {
			hosterIdx++;
			if(hosterIdx >= UploadHoster.availableHosters.length) {
				hosterIdx = 0;
			}
			ScreenshotConfig.uploadHoster = UploadHoster.availableHosters[hosterIdx];
			ConfigurationHandler.save();
			updateButtonText();
		} else if(button == btnScreenshotAction) {
			int action = ScreenshotConfig.screenshotAction;
			action++;
			if(action > ScreenshotConfig.VALUE_UPLOADCLIPBOARD) {
				action = ScreenshotConfig.VALUE_NONE;
			}
			ScreenshotConfig.screenshotAction = action;
			if(action != ScreenshotConfig.VALUE_NONE) {
				ScreenshotManager.getInstance().findNewScreenshots(false);
			}
			ConfigurationHandler.save();
			updateButtonText();
		}
	}
	
	public void updateButtonText() {
		btnUploadService.displayString = "Hoster: " + ScreenshotConfig.uploadHoster;
		UploadHoster host = UploadHoster.getUploadHoster(ScreenshotConfig.uploadHoster);
		if(host != null && host.isCustomizable()) {
			btnCustomUpload.enabled = true;
		} else {
			btnCustomUpload.enabled = false;
		}
		String autoAction = null;
		switch(ScreenshotConfig.screenshotAction) {
			case ScreenshotConfig.VALUE_UPLOAD: autoAction = "Upload"; break;
			case ScreenshotConfig.VALUE_UPLOADSHARE: autoAction = "Upload & Share"; break;
			case ScreenshotConfig.VALUE_UPLOADCLIPBOARD: autoAction = "Upload & Clipboard"; break;
			default: autoAction = "None"; break;
		}
		btnScreenshotAction.displayString = "Auto-Action: " + autoAction;
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.screenshots"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
	
}
