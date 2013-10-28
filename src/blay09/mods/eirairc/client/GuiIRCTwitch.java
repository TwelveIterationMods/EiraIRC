// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.Utils;
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
	private GuiButton btnBack;
	
	public GuiIRCTwitch(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
		config = ConfigurationHandler.getServerConfig(Globals.TWITCH_SERVER);
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		
		txtUsername = new GuiTextField(fontRenderer, width / 2 - 50, height / 2 - 80, 100, 15);
		txtUsername.setMaxStringLength(Integer.MAX_VALUE);
		txtPassword = new GuiPasswordTextField(fontRenderer, width / 2 - 50, height / 2 - 40, 100, 15);
		txtPassword.setMaxStringLength(Integer.MAX_VALUE);
		
		btnConnectOnStartup = new GuiButton(0, width / 2 - 100, height / 2 - 15, "Connect on Startup: ???");
		buttonList.add(btnConnectOnStartup);
		
		btnBack = new GuiButton(2, width / 2 - 100, height / 2 + 30, "Back");
		buttonList.add(btnBack);
		
		loadFromConfig();
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	public void loadFromConfig() {
		txtUsername.setText(config.getNick() != null ? config.getNick() : "");
		txtPassword.setText(config.getServerPassword() != null ? config.getServerPassword() : "");
		btnConnectOnStartup.displayString = "Connect on Startup: " + (config.isAutoConnect() ? "Yes" : "No");
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			ConfigurationHandler.save();
			if(config.isAutoConnect() || EiraIRC.instance.isConnectedTo(Globals.TWITCH_SERVER)) {
				IRCConnection connection = EiraIRC.instance.getConnection(Globals.TWITCH_SERVER);
				if(connection != null) {
					connection.disconnect(Utils.getQuitMessage(connection));
				}
				if(!config.getNick().isEmpty() && !config.getServerPassword().isEmpty()) {
					Utils.connectTo(config);
				}
			}
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
		} else if(button == btnConnectOnStartup) {
			config.setAutoConnect(!config.isAutoConnect());
			btnConnectOnStartup.displayString = "Connect on Startup: " + (config.isAutoConnect() ? "Yes" : "No");
		}
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtUsername.textboxKeyTyped(unicode, keyCode)) {
			config.setNick(txtUsername.getText());
			return;
		}
		if(txtPassword.textboxKeyTyped(unicode, keyCode)) {
			config.setServerPassword(txtPassword.getText());
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
		drawBackground(0);
		drawCenteredString(fontRenderer, "EiraIRC - Twitch Settings", width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		drawCenteredString(fontRenderer, "Twitch Username:", width / 2, height / 2 - 95, Globals.TEXT_COLOR);
		drawCenteredString(fontRenderer, "OAuth Token:", width / 2, height / 2 - 55, Globals.TEXT_COLOR);
		txtUsername.drawTextBox();
		txtPassword.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}
