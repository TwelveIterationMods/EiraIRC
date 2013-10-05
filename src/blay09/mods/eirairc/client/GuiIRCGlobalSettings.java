// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.Globals;

public class GuiIRCGlobalSettings extends GuiScreen {
	
	private static final int BUTTON_WIDTH = 190;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 5;
	
	private GuiTextField txtNick;
	private GuiButton btnDeathMessages;
	private GuiButton btnMCJoinLeave;
	private GuiButton btnIRCJoinLeave;
	private GuiButton btnPrivateMessages;
	private GuiButton btnLinkFilter;
	private GuiButton btnNickChanges;
	private GuiButton btnPersistentConnections;
	private GuiButton btnMessageDisplayMode;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		int leftX = width / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = width / 2 + BUTTON_GAP;
		
		txtNick = new GuiTextField(fontRenderer, width / 2 - 50, height / 2 - 90, 100, 15);
		
		btnDeathMessages = new GuiButton(1, leftX, height / 2 - 70, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay Death Messages: ???");
		buttonList.add(btnDeathMessages);
		
		btnMCJoinLeave = new GuiButton(2, leftX, height / 2 - 45, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay Minecraft Joins: ???");
		buttonList.add(btnMCJoinLeave);
		
		btnIRCJoinLeave = new GuiButton(3, leftX, height / 2 - 20, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay IRC Joins: ???");
		buttonList.add(btnIRCJoinLeave);
		
		btnNickChanges = new GuiButton(4, leftX, height / 2 + 5, BUTTON_WIDTH, BUTTON_HEIGHT, "Relay Nick Changes: ???");
		buttonList.add(btnNickChanges);
		
		btnPersistentConnections = new GuiButton(5, rightX, height / 2 - 70, BUTTON_WIDTH, BUTTON_HEIGHT, "Persistent Connections: ???");
		buttonList.add(btnPersistentConnections);
		
		btnLinkFilter = new GuiButton(6, rightX, height / 2 - 45, BUTTON_WIDTH, BUTTON_HEIGHT, "Filter Links: ???");
		buttonList.add(btnLinkFilter);
		
		btnPrivateMessages = new GuiButton(7, rightX, height / 2 - 20, BUTTON_WIDTH, BUTTON_HEIGHT, "Allow Private Messages: ???");
		buttonList.add(btnPrivateMessages);
		
		btnMessageDisplayMode = new GuiButton(8, rightX, height / 2 + 5, BUTTON_WIDTH, BUTTON_HEIGHT, "Message Display: ???");
		buttonList.add(btnMessageDisplayMode);
		
		btnBack = new GuiButton(0, width / 2 - 100, height / 2 + 35, "Back");
		buttonList.add(btnBack);
		
		loadFromConfig();
	}
	
	public void loadFromConfig() {
		txtNick.setText(GlobalConfig.nick);
		btnDeathMessages.displayString = "Relay Death Messages: " + (GlobalConfig.showDeathMessages ? "Yes" : "No");
		btnMCJoinLeave.displayString = "Relay Minecraft Joins: " + (GlobalConfig.showMinecraftJoinLeave ? "Yes" : "No");
		btnIRCJoinLeave.displayString = "Relay IRC Joins: " + (GlobalConfig.showIRCJoinLeave ? "Yes" : "No");
		btnPrivateMessages.displayString = "Allow Private Messages: " + (GlobalConfig.allowPrivateMessages ? "Yes" : "No");
		btnNickChanges.displayString = "Relay Nick Changes: " + (GlobalConfig.showNickChanges ? "Yes" : "No");
		btnLinkFilter.displayString = "Filter Links: " + (GlobalConfig.enableLinkFilter ? "Yes" : "No");
		btnPersistentConnections.displayString = "Persistent Connections: " + (GlobalConfig.persistentConnection ? "Yes" : "No");
		updateMessageDisplayMode();
	}
	
	public void updateMessageDisplayMode() {
		String msgDisplayMode = "Custom";
		String currentDM = GlobalConfig.mcChannelMsgFormat;
		if(currentDM.equals(GlobalConfig.MC_CMESSAGE_FORMAT_NORMAL)) {
			msgDisplayMode = "Default";
		} else if(currentDM.equals(GlobalConfig.MC_CMESSAGE_FORMAT_LIGHT)) {
			msgDisplayMode = "Light";
		} else if(currentDM.equals(GlobalConfig.MC_CMESSAGE_FORMAT_SLIGHT)) {
			msgDisplayMode = "S-Light";
		} else if(currentDM.equals(GlobalConfig.MC_CMESSAGE_FORMAT_DETAIL)) {
			msgDisplayMode = "Detailed";
		}
		btnMessageDisplayMode.displayString = "Message Display: " + msgDisplayMode;
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnDeathMessages) {
			GlobalConfig.showDeathMessages = !GlobalConfig.showDeathMessages;
			btnDeathMessages.displayString = "Relay Death Messages: " + (GlobalConfig.showDeathMessages ? "Yes" : "No");
		} else if(button == btnMCJoinLeave) {
			GlobalConfig.showMinecraftJoinLeave = !GlobalConfig.showMinecraftJoinLeave;
			btnMCJoinLeave.displayString = "Relay Minecraft Joins: " + (GlobalConfig.showMinecraftJoinLeave ? "Yes" : "No");
		} else if(button == btnIRCJoinLeave) {
			GlobalConfig.showIRCJoinLeave = !GlobalConfig.showIRCJoinLeave;
			btnIRCJoinLeave.displayString = "Relay IRC Joins: " + (GlobalConfig.showIRCJoinLeave ? "Yes" : "No");
		} else if(button == btnPrivateMessages) {
			GlobalConfig.allowPrivateMessages = !GlobalConfig.allowPrivateMessages;
			btnPrivateMessages.displayString = "Allow Private Messages: " + (GlobalConfig.allowPrivateMessages ? "Yes" : "No");
		} else if(button == btnNickChanges) {
			GlobalConfig.showNickChanges = !GlobalConfig.showNickChanges;
			btnNickChanges.displayString = "Relay Nick Changes: " + (GlobalConfig.showNickChanges ? "Yes" : "No");
		} else if(button == btnLinkFilter) {
			GlobalConfig.enableLinkFilter = !GlobalConfig.enableLinkFilter;
			btnLinkFilter.displayString = "Filter Links: " + (GlobalConfig.enableLinkFilter ? "Yes" : "No");
		} else if(button == btnPersistentConnections) {
			GlobalConfig.persistentConnection = !GlobalConfig.persistentConnection;
			btnPersistentConnections.displayString = "Persistent Connections: " + (GlobalConfig.persistentConnection ? "Yes" : "No");
		} else if(button == btnMessageDisplayMode) {
			String currentDM = GlobalConfig.mcChannelMsgFormat;
			if(currentDM.equals(GlobalConfig.MC_CMESSAGE_FORMAT_NORMAL)) {
				GlobalConfig.mcChannelMsgFormat = GlobalConfig.MC_CMESSAGE_FORMAT_LIGHT;
				GlobalConfig.mcPrivateMsgFormat = GlobalConfig.MC_PMESSAGE_FORMAT_LIGHT;
				GlobalConfig.mcChannelEmtFormat = GlobalConfig.MC_CEMOTE_FORMAT_NORMAL;
				GlobalConfig.mcPrivateEmtFormat = GlobalConfig.MC_PEMOTE_FORMAT_NORMAL;
			} else if(currentDM.equals(GlobalConfig.MC_CMESSAGE_FORMAT_LIGHT)) {
				GlobalConfig.mcChannelMsgFormat = GlobalConfig.MC_CMESSAGE_FORMAT_SLIGHT;
				GlobalConfig.mcPrivateMsgFormat = GlobalConfig.MC_PMESSAGE_FORMAT_SLIGHT;
				GlobalConfig.mcChannelEmtFormat = GlobalConfig.MC_CEMOTE_FORMAT_NORMAL;
				GlobalConfig.mcPrivateEmtFormat = GlobalConfig.MC_PEMOTE_FORMAT_NORMAL;
			} else if(currentDM.equals(GlobalConfig.MC_CMESSAGE_FORMAT_SLIGHT)) {
				GlobalConfig.mcChannelMsgFormat = GlobalConfig.MC_CMESSAGE_FORMAT_DETAIL;
				GlobalConfig.mcPrivateMsgFormat = GlobalConfig.MC_PMESSAGE_FORMAT_DETAIL;
				GlobalConfig.mcChannelEmtFormat = GlobalConfig.MC_CEMOTE_FORMAT_NORMAL;
				GlobalConfig.mcPrivateEmtFormat = GlobalConfig.MC_PEMOTE_FORMAT_NORMAL;
			} else if(currentDM.equals(GlobalConfig.MC_CMESSAGE_FORMAT_DETAIL)) {
				GlobalConfig.mcChannelMsgFormat = GlobalConfig.MC_CMESSAGE_FORMAT_NORMAL;
				GlobalConfig.mcPrivateMsgFormat = GlobalConfig.MC_PMESSAGE_FORMAT_NORMAL;
				GlobalConfig.mcChannelEmtFormat = GlobalConfig.MC_CEMOTE_FORMAT_NORMAL;
				GlobalConfig.mcPrivateEmtFormat = GlobalConfig.MC_PEMOTE_FORMAT_NORMAL;
			}
			updateMessageDisplayMode();
		} else if(button == btnBack) {
			ConfigurationHandler.save();
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCSettings());
		}
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtNick.textboxKeyTyped(unicode, keyCode)) {
			GlobalConfig.nick = txtNick.getText();
			return;
		}
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtNick.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		txtNick.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, "EiraIRC - Global Settings", width / 2, height / 2 - 125, Globals.TEXT_COLOR);
		drawCenteredString(fontRenderer, "Nickname:", width / 2, height / 2 - 105, Globals.TEXT_COLOR);
		txtNick.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
}
