// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import java.util.Iterator;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.DisplayFormatConfig;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.KeyConfig;
import blay09.mods.eirairc.config.NotificationConfig;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class GuiKeybinds extends GuiScreen {
	
	private static final int BUTTON_WIDTH = 190;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 5;
	
	private GuiButton btnOpenSettings;
	private GuiButton btnToggleRecording;
	private GuiButton btnToggleLive;
	private GuiButton btnToggleTarget;
	private GuiButton btnScreenshotShare;
	private GuiButton btnBack;
	
	private int currentKeyIdx = -1;
	
	@Override
	public void initGui() {
		int leftX = width / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = width / 2 + BUTTON_GAP;
		
		btnOpenSettings = new GuiButton(1, leftX, height / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnOpenSettings);
		
		btnToggleTarget = new GuiButton(2, leftX, height / 2 - 39, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnToggleTarget);
		
		btnScreenshotShare = new GuiButton(3, leftX, height / 2 - 14, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnScreenshotShare);
		
		btnToggleRecording = new GuiButton(4, rightX, height / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnToggleRecording);
		
		btnToggleLive = new GuiButton(5, rightX, height / 2 - 39, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnToggleLive);
		
		btnBack = new GuiButton(0, width / 2 - BUTTON_WIDTH / 2, height / 2 + 36, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		updateButtonText();
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
		} else if(button == btnOpenSettings) {
			currentKeyIdx = KeyConfig.IDX_OPENSETTINGS;
		} else if(button == btnToggleTarget) {
			currentKeyIdx = KeyConfig.IDX_TOGGLETARGET;
		} else if(button == btnScreenshotShare) {
			currentKeyIdx = KeyConfig.IDX_SCREENSHOTSHARE;
		} else if(button == btnToggleRecording) {
			currentKeyIdx = KeyConfig.IDX_TOGGLERECORDING;
		} else if(button == btnToggleLive) {
			currentKeyIdx = KeyConfig.IDX_TOGGLELIVE;
		}
	}
	
	@Override
	protected void keyTyped(char unicode, int keyCode) {
		if(currentKeyIdx != -1) {
			if(keyCode == Keyboard.KEY_ESCAPE) {
				currentKeyIdx = -1;
			} else {
				switch(currentKeyIdx) {
				case KeyConfig.IDX_OPENSETTINGS: KeyConfig.openMenu = keyCode; break;
				case KeyConfig.IDX_SCREENSHOTSHARE: KeyConfig.screenshotShare = keyCode; break;
				case KeyConfig.IDX_TOGGLELIVE: KeyConfig.toggleLive = keyCode; break;
				case KeyConfig.IDX_TOGGLERECORDING: KeyConfig.toggleRecording = keyCode; break;
				case KeyConfig.IDX_TOGGLETARGET: KeyConfig.toggleTarget = keyCode; break;
				}
			}
			currentKeyIdx = -1;
			ConfigurationHandler.save();
			updateButtonText();
			return;
		}
		super.keyTyped(unicode, keyCode);
	}
	
	public void updateButtonText() {
		btnOpenSettings.displayString = "Menu: " + getKeyName(KeyConfig.openMenu);
		btnScreenshotShare.displayString = "Screenshot & Share: " + getKeyName(KeyConfig.screenshotShare);
		btnToggleLive.displayString = "Toggle Live: " + getKeyName(KeyConfig.toggleLive);
		btnToggleRecording.displayString = "Toggle Recording: " + getKeyName(KeyConfig.toggleRecording);
		btnToggleTarget.displayString = "Toggle Target: " + getKeyName(KeyConfig.toggleTarget);
	}
	
	private String getKeyName(int keyCode) {
		if(keyCode == -1) {
			return "None";
		}
		return Keyboard.getKeyName(keyCode);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.keybinds"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
		if(currentKeyIdx != -1) {
			drawRect(0, height / 2 - 20, width, height / 2 + 20, Integer.MIN_VALUE);
			drawCenteredString(fontRenderer, "Press a key or ESC to continue...", width / 2, height / 2 - fontRenderer.FONT_HEIGHT / 2, Globals.TEXT_COLOR);
		}
	}
	
}
