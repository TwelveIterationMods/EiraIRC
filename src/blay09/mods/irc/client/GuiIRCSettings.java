// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.client;

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
		btnGlobalSettings = new GuiButton(0, width / 2 - 200 / 2, 30, "Global Settings");
		buttonList.add(btnGlobalSettings);
		
		btnServerList = new GuiButton(1, width / 2 - 200 / 2, 60, "Server List");
		btnServerList.enabled = false;
		buttonList.add(btnServerList);
		
		btnTwitch = new GuiButton(2, width / 2 - 200 / 2, 90, "Twitch");
		buttonList.add(btnTwitch);
		
		btnBack = new GuiButton(3, width / 2 - 200 / 2, 120, "Back");
		buttonList.add(btnBack);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnTwitch) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCTwitch());
		} else if(button == btnServerList) {
		} else if(button == btnGlobalSettings) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCGlobalSettings());
		} else if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(null);
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawBackground(0);
		String caption = "EiraIRC Settings";
		fontRenderer.drawString(caption, width / 2 - fontRenderer.getStringWidth(caption) / 2, 10, 16777215);
		super.drawScreen(par1, par2, par3);
	}
}
