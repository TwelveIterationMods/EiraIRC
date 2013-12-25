// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import blay09.mods.eirairc.config.ScreenshotConfig;
import blay09.mods.eirairc.util.Globals;

public class GuiSettings extends GuiScreen {

	private GuiButton btnGlobalSettings;
	private GuiButton btnServerList;
	private GuiButton btnTwitch;
	private GuiButton btnScreenshots;
	private GuiButton btnProfanityFilter;
	private GuiButton btnFriendsList;
	private GuiButton btnIgnoreList;
	private GuiButton btnNotifications;
	private GuiButton btnKeybinds;
	private GuiButton btnClientServer;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		btnGlobalSettings = new GuiButton(2, width / 2 - 152, height / 2 - 90, 150, 20, "Global Settings");
		buttonList.add(btnGlobalSettings);
		
		btnServerList = new GuiButton(3, width / 2 - 152, height / 2 - 65, 150, 20, "Server List");
		buttonList.add(btnServerList);
		
		btnTwitch = new GuiButton(4, width / 2 - 152, height / 2 - 40, 150, 20, "Twitch Chat");
		buttonList.add(btnTwitch);
		
		btnScreenshots = new GuiButton(5, width / 2 + 2, height / 2 - 90, 150, 20, "Screenshots");
		if(!ScreenshotConfig.manageScreenshots) {
			btnScreenshots.enabled = false;
		}
		buttonList.add(btnScreenshots);
		
		btnProfanityFilter = new GuiButton(6, width / 2 - 152, height / 2 - 15, 150, 20, "Profanity Filter");
		btnProfanityFilter.enabled = false;
		buttonList.add(btnProfanityFilter);
		
		btnFriendsList = new GuiButton(7, width / 2 + 2, height / 2 - 65, 150, 20, "Friends List");
		btnFriendsList.enabled = false;
		buttonList.add(btnFriendsList);
		
		btnIgnoreList = new GuiButton(8, width / 2 + 2, height / 2 - 40, 150, 20, "Ignore List");
		btnIgnoreList.enabled = false;
		buttonList.add(btnIgnoreList);
		
		btnNotifications = new GuiButton(9, width / 2 + 2, height / 2 - 15, 150, 20, "Notifications");
		buttonList.add(btnNotifications);
		
		btnKeybinds = new GuiButton(10, width / 2 - 152, height / 2 + 10, 150, 20, "Keybinds");
		buttonList.add(btnKeybinds);
		
		btnClientServer = new GuiButton(1, 1, 1, 60, 20, "Client");
		btnClientServer.enabled = false;
		buttonList.add(btnClientServer);
		
		btnBack = new GuiButton(0, width / 2 - 100, height / 2 + 90, 200, 20, "Back");
		buttonList.add(btnBack);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnTwitch) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiTwitch(this));
		} else if(button == btnServerList) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerList());
		} else if(button == btnGlobalSettings) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiGlobalSettings());
		} else if(button == btnScreenshots) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshots());
		} else if(button == btnNotifications) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiNotifications());
		} else if(button == btnKeybinds) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiKeybinds());
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
