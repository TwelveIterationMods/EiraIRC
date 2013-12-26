// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.config.DisplayConfig;
import blay09.mods.eirairc.config.DisplayFormatConfig;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.util.Globals;

public class GuiGlobalSettings extends GuiScreen {
	
	private static final int BUTTON_WIDTH = 190;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 5;
	
	private GuiTextField txtNick;
	private GuiButton btnLinkFilter;
	private GuiButton btnPersistentConnections;
	private GuiButton btnPrivateMessages;
	private GuiButton btnSaveCredentials;
	private GuiButton btnBack;
	
	private Iterator<DisplayFormatConfig> displayFormatIterator;
	
	@Override
	public void initGui() {
		int leftX = width / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = width / 2 + BUTTON_GAP;
		
		txtNick = new GuiTextField(fontRenderer, width / 2 - 50, height / 2 - 85, 100, 15);
		
		btnPersistentConnections = new GuiButton(1, leftX, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "Persistent Connections: ???");
		buttonList.add(btnPersistentConnections);
		
		btnLinkFilter = new GuiButton(2, leftX, height / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "Filter Links: ???");
		buttonList.add(btnLinkFilter);
		
		btnPrivateMessages = new GuiButton(3, rightX, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "Allow Private Messages: ???");
		buttonList.add(btnPrivateMessages);
		
		btnSaveCredentials = new GuiButton(4, rightX, height / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "Save Credentials: ???");
		buttonList.add(btnSaveCredentials);
		
		btnBack = new GuiButton(0, width / 2 - 100, height / 2 + 65, "Back");
		buttonList.add(btnBack);
		
		loadFromConfig();
	}
	
	public void loadFromConfig() {
		txtNick.setText(GlobalConfig.nick);
		btnPrivateMessages.displayString = "Allow Private Messages: " + (GlobalConfig.allowPrivateMessages ? "Yes" : "No");
		btnLinkFilter.displayString = "Filter Links: " + (GlobalConfig.enableLinkFilter ? "Yes" : "No");
		btnPersistentConnections.displayString = "Persistent Connections: " + (GlobalConfig.persistentConnection ? "Yes" : "No");
		btnSaveCredentials.displayString = "Save Credentials: " + (GlobalConfig.saveCredentials ? "Yes" : "No");
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnLinkFilter) {
			GlobalConfig.enableLinkFilter = !GlobalConfig.enableLinkFilter;
			btnLinkFilter.displayString = "Filter Links: " + (GlobalConfig.enableLinkFilter ? "Yes" : "No");
		} else if(button == btnPersistentConnections) {
			GlobalConfig.persistentConnection = !GlobalConfig.persistentConnection;
			btnPersistentConnections.displayString = "Persistent Connections: " + (GlobalConfig.persistentConnection ? "Yes" : "No");
		} else if(button == btnSaveCredentials) {
			GlobalConfig.saveCredentials = !GlobalConfig.saveCredentials;
			btnSaveCredentials.displayString = "Save Credentials: " + (GlobalConfig.saveCredentials ? "Yes" : "No");
		} else if(button == btnBack) {
			ConfigurationHandler.save();
			Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
		} else if(button == btnPrivateMessages) {
			GlobalConfig.allowPrivateMessages = !GlobalConfig.allowPrivateMessages;
			btnPrivateMessages.displayString = "Allow Private Messages: " + (GlobalConfig.allowPrivateMessages ? "Yes" : "No");
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
