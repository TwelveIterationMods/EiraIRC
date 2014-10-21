// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.GuiMenuButton;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshots;
import net.blay09.mods.eirairc.client.gui.servers.GuiServerConfigContainer;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class GuiEiraIRCMenu extends EiraGuiScreen {

	private static final ResourceLocation meow = new ResourceLocation("mob.cat.meow");

	private static final int BUTTON_SIZE = 64;

	private GuiMenuButton btnServers;
	private GuiMenuButton btnTwitch;
	private GuiMenuButton btnPlaceholder;
	private GuiMenuButton btnFriends;
	private GuiMenuButton btnScreenshots;
	private GuiMenuButton btnSettings;

	private int catCount;

	@Override
	public void initGui() {
		super.initGui();

		final int buttonCenterX = width / 2;
		final int buttonCenterY = height / 2;

		btnServers = new GuiMenuButton("Servers", buttonCenterX - 132, buttonCenterY - 95, 0, BUTTON_SIZE);
		menuButtonList.add(btnServers);

		btnTwitch = new GuiMenuButton("Twitch", buttonCenterX - 32, buttonCenterY - 95, 0, 0);
		menuButtonList.add(btnTwitch);

		btnPlaceholder = new GuiMenuButton("???", buttonCenterX + 64, buttonCenterY - 95, BUTTON_SIZE, BUTTON_SIZE);
		btnPlaceholder.setPlayButtonSound(false);
		menuButtonList.add(btnPlaceholder);

		btnFriends = new GuiMenuButton("Friends", buttonCenterX - 132, buttonCenterY, BUTTON_SIZE * 3, 0);
		menuButtonList.add(btnFriends);

		btnScreenshots = new GuiMenuButton("Screenshots", buttonCenterX - 32, buttonCenterY, BUTTON_SIZE, 0);
		menuButtonList.add(btnScreenshots);

		btnSettings = new GuiMenuButton("Settings", buttonCenterX + 64, buttonCenterY, BUTTON_SIZE * 2, 0);
		menuButtonList.add(btnSettings);
	}

	@Override
	public void actionPerformed(GuiMenuButton button) {
		if(button == btnTwitch) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiTwitch(this));
		} else if(button == btnServers) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerConfigContainer(this));
		} else if(button == btnScreenshots) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshots(this));
		} else if(button == btnSettings) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCConfig(this));
		} else if(button == btnPlaceholder) {
			catCount++;
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(meow, 1f));
			if(catCount >= 4) {
				Utils.openWebpage("https://www.youtube.com/results?search_query=Cute+Cat+Videos");
				catCount = Integer.MIN_VALUE;
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		super.drawScreen(mouseX, mouseY, par3);
	}
}
