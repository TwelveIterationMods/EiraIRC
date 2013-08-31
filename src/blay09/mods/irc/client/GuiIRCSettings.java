// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiIRCSettings extends GuiScreen {

	private GuiButton btnTwitch;
	private GuiButton btnNickServ;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		btnTwitch = new GuiButton(0, width / 2 - 200 / 2, 30, "Twitch");
		buttonList.add(btnTwitch);
		
		btnNickServ = new GuiButton(1, width / 2 - 200 / 2, 60, "NickServ");
		buttonList.add(btnNickServ);
		
		btnBack = new GuiButton(2, width / 2 - 200 / 2, 90, "Back");
		buttonList.add(btnBack);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnTwitch) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCTwitch());
		} else if(button == btnNickServ) {
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
