// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class GuiIRCChannelList extends GuiScreen {

	private final GuiScreen parentScreen;
	private GuiIRCChannelSlot guiChannelSlot;
	private GuiButton btnJoin;
	private GuiButton btnAdd;
	private GuiButton btnEdit;
	private GuiButton btnDelete;
	private GuiButton btnBack;
	
	private final ServerConfig parentConfig;
	private ChannelConfig[] configs;
	private int selectedElement;
	
	public GuiIRCChannelList(GuiScreen parentScreen, ServerConfig parentConfig) {
		this.parentScreen = parentScreen;
		this.parentConfig = parentConfig;
	}
	
	@Override
	public void initGui() {
		guiChannelSlot = new GuiIRCChannelSlot(this);
		
		btnJoin = new GuiButton(1, width / 2 - 153, height - 50, 150, 20, "Join");
		btnJoin.enabled = false;
		buttonList.add(btnJoin);
		
		btnAdd = new GuiButton(2, width / 2 + 3, height - 50, 150, 20, "Add Channel");
		buttonList.add(btnAdd);
		
		btnEdit = new GuiButton(3, width / 2 - 126, height - 25, 80, 20, "Edit");
		btnEdit.enabled = false;
		buttonList.add(btnEdit);
		
		btnDelete = new GuiButton(4, width / 2 - 40, height - 25, 80, 20, "Delete");
		btnDelete.enabled = false;
		buttonList.add(btnDelete);
		
		btnBack = new GuiButton(0, width / 2 + 46, height - 25, 80, 20, "Back");
		buttonList.add(btnBack);
		
		selectedElement = -1;
		configs = parentConfig.getChannelConfigs().toArray(new ChannelConfig[parentConfig.getChannelConfigs().size()]);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
		} else if(button == btnJoin) {
			IRCConnection connection = EiraIRC.instance.getConnection(parentConfig.getHost());
			if(connection != null) {
				if(EiraIRC.instance.getConnection(parentConfig.getHost()).getChannel(configs[selectedElement].getName()) == null) {
					connection.join(configs[selectedElement].getName(), configs[selectedElement].getPassword());
				} else {
					connection.part(configs[selectedElement].getName());
				}
			}
			onElementSelected(selectedElement);
		} else if(button == btnEdit) {
			onElementClicked(selectedElement);
		} else if(button == btnDelete) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(this, "Do you really want to remove this channel?", "This will delete it from the config file.", selectedElement));
		} else if(button == btnAdd) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCChannelConfig(parentScreen, parentConfig));
		}
	}
	
	@Override
	public void confirmClicked(boolean yup, int channelIdx) {
		if(!yup) {
			Minecraft.getMinecraft().displayGuiScreen(this);
			return;
		}
		IRCConnection connection = EiraIRC.instance.getConnection(parentConfig.getHost());
		if(connection != null) {
			connection.part(configs[channelIdx].getName());
		}
		parentConfig.removeChannelConfig(configs[channelIdx].getName());
		ConfigurationHandler.save();
		Minecraft.getMinecraft().displayGuiScreen(this);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		guiChannelSlot.drawScreen(par1, par2, par3);
		drawCenteredString(fontRenderer, "EiraIRC - Channel List", width / 2, 20, Globals.TEXT_COLOR);
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
		Minecraft.getMinecraft().displayGuiScreen(new GuiIRCChannelConfig(parentScreen, configs[i]));
	}
	
	public void onElementSelected(int i) {
		selectedElement = i;
		if(!hasElementSelected()) {
			return;
		}
		btnEdit.enabled = true;
		btnDelete.enabled = true;
		if(EiraIRC.instance.isConnectedTo(parentConfig.getHost())) {
			btnJoin.enabled = true;
			if(EiraIRC.instance.getConnection(parentConfig.getHost()).getChannel(configs[i].getName()) != null) {
				btnJoin.displayString = "Leave";
			} else {
				btnJoin.displayString = "Join";
			}
		}
	}
	
	public int getSelectedElement() {
		return selectedElement;
	}

	public ServerConfig getServerConfig() {
		return parentConfig;
	}
	
	public ChannelConfig getChannelConfig(int i) {
		return configs[i];
	}
	
}
