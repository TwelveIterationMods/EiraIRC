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
		int leftX = field_146294_l / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = field_146294_l / 2 + BUTTON_GAP;
		
		btnFriendJoined = new GuiButton(1, leftX, field_146295_m / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		btnFriendJoined.field_146124_l = false;
		field_146292_n.add(btnFriendJoined);
		
		btnNameMentioned = new GuiButton(2, leftX, field_146295_m / 2 - 39, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		btnNameMentioned.field_146124_l = false;
		field_146292_n.add(btnNameMentioned);
		
		btnUserRecording = new GuiButton(3, leftX, field_146295_m / 2 - 14, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnUserRecording);
		
		btnPrivateMessage = new GuiButton(4, rightX, field_146295_m / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnPrivateMessage);
		
		btnBack = new GuiButton(0, field_146294_l / 2 - 100, field_146295_m / 2 + 36, 200, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.back"));
		field_146292_n.add(btnBack);
		
		updateButtonText();
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().func_147108_a(new GuiSettings());
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
		btnFriendJoined.field_146126_j = Utils.getLocalizedMessage("irc.gui.notifications.friendJoined", getTextForValue(NotificationConfig.friendJoined));
		btnNameMentioned.field_146126_j = Utils.getLocalizedMessage("irc.gui.notifications.nameMentioned", getTextForValue(NotificationConfig.nameMentioned));
		btnUserRecording.field_146126_j = Utils.getLocalizedMessage("irc.gui.notifications.userRecording", getTextForValue(NotificationConfig.userRecording));
		btnPrivateMessage.field_146126_j = Utils.getLocalizedMessage("irc.gui.notifications.privateMessage", getTextForValue(NotificationConfig.privateMessage));
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
			case NotificationConfig.VALUE_TEXTONLY: return Utils.getLocalizedMessage("irc.gui.notifications.textOnly");
			case NotificationConfig.VALUE_SOUNDONLY: return Utils.getLocalizedMessage("irc.gui.notifications.soundOnly");
			case NotificationConfig.VALUE_TEXTANDSOUND: return Utils.getLocalizedMessage("irc.gui.notifications.textAndSound");
			default: return Utils.getLocalizedMessage("irc.gui.none");
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		func_146270_b(0);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.notifications"), field_146294_l / 2, field_146295_m / 2 - 115, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
	
}
