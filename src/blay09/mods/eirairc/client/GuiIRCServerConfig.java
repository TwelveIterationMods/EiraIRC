// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.Utils;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.Globals;
import blay09.mods.eirairc.config.ServerConfig;

public class GuiIRCServerConfig extends GuiScreen {

	private ServerConfig config;
	private GuiButton btnChannels;
	private GuiButton btnCancel;
	private GuiButton btnSave;
	private GuiButton btnAutoConnect;
	private GuiButton btnPrivateMessages;
	private GuiTextField txtHost;
	private GuiTextField txtNick;
	private GuiTextField txtNickServName;
	private GuiPasswordTextField txtNickServPassword;
	private GuiPasswordTextField txtServerPassword;
	
	private boolean autoConnect;
	private boolean saveCredentials;
	private boolean privateMessages;
	
	public GuiIRCServerConfig() {
	}
	
	public GuiIRCServerConfig(ServerConfig config) {
		this.config = config;
	}
	
	@Override
	public void initGui() {
		txtHost = new GuiTextField(fontRenderer, width / 2 - 120, height / 2 - 100, 100, 15);
		txtNick = new GuiTextField(fontRenderer, width / 2 - 120, height / 2 - 60, 100, 15);
		txtServerPassword = new GuiPasswordTextField(fontRenderer, width / 2 + 5, height / 2 - 100, 100, 15);
		txtNickServName = new GuiTextField(fontRenderer, width / 2 - 120, height / 2 - 20, 100, 15);
		txtNickServPassword = new GuiPasswordTextField(fontRenderer, width / 2 - 120, height / 2 + 20, 100, 15);
		
		btnPrivateMessages = new GuiButton(0, width / 2 - 10, height / 2 - 65, 130, 20, "Private Messages: ???");
		buttonList.add(btnPrivateMessages);
		
		btnAutoConnect = new GuiButton(0, width / 2 - 10, height / 2 - 40, 130, 20, "Connect on Startup: ???");
		buttonList.add(btnAutoConnect);
		
		btnSave = new GuiButton(0, width / 2 + 3, height / 2 + 50, 100, 20, "Save");
		buttonList.add(btnSave);
		
		btnCancel = new GuiButton(1, width / 2 - 103, height / 2 + 50, 100, 20, "Cancel");
		buttonList.add(btnCancel);
		
		if(config != null) {
			txtHost.setText(config.getHost());
			txtNick.setText(config.getNick());
			txtServerPassword.setText(config.getServerPassword());
			txtNickServName.setText(config.getNickServName());
			txtNickServPassword.setText(config.getNickServPassword());
			autoConnect = config.isAutoConnect();
			privateMessages = config.allowsPrivateMessages();
		} else {
			autoConnect = true;
			privateMessages = true;
		}
		updateButtons();
	}
	
	@Override
	public void updateScreen() {
		txtHost.updateCursorCounter();
		txtNick.updateCursorCounter();
		txtServerPassword.updateCursorCounter();
		txtNickServName.updateCursorCounter();
		txtNickServPassword.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, "EiraIRC - Edit Server", width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		fontRenderer.drawString("Server Address:", width / 2 - 125, height / 2 - 115, Globals.TEXT_COLOR);
		txtHost.drawTextBox();
		fontRenderer.drawString("Server Password:", width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		txtServerPassword.drawTextBox();
		fontRenderer.drawString("Nick:", width / 2 - 125, height / 2 - 75, Globals.TEXT_COLOR);
		txtNick.drawTextBox();
		fontRenderer.drawString("NickServ Username:", width / 2 - 125, height / 2 - 35, Globals.TEXT_COLOR);
		txtNickServName.drawTextBox();
		fontRenderer.drawString("NickServ Password:", width / 2 - 125, height / 2 + 5, Globals.TEXT_COLOR);
		txtNickServPassword.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtHost.textboxKeyTyped(unicode, keyCode)) {
			if(txtHost.getText().length() > 0) {
				btnSave.enabled = true;
			} else {
				btnSave.enabled = false;
			}
			return;
		}
		if(txtNick.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
		if(txtNickServName.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
		if(txtNickServPassword.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
		if(txtServerPassword.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtHost.mouseClicked(par1, par2, par3);
		txtNick.mouseClicked(par1, par2, par3);
		txtNickServName.mouseClicked(par1, par2, par3);
		txtNickServPassword.mouseClicked(par1, par2, par3);
		txtServerPassword.mouseClicked(par1, par2, par3);
	}
	
	private void updateButtons() {
		if(txtHost.getText().length() > 0) {
			btnSave.enabled = true;
		} else {
			btnSave.enabled = false;
		}
		btnPrivateMessages.displayString = "Private Messages: " + (privateMessages ? "Yes" : "No");
		btnAutoConnect.displayString = "Connect on Startup: " + (autoConnect ? "Yes" : "No");
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnSave) {
			if(config == null || !config.getHost().equals(txtHost.getText())) {
				config = ConfigurationHandler.getServerConfig(txtHost.getText());
			}
			config.setNick(txtNick.getText());
			config.setNickServ(txtNickServName.getText(), txtNickServPassword.getText());
			config.setServerPassword(txtServerPassword.getText());
			config.setAutoConnect(autoConnect);
			config.setAllowPrivateMessages(privateMessages);
			ConfigurationHandler.addServerConfig(config);
			ConfigurationHandler.save();
			if(autoConnect && !EiraIRC.instance.isConnectedTo(config.getHost())) {
				Utils.connectTo(config);
			}
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCServerList());
		} else if(button == btnCancel) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCServerList());
		} else if(button == btnPrivateMessages) {
			privateMessages = !privateMessages;
			updateButtons();
		} else if(button == btnAutoConnect) {
			autoConnect = !autoConnect;
			updateButtons();
		}
	}
}
