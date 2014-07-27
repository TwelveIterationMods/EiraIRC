// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiGlobalSettings extends GuiScreen {
	
	private static final int BUTTON_WIDTH = 190;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 5;
	
	private GuiTextField txtNick;
	private GuiButton btnPersistentConnections;
	private GuiButton btnSaveCredentials;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		int leftX = width / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = width / 2 + BUTTON_GAP;
		
		txtNick = new GuiTextField(fontRenderer, width / 2 - 50, height / 2 - 85, 100, 15);
		
		btnPersistentConnections = new GuiButton(1, leftX, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnPersistentConnections);
		
		btnSaveCredentials = new GuiButton(4, rightX, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnSaveCredentials);
		
		btnBack = new GuiButton(0, width / 2 - 100, height / 2 + 65, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		loadFromConfig();
	}
	
	public void loadFromConfig() {
		txtNick.setText(GlobalConfig.nick);
		updateButtonText();
	}
	
	public void updateButtonText() {
		String yes = Utils.getLocalizedMessage("irc.gui.yes");
		String no = Utils.getLocalizedMessage("irc.gui.no");
		btnPersistentConnections.displayString = Utils.getLocalizedMessage("irc.gui.globalSettings.persistentConnection", (GlobalConfig.persistentConnection ? yes : no));
		btnSaveCredentials.displayString = Utils.getLocalizedMessage("irc.gui.globalSettings.saveCredentials", (GlobalConfig.saveCredentials ? yes : no));
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnPersistentConnections) {
			GlobalConfig.persistentConnection = !GlobalConfig.persistentConnection;
		} else if(button == btnSaveCredentials) {
			GlobalConfig.saveCredentials = !GlobalConfig.saveCredentials;
		} else if(button == btnBack) {
			ConfigurationHandler.save();
			Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
			return;
		}
		updateButtonText();
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
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.globalSettings"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.globalSettings.nickname"), width / 2, height / 2 - 100, Globals.TEXT_COLOR);
		txtNick.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
}
