// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

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
		
		btnConnect = new GuiButton(1, field_146294_l / 2 - 153, field_146295_m - 50, 100, 20, Utils.getLocalizedMessage("irc.gui.serverList.connect"));
		btnConnect.field_146124_l = false;
		field_146292_n.add(btnConnect);
		
		btnAdd = new GuiButton(2, field_146294_l / 2 + 53, field_146295_m - 50, 100, 20, Utils.getLocalizedMessage("irc.gui.add", "Server"));
		field_146292_n.add(btnAdd);
		
		btnEdit = new GuiButton(3, field_146294_l / 2 - 126, field_146295_m - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.edit"));
		btnEdit.field_146124_l = false;
		field_146292_n.add(btnEdit);
		
		btnDelete = new GuiButton(4, field_146294_l / 2 - 40, field_146295_m - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.delete"));
		btnDelete.field_146124_l = false;
		field_146292_n.add(btnDelete);
		
		btnChannels = new GuiButton(5, field_146294_l / 2 - 50, field_146295_m - 50, 100, 20, Utils.getLocalizedMessage("irc.gui.serverList.channels"));
		btnChannels.field_146124_l = false;
		field_146292_n.add(btnChannels);
		
		btnBack = new GuiButton(0, field_146294_l / 2 + 46, field_146295_m - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.back"));
		field_146292_n.add(btnBack);
		
		selectedElement = -1;
		configs = ConfigurationHandler.getServerConfigs().toArray(new ServerConfig[ConfigurationHandler.getServerConfigs().size()]);
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().func_147108_a(new GuiSettings());
		} else if(button == btnConnect) {
			IRCConnection connection = EiraIRC.instance.getConnection(configs[selectedElement].getHost());
			if(connection != null) {
				connection.disconnect(Utils.getQuitMessage(connection));
			} else {
				Utils.connectTo(configs[selectedElement]);
			}
			onElementSelected(selectedElement);
		} else if(button == btnEdit) {
			Minecraft.getMinecraft().func_147108_a(new GuiServerConfig(configs[selectedElement]));
		} else if(button == btnDelete) {
			Minecraft.getMinecraft().func_147108_a(new GuiYesNo(this, Utils.getLocalizedMessage("irc.gui.reallyRemove", "server"), Utils.getLocalizedMessage("irc.gui.configDelete"), selectedElement));
		} else if(button == btnAdd) {
			Minecraft.getMinecraft().func_147108_a(new GuiServerConfig());
		} else if(button == btnChannels) {
			Minecraft.getMinecraft().func_147108_a(new GuiChannelList(this, configs[selectedElement]));
		}
	}
	
	@Override
	public void confirmClicked(boolean yup, int serverIdx) {
		if(!yup) {
			Minecraft.getMinecraft().func_147108_a(this);
			return;
		}
		IRCConnection connection = EiraIRC.instance.getConnection(configs[serverIdx].getHost());
		if(connection != null) {
			connection.disconnect(Utils.getQuitMessage(connection));
		}
		ConfigurationHandler.removeServerConfig(configs[serverIdx].getHost());
		ConfigurationHandler.save();
		Minecraft.getMinecraft().func_147108_a(this);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		guiServerSlot.drawScreen(par1, par2, par3);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.serverList"), field_146294_l / 2, 20, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}

	public int size() {
		return configs.length;
	}

	public FontRenderer getFontRenderer() {
		return field_146289_q;
	}

	public boolean hasElementSelected() {
		return (selectedElement >= 0 && selectedElement < configs.length);
	}
	
	public void onElementClicked(int i) {
		if(EiraIRC.instance.isConnectedTo(configs[i].getHost())) {
			Minecraft.getMinecraft().func_147108_a(new GuiChannelList(this, configs[i]));
		} else {
			Minecraft.getMinecraft().func_147108_a(new GuiServerConfig(configs[i]));
		}
	}
	
	public void onElementSelected(int i) {
		selectedElement = i;
		if(!hasElementSelected()) {
			return;
		}
		btnConnect.field_146124_l = true;
		btnEdit.field_146124_l = true;
		btnDelete.field_146124_l = true;
		btnChannels.field_146124_l = true;
		if(EiraIRC.instance.isConnectedTo(configs[i].getHost())) {
			btnConnect.field_146126_j = Utils.getLocalizedMessage("irc.gui.serverList.disconnect");
		} else {
			btnConnect.field_146126_j = Utils.getLocalizedMessage("irc.gui.serverList.connect");
		}
	}
	
	public int getSelectedElement() {
		return selectedElement;
	}

	public ServerConfig getServerConfig(int i) {
		return configs[i];
	}
	
}
