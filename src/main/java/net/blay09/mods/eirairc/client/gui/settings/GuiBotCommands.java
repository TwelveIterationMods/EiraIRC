// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import org.lwjgl.input.Keyboard;

import net.blay09.mods.eirairc.client.gui.GuiAdvancedTextField;
import net.blay09.mods.eirairc.config.BotProfile;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiBotCommands extends GuiScreen {

	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;

	private final GuiScreen parentScreen;
	private final BotProfile botProfile;
	
	private GuiButton btnInterOp;
	private GuiAdvancedTextField txtDisabledInterOpCommands;
	private GuiAdvancedTextField txtDisabledCommands;
	private GuiButton btnBack;
	
	public GuiBotCommands(GuiScreen parentScreen, BotProfile botProfile) {
		this.parentScreen = parentScreen;
		this.botProfile = botProfile;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		int leftX = width /  2 - 172;
		int rightX = width / 2 + 2;
		int topY = height / 2 - 60;
		
		btnInterOp = new GuiButton(1, leftX, topY - 6, BUTTON_WIDTH, BUTTON_HEIGHT, "InterOp: ???");
		btnInterOp.enabled = false;
		buttonList.add(btnInterOp);

		txtDisabledInterOpCommands = new GuiAdvancedTextField(fontRendererObj, rightX, topY + 2, BUTTON_WIDTH, 16);
		txtDisabledCommands = new GuiAdvancedTextField(fontRendererObj, rightX, topY + 42, BUTTON_WIDTH, 16);
		txtDisabledCommands.setDefaultText("Example: who, players", true);
		
		btnBack = new GuiButton(0, width / 2 - 100, topY + 150, 200, 20, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);

		updateButtonText();
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	private void updateButtonText() {
		btnInterOp.displayString = Utils.getLocalizedMessage("irc.gui.config.interOp", Utils.getLocalizedMessage((botProfile.isInterOp() ? "irc.gui.yes" : "irc.gui.no")));
		txtDisabledInterOpCommands.setEnabled(botProfile.isInterOp());
		if(!botProfile.isInterOp()) {
			txtDisabledInterOpCommands.setDefaultText("InterOp is not enabled.", true);
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
			return;
		}
	}
	
	@Override
	public void updateScreen() {
		txtDisabledCommands.updateCursorCounter();
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtDisabledCommands.mouseClicked(par1, par2, par3);
		if(txtDisabledInterOpCommands.isEnabled()) {
			txtDisabledInterOpCommands.mouseClicked(par1, par2, par3);
		}
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtDisabledCommands.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
		if(txtDisabledInterOpCommands.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		this.drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.botCommands"), width / 2, height / 2 - 110, Globals.TEXT_COLOR);
		drawString(fontRendererObj, "Disabled InterOp Commands:", width / 2, height / 2 - 75, Globals.TEXT_COLOR);
		txtDisabledInterOpCommands.drawTextBox();
		drawString(fontRendererObj, "Disabled Bot Commands:", width / 2, height / 2 - 34, Globals.TEXT_COLOR);
		txtDisabledCommands.drawTextBox();
		drawString(fontRendererObj, "Custom Bot Commands:", width / 2 - 170, height / 2 - 34, Globals.TEXT_COLOR);
		drawString(fontRendererObj, "An asterisk ('*') disables all commands.", width / 2, height / 2 + 6, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
}
