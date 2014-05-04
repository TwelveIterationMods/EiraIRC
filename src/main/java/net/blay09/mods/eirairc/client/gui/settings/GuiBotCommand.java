// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.bot.BotCommandCustom;
import net.blay09.mods.eirairc.client.gui.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.GuiList;
import net.blay09.mods.eirairc.client.gui.GuiListTextEntry;
import net.blay09.mods.eirairc.config.BotProfile;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public class GuiBotCommand extends GuiScreen {

	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;

	private final GuiScreen parentScreen;
	private final BotProfile botProfile;
	private final BotCommandCustom botCommand;
	
	private GuiAdvancedTextField txtCommand;
	private GuiAdvancedTextField txtMinecraftCommand;
	private GuiButton btnAllowArgs;
	private GuiButton btnRunAsOp;
	private GuiButton btnRequireAuth;
	private GuiButton btnBroadcastResult;
	private GuiButton btnDelete;
	private GuiButton btnBack;
	
	public GuiBotCommand(GuiScreen parentScreen, BotProfile botProfile, BotCommandCustom botCommand) {
		this.parentScreen = parentScreen;
		this.botProfile = botProfile;
		this.botCommand = botCommand;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		int leftX = width /  2 - 152;
		int rightX = width / 2 + 2;
		int topY = height / 2 - 60;
		
		txtCommand = new GuiAdvancedTextField(fontRendererObj, leftX, topY, 100, 15);
		txtCommand.setDefaultText("Example: whitelist", true);
		txtMinecraftCommand = new GuiAdvancedTextField(fontRendererObj, leftX, topY + 40, 150, 15);
		txtMinecraftCommand.setDefaultText("Example: whitelist add", true);
		
		
		btnDelete = new GuiButton(1, leftX, topY + 75, 100, 20, Utils.getLocalizedMessage("irc.gui.delete"));
		buttonList.add(btnDelete);
		
		btnBack = new GuiButton(0, width / 2 - 100, topY + 150, 200, 20, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);

		loadFromProfile();
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	private void loadFromProfile() {
		txtCommand.setText(botCommand.getCommandName());
		txtMinecraftCommand.setText(botCommand.getMinecraftCommand());
//		btnAllowArgs.displayString = Utils.getLocalizedMessage("irc.gui.config.botcommand.allowArgs", Utils.getLocalizedMessage((botCommand.allowsArgs() ? "irc.gui.yes" : "irc.gui.no")));
//		btnRunAsOp.displayString = Utils.getLocalizedMessage("irc.gui.config.botcommand.runAsOp", Utils.getLocalizedMessage((botCommand.isRunAsOp() ? "irc.gui.yes" : "irc.gui.no")));
//		btnRequireAuth.displayString = Utils.getLocalizedMessage("irc.gui.config.botcommand.requireAuth", Utils.getLocalizedMessage((botCommand.requiresAuth() ? "irc.gui.yes" : "irc.gui.no")));
//		btnBroadcastResult.displayString = Utils.getLocalizedMessage("irc.gui.config.botcommand.broadcastResult", Utils.getLocalizedMessage((botCommand.isBroadcastResult() ? "irc.gui.yes" : "irc.gui.no")));
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
		txtCommand.updateCursorCounter();
		txtMinecraftCommand.updateCursorCounter();
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtCommand.mouseClicked(par1, par2, par3);
		txtMinecraftCommand.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(keyCode != Keyboard.KEY_SPACE && txtCommand.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
		if(txtMinecraftCommand.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		this.drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.botCommand"), width / 2, height / 2 - 110, Globals.TEXT_COLOR);
		drawString(fontRendererObj, "Bot Command:", width / 2 - 150, height / 2 - 75, Globals.TEXT_COLOR);
		txtCommand.drawTextBox();
		drawString(fontRendererObj, "Run Minecraft Command:", width / 2 - 150, height / 2 - 35, Globals.TEXT_COLOR);
		txtMinecraftCommand.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}
