// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import blay09.mods.irc.EiraIRC;
import blay09.mods.irc.IRCConnection;
import blay09.mods.irc.config.ConfigurationHandler;
import blay09.mods.irc.config.Globals;
import blay09.mods.irc.config.ServerConfig;

public class GuiIRCServerList extends GuiScreen {

	private GuiIRCServerSlot guiServerSlot;
	private GuiButton btnConnect;
	private GuiButton btnAdd;
	private GuiButton btnEdit;
	private GuiButton btnDelete;
	private GuiButton btnBack;
	
	private ServerConfig[] configs;
	private int selectedElement;
	
	@Override
	public void initGui() {
		guiServerSlot = new GuiIRCServerSlot(this);
		
		btnConnect = new GuiButton(0, width / 2 - 153, height - 50, 150, 20, "Connect");
		btnConnect.enabled = false;
		buttonList.add(btnConnect);
		
		btnAdd = new GuiButton(1, width / 2 + 3, height - 50, 150, 20, "Add Server");
		buttonList.add(btnAdd);
		
		btnEdit = new GuiButton(2, width / 2 - 126, height - 25, 80, 20, "Edit");
		btnEdit.enabled = false;
		buttonList.add(btnEdit);
		
		btnDelete = new GuiButton(3, width / 2 - 40, height - 25, 80, 20, "Delete");
		btnDelete.enabled = false;
		buttonList.add(btnDelete);
		
		btnBack = new GuiButton(4, width / 2 + 46, height - 25, 80, 20, "Back");
		buttonList.add(btnBack);
		
		selectedElement = -1;
		configs = ConfigurationHandler.getServerConfigs().toArray(new ServerConfig[ConfigurationHandler.getServerConfigs().size()]);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCSettings());
		} else if(button == btnConnect) {
			IRCConnection connection = EiraIRC.instance.getConnection(configs[selectedElement].host);
			if(connection != null) {
				connection.disconnect();
			} else {
				connection = new IRCConnection(configs[selectedElement].host, true);
				if(connection.connect()) {
					EiraIRC.instance.addConnection(connection);
				}
			}
			onElementSelected(selectedElement);
		} else if(button == btnEdit) {
			onElementClicked(selectedElement);
		} else if(button == btnDelete) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(this, "Do you really want to remove this server?", "This will delete it from the config file.", selectedElement));
		} else if(button == btnAdd) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCServerConfig());
		}
	}
	
	@Override
	public void confirmClicked(boolean yup, int serverIdx) {
		if(!yup) {
			Minecraft.getMinecraft().displayGuiScreen(this);
			return;
		}
		IRCConnection connection = EiraIRC.instance.getConnection(configs[serverIdx].host);
		if(connection != null) {
			connection.disconnect();
		}
		ConfigurationHandler.removeServerConfig(configs[serverIdx].host);
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
		Minecraft.getMinecraft().displayGuiScreen(new GuiIRCServerConfig(configs[i]));
	}
	
	public void onElementSelected(int i) {
		selectedElement = i;
		if(!hasElementSelected()) {
			return;
		}
		btnConnect.enabled = true;
		btnEdit.enabled = true;
		btnDelete.enabled = true;
		if(EiraIRC.instance.isConnectedTo(configs[i].host)) {
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
