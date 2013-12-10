// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.client.Screenshot;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.Globals;
import blay09.mods.eirairc.config.ServerConfig;

public class GuiIRCScreenshotSlot extends GuiSlot {

	private final GuiIRCScreenshotList parentGui;
	
	public GuiIRCScreenshotSlot(GuiIRCScreenshotList parentGui) {
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
		Screenshot screenshot = parentGui.getScreenshot(i);
		parentGui.drawString(parentGui.getFontRenderer(), screenshot.getName(), x + 2, y + 1, Globals.TEXT_COLOR);
		String sharedString = "Private";
		if(screenshot.isUploaded()) {
			sharedString = "Uploaded";
		}
		parentGui.drawString(parentGui.getFontRenderer(), sharedString, x + 4, y + 11, Globals.TEXT_COLOR);
	}

}
