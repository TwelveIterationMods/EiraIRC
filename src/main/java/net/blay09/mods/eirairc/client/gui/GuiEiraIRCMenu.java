// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.GuiMenuButton;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshots;
import net.blay09.mods.eirairc.client.gui.servers.GuiServerConfigContainer;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiEiraIRCMenu extends EiraGuiScreen {

	private static final ResourceLocation meow = new ResourceLocation("mob.cat.meow");

	private static final int BUTTON_SIZE = 64;

	private GuiMenuButton btnServers;
	private GuiMenuButton btnTwitch;
	private GuiMenuButton btnHelp;
	private GuiMenuButton btnFriends;
	private GuiMenuButton btnScreenshots;
	private GuiMenuButton btnSettings;

	@Override
	public void initGui() {
		super.initGui();

		final int buttonCenterX = width / 2;
		final int buttonCenterY = height / 2;

		btnServers = new GuiMenuButton(0, "Servers", buttonCenterX - 132, buttonCenterY - 95, BUTTON_SIZE, BUTTON_SIZE, EiraGui.atlas.findRegion("menu_servers"));
		buttonList.add(btnServers);

		btnTwitch = new GuiMenuButton(1, "Twitch", buttonCenterX - 32, buttonCenterY - 95, BUTTON_SIZE, BUTTON_SIZE, EiraGui.atlas.findRegion("menu_twitch"));
		buttonList.add(btnTwitch);

		btnScreenshots = new GuiMenuButton(2, "Screenshots", buttonCenterX + 64, buttonCenterY - 95, BUTTON_SIZE, BUTTON_SIZE, EiraGui.atlas.findRegion("menu_screenshots"));
		buttonList.add(btnScreenshots);

		btnFriends = new GuiMenuButton(3, "EiraIRC Channels", buttonCenterX - 132, buttonCenterY, BUTTON_SIZE, BUTTON_SIZE, EiraGui.atlas.findRegion("menu_friends"));
		buttonList.add(btnFriends);

		btnHelp = new GuiMenuButton(4, "Help", buttonCenterX - 32, buttonCenterY, BUTTON_SIZE, BUTTON_SIZE, EiraGui.atlas.findRegion("menu_cat"));
		btnHelp.setPlayButtonSound(false);
		buttonList.add(btnHelp);

		btnSettings = new GuiMenuButton(5, "Settings", buttonCenterX + 64, buttonCenterY, BUTTON_SIZE, BUTTON_SIZE, EiraGui.atlas.findRegion("menu_settings"));
		buttonList.add(btnSettings);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnTwitch) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiTwitch(this));
		} else if(button == btnServers) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerConfigContainer(this));
		} else if(button == btnScreenshots) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshots(this));
		} else if(button == btnSettings) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCConfig(this));
		} else if(button == btnHelp) {
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(meow, 1f));
			Utils.openWebpage("http://blay09.net/?page_id=63");
		} else if(button == btnFriends) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiWelcome());
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		super.drawScreen(mouseX, mouseY, par3);
	}
}
