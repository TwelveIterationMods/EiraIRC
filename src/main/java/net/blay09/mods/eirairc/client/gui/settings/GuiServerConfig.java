// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.GuiToggleButton;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

public class GuiServerConfig extends GuiScreen {

	private ServerConfig config;
	private GuiButton btnChannels;
	private GuiButton btnCancel;
	private GuiButton btnSave;
	private GuiToggleButton btnAutoConnect;
	private GuiTextField txtHost;
	private GuiTextField txtNick;
	private GuiAdvancedTextField txtIdent;
	private GuiAdvancedTextField txtDescription;
	private GuiTextField txtNickServName;
	private GuiAdvancedTextField txtNickServPassword;
	private GuiAdvancedTextField txtServerPassword;
	
	public GuiServerConfig() {}
	
	public GuiServerConfig(ServerConfig config) {
		this.config = config;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		txtHost = new GuiTextField(fontRendererObj, width / 2 - 120, height / 2 - 85, 100, 15);
		txtNick = new GuiTextField(fontRendererObj, width / 2 - 120, height / 2 - 45, 100, 15);
		txtServerPassword = new GuiAdvancedTextField(fontRendererObj, width / 2 + 5, height / 2 - 85, 100, 15);
		txtServerPassword.setMaxStringLength(Integer.MAX_VALUE);
		txtServerPassword.setDefaultPasswordChar();
		txtIdent = new GuiAdvancedTextField(fontRendererObj, width / 2 + 5, height / 2 - 45, 100, 15);
		txtIdent.setDefaultText(Globals.DEFAULT_IDENT, false);
		txtDescription = new GuiAdvancedTextField(fontRendererObj, width / 2 - 120, height / 2 - 5, 100, 15);
		txtDescription.setDefaultText(Globals.DEFAULT_DESCRIPTION, false);
		txtNickServName = new GuiTextField(fontRendererObj, width / 2 - 120, height / 2 + 35, 100, 15);
		txtNickServName.setMaxStringLength(Integer.MAX_VALUE);
		txtNickServPassword = new GuiAdvancedTextField(fontRendererObj, width / 2 - 120, height / 2 + 75, 100, 15);
		txtNickServPassword.setMaxStringLength(Integer.MAX_VALUE);
		txtNickServPassword.setDefaultPasswordChar();
		
		btnAutoConnect = new GuiToggleButton(3, width / 2 - 10, height / 2 + 25, 130, 20, "irc.gui.config.connectStartup");
		buttonList.add(btnAutoConnect);
		
		btnChannels = new GuiButton(4, width / 2 - 10, height / 2 + 50, 130, 20, Utils.getLocalizedMessage("irc.gui.serverList.channels"));
		buttonList.add(btnChannels);
		
		btnSave = new GuiButton(1, width / 2 + 3, height / 2 + 95, 100, 20, Utils.getLocalizedMessage("irc.gui.save"));
		buttonList.add(btnSave);
		
		btnCancel = new GuiButton(0, width / 2 - 103, height / 2 + 95, 100, 20, Utils.getLocalizedMessage("irc.gui.cancel"));
		buttonList.add(btnCancel);
		
		loadFromConfig();
	}
	
	public void setBotProfile(String profileName) {
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
		drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.editServer"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.editServer.serverAddress"), width / 2 - 125, height / 2 - 100, Globals.TEXT_COLOR);
		txtHost.drawTextBox();
		
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.editServer.serverPassword"), width / 2, height / 2 - 100, Globals.TEXT_COLOR);
		txtServerPassword.drawTextBox();
		
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.editServer.nick"), width / 2 - 125, height / 2 - 60, Globals.TEXT_COLOR);
		txtNick.drawTextBox();
		
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.editServer.ident"), width / 2, height / 2 - 60, Globals.TEXT_COLOR);
		txtIdent.drawTextBox();
		
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.editServer.description"), width / 2 - 125, height / 2 - 20, Globals.TEXT_COLOR);
		txtDescription.drawTextBox();
		
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.editServer.nickServName"), width / 2 - 125, height / 2 + 20, Globals.TEXT_COLOR);
		txtNickServName.drawTextBox();
		
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.editServer.nickServPassword"), width / 2 - 125, height / 2 + 60, Globals.TEXT_COLOR);
		txtNickServPassword.drawTextBox();
		
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.config.profile"), width / 2 - 10, height / 2 - 15, Globals.TEXT_COLOR);
		
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
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnSave) {
			saveToConfig();
			if(btnAutoConnect.getState() && !EiraIRC.instance.getConnectionManager().isConnectedTo(config.getAddress())) {
				Utils.connectTo(config);
			}
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerList());
		} else if(button == btnCancel) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerList());
		} else if(button == btnChannels) {
			saveToConfig();
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(this, config));
		}
	}
	
	public void loadFromConfig() {
		if(config != null) {
			txtHost.setText(config.getAddress());
			txtNick.setText(config.getNick());
			txtServerPassword.setText(config.getServerPassword());
			txtNickServName.setText(config.getNickServName());
			txtNickServPassword.setText(config.getNickServPassword());
		} else {
			btnAutoConnect.setState(true);
		}
		updateButtons();
	}
	
	public void saveToConfig() {
		if(config == null || !config.getAddress().equals(txtHost.getText())) {
			if(config != null) {
				ConfigurationHandler.removeServerConfig(config.getAddress());
			}
			config = ConfigurationHandler.getServerConfig(txtHost.getText());
		}
		config.setNick(txtNick.getText());
		config.setNickServ(txtNickServName.getText(), txtNickServPassword.getText());
		config.setServerPassword(txtServerPassword.getText());
		IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(config.getAddress());
		if(connection != null) {
			IRCBotImpl bot = (IRCBotImpl) connection.getBot();
			bot.updateProfiles();
		}
		ConfigurationHandler.addServerConfig(config);
		ConfigurationHandler.save();
	}
}
