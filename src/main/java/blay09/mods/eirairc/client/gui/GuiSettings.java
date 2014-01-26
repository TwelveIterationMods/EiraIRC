// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import blay09.mods.eirairc.config.ScreenshotConfig;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class GuiSettings extends GuiScreen {

	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;
	
	private GuiButton btnGlobalSettings;
	private GuiButton btnServerList;
	private GuiButton btnTwitch;
	private GuiButton btnDisplaySettings;
	private GuiButton btnBotSettings;
	private GuiButton btnScreenshots;
	private GuiButton btnFriendIgnore;
	private GuiButton btnNotifications;
	private GuiButton btnKeybinds;
	private GuiButton btnClientServer;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		int leftX = field_146294_l /  2 - 152;
		int rightX = field_146294_l / 2 + 2;
		
		btnGlobalSettings = new GuiButton(2, leftX, field_146295_m / 2 - 90, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.global"));
		field_146292_n.add(btnGlobalSettings);
		
		btnServerList = new GuiButton(3, leftX, field_146295_m / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.serverList"));
		field_146292_n.add(btnServerList);
		
		btnFriendIgnore = new GuiButton(4, leftX, field_146295_m / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.friendsIgnoreList"));
		btnFriendIgnore.field_146124_l = false;
		field_146292_n.add(btnFriendIgnore);
		
		btnTwitch = new GuiButton(5, leftX, field_146295_m / 2 - 15, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.twitch"));
		field_146292_n.add(btnTwitch);
		
		btnScreenshots = new GuiButton(6, leftX, field_146295_m / 2 + 10, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.screenshots"));
		if(!ScreenshotConfig.manageScreenshots) {
			btnScreenshots.field_146124_l = false;
		}
		field_146292_n.add(btnScreenshots);
		
		btnDisplaySettings = new GuiButton(7, rightX, field_146295_m / 2 - 90, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.displaySettings"));
		field_146292_n.add(btnDisplaySettings);
		
		btnBotSettings = new GuiButton(8, rightX, field_146295_m / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.botSettings"));
		field_146292_n.add(btnBotSettings);
		
		btnNotifications = new GuiButton(9, rightX, field_146295_m / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.notifications"));
		field_146292_n.add(btnNotifications);
		
		btnKeybinds = new GuiButton(10, rightX, field_146295_m / 2 - 15, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.keybinds"));
		field_146292_n.add(btnKeybinds);
		
		btnClientServer = new GuiButton(1, 1, 1, 60, 20, Utils.getLocalizedMessage("irc.gui.settings.client"));
		btnClientServer.field_146124_l = false;
		field_146292_n.add(btnClientServer);
		
		btnBack = new GuiButton(0, field_146294_l / 2 - 100, field_146295_m / 2 + 90, 200, 20, Utils.getLocalizedMessage("irc.gui.back"));
		field_146292_n.add(btnBack);
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
		if(button == btnTwitch) {
			Minecraft.getMinecraft().func_147108_a(new GuiTwitch(this));
		} else if(button == btnServerList) {
			Minecraft.getMinecraft().func_147108_a(new GuiServerList());
		} else if(button == btnGlobalSettings) {
			Minecraft.getMinecraft().func_147108_a(new GuiGlobalSettings());
		} else if(button == btnScreenshots) {
			Minecraft.getMinecraft().func_147108_a(new GuiScreenshots());
		} else if(button == btnNotifications) {
			Minecraft.getMinecraft().func_147108_a(new GuiNotifications());
		} else if(button == btnKeybinds) {
			Minecraft.getMinecraft().func_147108_a(new GuiKeybinds());
		} else if(button == btnDisplaySettings) {
			Minecraft.getMinecraft().func_147108_a(new GuiDisplaySettings());
		} else if(button == btnBotSettings) {
			Minecraft.getMinecraft().func_147108_a(new GuiBotSettings());
		} else if(button == btnBack) {
			Minecraft.getMinecraft().func_147108_a(null);
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		func_146270_b(0);
		this.drawCenteredString(field_146289_q, Utils.getLocalizedMessage("irc.gui.settings"), field_146294_l / 2, field_146295_m / 2 - 110, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
}
