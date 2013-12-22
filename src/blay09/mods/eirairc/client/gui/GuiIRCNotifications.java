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

public class GuiIRCNotifications extends GuiScreen {
	
	private static final int BUTTON_WIDTH = 190;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 5;
	
	private GuiButton btnFriendJoined;
	private GuiButton btnPlayerMentioned;
	private GuiButton btnUserRecording;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		int leftX = width / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = width / 2 + BUTTON_GAP;
		
		btnFriendJoined = new GuiButton(1, leftX, height / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnFriendJoined);
		
		btnPlayerMentioned = new GuiButton(2, leftX, height / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnPlayerMentioned);
		
		btnUserRecording = new GuiButton(3, leftX, height / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnUserRecording);
		
		btnBack = new GuiButton(0, leftX, height / 2, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		updateButtonText();
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCSettings());
		} else if(button == btnFriendJoined) {
			NotificationConfig.friendJoined++;
			if(NotificationConfig.friendJoined > NotificationConfig.VALUE_TEXTANDSOUND) {
				NotificationConfig.friendJoined = NotificationConfig.VALUE_NONE;
			}
		} else if(button == btnPlayerMentioned) {
			NotificationConfig.playerMentioned++;
			if(NotificationConfig.playerMentioned > NotificationConfig.VALUE_TEXTANDSOUND) {
				NotificationConfig.playerMentioned = NotificationConfig.VALUE_NONE;
			}
		} else if(button == btnUserRecording) {
			NotificationConfig.userRecording++;
			if(NotificationConfig.userRecording > NotificationConfig.VALUE_TEXTANDSOUND) {
				NotificationConfig.userRecording = NotificationConfig.VALUE_NONE;
			}
		}
		updateButtonText();
	}
	
	public void updateButtonText() {
		String none = Utils.getLocalizedMessage("irc.gui.none");
		String textOnly = Utils.getLocalizedMessage("irc.gui.textOnly");
		String soundOnly = Utils.getLocalizedMessage("irc.gui.soundOnly");
		String textAndSound = Utils.getLocalizedMessage("irc.gui.textAndSound");
		
		btnFriendJoined.displayString = Utils.getLocalizedMessage("irc.gui.friendJoined", (NotificationConfig.friendJoined == NotificationConfig.VALUE_NONE) ? none : (NotificationConfig.friendJoined == NotificationConfig.VALUE_TEXTONLY) ? textOnly : (NotificationConfig.friendJoined == NotificationConfig.VALUE_SOUNDONLY) ? soundOnly : textAndSound);
		btnPlayerMentioned.displayString = Utils.getLocalizedMessage("irc.gui.playerMentioned", (NotificationConfig.playerMentioned == NotificationConfig.VALUE_NONE) ? none : (NotificationConfig.playerMentioned == NotificationConfig.VALUE_TEXTONLY) ? textOnly : (NotificationConfig.playerMentioned == NotificationConfig.VALUE_SOUNDONLY) ? soundOnly : textAndSound);
		btnUserRecording.displayString = Utils.getLocalizedMessage("irc.gui.userRecording", (NotificationConfig.userRecording == NotificationConfig.VALUE_NONE) ? none : (NotificationConfig.userRecording == NotificationConfig.VALUE_TEXTONLY) ? textOnly : (NotificationConfig.userRecording == NotificationConfig.VALUE_SOUNDONLY) ? soundOnly : textAndSound);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.notifications"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
	
}
