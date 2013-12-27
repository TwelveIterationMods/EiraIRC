// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import blay09.mods.eirairc.config.DisplayConfig;
import blay09.mods.eirairc.config.DisplayFormatConfig;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.ScreenshotConfig;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class GuiBotSettings extends GuiScreen {

	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;
	
	private GuiButton btnProfile;
	private GuiButton btnDeathMessages;
	private GuiButton btnMCJoinLeave;
	private GuiButton btnIRCJoinLeave;
	private GuiButton btnNickChanges;
	private GuiButton btnInterOp;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		int leftX = width /  2 - 152;
		int rightX = width / 2 + 2;
		
		btnProfile = new GuiButton(1, leftX, height / 2 - 90, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		btnProfile.enabled = false;
		buttonList.add(btnProfile);
		
		btnDeathMessages = new GuiButton(2, leftX, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnDeathMessages);
		
		btnMCJoinLeave = new GuiButton(3, leftX, height / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnMCJoinLeave);
		
		btnNickChanges = new GuiButton(4, rightX, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnNickChanges);
		
		btnIRCJoinLeave = new GuiButton(5, rightX, height / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnIRCJoinLeave);
		
		btnInterOp = new GuiButton(6, leftX, height / 2 - 15, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		btnInterOp.enabled = false;
		buttonList.add(btnInterOp);
		
		btnBack = new GuiButton(0, width / 2 - 100, height / 2 + 90, 200, 20, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		updateButtonText();
	}
	
	public void updateButtonText() {
		btnProfile.displayString = Utils.getLocalizedMessage("irc.gui.botSettings.profile", DisplayConfig.botProfile);
		btnDeathMessages.displayString = Utils.getLocalizedMessage("irc.gui.config.relayDeathMessages", Utils.getLocalizedMessage((DisplayConfig.relayDeathMessages ? "irc.gui.yes" : "irc.gui.no")));
		btnMCJoinLeave.displayString = Utils.getLocalizedMessage("irc.gui.config.relayMinecraftJoins", Utils.getLocalizedMessage((DisplayConfig.relayMinecraftJoinLeave ? "irc.gui.yes" : "irc.gui.no")));
		btnIRCJoinLeave.displayString = Utils.getLocalizedMessage("irc.gui.config.relayIRCJoins", Utils.getLocalizedMessage((DisplayConfig.relayIRCJoinLeave ? "irc.gui.yes" : "irc.gui.no")));
		btnNickChanges.displayString = Utils.getLocalizedMessage("irc.gui.config.relayNickChanges", Utils.getLocalizedMessage((DisplayConfig.relayNickChanges ? "irc.gui.yes" : "irc.gui.no")));
		btnInterOp.displayString = Utils.getLocalizedMessage("irc.gui.botSettings.interOp", Utils.getLocalizedMessage((GlobalConfig.interOp ? "irc.gui.yes" : "irc.gui.no")));
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
			return;
		} else if(button == btnDeathMessages) {
			DisplayConfig.relayDeathMessages = !DisplayConfig.relayDeathMessages;
		} else if(button == btnMCJoinLeave) {
			DisplayConfig.relayMinecraftJoinLeave = !DisplayConfig.relayMinecraftJoinLeave;
		} else if(button == btnIRCJoinLeave) {
			DisplayConfig.relayIRCJoinLeave = !DisplayConfig.relayIRCJoinLeave;
		} else if(button == btnNickChanges) {
			DisplayConfig.relayNickChanges = !DisplayConfig.relayNickChanges;
		}
		updateButtonText();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		this.drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.botSettings"), width / 2, height / 2 - 110, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
}
