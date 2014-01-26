// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class GuiChannelConfig extends GuiScreen {

	private static final int BUTTON_WIDTH = 140;
	private static final int BUTTON_HEIGHT = 20;
	
	private final ServerConfig serverConfig;
	private final GuiScreen listParentScreen;
	private ChannelConfig config;
	private GuiButton btnCancel;
	private GuiButton btnSave;
	private GuiButton btnAutoJoin;
	private GuiButton btnAutoWho;
	private GuiButton btnReadOnly;
	private GuiButton btnMuted;
	private GuiButton btnRelayMinecraftJoinLeave;
	private GuiButton btnRelayIRCJoinLeave;
	private GuiButton btnRelayDeathMessages;
	private GuiButton btnRelayNickChanges;
	
	private GuiTextField txtName;
	private GuiPasswordTextField txtChannelPassword;
	
	private boolean autoJoin;
	private boolean autoWho;
	private boolean readOnly;
	private boolean muted;
	private boolean relayMinecraftJoinLeave;
	private boolean relayIRCJoinLeave;
	private boolean relayDeathMessages;
	private boolean relayNickChanges;
	
	public GuiChannelConfig(GuiScreen listParentScreen, ServerConfig serverConfig) {
		this.listParentScreen = listParentScreen;
		this.serverConfig = serverConfig;
	}
	
	public GuiChannelConfig(GuiScreen listParentScreen, ChannelConfig config) {
		this.listParentScreen = listParentScreen;
		this.config = config;
		serverConfig = config.getServerConfig();
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		txtName = new GuiTextField(field_146289_q, field_146294_l / 2 - 106, field_146295_m / 2 - 85, 100, 15);
		txtChannelPassword = new GuiPasswordTextField(field_146289_q, field_146294_l / 2 + 6, field_146295_m / 2 - 85, 100, 15);
		
		btnAutoJoin = new GuiButton(3, field_146294_l / 2 + 3, field_146295_m / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnAutoJoin);
		
		btnReadOnly = new GuiButton(4, field_146294_l / 2 + 3, field_146295_m / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnReadOnly);
		
		btnMuted = new GuiButton(5, field_146294_l / 2 + 3, field_146295_m / 2 - 15, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnMuted);
		
		btnRelayMinecraftJoinLeave = new GuiButton(6, field_146294_l / 2 - BUTTON_WIDTH - 3, field_146295_m / 2 - 64, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnRelayMinecraftJoinLeave);
		
		btnRelayIRCJoinLeave = new GuiButton(7, field_146294_l / 2 - BUTTON_WIDTH - 3, field_146295_m / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnRelayIRCJoinLeave);

		btnRelayDeathMessages = new GuiButton(8, field_146294_l / 2 - BUTTON_WIDTH - 3, field_146295_m / 2 - 15, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnRelayDeathMessages);

		btnRelayNickChanges = new GuiButton(9, field_146294_l / 2 - BUTTON_WIDTH - 3, field_146295_m / 2 + 10, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnRelayNickChanges);
		
		btnAutoWho = new GuiButton(10, field_146294_l / 2 + 3, field_146295_m / 2 + 10, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		field_146292_n.add(btnAutoWho);
		
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
		txtName.updateCursorCounter();
		txtChannelPassword.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		func_146270_b(0);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.editChannel"), field_146294_l / 2, field_146295_m / 2 - 115, Globals.TEXT_COLOR);
		field_146289_q.drawString(Utils.getLocalizedMessage("irc.gui.editChannel.name"), field_146294_l / 2 - 106, field_146295_m / 2 - 100, Globals.TEXT_COLOR);
		txtName.drawTextBox();
		field_146289_q.drawString(Utils.getLocalizedMessage("irc.gui.editChannel.password"), field_146294_l / 2 + 6, field_146295_m / 2 - 100, Globals.TEXT_COLOR);
		txtChannelPassword.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtName.textboxKeyTyped(unicode, keyCode)) {
			if(txtName.getText().length() > 0) {
				btnSave.field_146124_l = true;
			} else {
				btnSave.field_146124_l = false;
			}
			return;
		}
		if(txtChannelPassword.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtName.mouseClicked(par1, par2, par3);
		txtChannelPassword.mouseClicked(par1, par2, par3);
	}
	
	private void updateButtons() {
		if(txtName.getText().length() > 0) {
			btnSave.field_146124_l = true;
		} else {
			btnSave.field_146124_l = false;
		}
		final String yes = Utils.getLocalizedMessage("irc.gui.yes");
		final String no = Utils.getLocalizedMessage("irc.gui.no");
		btnAutoJoin.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.joinStartup", (autoJoin ? yes : no));
		btnMuted.field_146126_j = Utils.getLocalizedMessage("irc.gui.editChannel.muted", (muted ? yes : no));
		btnReadOnly.field_146126_j = Utils.getLocalizedMessage("irc.gui.editChannel.readOnly", (readOnly ? yes : no));
		btnRelayMinecraftJoinLeave.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.relayMinecraftJoins", (relayMinecraftJoinLeave ? yes : no));
		btnRelayIRCJoinLeave.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.relayIRCJoins", (relayIRCJoinLeave ? yes : no));
		btnRelayDeathMessages.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.relayDeathMessages", (relayDeathMessages ? yes : no));
		btnRelayNickChanges.field_146126_j = Utils.getLocalizedMessage("irc.gui.config.relayNickChanges", (relayNickChanges ? yes : no));
		btnAutoWho.field_146126_j = Utils.getLocalizedMessage("irc.gui.editChannel.autoWho", (autoWho ? yes : no));
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
		if(button == btnSave) {
			saveToConfig();
			if(autoJoin && EiraIRC.instance.isConnectedTo(serverConfig.getHost())) {
				EiraIRC.instance.getConnection(serverConfig.getHost()).join(config.getName(), config.getPassword());
			}
			Minecraft.getMinecraft().func_147108_a(new GuiChannelList(listParentScreen, serverConfig));
		} else if(button == btnCancel) {
			Minecraft.getMinecraft().func_147108_a(new GuiChannelList(listParentScreen, serverConfig));
		} else if(button == btnAutoJoin) {
			autoJoin = !autoJoin;
			updateButtons();
		} else if(button == btnMuted) {
			muted = !muted;
			updateButtons();
		} else if(button == btnReadOnly) {
			readOnly = !readOnly;
			updateButtons();
		} else if(button == btnRelayMinecraftJoinLeave) {
			relayMinecraftJoinLeave = !relayMinecraftJoinLeave;
			updateButtons();
		} else if(button == btnRelayIRCJoinLeave) {
			relayIRCJoinLeave = !relayIRCJoinLeave;
			updateButtons();
		} else if(button == btnRelayDeathMessages) {
			relayDeathMessages = !relayDeathMessages;
			updateButtons();
		} else if(button == btnRelayNickChanges) {
			relayNickChanges = !relayNickChanges;
			updateButtons();
		} else if(button == btnAutoWho) {
			autoWho = !autoWho;
			updateButtons();
		}
	}
	
	public void loadFromConfig() {
		if(config != null) {
			txtName.setText(config.getName());
			txtChannelPassword.setText(config.getPassword() != null ? config.getPassword() : "");
			autoJoin = config.isAutoJoin();
			muted = config.isMuted();
			readOnly = config.isReadOnly();
			relayMinecraftJoinLeave = config.relayMinecraftJoinLeave;
			relayIRCJoinLeave = config.relayIRCJoinLeave;
			relayDeathMessages= config.relayDeathMessages;
			relayNickChanges = config.relayNickChanges;
			autoWho = config.isAutoWho();
		} else {
			autoJoin = true;
			if(serverConfig.isClientSide()) {
				relayIRCJoinLeave = true;
				relayNickChanges = true;
			} else {
				relayMinecraftJoinLeave = true;
				relayIRCJoinLeave = true;
				relayDeathMessages = true;
				relayNickChanges = true;
				autoWho = true;
			}
		}
		updateButtons();
	}
	
	public void saveToConfig() {
		if(config == null || !config.getName().equals(txtName.getText())) {
			if(config != null) {
				serverConfig.removeChannelConfig(config.getName());
			}
			config = serverConfig.getChannelConfig(txtName.getText());
		}
		config.setPassword(txtChannelPassword.getText());
		config.setAutoJoin(autoJoin);
		config.setAutoWho(autoWho);
		config.setMuted(muted);
		config.setReadOnly(readOnly);
		config.relayMinecraftJoinLeave = relayMinecraftJoinLeave;
		config.relayIRCJoinLeave = relayIRCJoinLeave;
		config.relayDeathMessages = relayDeathMessages;
		config.relayNickChanges = relayNickChanges;
		serverConfig.addChannelConfig(config);
		ConfigurationHandler.save();
	}
}
