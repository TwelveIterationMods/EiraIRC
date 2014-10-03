// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

public class GuiChannelConfig extends GuiScreen {

	private static final int BUTTON_WIDTH = 140;
	private static final int BUTTON_HEIGHT = 20;
	
	private final ServerConfig serverConfig;
	private final GuiScreen listParentScreen;
	private ChannelConfig config;
	private GuiButton btnCancel;
	private GuiButton btnSave;

	private GuiTextField txtName;
	private GuiAdvancedTextField txtChannelPassword;
	
	public GuiChannelConfig(GuiScreen listParentScreen, ServerConfig serverConfig) {
		this.listParentScreen = listParentScreen;
		this.serverConfig = serverConfig;
	}
	
	public GuiChannelConfig(GuiScreen listParentScreen, ChannelConfig config) {
		this.listParentScreen = listParentScreen;
		this.config = config;
		serverConfig = config.getServerConfig();
	}
	
	@Override
	public void initGui() {
		int leftX = width / 2 - 146;
		int rightX = width / 2 + 3;
		int topY = height / 2 - 85;
		
		Keyboard.enableRepeatEvents(true);
		txtName = new GuiTextField(fontRendererObj, leftX, topY, 140, 15);
		txtChannelPassword = new GuiAdvancedTextField(fontRendererObj, rightX, topY, 140, 15);
		txtChannelPassword.setDefaultPasswordChar();
		
		btnSave = new GuiButton(1, rightX, topY + 160, 100, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.save"));
		buttonList.add(btnSave);
		
		btnCancel = new GuiButton(0, leftX + 40, topY + 160, 100, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.cancel"));
		buttonList.add(btnCancel);
		
		loadFromConfig();
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		txtName.updateCursorCounter();
		txtChannelPassword.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.editChannel"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.editChannel.name"), width / 2 - 146, height / 2 - 100, Globals.TEXT_COLOR);
		txtName.drawTextBox();
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.editChannel.password"), width / 2 + 6, height / 2 - 100, Globals.TEXT_COLOR);
		txtChannelPassword.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtName.textboxKeyTyped(unicode, keyCode)) {
			if(txtName.getText().length() > 0) {
				btnSave.enabled = true;
			} else {
				btnSave.enabled = false;
			}
			return;
		}
		if(txtChannelPassword.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtName.mouseClicked(par1, par2, par3);
		txtChannelPassword.mouseClicked(par1, par2, par3);
	}
	
	private void updateButtons() {
		if(txtName.getText().length() > 0) {
			btnSave.enabled = true;
		} else {
			btnSave.enabled = false;
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnSave) {
			saveToConfig();
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(listParentScreen, serverConfig));
		} else if(button == btnCancel) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(listParentScreen, serverConfig));
		}
	}
	
	public void loadFromConfig() {
		if(config != null) {
			txtName.setText(config.getName());
			txtChannelPassword.setText(config.getPassword() != null ? config.getPassword() : "");
		} else {
		}
		updateButtons();
	}
	
	public void saveToConfig() {
		if(config == null || !config.getName().equals(txtName.getText())) {
			if(config != null) {
				serverConfig.removeChannelConfig(config.getName());
			}
			config = serverConfig.getOrCreateChannelConfig(txtName.getText());
		}
		config.setPassword(txtChannelPassword.getText());
		IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getAddress());
		if(connection != null) {
			IRCBotImpl bot = (IRCBotImpl) connection.getBot();
			bot.updateProfiles();
		}
		serverConfig.addChannelConfig(config);
		ConfigurationHandler.save();
	}
}
