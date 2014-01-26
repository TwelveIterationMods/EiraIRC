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
		int leftX = field_146294_l / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = field_146294_l / 2 + BUTTON_GAP;
		
		btnScreenshotList = new GuiButton(1, leftX, field_146295_m / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.screenshots.archive"));
		field_146292_n.add(btnScreenshotList);
		
		btnUploadService = new GuiButton(2, rightX, field_146295_m / 2 - 64, BUTTON_WIDTH - BUTTON_HEIGHT - CUSTOM_GAP, BUTTON_HEIGHT, "");
		field_146292_n.add(btnUploadService);
		
		btnCustomUpload = new GuiButton(3, rightX + BUTTON_WIDTH - BUTTON_HEIGHT, field_146295_m / 2 - 64, BUTTON_HEIGHT, BUTTON_HEIGHT, "...");
		btnCustomUpload.field_146124_l = false;
		field_146292_n.add(btnCustomUpload);
		
		btnScreenshotAction = new GuiButton(4, leftX, field_146295_m / 2 - 39, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnScreenshotAction);
		
		btnBack = new GuiButton(0, field_146294_l / 2 - 100, field_146295_m / 2, 200, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.back"));
		field_146292_n.add(btnBack);
		
		updateButtonText();
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
		if(button == btnScreenshotList) {
			Minecraft.getMinecraft().func_147108_a(new GuiScreenshotList(this));
		} else if(button == btnBack) {
			Minecraft.getMinecraft().func_147108_a(new GuiSettings());
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
		btnUploadService.field_146126_j = Utils.getLocalizedMessage("irc.gui.screenshots.hoster", ScreenshotConfig.uploadHoster);
		UploadHoster host = UploadHoster.getUploadHoster(ScreenshotConfig.uploadHoster);
		if(host != null && host.isCustomizable()) {
			btnCustomUpload.field_146124_l = true;
		} else {
			btnCustomUpload.field_146124_l = false;
		}
		String autoAction = null;
		switch(ScreenshotConfig.screenshotAction) {
			case ScreenshotConfig.VALUE_UPLOAD: autoAction = "irc.gui.screenshots.upload"; break;
			case ScreenshotConfig.VALUE_UPLOADSHARE: autoAction = "irc.gui.screenshots.uploadShare"; break;
			case ScreenshotConfig.VALUE_UPLOADCLIPBOARD: autoAction = "irc.gui.screenshots.uploadClipboard"; break;
			default: autoAction = "irc.gui.none"; break;
		}
		btnScreenshotAction.field_146126_j = Utils.getLocalizedMessage("irc.gui.screenshots.autoAction", Utils.getLocalizedMessage(autoAction));
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		func_146270_b(0);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.screenshots"), field_146294_l / 2, field_146295_m / 2 - 115, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
	
}
