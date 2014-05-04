// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import java.util.List;

import net.blay09.mods.eirairc.client.gui.GuiAdvancedTextField;
import net.blay09.mods.eirairc.config.BotProfile;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public class GuiBotCommands extends GuiScreen {

	private static final int BUTTON_WIDTH = 170;
	private static final int BUTTON_HEIGHT = 20;

	private GuiButton btnInterOp;
	private GuiButton btnBack;
	
	private final GuiScreen parentScreen;
	private final BotProfile botProfile;
	
	public GuiBotCommands(GuiScreen parentScreen, BotProfile botProfile) {
		this.parentScreen = parentScreen;
		this.botProfile = botProfile;
	}
	
	@Override
	public void initGui() {
		int leftX = width /  2 - 172;
		int rightX = width / 2 + 2;
		int topY = height / 2 - 60;
		
		btnInterOp = new GuiButton(1, leftX, topY, BUTTON_WIDTH, BUTTON_HEIGHT, "InterOp: ???");
		buttonList.add(btnInterOp);

		
		btnBack = new GuiButton(0, width / 2 - 100, topY + 150, 200, 20, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);

	}
	
	private void updateButtonText() {
		btnInterOp.displayString = Utils.getLocalizedMessage("irc.gui.config.interOp", Utils.getLocalizedMessage((botProfile.isInterOp() ? "irc.gui.yes" : "irc.gui.no")));
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
			return;
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		this.drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.botCommands"), width / 2, height / 2 - 110, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
}
