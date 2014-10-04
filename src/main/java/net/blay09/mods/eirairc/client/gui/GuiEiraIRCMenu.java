// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.MenuButton;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshotList;
import net.blay09.mods.eirairc.client.gui.settings.GuiServerList;
import net.minecraft.client.Minecraft;

public class GuiEiraIRCMenu extends EiraGuiScreen {

	private static final int BUTTON_SIZE = 64;

	private MenuButton btnServers;
	private MenuButton btnTwitch;
	private MenuButton btnPlaceholder;
	private MenuButton btnFriends;
	private MenuButton btnScreenshots;
	private MenuButton btnSettings;

	@Override
	public void initGui() {
		super.initGui();

		final int buttonCenterX = width / 2;
		final int buttonCenterY = height / 2;

		btnServers = new MenuButton("Servers", buttonCenterX - 132, buttonCenterY - 95, 0, BUTTON_SIZE);
		menuButtonList.add(btnServers);

		btnTwitch = new MenuButton("Twitch", buttonCenterX - 32, buttonCenterY - 95, 0, 0);
		menuButtonList.add(btnTwitch);

		btnPlaceholder = new MenuButton("???", buttonCenterX + 64, buttonCenterY - 95, BUTTON_SIZE, BUTTON_SIZE);
		menuButtonList.add(btnPlaceholder);

		btnFriends = new MenuButton("Friends", buttonCenterX - 132, buttonCenterY, BUTTON_SIZE * 3, 0);
		menuButtonList.add(btnFriends);

		btnScreenshots = new MenuButton("Screenshots", buttonCenterX - 32, buttonCenterY, BUTTON_SIZE, 0);
		menuButtonList.add(btnScreenshots);

		btnSettings = new MenuButton("Settings", buttonCenterX + 64, buttonCenterY, BUTTON_SIZE * 2, 0);
		menuButtonList.add(btnSettings);
	}

	@Override
	public void actionPerformed(MenuButton button) {
		if(button == btnTwitch) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiTwitch(this));
		} else if(button == btnServers) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerList());
		} else if(button == btnScreenshots) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshotList(this));
		} else if(button == btnSettings) {
			Minecraft.getMinecraft().displayGuiScreen(new EiraIRCConfigGUI(this));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		final int menuWidth = 300;
		final int menuHeight = 200;
		final int menuX = width / 2 - menuWidth / 2;
		final int menuY = height / 2 - menuHeight / 2;

		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		super.drawScreen(mouseX, mouseY, par3);
	}
}
