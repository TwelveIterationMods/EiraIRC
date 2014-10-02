// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.client.gui.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.GuiToggleButton;
import net.blay09.mods.eirairc.config2.base.BotProfileImpl;
import net.blay09.mods.eirairc.config2.ServerConfig;
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
	private GuiButton btnProfilePrev;
	private GuiButton btnProfile;
	private GuiButton btnProfileNext;
	private GuiTextField txtHost;
	private GuiTextField txtNick;
	private GuiAdvancedTextField txtIdent;
	private GuiAdvancedTextField txtDescription;
	private GuiTextField txtNickServName;
	private GuiAdvancedTextField txtNickServPassword;
	private GuiAdvancedTextField txtServerPassword;
	
	private List<BotProfileImpl> profileList;
	private int currentProfileIdx;
	private String currentProfile;
	
	public GuiServerConfig() {
		profileList = ConfigurationHandler.getBotProfiles();
	}
	
	public GuiServerConfig(ServerConfig config) {
		this.config = config;
		profileList = ConfigurationHandler.getBotProfiles();
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
		
		btnProfilePrev = new GuiButton(5, width / 2 - 10, height / 2, 18, 20, "<");
		buttonList.add(btnProfilePrev);
		
		btnProfile = new GuiButton(6, width / 2 + 10, height / 2, 90, 20, "");
		buttonList.add(btnProfile);
		
		btnProfileNext = new GuiButton(7, width / 2 + 102, height / 2, 18, 20, ">");
		buttonList.add(btnProfileNext);
		
		btnSave = new GuiButton(1, width / 2 + 3, height / 2 + 95, 100, 20, Utils.getLocalizedMessage("irc.gui.save"));
		buttonList.add(btnSave);
		
		btnCancel = new GuiButton(0, width / 2 - 103, height / 2 + 95, 100, 20, Utils.getLocalizedMessage("irc.gui.cancel"));
		buttonList.add(btnCancel);
		
		loadFromConfig();
	}
	
	public void setBotProfile(String profileName) {
		config.setBotProfile(profileName);
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
		btnProfile.displayString = currentProfile;
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnSave) {
			saveToConfig();
			if(btnAutoConnect.getState() && !EiraIRC.instance.isConnectedTo(config.getAddress())) {
				Utils.connectTo(config);
			}
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerList());
		} else if(button == btnCancel) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerList());
		} else if(button == btnChannels) {
			saveToConfig();
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(this, config));
		} else if(button == btnProfilePrev) {
			nextProfile(-1);
		} else if(button == btnProfileNext) {
			nextProfile(1);
		} else if(button == btnProfile) {
			saveToConfig();
			Minecraft.getMinecraft().displayGuiScreen(new GuiBotProfiles(this, txtHost.getText().isEmpty() ? config.getAddress() : txtHost.getText(), ConfigurationHandler.getBotProfile(currentProfile)));
		}
	}
	
	private void nextProfile(int dir) {
		currentProfileIdx += dir;
		if(currentProfileIdx >= profileList.size()) {
			currentProfileIdx = 0;
		} else if(currentProfileIdx < 0) {
			currentProfileIdx = profileList.size() - 1;
		}
		currentProfile = profileList.get(currentProfileIdx).getName();
		updateButtons();
	}
	
	public void loadFromConfig() {
		if(config != null) {
			txtHost.setText(config.getAddress());
			txtNick.setText(config.getNick());
			txtServerPassword.setText(config.getServerPassword());
			txtNickServName.setText(config.getNickServName());
			txtNickServPassword.setText(config.getNickServPassword());
			txtIdent.setText(config.getIdent());
			txtDescription.setText(config.getDescription());
			btnAutoConnect.setState(config.isAutoConnect());
			currentProfile = config.getBotProfile();
		} else {
			btnAutoConnect.setState(true);
			currentProfile = ConfigurationHandler.getDefaultBotProfile().getName();
		}
		for(int i = 0; i < profileList.size(); i++) {
			if(profileList.get(i).getName().equals(currentProfile)) {
				currentProfileIdx = i;
				break;
			}
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
		config.setAutoConnect(btnAutoConnect.getState());
		config.setIdent(!txtIdent.getText().isEmpty() ? txtIdent.getText() : Globals.DEFAULT_IDENT);
		config.setDescription(!txtDescription.getText().isEmpty() ? txtDescription.getText() : Globals.DEFAULT_DESCRIPTION);
		config.setBotProfile(currentProfile);
		IRCConnection connection = EiraIRC.instance.getConnection(config.getAddress());
		if(connection != null) {
			IRCBotImpl bot = (IRCBotImpl) connection.getBot();
			bot.updateProfiles();
		}
		ConfigurationHandler.addServerConfig(config);
		ConfigurationHandler.save();
	}
}
