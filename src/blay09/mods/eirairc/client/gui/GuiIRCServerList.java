// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.Globals;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.util.Utils;

public class GuiIRCServerList extends GuiScreen {

	private GuiIRCServerSlot guiServerSlot;
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
		guiServerSlot = new GuiIRCServerSlot(this);
		
		btnConnect = new GuiButton(1, width / 2 - 153, height - 50, 100, 20, "Connect");
		btnConnect.enabled = false;
		buttonList.add(btnConnect);
		
		btnAdd = new GuiButton(2, width / 2 + 53, height - 50, 100, 20, "Add Server");
		buttonList.add(btnAdd);
		
		btnEdit = new GuiButton(3, width / 2 - 126, height - 25, 80, 20, "Edit");
		btnEdit.enabled = false;
		buttonList.add(btnEdit);
		
		btnDelete = new GuiButton(4, width / 2 - 40, height - 25, 80, 20, "Delete");
		btnDelete.enabled = false;
		buttonList.add(btnDelete);
		
		btnChannels = new GuiButton(5, width / 2 - 50, height - 50, 100, 20, "Channels");
		btnChannels.enabled = false;
		buttonList.add(btnChannels);
		
		btnBack = new GuiButton(0, width / 2 + 46, height - 25, 80, 20, "Back");
		buttonList.add(btnBack);
		
		selectedElement = -1;
		configs = ConfigurationHandler.getServerConfigs().toArray(new ServerConfig[ConfigurationHandler.getServerConfigs().size()]);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCSettings());
		} else if(button == btnConnect) {
			IRCConnection connection = EiraIRC.instance.getConnection(configs[selectedElement].getHost());
			if(connection != null) {
				connection.disconnect(Utils.getQuitMessage(connection));
			} else {
				Utils.connectTo(configs[selectedElement]);
			}
			onElementSelected(selectedElement);
		} else if(button == btnEdit) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCServerConfig(configs[selectedElement]));
		} else if(button == btnDelete) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(this, "Do you really want to remove this server?", "This will delete it from the config file.", selectedElement));
		} else if(button == btnAdd) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCServerConfig());
		} else if(button == btnChannels) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCChannelList(this, configs[selectedElement]));
		}
	}
	
	@Override
	public void confirmClicked(boolean yup, int serverIdx) {
		if(!yup) {
			Minecraft.getMinecraft().displayGuiScreen(this);
			return;
		}
		IRCConnection connection = EiraIRC.instance.getConnection(configs[serverIdx].getHost());
		if(connection != null) {
			connection.disconnect(Utils.getQuitMessage(connection));
		}
		ConfigurationHandler.removeServerConfig(configs[serverIdx].getHost());
		ConfigurationHandler.save();
		Minecraft.getMinecraft().displayGuiScreen(this);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		guiServerSlot.drawScreen(par1, par2, par3);
		drawCenteredString(fontRenderer, "EiraIRC - Server List", width / 2, 20, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}

	public int size() {
		return configs.length;
	}

	public FontRenderer getFontRenderer() {
		return fontRenderer;
	}

	public boolean hasElementSelected() {
		return (selectedElement >= 0 && selectedElement < configs.length);
	}
	
	public void onElementClicked(int i) {
		if(EiraIRC.instance.isConnectedTo(configs[i].getHost())) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCChannelList(this, configs[i]));
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCServerConfig(configs[i]));
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
			btnConnect.displayString = "Disconnect";
		} else {
			btnConnect.displayString = "Connect";
		}
	}
	
	public int getSelectedElement() {
		return selectedElement;
	}

	public ServerConfig getServerConfig(int i) {
		return configs[i];
	}
	
}
