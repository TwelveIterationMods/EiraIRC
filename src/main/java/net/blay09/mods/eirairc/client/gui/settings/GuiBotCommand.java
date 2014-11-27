// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import net.blay09.mods.eirairc.bot.BotCommandCustom;
import net.blay09.mods.eirairc.client.gui.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.GuiToggleButton;
import net.blay09.mods.eirairc.config.BotProfileImpl;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiBotCommand extends GuiScreen {

	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;

	private final GuiScreen parentScreen;
	private final BotProfileImpl botProfile;
	private final BotCommandCustom botCommand;
	
	private GuiAdvancedTextField txtCommand;
	private GuiAdvancedTextField txtMinecraftCommand;
	private GuiToggleButton btnAllowArgs;
	private GuiToggleButton btnRunAsOp;
	private GuiToggleButton btnRequireAuth;
	private GuiToggleButton btnBroadcastResult;
	private GuiButton btnDelete;
	private GuiButton btnBack;
	private GuiButton btnSave;
	
	private boolean allowArgs;
	private boolean runAsOp;
	private boolean requireAuth;
	private boolean broadcastResult;
	
	public GuiBotCommand(GuiScreen parentScreen, BotProfileImpl botProfile, BotCommandCustom botCommand) {
		this.parentScreen = parentScreen;
		this.botProfile = botProfile;
		this.botCommand = botCommand;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		int leftX = width /  2 - 172;
		int rightX = width / 2 + 20;
		int topY = height / 2 - 60;
		
		txtCommand = new GuiAdvancedTextField(0, fontRendererObj, leftX, topY, 100, 15);
		txtCommand.setDefaultText("Example: whitelist", true);
		txtCommand.setMaxStringLength(Integer.MAX_VALUE);
		txtMinecraftCommand = new GuiAdvancedTextField(1, fontRendererObj, leftX, topY + 40, 150, 15);
		txtMinecraftCommand.setDefaultText("Example: whitelist add", true);
		txtMinecraftCommand.setMaxStringLength(Integer.MAX_VALUE);
		
		btnAllowArgs = new GuiToggleButton(2, rightX, topY, BUTTON_WIDTH, BUTTON_HEIGHT, "irc.gui.botCommand.allowArgs");
		buttonList.add(btnAllowArgs);
		
		btnRunAsOp = new GuiToggleButton(3, rightX, topY + 25, BUTTON_WIDTH, BUTTON_HEIGHT, "irc.gui.botCommand.runAsOp");
		buttonList.add(btnRunAsOp);
		
		btnRequireAuth = new GuiToggleButton(4, rightX, topY + 50, BUTTON_WIDTH, BUTTON_HEIGHT, "irc.gui.botCommand.requireAuth");
		buttonList.add(btnRequireAuth);
		
		btnBroadcastResult = new GuiToggleButton(5, rightX, topY + 75, BUTTON_WIDTH, BUTTON_HEIGHT, "irc.gui.botCommand.broadcastResult");
		buttonList.add(btnBroadcastResult);
		
		btnDelete = new GuiButton(6, leftX, topY + 75, 50, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.delete"));
//		btnDelete.packedFGColour = -65536;
		btnDelete.packedFGColour = -1048576;
		buttonList.add(btnDelete);
		
		btnBack = new GuiButton(0, width / 2 - 102, topY + 140, 100, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		btnSave = new GuiButton(1, width / 2 + 2, topY + 140, 100, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.save"));
		buttonList.add(btnSave);

		loadFromProfile();
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	private void loadFromProfile() {
		txtCommand.setText(botCommand.getCommandName());
		txtMinecraftCommand.setText(botCommand.getMinecraftCommand());
		btnAllowArgs.setState(botCommand.allowsArgs());
		btnRunAsOp.setState(botCommand.isRunAsOp());
		btnRequireAuth.setState(botCommand.requiresAuth());
		btnBroadcastResult.setState(botCommand.isBroadcastResult());
		updateSaveButton();
	}
	
	private void updateSaveButton() {
		btnSave.enabled = !txtCommand.getText().isEmpty() && !txtMinecraftCommand.getText().isEmpty();
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
		} else if(button == btnSave) {
			botProfile.deleteCustomCommand(botCommand);
			botCommand.setCommandName(txtCommand.getText());
			botCommand.setMinecraftCommand(txtMinecraftCommand.getText());
			botCommand.setAllowArgs(btnAllowArgs.getState());
			botCommand.setRunAsOp(btnRunAsOp.getState());
			botCommand.setRequireAuth(btnRequireAuth.getState());
			botCommand.setBroadcastResult(btnBroadcastResult.getState());
			botProfile.addCustomCommand(botCommand);
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
		} else if(button == btnDelete) {
			botProfile.deleteCustomCommand(botCommand);
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
		} else if(button == btnAllowArgs) {
			allowArgs = !allowArgs;
		} else if(button == btnRunAsOp) {
			runAsOp = !runAsOp;
		} else if(button == btnRequireAuth) {
			requireAuth = !requireAuth;
		} else if(button == btnBroadcastResult) {
			broadcastResult = !broadcastResult;
		}
	}
	
	@Override
	public void updateScreen() {
		txtCommand.updateCursorCounter();
		txtMinecraftCommand.updateCursorCounter();
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) throws IOException {
		super.mouseClicked(par1, par2, par3);
		txtCommand.mouseClicked(par1, par2, par3);
		txtMinecraftCommand.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) throws IOException {
		super.keyTyped(unicode, keyCode);
		if(unicode != ' ' && unicode != '!' && txtCommand.textboxKeyTyped(unicode, keyCode)) {
			updateSaveButton();
			return;
		}
		if(txtMinecraftCommand.textboxKeyTyped(unicode, keyCode)) {
			updateSaveButton();
			return;
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		this.drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.botCommand"), width / 2, height / 2 - 110, Globals.TEXT_COLOR);
		drawString(fontRendererObj, "Bot Command:", width / 2 - 170, height / 2 - 75, Globals.TEXT_COLOR);
		txtCommand.drawTextBox();
		drawString(fontRendererObj, "Run Minecraft Command:", width / 2 - 170, height / 2 - 35, Globals.TEXT_COLOR);
		txtMinecraftCommand.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}
