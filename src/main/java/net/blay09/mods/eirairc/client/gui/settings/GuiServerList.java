// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;

public class GuiServerList extends GuiScreen {

	private GuiServerSlot guiServerSlot;
	private GuiButton btnConnect;
	private GuiButton btnAdd;
	private GuiButton btnEdit;
	private GuiButton btnChannels;
	private GuiButton btnDelete;
	private GuiButton btnBack;
	
	private ServerConfig[] configs;
	private int selectedElement;
	
	@Override
	public void initGui() {
		guiServerSlot = new GuiServerSlot(this);
		
		btnConnect = new GuiButton(1, width / 2 - 153, height - 50, 100, 20, Utils.getLocalizedMessage("irc.gui.serverList.connect"));
		btnConnect.enabled = false;
		buttonList.add(btnConnect);
		
		btnAdd = new GuiButton(2, width / 2 + 53, height - 50, 100, 20, Utils.getLocalizedMessage("irc.gui.add", "Server"));
		buttonList.add(btnAdd);
		
		btnEdit = new GuiButton(3, width / 2 - 126, height - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.edit"));
		btnEdit.enabled = false;
		buttonList.add(btnEdit);
		
		btnDelete = new GuiButton(4, width / 2 - 40, height - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.delete"));
		btnDelete.enabled = false;
		buttonList.add(btnDelete);
		
		btnChannels = new GuiButton(5, width / 2 - 50, height - 50, 100, 20, Utils.getLocalizedMessage("irc.gui.serverList.channels"));
		btnChannels.enabled = false;
		buttonList.add(btnChannels);
		
		btnBack = new GuiButton(0, width / 2 + 46, height - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		selectedElement = -1;
		configs = ConfigurationHandler.getServerConfigs().toArray(new ServerConfig[ConfigurationHandler.getServerConfigs().size()]);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
		} else if(button == btnConnect) {
			IIRCConnection connection = EiraIRC.instance.getConnection(configs[selectedElement].getHost());
			if(connection != null) {
				connection.disconnect(ConfigHelper.getQuitMessage(connection));
			} else {
				Utils.connectTo(configs[selectedElement]);
			}
			onElementSelected(selectedElement);
		} else if(button == btnEdit) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerConfig(configs[selectedElement]));
		} else if(button == btnDelete) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(this, Utils.getLocalizedMessage("irc.gui.reallyRemove", "server"), Utils.getLocalizedMessage("irc.gui.configDelete"), selectedElement));
		} else if(button == btnAdd) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerConfig());
		} else if(button == btnChannels) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(this, configs[selectedElement]));
		}
	}
	
	@Override
	public void confirmClicked(boolean yup, int serverIdx) {
		if(!yup) {
			Minecraft.getMinecraft().displayGuiScreen(this);
			return;
		}
		IIRCConnection connection = EiraIRC.instance.getConnection(configs[serverIdx].getHost());
		if(connection != null) {
			connection.disconnect(ConfigHelper.getQuitMessage(connection));
		}
		ConfigurationHandler.removeServerConfig(configs[serverIdx].getHost());
		ConfigurationHandler.save();
		Minecraft.getMinecraft().displayGuiScreen(this);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		guiServerSlot.drawScreen(par1, par2, par3);
		drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.serverList"), width / 2, 20, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}

	public int size() {
		return configs.length;
	}

	public FontRenderer getFontRenderer() {
		return fontRendererObj;
	}

	public boolean hasElementSelected() {
		return (selectedElement >= 0 && selectedElement < configs.length);
	}
	
	public void onElementClicked(int i) {
		if(EiraIRC.instance.isConnectedTo(configs[i].getHost())) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(this, configs[i]));
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerConfig(configs[i]));
		}
	}
	
	public void onElementSelected(int i) {
		selectedElement = i;
		if(!hasElementSelected()) {
			return;
		}
		btnConnect.enabled = true;
		btnEdit.enabled = true;
		btnDelete.enabled = true;
		btnChannels.enabled = true;
		if(EiraIRC.instance.isConnectedTo(configs[i].getHost())) {
			btnConnect.displayString = Utils.getLocalizedMessage("irc.gui.serverList.disconnect");
		} else {
			btnConnect.displayString = Utils.getLocalizedMessage("irc.gui.serverList.connect");
		}
	}
	
	public int getSelectedElement() {
		return selectedElement;
	}

	public ServerConfig getServerConfig(int i) {
		return configs[i];
	}
	
}
