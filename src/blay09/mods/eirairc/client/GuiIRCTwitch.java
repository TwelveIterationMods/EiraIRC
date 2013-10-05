// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.Globals;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IRCConnection;

public class GuiIRCTwitch extends GuiScreen {

	private GuiScreen parentScreen;
	private ServerConfig config;
	private GuiTextField txtUsername;
	private GuiTextField txtPassword;
	private GuiButton btnConnectOnStartup;
	private GuiButton btnSaveCredentials;
	private GuiButton btnBack;
	
	public GuiIRCTwitch(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
		config = ConfigurationHandler.getServerConfig(Globals.TWITCH_SERVER);
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		
		txtUsername = new GuiTextField(fontRenderer, width / 2 - 50, height / 2 - 85, 100, 15);
		txtUsername.setMaxStringLength(Integer.MAX_VALUE);
		txtPassword = new GuiPasswordTextField(fontRenderer, width / 2 - 50, height / 2 - 45, 100, 15);
		txtPassword.setMaxStringLength(Integer.MAX_VALUE);
		
		btnConnectOnStartup = new GuiButton(0, width / 2 - 100, height / 2 - 20, "Connect on Startup: ???");
		buttonList.add(btnConnectOnStartup);
		
		btnSaveCredentials = new GuiButton(0, width / 2 - 100, height / 2 + 5, "Save Credentials: ???");
		buttonList.add(btnSaveCredentials);
		
		btnBack = new GuiButton(2, width / 2 - 100, height / 2 + 35, "Back");
		buttonList.add(btnBack);
		
		loadFromConfig();
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	public void loadFromConfig() {
		txtUsername.setText(config.nick);
		txtPassword.setText(config.serverPassword);
		btnConnectOnStartup.displayString = "Connect on Startup: " + (config.autoConnect ? "Yes" : "No");
		btnSaveCredentials.displayString = "Save Credentials: " + (config.saveCredentials ? "Yes" : "No");
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			ConfigurationHandler.save();
			if(config.autoConnect || EiraIRC.instance.isConnectedTo(Globals.TWITCH_SERVER)) {
				IRCConnection connection = EiraIRC.instance.getConnection(Globals.TWITCH_SERVER);
				if(connection != null) {
					connection.disconnect();
					EiraIRC.instance.removeConnection(connection);
				}
				connection = new IRCConnection(Globals.TWITCH_SERVER, true);
				if(connection.connect()) {
					EiraIRC.instance.addConnection(connection);
				}
			}
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
		} else if(button == btnConnectOnStartup) {
			config.autoConnect = !config.autoConnect;
			btnConnectOnStartup.displayString = "Connect on Startup: " + (config.autoConnect ? "Yes" : "No");
		} else if(button == btnSaveCredentials) {
			config.saveCredentials = !config.saveCredentials;
			btnSaveCredentials.displayString = "Save Credentials: " + (config.saveCredentials ? "Yes" : "No");
		}
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtUsername.textboxKeyTyped(unicode, keyCode)) {
			config.nick = txtUsername.getText();
			return;
		}
		if(txtPassword.textboxKeyTyped(unicode, keyCode)) {
			config.serverPassword = txtPassword.getText();
			return;
		}
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtUsername.mouseClicked(par1, par2, par3);
		txtPassword.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		txtUsername.updateCursorCounter();
		txtPassword.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, "EiraIRC - Twitch Settings", width / 2, height / 2 - 120, Globals.TEXT_COLOR);
		drawCenteredString(fontRenderer, "Twitch Username:", width / 2, height / 2 - 100, Globals.TEXT_COLOR);
		drawCenteredString(fontRenderer, "OAuth Token:", width / 2, height / 2 - 60, Globals.TEXT_COLOR);
		txtUsername.drawTextBox();
		txtPassword.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}
