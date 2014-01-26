// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class GuiServerConfig extends GuiScreen {

	private ServerConfig config;
	private GuiButton btnChannels;
	private GuiButton btnCancel;
	private GuiButton btnSave;
	private GuiButton btnAutoConnect;
	private GuiButton btnPrivateMessages;
	private GuiTextField txtHost;
	private GuiTextField txtNick;
	private GuiTextField txtNickServName;
	private GuiPasswordTextField txtNickServPassword;
	private GuiPasswordTextField txtServerPassword;
	
	private boolean autoConnect;
	private boolean privateMessages;
	
	public GuiServerConfig() {
	}
	
	public GuiServerConfig(ServerConfig config) {
		this.config = config;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		txtHost = new GuiTextField(field_146289_q, field_146294_l / 2 - 120, field_146295_m / 2 - 85, 100, 15);
		txtNick = new GuiTextField(field_146289_q, field_146294_l / 2 - 120, field_146295_m / 2 - 45, 100, 15);
		txtServerPassword = new GuiPasswordTextField(field_146289_q, field_146294_l / 2 + 5, field_146295_m / 2 - 85, 100, 15);
		txtNickServName = new GuiTextField(field_146289_q, field_146294_l / 2 - 120, field_146295_m / 2 - 5, 100, 15);
		txtNickServPassword = new GuiPasswordTextField(field_146289_q, field_146294_l / 2 - 120, field_146295_m / 2 + 35, 100, 15);
		
		btnPrivateMessages = new GuiButton(2, field_146294_l / 2 - 10, field_146295_m / 2 - 65, 130, 20, "");
		field_146292_n.add(btnPrivateMessages);
		
		btnAutoConnect = new GuiButton(3, field_146294_l / 2 - 10, field_146295_m / 2 - 40, 130, 20, "");
		field_146292_n.add(btnAutoConnect);
		
		btnChannels = new GuiButton(4, field_146294_l / 2 - 10, field_146295_m / 2 - 15, 130, 20, Utils.getLocalizedMessage("irc.gui.serverList.channels"));
		field_146292_n.add(btnChannels);
		
		btnSave = new GuiButton(1, field_146294_l / 2 + 3, field_146295_m / 2 + 65, 100, 20, Utils.getLocalizedMessage("irc.gui.save"));
		field_146292_n.add(btnSave);
		
		btnCancel = new GuiButton(0, field_146294_l / 2 - 103, field_146295_m / 2 + 65, 100, 20, Utils.getLocalizedMessage("irc.gui.cancel"));
		field_146292_n.add(btnCancel);
		
		loadFromConfig();
	}
	
	@Override
	public void func_146281_b() {
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void updateScreen() {
		txtHost.updateCursorCounter();
		txtNick.updateCursorCounter();
		txtServerPassword.updateCursorCounter();
		txtNickServName.updateCursorCounter();
		txtNickServPassword.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		func_146270_b(0);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.editServer"), field_146294_l / 2, field_146295_m / 2 - 115, Globals.TEXT_COLOR);
		field_146289_q.drawString(Utils.getLocalizedMessage("irc.gui.editServer.serverAddress"), field_146294_l / 2 - 125, field_146295_m / 2 - 100, Globals.TEXT_COLOR);
		txtHost.drawTextBox();
		field_146289_q.drawString(Utils.getLocalizedMessage("irc.gui.editServer.serverPassword"), field_146294_l / 2, field_146295_m / 2 - 100, Globals.TEXT_COLOR);
		txtServerPassword.drawTextBox();
		field_146289_q.drawString(Utils.getLocalizedMessage("irc.gui.editServer.nick"), field_146294_l / 2 - 125, field_146295_m / 2 - 60, Globals.TEXT_COLOR);
		txtNick.drawTextBox();
		field_146289_q.drawString(Utils.getLocalizedMessage("irc.gui.editServer.nickServName"), field_146294_l / 2 - 125, field_146295_m / 2 - 20, Globals.TEXT_COLOR);
		txtNickServName.drawTextBox();
		field_146289_q.drawString(Utils.getLocalizedMessage("irc.gui.editServer.nickServPassword"), field_146294_l / 2 - 125, field_146295_m / 2 + 20, Globals.TEXT_COLOR);
		txtNickServPassword.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtHost.textboxKeyTyped(unicode, keyCode)) {
			if(txtHost.getText().length() > 0) {
				btnSave.field_146124_l = true;
				btnChannels.field_146124_l = true;
			} else {
				btnSave.field_146124_l = false;
				btnChannels.field_146124_l = false;
			}
			return;
		}
		if(txtNick.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
		if(txtNickServName.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
		if(txtNickServPassword.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
		if(txtServerPassword.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtHost.mouseClicked(par1, par2, par3);
		txtNick.mouseClicked(par1, par2, par3);
		txtNickServName.mouseClicked(par1, par2, par3);
		txtNickServPassword.mouseClicked(par1, par2, par3);
		txtServerPassword.mouseClicked(par1, par2, par3);
	}
	
	private void updateButtons() {
		if(txtHost.getText().length() > 0) {
			btnSave.field_146124_l = true;
			btnChannels.field_146124_l = true;
		} else {
			btnSave.field_146124_l = false;
			btnChannels.field_146124_l = false;
		}
		btnPrivateMessages.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.privateMessages", Utils.getLocalizedMessage(privateMessages ? "irc.gui.yes" : "irc.gui.no"));
		btnAutoConnect.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.connectStartup", Utils.getLocalizedMessage(autoConnect ? "irc.gui.yes" : "irc.gui.no"));
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
		if(button == btnSave) {
			saveToConfig();
			if(autoConnect && !EiraIRC.instance.isConnectedTo(config.getHost())) {
				Utils.connectTo(config);
			}
			Minecraft.getMinecraft().func_147108_a(new GuiServerList());
		} else if(button == btnCancel) {
			Minecraft.getMinecraft().func_147108_a(new GuiServerList());
		} else if(button == btnPrivateMessages) {
			privateMessages = !privateMessages;
			updateButtons();
		} else if(button == btnAutoConnect) {
			autoConnect = !autoConnect;
			updateButtons();
		} else if(button == btnChannels) {
			saveToConfig();
			Minecraft.getMinecraft().func_147108_a(new GuiChannelList(this, config));
		}
	}
	
	public void loadFromConfig() {
		if(config != null) {
			txtHost.setText(config.getHost());
			txtNick.setText(config.getNick());
			txtServerPassword.setText(config.getServerPassword());
			txtNickServName.setText(config.getNickServName());
			txtNickServPassword.setText(config.getNickServPassword());
			autoConnect = config.isAutoConnect();
			privateMessages = config.allowsPrivateMessages();
		} else {
			autoConnect = true;
			privateMessages = true;
		}
		updateButtons();
	}
	
	public void saveToConfig() {
		if(config == null || !config.getHost().equals(txtHost.getText())) {
			if(config != null) {
				ConfigurationHandler.removeServerConfig(config.getHost());
			}
			config = ConfigurationHandler.getServerConfig(txtHost.getText());
		}
		config.setNick(txtNick.getText());
		config.setNickServ(txtNickServName.getText(), txtNickServPassword.getText());
		config.setServerPassword(txtServerPassword.getText());
		config.setAutoConnect(autoConnect);
		config.setAllowPrivateMessages(privateMessages);
		ConfigurationHandler.addServerConfig(config);
		ConfigurationHandler.save();
	}
}
