// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.client;

import blay09.mods.irc.config.ConfigurationHandler;
import blay09.mods.irc.config.Globals;
import blay09.mods.irc.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiIRCTwitch extends GuiScreen {

	private static final int TEXT_COLOR = 16777215;
	
	private ServerConfig config;
	private GuiTextField txtUsername;
	private GuiTextField txtPassword;
	private GuiButton btnConnectOnStartup;
	private GuiButton btnSaveCredentials;
	private GuiButton btnBack;
	
	public GuiIRCTwitch() {
		config = ConfigurationHandler.getServerConfig(Globals.TWITCH_SERVER);
	}
	
	@Override
	public void initGui() {
		txtUsername = new GuiTextField(fontRenderer, width / 2 - 100 / 2, 50, 100, 15);
		txtUsername.setMaxStringLength(16);
		txtUsername.setEnableBackgroundDrawing(true);
		txtUsername.setVisible(true);
		txtUsername.setTextColor(TEXT_COLOR);
		
		txtPassword = new GuiTextField(fontRenderer, width / 2 - 100 / 2, 90, 100, 15) {
			private static final char PASSWORD_CHAR = '*';
			
			@Override
			public void drawTextBox() {
				String oldText = getText();
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < oldText.length(); i++) {
					sb.append(PASSWORD_CHAR);
				}
				setText(sb.toString());
				super.drawTextBox();
				setText(oldText);
			}
		};
		txtPassword.setMaxStringLength(32);
		txtPassword.setEnableBackgroundDrawing(true);
		txtPassword.setVisible(true);
		txtPassword.setTextColor(TEXT_COLOR);
		
		btnConnectOnStartup = new GuiButton(0, width / 2 - 200 / 2, 120, "Connect on Startup: ???");
		btnConnectOnStartup.enabled = false;
		buttonList.add(btnConnectOnStartup);
		
		btnSaveCredentials = new GuiButton(0, width / 2 - 200 / 2, 145, "Save Credentials: ???");
		buttonList.add(btnSaveCredentials);
		
		btnBack = new GuiButton(2, width / 2 - 200 / 2, 170, "Back");
		buttonList.add(btnBack);
		
		loadFromConfig();
	}
	
	public void loadFromConfig() {
		txtUsername.setText(config.nick);
		txtPassword.setText(config.serverPassword);
		btnConnectOnStartup.displayString = "Connect on Startup: " + (config.saveCredentials ? "Yes" : "No");
		btnSaveCredentials.displayString = "Save Credentials: " + (config.saveCredentials ? "Yes" : "No");
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCSettings());
		} else if(button == btnConnectOnStartup) {
			
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
		this.drawBackground(0);
		String caption = "EiraIRC - Twitch Settings";
		fontRenderer.drawString(caption, width / 2 - fontRenderer.getStringWidth(caption) / 2, 10, TEXT_COLOR);
		String usernameCaption = "Twitch Username:";
		fontRenderer.drawString(usernameCaption, width / 2 - fontRenderer.getStringWidth(usernameCaption) / 2, 35, TEXT_COLOR);
		String passwordCaption = "Twitch Password:";
		fontRenderer.drawString(passwordCaption, width / 2 - fontRenderer.getStringWidth(passwordCaption) / 2, 75, TEXT_COLOR);
		txtUsername.drawTextBox();
		txtPassword.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}
