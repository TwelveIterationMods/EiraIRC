// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui;

import java.net.URI;
import java.net.URL;

import org.lwjgl.input.Keyboard;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiTwitch extends GuiScreen {

	private GuiScreen parentScreen;
	private ServerConfig config;
	private GuiTextField txtUsername;
	private GuiTextField txtPassword;
	private GuiButton btnOAuthHelp;
	private GuiButton btnConnectOnStartup;
	private GuiButton btnBack;
	
	public GuiTwitch(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
		config = ConfigurationHandler.getServerConfig(Globals.TWITCH_SERVER);
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		
		txtUsername = new GuiTextField(fontRenderer, width / 2 - 90, height / 2 - 80, 180, 15);
		txtUsername.setMaxStringLength(Integer.MAX_VALUE);
		txtPassword = new GuiPasswordTextField(fontRenderer, width / 2 - 90, height / 2 - 40, 180, 15);
		txtPassword.setMaxStringLength(Integer.MAX_VALUE);
		
		btnOAuthHelp = new GuiButton(1, width / 2 + 94, height / 2 - 42, 20, 20, "?");
		buttonList.add(btnOAuthHelp);
		
		btnConnectOnStartup = new GuiButton(0, width / 2 - 100, height / 2 - 15, "");
		buttonList.add(btnConnectOnStartup);
		
		btnBack = new GuiButton(2, width / 2 - 100, height / 2 + 30, Utils.getLocalizedMessage("irc.gui.back"));
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
		updateButtonText();
	}
	
	public void updateButtonText() {
		btnConnectOnStartup.displayString = Utils.getLocalizedMessage("irc.gui.config.connectStartup", Utils.getLocalizedMessage(config.isAutoConnect() ? "irc.gui.yes" : "irc.gui.no"));
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			if(config.isAutoConnect() || EiraIRC.instance.isConnectedTo(Globals.TWITCH_SERVER)) {
				IRCConnection connection = EiraIRC.instance.getConnection(Globals.TWITCH_SERVER);
				if(connection != null) {
					connection.disconnect(Utils.getQuitMessage(connection));
				}
				if(config.getNick() != null && !config.getNick().isEmpty() && config.getServerPassword() != null && !config.getServerPassword().isEmpty()) {
					Utils.connectTo(config);
					ConfigurationHandler.addServerConfig(config);
					ConfigurationHandler.save();
				} else {
					ConfigurationHandler.removeServerConfig(config.getHost());
					ConfigurationHandler.save();
				}
			}
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
		} else if(button == btnConnectOnStartup) {
			config.setAutoConnect(!config.isAutoConnect());
			updateButtonText();
		} else if(button == btnOAuthHelp) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiConfirmOpenLink(this, Globals.TWITCH_OAUTH, 0, false));
		}
	}
	
	@Override
	public void confirmClicked(boolean yup, int id) {
		if(yup) {
			Utils.openWebpage(Globals.TWITCH_OAUTH);
		}
		Minecraft.getMinecraft().displayGuiScreen(this);
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
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.twitch"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.twitch.username"), width / 2, height / 2 - 95, Globals.TEXT_COLOR);
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.twitch.oauth"), width / 2, height / 2 - 55, Globals.TEXT_COLOR);
		txtUsername.drawTextBox();
		txtPassword.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}
