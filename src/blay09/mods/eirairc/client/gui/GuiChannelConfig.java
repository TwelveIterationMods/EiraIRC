// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.util.Globals;

public class GuiChannelConfig extends GuiScreen {

	private static final int BUTTON_WIDTH = 140;
	private static final int BUTTON_HEIGHT = 20;
	
	private final ServerConfig serverConfig;
	private final GuiScreen listParentScreen;
	private ChannelConfig config;
	private GuiButton btnCancel;
	private GuiButton btnSave;
	private GuiButton btnAutoJoin;
	private GuiButton btnAutoWho;
	private GuiButton btnReadOnly;
	private GuiButton btnMuted;
	private GuiButton btnRelayMinecraftJoinLeave;
	private GuiButton btnRelayIRCJoinLeave;
	private GuiButton btnRelayDeathMessages;
	private GuiButton btnRelayNickChanges;
	
	private GuiTextField txtName;
	private GuiPasswordTextField txtChannelPassword;
	
	private boolean autoJoin;
	private boolean autoWho;
	private boolean readOnly;
	private boolean muted;
	private boolean relayMinecraftJoinLeave;
	private boolean relayIRCJoinLeave;
	private boolean relayDeathMessages;
	private boolean relayNickChanges;
	
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
		txtName = new GuiTextField(fontRenderer, width / 2 - 106, height / 2 - 85, 100, 15);
		txtChannelPassword = new GuiPasswordTextField(fontRenderer, width / 2 + 6, height / 2 - 85, 100, 15);
		
		btnAutoJoin = new GuiButton(3, width / 2 + 3, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "Join on Startup: ???");
		buttonList.add(btnAutoJoin);
		
		btnReadOnly = new GuiButton(4, width / 2 + 3, height / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "Read-Only: ???");
		buttonList.add(btnReadOnly);
		
		btnMuted = new GuiButton(5, width / 2 + 3, height / 2 - 15, BUTTON_WIDTH, BUTTON_HEIGHT, "Muted: ???");
		buttonList.add(btnMuted);
		
		btnRelayMinecraftJoinLeave = new GuiButton(6, width / 2 - BUTTON_WIDTH - 3, height / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay Minecraft Joins: ???");
		buttonList.add(btnRelayMinecraftJoinLeave);
		
		btnRelayIRCJoinLeave = new GuiButton(7, width / 2 - BUTTON_WIDTH - 3, height / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay IRC Joins: ???");
		buttonList.add(btnRelayIRCJoinLeave);

		btnRelayDeathMessages = new GuiButton(8, width / 2 - BUTTON_WIDTH - 3, height / 2 - 15, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay Death Messages: ???");
		buttonList.add(btnRelayDeathMessages);

		btnRelayNickChanges = new GuiButton(9, width / 2 - BUTTON_WIDTH - 3, height / 2 + 10, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay Nick Changes: ???");
		buttonList.add(btnRelayNickChanges);
		
		btnAutoWho = new GuiButton(10, width / 2 + 3, height / 2 + 10, BUTTON_WIDTH, BUTTON_HEIGHT, "List users on Startup: ???");
		buttonList.add(btnAutoWho);
		
		btnSave = new GuiButton(1, width / 2 + 3, height / 2 + 65, 100, 20, "Save");
		buttonList.add(btnSave);
		
		btnCancel = new GuiButton(0, width / 2 - 103, height / 2 + 65, 100, 20, "Cancel");
		buttonList.add(btnCancel);
		
		loadFromConfig();
	}
	
	@Override
	public void updateScreen() {
		txtName.updateCursorCounter();
		txtChannelPassword.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRenderer, "EiraIRC - Edit Channel", width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		fontRenderer.drawString("Channel Name:", width / 2 - 106, height / 2 - 100, Globals.TEXT_COLOR);
		txtName.drawTextBox();
		fontRenderer.drawString("Channel Password:", width / 2 + 6, height / 2 - 100, Globals.TEXT_COLOR);
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
		btnAutoJoin.displayString = "Join on Startup: " + (autoJoin ? "Yes" : "No");
		btnMuted.displayString = "Muted: " + (muted ? "Yes" : "No");
		btnReadOnly.displayString = "Read Only: " + (readOnly ? "Yes" : "No");
		btnRelayMinecraftJoinLeave.displayString = "Relay Minecraft Joins: " + (relayMinecraftJoinLeave ? "Yes" : "No");
		btnRelayIRCJoinLeave.displayString = "Relay IRC Joins: " + (relayIRCJoinLeave ? "Yes" : "No");
		btnRelayDeathMessages.displayString = "Relay Death Messages: " + (relayDeathMessages ? "Yes" : "No");
		btnRelayNickChanges.displayString = "Relay Nick Changes: " + (relayNickChanges ? "Yes" : "No");
		btnAutoWho.displayString = "List users on Startup: " +  (autoWho ? "Yes" : "No");
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnSave) {
			saveToConfig();
			if(autoJoin && EiraIRC.instance.isConnectedTo(serverConfig.getHost())) {
				EiraIRC.instance.getConnection(serverConfig.getHost()).join(config.getName(), config.getPassword());
			}
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(listParentScreen, serverConfig));
		} else if(button == btnCancel) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(listParentScreen, serverConfig));
		} else if(button == btnAutoJoin) {
			autoJoin = !autoJoin;
			updateButtons();
		} else if(button == btnMuted) {
			muted = !muted;
			updateButtons();
		} else if(button == btnReadOnly) {
			readOnly = !readOnly;
			updateButtons();
		} else if(button == btnRelayMinecraftJoinLeave) {
			relayMinecraftJoinLeave = !relayMinecraftJoinLeave;
			updateButtons();
		} else if(button == btnRelayIRCJoinLeave) {
			relayIRCJoinLeave = !relayIRCJoinLeave;
			updateButtons();
		} else if(button == btnRelayDeathMessages) {
			relayDeathMessages = !relayDeathMessages;
			updateButtons();
		} else if(button == btnRelayNickChanges) {
			relayNickChanges = !relayNickChanges;
			updateButtons();
		} else if(button == btnAutoWho) {
			autoWho = !autoWho;
			updateButtons();
		}
	}
	
	public void loadFromConfig() {
		if(config != null) {
			txtName.setText(config.getName());
			txtChannelPassword.setText(config.getPassword() != null ? config.getPassword() : "");
			autoJoin = config.isAutoJoin();
			muted = config.isMuted();
			readOnly = config.isReadOnly();
			relayMinecraftJoinLeave = config.relayMinecraftJoinLeave;
			relayIRCJoinLeave = config.relayIRCJoinLeave;
			relayDeathMessages= config.relayDeathMessages;
			relayNickChanges = config.relayNickChanges;
			autoWho = config.isAutoWho();
		} else {
			autoJoin = true;
			if(serverConfig.isClientSide()) {
				relayIRCJoinLeave = true;
				relayNickChanges = true;
			} else {
				relayMinecraftJoinLeave = true;
				relayIRCJoinLeave = true;
				relayDeathMessages = true;
				relayNickChanges = true;
				autoWho = true;
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
		config.setAutoJoin(autoJoin);
		config.setAutoWho(autoWho);
		config.setMuted(muted);
		config.setReadOnly(readOnly);
		config.relayMinecraftJoinLeave = relayMinecraftJoinLeave;
		config.relayIRCJoinLeave = relayIRCJoinLeave;
		config.relayDeathMessages = relayDeathMessages;
		config.relayNickChanges = relayNickChanges;
		serverConfig.addChannelConfig(config);
		ConfigurationHandler.save();
	}
}
