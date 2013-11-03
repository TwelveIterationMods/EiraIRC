// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import blay09.mods.eirairc.config.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiIRCSettings extends GuiScreen {

	private GuiButton btnGlobalSettings;
	private GuiButton btnServerList;
	private GuiButton btnTwitch;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		btnGlobalSettings = new GuiButton(0, width / 2 - 100, height / 2 - 90, "Global Settings");
		buttonList.add(btnGlobalSettings);
		
		btnServerList = new GuiButton(1, width / 2 - 100, height / 2 - 65, "Server List");
		buttonList.add(btnServerList);
		
		btnTwitch = new GuiButton(2, width / 2 - 100, height / 2 - 40, "Twitch");
		buttonList.add(btnTwitch);
		
		btnBack = new GuiButton(3, width / 2 - 100, height / 2 - 10, "Back");
		buttonList.add(btnBack);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnTwitch) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCTwitch(this));
		} else if(button == btnServerList) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCServerList());
		} else if(button == btnGlobalSettings) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCGlobalSettings());
		} else if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(null);
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		this.drawCenteredString(fontRenderer, "EiraIRC Settings", width / 2, height / 2 - 110, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
}
