// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import blay09.mods.irc.EiraIRC;
import blay09.mods.irc.config.Globals;
import blay09.mods.irc.config.ServerConfig;

public class GuiIRCServerSlot extends GuiSlot {

	private final GuiIRCServerList parentGui;
	
	public GuiIRCServerSlot(GuiIRCServerList parentGui) {
		super(Minecraft.getMinecraft(), parentGui.width, parentGui.height, 32, parentGui.height - 64, 36);
		this.parentGui = parentGui;
	}

	@Override
	protected int getSize() {
		return parentGui.size();
	}

	@Override
	protected void elementClicked(int i, boolean flag) {
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
	protected void drawSlot(int i, int x, int y, int l, Tessellator tessellator) {
		ServerConfig config = parentGui.getServerConfig(i);
		String connectedString = EiraIRC.instance.isConnectedTo(config.host) ? EnumChatFormatting.GREEN + "Connected" :  EnumChatFormatting.RED + "Not Connected";
		
		parentGui.drawString(parentGui.getFontRenderer(), config.host, x + 2, y + 1, Globals.TEXT_COLOR);
		parentGui.drawString(parentGui.getFontRenderer(), connectedString, x + 4, y + 11, Globals.TEXT_COLOR);
	}

}
