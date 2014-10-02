// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.NotificationStyle;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

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
		btnFriendJoined.enabled = false;
		buttonList.add(btnFriendJoined);
		
		btnNameMentioned = new GuiButton(2, leftX, height / 2 - 39, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		btnNameMentioned.enabled = false;
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
			ClientGlobalConfig.ntfyFriendJoined = getNextValue(ClientGlobalConfig.ntfyFriendJoined);
		} else if(button == btnNameMentioned) {
			ClientGlobalConfig.ntfyNameMentioned = getNextValue(ClientGlobalConfig.ntfyNameMentioned);
		} else if(button == btnUserRecording) {
			ClientGlobalConfig.ntfyUserRecording = getNextValue(ClientGlobalConfig.ntfyUserRecording);
		} else if(button == btnPrivateMessage) {
			ClientGlobalConfig.ntfyPrivateMessage = getNextValue(ClientGlobalConfig.ntfyPrivateMessage);
		}
		updateButtonText();
	}
	
	public void updateButtonText() {
		btnFriendJoined.displayString = Utils.getLocalizedMessage("irc.gui.notifications.friendJoined", getTextForValue(ClientGlobalConfig.ntfyFriendJoined));
		btnNameMentioned.displayString = Utils.getLocalizedMessage("irc.gui.notifications.nameMentioned", getTextForValue(ClientGlobalConfig.ntfyNameMentioned));
		btnUserRecording.displayString = Utils.getLocalizedMessage("irc.gui.notifications.userRecording", getTextForValue(ClientGlobalConfig.ntfyUserRecording));
		btnPrivateMessage.displayString = Utils.getLocalizedMessage("irc.gui.notifications.privateMessage", getTextForValue(ClientGlobalConfig.ntfyPrivateMessage));
	}
	
	private NotificationStyle getNextValue(NotificationStyle prevValue) {
		int value = prevValue.ordinal() + 1;
		if(value > NotificationStyle.MAX) {
			value = 0;
		}
		return NotificationStyle.values[value];
	}
	
	private String getTextForValue(NotificationStyle configValue) {
		switch(configValue) {
			case TextOnly: return Utils.getLocalizedMessage("irc.gui.notifications.textOnly");
			case SoundOnly: return Utils.getLocalizedMessage("irc.gui.notifications.soundOnly");
			case TextAndSound: return Utils.getLocalizedMessage("irc.gui.notifications.textAndSound");
			default: return Utils.getLocalizedMessage("irc.gui.none");
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.notifications"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
	
}
