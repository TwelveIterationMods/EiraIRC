package blay09.mods.irc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.irc.config.ConfigurationHandler;
import blay09.mods.irc.config.GlobalConfig;
import blay09.mods.irc.config.Globals;

public class GuiIRCGlobalSettings extends GuiScreen {
	
	private GuiTextField txtNick;
	private GuiButton btnDeathMessages;
	private GuiButton btnMCJoinLeave;
	private GuiButton btnIRCJoinLeave;
	private GuiButton btnPrivateMessages;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		txtNick = new GuiTextField(fontRenderer, width / 2 - 100 / 2, 45, 100, 15);
		
		btnDeathMessages = new GuiButton(0, width / 2 - 200 / 2, 65, "Relay Death Messages: ???");
		buttonList.add(btnDeathMessages);
		
		btnMCJoinLeave = new GuiButton(1, width / 2 - 200 / 2, 90, "Relay Minecraft Joins: ???");
		buttonList.add(btnMCJoinLeave);
		
		btnIRCJoinLeave = new GuiButton(2, width / 2 - 200 / 2, 115, "Relay IRC Joins: ???");
		buttonList.add(btnIRCJoinLeave);
		
		btnPrivateMessages = new GuiButton(3, width / 2 - 200 / 2, 140, "Allow Private Messages: ???");
		buttonList.add(btnPrivateMessages);
		
		btnBack = new GuiButton(4, width / 2 - 200 / 2, 165, "Back");
		buttonList.add(btnBack);
		
		loadFromConfig();
	}
	
	public void loadFromConfig() {
		txtNick.setText(GlobalConfig.nick);
		btnDeathMessages.displayString = "Relay Death Messages: " + (GlobalConfig.showDeathMessages ? "Yes" : "No");
		btnMCJoinLeave.displayString = "Relay Minecraft Joins: " + (GlobalConfig.showMinecraftJoinLeave ? "Yes" : "No");
		btnIRCJoinLeave.displayString = "Relay IRC Joins: " + (GlobalConfig.showIRCJoinLeave ? "Yes" : "No");
		btnPrivateMessages.displayString = "Allow Private Messages: " + (GlobalConfig.allowPrivateMessages ? "Yes" : "No");
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
		drawBackground(0);
		String caption = "EiraIRC - Global Settings";
		fontRenderer.drawString(caption, width / 2 - fontRenderer.getStringWidth(caption) / 2, 10, Globals.TEXT_COLOR);
		String nickCaption = "Nickname";
		fontRenderer.drawString(nickCaption, width / 2 - fontRenderer.getStringWidth(nickCaption) / 2, 30, Globals.TEXT_COLOR);
		txtNick.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
}
