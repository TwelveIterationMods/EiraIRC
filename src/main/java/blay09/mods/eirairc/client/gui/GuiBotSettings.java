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
		int leftX = field_146294_l /  2 - 152;
		int rightX = field_146294_l / 2 + 2;
		
		btnProfile = new GuiButton(1, leftX, field_146295_m / 2 - 90, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		btnProfile.field_146124_l = false;
		field_146292_n.add(btnProfile);
		
		btnDeathMessages = new GuiButton(2, leftX, field_146295_m / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnDeathMessages);
		
		btnMCJoinLeave = new GuiButton(3, leftX, field_146295_m / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnMCJoinLeave);
		
		btnNickChanges = new GuiButton(4, rightX, field_146295_m / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnNickChanges);
		
		btnIRCJoinLeave = new GuiButton(5, rightX, field_146295_m / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnIRCJoinLeave);
		
		btnInterOp = new GuiButton(6, leftX, field_146295_m / 2 - 15, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		btnInterOp.field_146124_l = false;
		field_146292_n.add(btnInterOp);
		
		btnBack = new GuiButton(0, field_146294_l / 2 - 100, field_146295_m / 2 + 90, 200, 20, Utils.getLocalizedMessage("irc.gui.back"));
		field_146292_n.add(btnBack);
		
		updateButtonText();
	}
	
	public void updateButtonText() {
		btnProfile.field_146126_j = Utils.getLocalizedMessage("irc.gui.botSettings.profile", DisplayConfig.botProfile);
		btnDeathMessages.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.relayDeathMessages", Utils.getLocalizedMessage((DisplayConfig.relayDeathMessages ? "irc.gui.yes" : "irc.gui.no")));
		btnMCJoinLeave.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.relayMinecraftJoins", Utils.getLocalizedMessage((DisplayConfig.relayMinecraftJoinLeave ? "irc.gui.yes" : "irc.gui.no")));
		btnIRCJoinLeave.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.relayIRCJoins", Utils.getLocalizedMessage((DisplayConfig.relayIRCJoinLeave ? "irc.gui.yes" : "irc.gui.no")));
		btnNickChanges.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.relayNickChanges", Utils.getLocalizedMessage((DisplayConfig.relayNickChanges ? "irc.gui.yes" : "irc.gui.no")));
		btnInterOp.field_146126_j = Utils.getLocalizedMessage("irc.gui.botSettings.interOp", Utils.getLocalizedMessage((GlobalConfig.interOp ? "irc.gui.yes" : "irc.gui.no")));
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().func_147108_a(new GuiSettings());
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
		func_146270_b(0);
		this.drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.botSettings"), field_146294_l / 2, field_146295_m / 2 - 110, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
}
