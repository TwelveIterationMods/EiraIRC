// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import java.util.Iterator;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.config.DisplayFormatConfig;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.KeyConfig;
import blay09.mods.eirairc.config.NotificationConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
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
		int leftX = field_146294_l / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = field_146294_l / 2 + BUTTON_GAP;
		
		btnOpenSettings = new GuiButton(1, leftX, field_146295_m / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnOpenSettings);
		
		btnToggleTarget = new GuiButton(2, leftX, field_146295_m / 2 - 39, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnToggleTarget);
		
		btnScreenshotShare = new GuiButton(3, leftX, field_146295_m / 2 - 14, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnScreenshotShare);
		
		btnToggleRecording = new GuiButton(4, rightX, field_146295_m / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnToggleRecording);
		
		btnToggleLive = new GuiButton(5, rightX, field_146295_m / 2 - 39, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnToggleLive);
		
		btnBack = new GuiButton(0, field_146294_l / 2 - BUTTON_WIDTH / 2, field_146295_m / 2 + 36, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.back"));
		field_146292_n.add(btnBack);
		
		updateButtonText();
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().func_147108_a(new GuiSettings());
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
				keyCode = 0;
			}
			switch(currentKeyIdx) {
				case KeyConfig.IDX_OPENSETTINGS: KeyConfig.openMenu = keyCode; break;
				case KeyConfig.IDX_SCREENSHOTSHARE: KeyConfig.screenshotShare = keyCode; break;
				case KeyConfig.IDX_TOGGLELIVE: KeyConfig.toggleLive = keyCode; break;
				case KeyConfig.IDX_TOGGLERECORDING: KeyConfig.toggleRecording = keyCode; break;
				case KeyConfig.IDX_TOGGLETARGET: KeyConfig.toggleTarget = keyCode; break;
			}
			currentKeyIdx = -1;
			ConfigurationHandler.save();
			updateButtonText();
			return;
		}
		super.keyTyped(unicode, keyCode);
	}
	
	public void updateButtonText() {
		btnOpenSettings.field_146126_j = Utils.getLocalizedMessage("irc.gui.keybinds.menu", getKeyName(KeyConfig.openMenu));
		btnScreenshotShare.field_146126_j = Utils.getLocalizedMessage("irc.gui.keybinds.screenshotShare", getKeyName(KeyConfig.screenshotShare));
		btnToggleLive.field_146126_j = Utils.getLocalizedMessage("irc.gui.keybinds.toggleLive", getKeyName(KeyConfig.toggleLive));
		btnToggleRecording.field_146126_j = Utils.getLocalizedMessage("irc.gui.keybinds.toggleRecording", getKeyName(KeyConfig.toggleRecording));
		btnToggleTarget.field_146126_j = Utils.getLocalizedMessage("irc.gui.keybinds.toggleTarget", getKeyName(KeyConfig.toggleTarget));
	}
	
	private String getKeyName(int keyCode) {
		if(keyCode == -1) {
			return Utils.getLocalizedMessage("irc.gui.none");
		}
		return Keyboard.getKeyName(keyCode);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		func_146270_b(0);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.keybinds"), field_146294_l / 2, field_146295_m / 2 - 115, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
		if(currentKeyIdx != -1) {
			drawRect(0, field_146295_m / 2 - 20, field_146294_l, field_146295_m / 2 + 20, Integer.MIN_VALUE);
			drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.keybinds.selectKey"), field_146294_l / 2, field_146295_m / 2 - field_146289_q.FONT_HEIGHT / 2, Globals.TEXT_COLOR);
		}
	}
	
}
