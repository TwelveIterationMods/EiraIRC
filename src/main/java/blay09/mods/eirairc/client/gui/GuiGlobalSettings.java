// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.config.DisplayFormatConfig;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class GuiGlobalSettings extends GuiScreen {
	
	private static final int BUTTON_WIDTH = 190;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 5;
	
	private GuiTextField txtNick;
	private GuiButton btnLinkFilter;
	private GuiButton btnPersistentConnections;
	private GuiButton btnPrivateMessages;
	private GuiButton btnSaveCredentials;
	private GuiButton btnBack;
	
	private Iterator<DisplayFormatConfig> displayFormatIterator;
	
	@Override
	public void initGui() {
		int leftX = field_146294_l / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = field_146294_l / 2 + BUTTON_GAP;
		
		txtNick = new GuiTextField(field_146289_q, field_146294_l / 2 - 50, field_146295_m / 2 - 85, 100, 15);
		
		btnPersistentConnections = new GuiButton(1, leftX, field_146295_m / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnPersistentConnections);
		
		btnLinkFilter = new GuiButton(2, leftX, field_146295_m / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnLinkFilter);
		
		btnPrivateMessages = new GuiButton(3, rightX, field_146295_m / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnPrivateMessages);
		
		btnSaveCredentials = new GuiButton(4, rightX, field_146295_m / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnSaveCredentials);
		
		btnBack = new GuiButton(0, field_146294_l / 2 - 100, field_146295_m / 2 + 65, Utils.getLocalizedMessage("irc.gui.back"));
		field_146292_n.add(btnBack);
		
		loadFromConfig();
	}
	
	public void loadFromConfig() {
		txtNick.setText(GlobalConfig.nick);
		updateButtonText();
	}
	
	public void updateButtonText() {
		String yes = Utils.getLocalizedMessage("irc.gui.yes");
		String no = Utils.getLocalizedMessage("irc.gui.no");
		btnPrivateMessages.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.privateMessages", (GlobalConfig.allowPrivateMessages ? yes : no));
		btnLinkFilter.field_146126_j = Utils.getLocalizedMessage("irc.gui.globalSettings.linkFilter", (GlobalConfig.enableLinkFilter ? yes : no));
		btnPersistentConnections.field_146126_j = Utils.getLocalizedMessage("irc.gui.globalSettings.persistentConnection", (GlobalConfig.persistentConnection ? yes : no));
		btnSaveCredentials.field_146126_j = Utils.getLocalizedMessage("irc.gui.globalSettings.saveCredentials", (GlobalConfig.saveCredentials ? yes : no));
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
		if(button == btnLinkFilter) {
			GlobalConfig.enableLinkFilter = !GlobalConfig.enableLinkFilter;
		} else if(button == btnPersistentConnections) {
			GlobalConfig.persistentConnection = !GlobalConfig.persistentConnection;
		} else if(button == btnSaveCredentials) {
			GlobalConfig.saveCredentials = !GlobalConfig.saveCredentials;
		} else if(button == btnPrivateMessages) {
			GlobalConfig.allowPrivateMessages = !GlobalConfig.allowPrivateMessages;
		} else if(button == btnBack) {
			ConfigurationHandler.save();
			Minecraft.getMinecraft().func_147108_a(new GuiSettings());
			return;
		}
		updateButtonText();
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
		func_146270_b(0);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.globalSettings"), field_146294_l / 2, field_146295_m / 2 - 115, Globals.TEXT_COLOR);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.globalSettings.nickname"), field_146294_l / 2, field_146295_m / 2 - 100, Globals.TEXT_COLOR);
		txtNick.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
}
