// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.config.DisplayFormatConfig;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.NotificationConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class GuiNotifications extends GuiScreen {
	
	private static final int BUTTON_WIDTH = 190;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 5;
	
	private GuiButton btnFriendJoined;
	private GuiButton btnNameMentioned;
	private GuiButton btnUserRecording;
	private GuiButton btnPrivateMessage;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		int leftX = width / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = width / 2 + BUTTON_GAP;
		
		btnFriendJoined = new GuiButton(1, leftX, height / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnFriendJoined);
		
		btnNameMentioned = new GuiButton(2, leftX, height / 2 - 39, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnNameMentioned);
		
		btnUserRecording = new GuiButton(3, leftX, height / 2 - 14, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnUserRecording);
		
		btnPrivateMessage = new GuiButton(4, rightX, height / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnPrivateMessage);
		
		btnBack = new GuiButton(0, width / 2 - 100, height / 2 + 36, 200, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		updateButtonText();
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
		} else if(button == btnFriendJoined) {
			NotificationConfig.friendJoined = getNextValue(NotificationConfig.friendJoined);
		} else if(button == btnNameMentioned) {
			NotificationConfig.nameMentioned = getNextValue(NotificationConfig.nameMentioned);
		} else if(button == btnUserRecording) {
			NotificationConfig.userRecording = getNextValue(NotificationConfig.userRecording);
		} else if(button == btnPrivateMessage) {
			NotificationConfig.privateMessage = getNextValue(NotificationConfig.privateMessage);
		}
		updateButtonText();
	}
	
	public void updateButtonText() {
		btnFriendJoined.displayString = Utils.getLocalizedMessage("irc.gui.friendJoined", getTextForValue(NotificationConfig.friendJoined));
		btnNameMentioned.displayString = Utils.getLocalizedMessage("irc.gui.nameMentioned", getTextForValue(NotificationConfig.nameMentioned));
		btnUserRecording.displayString = Utils.getLocalizedMessage("irc.gui.userRecording", getTextForValue(NotificationConfig.userRecording));
		btnPrivateMessage.displayString = Utils.getLocalizedMessage("irc.gui.privateMessage", getTextForValue(NotificationConfig.privateMessage));
	}
	
	private int getNextValue(int value) {
		value++;
		if(value > NotificationConfig.VALUE_TEXTANDSOUND) {
			value = NotificationConfig.VALUE_NONE;
		}
		return value;
	}
	
	private String getTextForValue(int configValue) {
		switch(configValue) {
			case NotificationConfig.VALUE_TEXTONLY: return Utils.getLocalizedMessage("irc.gui.textOnly");
			case NotificationConfig.VALUE_SOUNDONLY: return Utils.getLocalizedMessage("irc.gui.soundOnly");
			case NotificationConfig.VALUE_TEXTANDSOUND: return Utils.getLocalizedMessage("irc.gui.textAndSound");
			default: return Utils.getLocalizedMessage("irc.gui.none");
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.notifications"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
	
}
