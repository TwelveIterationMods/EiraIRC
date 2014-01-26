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
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class GuiChannelList extends GuiScreen {

	private final GuiScreen parentScreen;
	private GuiChannelSlot guiChannelSlot;
	private GuiButton btnJoin;
	private GuiButton btnAdd;
	private GuiButton btnEdit;
	private GuiButton btnDelete;
	private GuiButton btnBack;
	
	private final ServerConfig parentConfig;
	private ChannelConfig[] configs;
	private int selectedElement;
	
	public GuiChannelList(GuiScreen parentScreen, ServerConfig parentConfig) {
		this.parentScreen = parentScreen;
		this.parentConfig = parentConfig;
	}
	
	@Override
	public void initGui() {
		guiChannelSlot = new GuiChannelSlot(this);
		
		btnJoin = new GuiButton(1, field_146294_l / 2 - 153, field_146295_m - 50, 150, 20, Utils.getLocalizedMessage("irc.gui.channelList.join"));
		btnJoin.field_146124_l = false;
		field_146292_n.add(btnJoin);
		
		btnAdd = new GuiButton(2, field_146294_l / 2 + 3, field_146295_m - 50, 150, 20, Utils.getLocalizedMessage("irc.gui.add", "Channel"));
		field_146292_n.add(btnAdd);
		
		btnEdit = new GuiButton(3, field_146294_l / 2 - 126, field_146295_m - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.edit"));
		btnEdit.field_146124_l = false;
		field_146292_n.add(btnEdit);
		
		btnDelete = new GuiButton(4, field_146294_l / 2 - 40, field_146295_m - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.delete"));
		btnDelete.field_146124_l = false;
		field_146292_n.add(btnDelete);
		
		btnBack = new GuiButton(0, field_146294_l / 2 + 46, field_146295_m - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.back"));
		field_146292_n.add(btnBack);
		
		selectedElement = -1;
		configs = parentConfig.getChannelConfigs().toArray(new ChannelConfig[parentConfig.getChannelConfigs().size()]);
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().func_147108_a(parentScreen);
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
			Minecraft.getMinecraft().func_147108_a(new GuiYesNo(this, Utils.getLocalizedMessage("irc.gui.reallyRemove", "channel"), Utils.getLocalizedMessage("irc.gui.configDelete"), selectedElement));
		} else if(button == btnAdd) {
			Minecraft.getMinecraft().func_147108_a(new GuiChannelConfig(parentScreen, parentConfig));
		}
	}
	
	@Override
	public void confirmClicked(boolean yup, int channelIdx) {
		if(!yup) {
			Minecraft.getMinecraft().func_147108_a(this);
			return;
		}
		IRCConnection connection = EiraIRC.instance.getConnection(parentConfig.getHost());
		if(connection != null) {
			connection.part(configs[channelIdx].getName());
		}
		parentConfig.removeChannelConfig(configs[channelIdx].getName());
		ConfigurationHandler.save();
		Minecraft.getMinecraft().func_147108_a(this);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		guiChannelSlot.drawScreen(par1, par2, par3);
		drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.channelList"), field_146294_l / 2, 20, Globals.TEXT_COLOR);
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
		Minecraft.getMinecraft().func_147108_a(new GuiChannelConfig(parentScreen, configs[i]));
	}
	
	public void onElementSelected(int i) {
		selectedElement = i;
		if(!hasElementSelected()) {
			return;
		}
		btnEdit.field_146124_l = true;
		btnDelete.field_146124_l = true;
		if(EiraIRC.instance.isConnectedTo(parentConfig.getHost())) {
			btnJoin.field_146124_l = true;
			if(EiraIRC.instance.getConnection(parentConfig.getHost()).getChannel(configs[i].getName()) != null) {
				btnJoin.field_146126_j = Utils.getLocalizedMessage("irc.gui.channelList.leave");
			} else {
				btnJoin.field_146126_j = Utils.getLocalizedMessage("irc.gui.channelList.join");
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
