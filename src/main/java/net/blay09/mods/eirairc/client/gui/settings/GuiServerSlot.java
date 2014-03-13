// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;

public class GuiServerSlot extends GuiSlot {

	private final GuiServerList parentGui;
	
	public GuiServerSlot(GuiServerList parentGui) {
		super(Minecraft.getMinecraft(), parentGui.width, parentGui.height, 32, parentGui.height - 64, 36);
		this.parentGui = parentGui;
	}

	@Override
	protected int getSize() {
		return parentGui.size();
	}

	@Override
	protected void elementClicked(int i, boolean flag, int j, int k) {
		parentGui.onElementSelected(i);
		if(flag) {
			parentGui.onElementClicked(i);
		}
	}

	@Override
	protected boolean isSelected(int i) {
		return parentGui.getSelectedElement() == i;
	}

	@Override
	protected void drawBackground() {
		parentGui.drawDefaultBackground();
	}
	
	protected int getContentHeight() {
		return getSize() * 36;
	}

	@Override
	protected void drawSlot(int i, int x, int y, int l, Tessellator tessellator, int k, int j) {
		ServerConfig config = parentGui.getServerConfig(i);
		String connectedString = EiraIRC.instance.isConnectedTo(config.getHost()) ? Utils.getLocalizedMessage("irc.gui.serverList.connected") :  Utils.getLocalizedMessage("irc.gui.serverList.notConnected");
		
		parentGui.drawString(parentGui.getFontRenderer(), config.getHost(), x + 2, y + 1, Globals.TEXT_COLOR);
		parentGui.drawString(parentGui.getFontRenderer(), connectedString, x + 4, y + 11, Globals.TEXT_COLOR);
	}

}
