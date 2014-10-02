// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.screenshot;

import net.blay09.mods.eirairc.api.upload.IUploadHoster;
import net.blay09.mods.eirairc.api.upload.UploadManager;
import net.blay09.mods.eirairc.client.gui.settings.GuiSettings;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.ScreenshotAction;
import net.blay09.mods.eirairc.config.TempPlaceholder;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

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
		
		btnScreenshotList = new GuiButton(1, leftX, height / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.screenshots.archive"));
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
			if(hosterIdx >= UploadManager.getAvailableHosters().length) {
				hosterIdx = 0;
			}
			ClientGlobalConfig.screenshotHoster = UploadManager.getAvailableHosters()[hosterIdx];
			ConfigurationHandler.save();
			updateButtonText();
		} else if(button == btnScreenshotAction) {
			int actionIdx = ClientGlobalConfig.screenshotAction.ordinal() + 1;
			if(actionIdx > ScreenshotAction.MAX) {
				actionIdx = 0;
			}
			ClientGlobalConfig.screenshotAction = ScreenshotAction.values[actionIdx];
			if(ClientGlobalConfig.screenshotAction != ScreenshotAction.None) {
				ScreenshotManager.getInstance().findNewScreenshots(false);
			}
			ConfigurationHandler.save();
			updateButtonText();
		}
	}
	
	public void updateButtonText() {
		btnUploadService.displayString = Utils.getLocalizedMessage("irc.gui.screenshots.hoster", ClientGlobalConfig.screenshotHoster);
		IUploadHoster hoster = UploadManager.getUploadHoster(ClientGlobalConfig.screenshotHoster);
		if(hoster != null && hoster.isCustomizable()) {
			btnCustomUpload.enabled = true;
		} else {
			btnCustomUpload.enabled = false;
		}
		String autoAction;
		switch(ClientGlobalConfig.screenshotAction) {
			case Upload: autoAction = "irc.gui.screenshots.upload"; break;
			case UploadShare: autoAction = "irc.gui.screenshots.uploadShare"; break;
			case UploadClipboard: autoAction = "irc.gui.screenshots.uploadClipboard"; break;
			default: autoAction = "irc.gui.none"; break;
		}
		btnScreenshotAction.displayString = Utils.getLocalizedMessage("irc.gui.screenshots.autoAction", Utils.getLocalizedMessage(autoAction));
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.screenshots"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
	
}
