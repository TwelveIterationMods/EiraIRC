// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.client.gui.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.GuiToggleButton;
import net.blay09.mods.eirairc.config.BotProfileImpl;
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
	private GuiButton btnProfilePrev;
	private GuiButton btnProfile;
	private GuiButton btnProfileNext;
	private GuiToggleButton btnAutoJoin;
	private GuiToggleButton btnAutoWho;
	
	private GuiTextField txtName;
	private GuiAdvancedTextField txtChannelPassword;
	
	private List<BotProfileImpl> profileList;
	private int currentProfileIdx;
	private String currentProfile;
	
	public GuiChannelConfig(GuiScreen listParentScreen, ServerConfig serverConfig) {
		this.listParentScreen = listParentScreen;
		this.serverConfig = serverConfig;
		profileList = ConfigurationHandler.getBotProfiles();
	}
	
	public GuiChannelConfig(GuiScreen listParentScreen, ChannelConfig config) {
		this.listParentScreen = listParentScreen;
		this.config = config;
		serverConfig = config.getServerConfig();
		profileList = ConfigurationHandler.getBotProfiles();
	}
	
	@Override
	public void initGui() {
		int leftX = width / 2 - 146;
		int rightX = width / 2 + 3;
		int topY = height / 2 - 85;
		
		Keyboard.enableRepeatEvents(true);
		txtName = new GuiTextField(fontRenderer, leftX, topY, 140, 15);
		txtChannelPassword = new GuiAdvancedTextField(fontRenderer, rightX, topY, 140, 15);
		txtChannelPassword.setDefaultPasswordChar();
		
		btnAutoJoin = new GuiToggleButton(2, rightX, topY + 25, BUTTON_WIDTH, BUTTON_HEIGHT, "irc.gui.config.joinStartup");
		buttonList.add(btnAutoJoin);
		
		btnAutoWho = new GuiToggleButton(3, rightX, topY + 50, BUTTON_WIDTH, BUTTON_HEIGHT, "irc.gui.config.autoWho");
		buttonList.add(btnAutoWho);
		
		btnProfilePrev = new GuiButton(4, leftX, topY + 25, 18, 20, "<");
		buttonList.add(btnProfilePrev);
		
		btnProfile = new GuiButton(5, leftX + 20, topY + 25, 100, 20, "");
		buttonList.add(btnProfile);
		
		btnProfileNext = new GuiButton(6, leftX + 122, topY + 25, 18, 20, ">");
		buttonList.add(btnProfileNext);
		
		btnSave = new GuiButton(1, rightX, topY + 160, 100, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.save"));
		buttonList.add(btnSave);
		
		btnCancel = new GuiButton(0, leftX + 40, topY + 160, 100, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.cancel"));
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
		super.updateScreen();
		txtName.updateCursorCounter();
		txtChannelPassword.updateCursorCounter();
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
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.editChannel"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		fontRenderer.drawString(Utils.getLocalizedMessage("irc.gui.editChannel.name"), width / 2 - 146, height / 2 - 100, Globals.TEXT_COLOR);
		txtName.drawTextBox();
		fontRenderer.drawString(Utils.getLocalizedMessage("irc.gui.editChannel.password"), width / 2 + 6, height / 2 - 100, Globals.TEXT_COLOR);
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
		btnProfile.displayString = currentProfile;
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnSave) {
			saveToConfig();
			if(btnAutoJoin.getState() && EiraIRC.instance.isConnectedTo(serverConfig.getHost())) {
				EiraIRC.instance.getConnection(serverConfig.getHost()).join(config.getName(), config.getPassword());
			}
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(listParentScreen, serverConfig));
		} else if(button == btnCancel) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(listParentScreen, serverConfig));
		} else if(button == btnProfilePrev) {
			nextProfile(-1);
		} else if(button == btnProfileNext) {
			nextProfile(1);
		} else if(button == btnProfile) {
			saveToConfig();
			Minecraft.getMinecraft().displayGuiScreen(new GuiBotProfiles(this, txtName.getText().isEmpty() ? config.getName() : txtName.getText(), ConfigurationHandler.getBotProfile(currentProfile)));
		}
	}
	
	public void loadFromConfig() {
		if(config != null) {
			txtName.setText(config.getName());
			txtChannelPassword.setText(config.getPassword() != null ? config.getPassword() : "");
			btnAutoJoin.setState(config.isAutoJoin());
			btnAutoWho.setState(config.isAutoWho());
			currentProfile = config.getBotProfile();
		} else {
			btnAutoJoin.setState(true);
			btnAutoWho.setState(false);
			currentProfile = serverConfig.getBotProfile();
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
		if(config == null || !config.getName().equals(txtName.getText())) {
			if(config != null) {
				serverConfig.removeChannelConfig(config.getName());
			}
			config = serverConfig.getChannelConfig(txtName.getText());
		}
		config.setPassword(txtChannelPassword.getText());
		config.setAutoJoin(btnAutoJoin.getState());
		config.setAutoWho(btnAutoWho.getState());
		config.setBotProfile(currentProfile);
		IRCConnection connection = EiraIRC.instance.getConnection(serverConfig.getHost());
		if(connection != null) {
			IRCBotImpl bot = (IRCBotImpl) connection.getBot();
			bot.updateProfiles();
		}
		serverConfig.addChannelConfig(config);
		ConfigurationHandler.save();
	}
}
