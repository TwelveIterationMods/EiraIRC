// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.DisplayFormatConfig;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.Globals;

public class GuiIRCGlobalSettings extends GuiScreen {
	
	private static final int BUTTON_WIDTH = 190;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 5;
	
	private GuiTextField txtNick;
	private GuiButton btnDeathMessages;
	private GuiButton btnMCJoinLeave;
	private GuiButton btnIRCJoinLeave;
	private GuiButton btnPrivateMessages;
	private GuiButton btnLinkFilter;
	private GuiButton btnNickChanges;
	private GuiButton btnPersistentConnections;
	private GuiButton btnMessageDisplayMode;
	private GuiButton btnSaveCredentials;
	private GuiButton btnBack;
	
	private Iterator<DisplayFormatConfig> displayFormatIterator;
	
	@Override
	public void initGui() {
		int leftX = width / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = width / 2 + BUTTON_GAP;
		
		txtNick = new GuiTextField(fontRenderer, width / 2 - 50, height / 2 - 85, 100, 15);
		
		btnDeathMessages = new GuiButton(1, leftX, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay Death Messages: ???");
		buttonList.add(btnDeathMessages);
		
		btnMCJoinLeave = new GuiButton(2, leftX, height / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay Minecraft Joins: ???");
		buttonList.add(btnMCJoinLeave);
		
		btnIRCJoinLeave = new GuiButton(3, leftX, height / 2 - 15, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay IRC Joins: ???");
		buttonList.add(btnIRCJoinLeave);
		
		btnNickChanges = new GuiButton(4, leftX, height / 2 + 10, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay Nick Changes: ???");
		buttonList.add(btnNickChanges);
		
		btnPersistentConnections = new GuiButton(5, rightX, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "Persistent Connections: ???");
		buttonList.add(btnPersistentConnections);
		
		btnLinkFilter = new GuiButton(6, rightX, height / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "Filter Links: ???");
		buttonList.add(btnLinkFilter);
		
		btnPrivateMessages = new GuiButton(7, rightX, height / 2 - 15, BUTTON_WIDTH, BUTTON_HEIGHT, "Allow Private Messages: ???");
		buttonList.add(btnPrivateMessages);
		
		btnMessageDisplayMode = new GuiButton(8, rightX, height / 2 + 10, BUTTON_WIDTH, BUTTON_HEIGHT, "Message Display: ???");
		buttonList.add(btnMessageDisplayMode);
		
		btnSaveCredentials = new GuiButton(9, rightX, height / 2 + 35, BUTTON_WIDTH, BUTTON_HEIGHT, "Save Credentials: ???");
		buttonList.add(btnSaveCredentials);
		
		btnBack = new GuiButton(0, width / 2 - 100, height / 2 + 65, "Back");
		buttonList.add(btnBack);
		
		loadFromConfig();
	}
	
	public void loadFromConfig() {
		txtNick.setText(GlobalConfig.nick);
		btnDeathMessages.displayString = "Relay Death Messages: " + (GlobalConfig.relayDeathMessages ? "Yes" : "No");
		btnMCJoinLeave.displayString = "Relay Minecraft Joins: " + (GlobalConfig.relayMinecraftJoinLeave ? "Yes" : "No");
		btnIRCJoinLeave.displayString = "Relay IRC Joins: " + (GlobalConfig.relayIRCJoinLeave ? "Yes" : "No");
		btnPrivateMessages.displayString = "Allow Private Messages: " + (GlobalConfig.allowPrivateMessages ? "Yes" : "No");
		btnNickChanges.displayString = "Relay Nick Changes: " + (GlobalConfig.relayNickChanges ? "Yes" : "No");
		btnLinkFilter.displayString = "Filter Links: " + (GlobalConfig.enableLinkFilter ? "Yes" : "No");
		btnPersistentConnections.displayString = "Persistent Connections: " + (GlobalConfig.persistentConnection ? "Yes" : "No");
		btnSaveCredentials.displayString = "Save Credentials: " + (GlobalConfig.saveCredentials ? "Yes" : "No");
		displayFormatIterator = GlobalConfig.displayFormates.values().iterator();
		updateMessageDisplayMode();
	}
	
	public void updateMessageDisplayMode() {
		btnMessageDisplayMode.displayString = "Message Display: " + GlobalConfig.displayMode;
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnDeathMessages) {
			GlobalConfig.relayDeathMessages = !GlobalConfig.relayDeathMessages;
			btnDeathMessages.displayString = "Relay Death Messages: " + (GlobalConfig.relayDeathMessages ? "Yes" : "No");
		} else if(button == btnMCJoinLeave) {
			GlobalConfig.relayMinecraftJoinLeave = !GlobalConfig.relayMinecraftJoinLeave;
			btnMCJoinLeave.displayString = "Relay Minecraft Joins: " + (GlobalConfig.relayMinecraftJoinLeave ? "Yes" : "No");
		} else if(button == btnIRCJoinLeave) {
			GlobalConfig.relayIRCJoinLeave = !GlobalConfig.relayIRCJoinLeave;
			btnIRCJoinLeave.displayString = "Relay IRC Joins: " + (GlobalConfig.relayIRCJoinLeave ? "Yes" : "No");
		} else if(button == btnPrivateMessages) {
			GlobalConfig.allowPrivateMessages = !GlobalConfig.allowPrivateMessages;
			btnPrivateMessages.displayString = "Allow Private Messages: " + (GlobalConfig.allowPrivateMessages ? "Yes" : "No");
		} else if(button == btnNickChanges) {
			GlobalConfig.relayNickChanges = !GlobalConfig.relayNickChanges;
			btnNickChanges.displayString = "Relay Nick Changes: " + (GlobalConfig.relayNickChanges ? "Yes" : "No");
		} else if(button == btnLinkFilter) {
			GlobalConfig.enableLinkFilter = !GlobalConfig.enableLinkFilter;
			btnLinkFilter.displayString = "Filter Links: " + (GlobalConfig.enableLinkFilter ? "Yes" : "No");
		} else if(button == btnPersistentConnections) {
			GlobalConfig.persistentConnection = !GlobalConfig.persistentConnection;
			btnPersistentConnections.displayString = "Persistent Connections: " + (GlobalConfig.persistentConnection ? "Yes" : "No");
		} else if(button == btnSaveCredentials) {
			GlobalConfig.saveCredentials = !GlobalConfig.saveCredentials;
			btnSaveCredentials.displayString = "Save Credentials: " + (GlobalConfig.saveCredentials ? "Yes" : "No");
		} else if(button == btnMessageDisplayMode) {
			if(!displayFormatIterator.hasNext()) {
				displayFormatIterator = GlobalConfig.displayFormates.values().iterator();
			}
			GlobalConfig.displayMode = displayFormatIterator.next().getName();
			updateMessageDisplayMode();
		} else if(button == btnBack) {
			ConfigurationHandler.save();
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCSettings());
		}
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtNick.textboxKeyTyped(unicode, keyCode)) {
			GlobalConfig.nick = txtNick.getText();
			return;
		}
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtNick.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		txtNick.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRenderer, "EiraIRC - Global Settings", width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		drawCenteredString(fontRenderer, "Nickname:", width / 2, height / 2 - 100, Globals.TEXT_COLOR);
		txtNick.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
}
