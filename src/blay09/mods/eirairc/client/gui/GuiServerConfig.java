// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class GuiServerConfig extends GuiScreen {

	private ServerConfig config;
	private GuiButton btnChannels;
	private GuiButton btnCancel;
	private GuiButton btnSave;
	private GuiButton btnAutoConnect;
	private GuiButton btnPrivateMessages;
	private GuiTextField txtHost;
	private GuiTextField txtNick;
	private GuiDefaultTextField txtIdent;
	private GuiDefaultTextField txtDescription;
	private GuiTextField txtNickServName;
	private GuiPasswordTextField txtNickServPassword;
	private GuiPasswordTextField txtServerPassword;
	
	private boolean autoConnect;
	private boolean privateMessages;
	
	public GuiServerConfig() {
	}
	
	public GuiServerConfig(ServerConfig config) {
		this.config = config;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		txtHost = new GuiTextField(fontRenderer, width / 2 - 120, height / 2 - 85, 100, 15);
		txtNick = new GuiTextField(fontRenderer, width / 2 - 120, height / 2 - 45, 100, 15);
		txtServerPassword = new GuiPasswordTextField(fontRenderer, width / 2 + 5, height / 2 - 85, 100, 15);
		txtNickServName = new GuiTextField(fontRenderer, width / 2 - 120, height / 2 - 5, 100, 15);
		txtNickServPassword = new GuiPasswordTextField(fontRenderer, width / 2 - 120, height / 2 + 35, 100, 15);
		txtIdent = new GuiDefaultTextField(fontRenderer, width / 2 + 5, height / 2 - 45, 100, 15);
		txtIdent.setDefaultText(Globals.DEFAULT_IDENT);
		txtDescription = new GuiDefaultTextField(fontRenderer, width / 2 - 120, height / 2 - 5, 100, 15);
		txtDescription.setDefaultText(Globals.DEFAULT_DESCRIPTION);
		txtNickServName = new GuiTextField(fontRenderer, width / 2 - 120, height / 2 + 35, 100, 15);
		txtNickServPassword = new GuiPasswordTextField(fontRenderer, width / 2 - 120, height / 2 + 75, 100, 15);
		
		btnPrivateMessages = new GuiButton(2, width / 2 - 10, height / 2 - 20, 130, 20, "");
		buttonList.add(btnPrivateMessages);
		
		btnAutoConnect = new GuiButton(3, width / 2 - 10, height / 2 + 5, 130, 20, "");
		buttonList.add(btnAutoConnect);
		
		btnChannels = new GuiButton(4, width / 2 - 10, height / 2 + 30, 130, 20, Utils.getLocalizedMessage("irc.gui.serverList.channels"));
		buttonList.add(btnChannels);
		
		btnSave = new GuiButton(1, width / 2 + 3, height / 2 + 95, 100, 20, Utils.getLocalizedMessage("irc.gui.save"));
		buttonList.add(btnSave);
		
		btnCancel = new GuiButton(0, width / 2 - 103, height / 2 + 95, 100, 20, Utils.getLocalizedMessage("irc.gui.cancel"));
		buttonList.add(btnCancel);
		
		loadFromConfig();
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void updateScreen() {
		txtHost.updateCursorCounter();
		txtNick.updateCursorCounter();
		txtServerPassword.updateCursorCounter();
		txtNickServName.updateCursorCounter();
		txtNickServPassword.updateCursorCounter();
		txtIdent.updateCursorCounter();
		txtDescription.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.editServer"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		
		fontRenderer.drawString(Utils.getLocalizedMessage("irc.gui.editServer.serverAddress"), width / 2 - 125, height / 2 - 100, Globals.TEXT_COLOR);
		txtHost.drawTextBox();
		
		fontRenderer.drawString(Utils.getLocalizedMessage("irc.gui.editServer.serverPassword"), width / 2, height / 2 - 100, Globals.TEXT_COLOR);
		txtServerPassword.drawTextBox();
		
		fontRenderer.drawString(Utils.getLocalizedMessage("irc.gui.editServer.nick"), width / 2 - 125, height / 2 - 60, Globals.TEXT_COLOR);
		txtNick.drawTextBox();
		
		fontRenderer.drawString(Utils.getLocalizedMessage("irc.gui.editServer.ident"), width / 2, height / 2 - 60, Globals.TEXT_COLOR);
		txtIdent.drawTextBox();

		fontRenderer.drawString(Utils.getLocalizedMessage("irc.gui.editServer.description"), width / 2 - 125, height / 2 - 20, Globals.TEXT_COLOR);
		txtDescription.drawTextBox();
		
		fontRenderer.drawString(Utils.getLocalizedMessage("irc.gui.editServer.nickServName"), width / 2 - 125, height / 2 + 20, Globals.TEXT_COLOR);
		txtNickServName.drawTextBox();
		
		fontRenderer.drawString(Utils.getLocalizedMessage("irc.gui.editServer.nickServPassword"), width / 2 - 125, height / 2 + 60, Globals.TEXT_COLOR);
		txtNickServPassword.drawTextBox();
		
		
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtHost.textboxKeyTyped(unicode, keyCode)) {
			if(txtHost.getText().length() > 0) {
				btnSave.enabled = true;
				btnChannels.enabled = true;
			} else {
				btnSave.enabled = false;
				btnChannels.enabled = false;
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
		if(txtIdent.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
		if(txtDescription.textboxKeyTyped(unicode, keyCode)) {
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
		txtIdent.mouseClicked(par1, par2, par3);
		txtDescription.mouseClicked(par1, par2, par3);
	}
	
	private void updateButtons() {
		if(txtHost.getText().length() > 0) {
			btnSave.enabled = true;
			btnChannels.enabled = true;
		} else {
			btnSave.enabled = false;
			btnChannels.enabled = false;
		}
		btnPrivateMessages.displayString = Utils.getLocalizedMessage("irc.gui.config.privateMessages", Utils.getLocalizedMessage(privateMessages ? "irc.gui.yes" : "irc.gui.no"));
		btnAutoConnect.displayString = Utils.getLocalizedMessage("irc.gui.config.connectStartup", Utils.getLocalizedMessage(autoConnect ? "irc.gui.yes" : "irc.gui.no"));
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnSave) {
			saveToConfig();
			if(autoConnect && !EiraIRC.instance.isConnectedTo(config.getHost())) {
				Utils.connectTo(config);
			}
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerList());
		} else if(button == btnCancel) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerList());
		} else if(button == btnPrivateMessages) {
			privateMessages = !privateMessages;
			updateButtons();
		} else if(button == btnAutoConnect) {
			autoConnect = !autoConnect;
			updateButtons();
		} else if(button == btnChannels) {
			saveToConfig();
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(this, config));
		}
	}
	
	public void loadFromConfig() {
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
	
	public void saveToConfig() {
		if(config == null || !config.getHost().equals(txtHost.getText())) {
			if(config != null) {
				ConfigurationHandler.removeServerConfig(config.getHost());
			}
			config = ConfigurationHandler.getServerConfig(txtHost.getText());
		}
		config.setNick(txtNick.getText());
		config.setNickServ(txtNickServName.getText(), txtNickServPassword.getText());
		config.setServerPassword(txtServerPassword.getText());
		config.setAutoConnect(autoConnect);
		config.setAllowPrivateMessages(privateMessages);
		ConfigurationHandler.addServerConfig(config);
		ConfigurationHandler.save();
	}
}
