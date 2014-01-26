// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

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
		
		txtUsername = new GuiTextField(field_146289_q, field_146294_l / 2 - 50, field_146295_m / 2 - 80, 100, 15);
		txtUsername.func_146203_f(Integer.MAX_VALUE);
		txtPassword = new GuiPasswordTextField(field_146289_q, field_146294_l / 2 - 50, field_146295_m / 2 - 40, 100, 15);
		txtPassword.func_146203_f(Integer.MAX_VALUE);
		
		btnOAuthHelp = new GuiButton(1, field_146294_l / 2 + 54, field_146295_m / 2 - 42, 20, 20, "?");
		field_146292_n.add(btnOAuthHelp);
		
		btnConnectOnStartup = new GuiButton(0, field_146294_l / 2 - 100, field_146295_m / 2 - 15, "");
		field_146292_n.add(btnConnectOnStartup);
		
		btnBack = new GuiButton(2, field_146294_l / 2 - 100, field_146295_m / 2 + 30, Utils.getLocalizedMessage("irc.gui.back"));
		field_146292_n.add(btnBack);
		
		loadFromConfig();
	}
	
	@Override
	public void func_146281_b() {
		Keyboard.enableRepeatEvents(false);
	}
	
	public void loadFromConfig() {
		txtUsername.func_146180_a(config.getNick() != null ? config.getNick() : "");
		txtPassword.func_146180_a(config.getServerPassword() != null ? config.getServerPassword() : "");
		updateButtonText();
	}
	
	public void updateButtonText() {
		btnConnectOnStartup.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.connectStartup", Utils.getLocalizedMessage(config.isAutoConnect() ? "irc.gui.yes" : "irc.gui.no"));
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
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
			Minecraft.getMinecraft().func_147108_a(parentScreen);
		} else if(button == btnConnectOnStartup) {
			config.setAutoConnect(!config.isAutoConnect());
			updateButtonText();
		} else if(button == btnOAuthHelp) {
			Minecraft.getMinecraft().func_147108_a(new GuiConfirmOpenLink(this, Globals.TWITCH_OAUTH, 0, false));
		}
	}
	
	@Override
	public void confirmClicked(boolean yup, int id) {
		if(yup) {
			Utils.openWebpage(Globals.TWITCH_OAUTH);
		}
		Minecraft.getMinecraft().func_147108_a(this);
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
		func_146270_b(0);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.twitch"), field_146294_l / 2, field_146295_m / 2 - 115, Globals.TEXT_COLOR);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.twitch.username"), field_146294_l / 2, field_146295_m / 2 - 95, Globals.TEXT_COLOR);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.twitch.oauth"), field_146294_l / 2, field_146295_m / 2 - 55, Globals.TEXT_COLOR);
		txtUsername.drawTextBox();
		txtPassword.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}
