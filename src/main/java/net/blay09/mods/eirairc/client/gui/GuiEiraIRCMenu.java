// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.MenuButton;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshotList;
import net.blay09.mods.eirairc.client.gui.settings.GuiServerList;
import net.blay09.mods.eirairc.client.gui.settings.GuiTwitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

public class GuiEiraIRCMenu extends GuiScreen {

	private static final int BUTTON_SIZE = 64;

	private final List<MenuButton> buttonList = new ArrayList<MenuButton>();

	private MenuButton btnServers;
	private MenuButton btnTwitch;
	private MenuButton btnPlaceholder;
	private MenuButton btnFriends;
	private MenuButton btnScreenshots;
	private MenuButton btnSettings;

	@Override
	public void initGui() {
		final int buttonCenterX = width / 2;
		final int buttonCenterY = height / 2;

		buttonList.clear();

		btnServers = new MenuButton("Servers", buttonCenterX - 132, buttonCenterY - 95, 0, BUTTON_SIZE);
		buttonList.add(btnServers);

		btnTwitch = new MenuButton("Twitch", buttonCenterX - 32, buttonCenterY - 95, 0, 0);
		buttonList.add(btnTwitch);

		btnPlaceholder = new MenuButton("???", buttonCenterX + 64, buttonCenterY - 95, BUTTON_SIZE, BUTTON_SIZE);
		buttonList.add(btnPlaceholder);

		btnFriends = new MenuButton("Friends", buttonCenterX - 132, buttonCenterY, BUTTON_SIZE * 3, 0);
		buttonList.add(btnFriends);

		btnScreenshots = new MenuButton("Screenshots", buttonCenterX - 32, buttonCenterY, BUTTON_SIZE, 0);
		buttonList.add(btnScreenshots);

		btnSettings = new MenuButton("Settings", buttonCenterX + 64, buttonCenterY, BUTTON_SIZE * 2, 0);
		buttonList.add(btnSettings);
	}
	
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
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 0) {
			for (int i = 0; i < buttonList.size(); i++) {
				buttonList.get(i).mouseClicked(mouseX, mouseY);
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		final int menuWidth = 300;
		final int menuHeight = 200;
		final int menuX = width / 2 - menuWidth / 2;
		final int menuY = height / 2 - menuHeight / 2;
		drawGradientRect(menuX, menuY, menuX + menuWidth, menuY + menuHeight, -1072689136, -804253680);

		for(int i = 0; i < buttonList.size(); i++) {
			buttonList.get(i).draw(mouseX, mouseY);
		}

		super.drawScreen(mouseX, mouseY, par3);
	}
}
