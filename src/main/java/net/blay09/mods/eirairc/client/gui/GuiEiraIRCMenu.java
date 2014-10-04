// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshotList;
import net.blay09.mods.eirairc.client.gui.settings.GuiServerList;
import net.blay09.mods.eirairc.client.gui.settings.GuiTwitch;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiEiraIRCMenu extends GuiScreen {

	private static final ResourceLocation twitchIcon = new ResourceLocation("eirairc", "gfx/menu.png");

	private static final int BUTTON_WIDTH = 180;
	private static final int BUTTON_HEIGHT = 20;

	private GuiButton btnSettings;
	private GuiButton btnServerList;
	private GuiButton btnTwitch;
	private GuiButton btnScreenshots;
	private GuiButton btnFriendIgnore;
	private GuiButton btnBack;

	@Override
	public void initGui() {
//		final int buttonX = width / 2 - 90;

//		btnBack = new GuiButton(0, width / 2 - 100, height / 2 + 90, 200, 20, I18n.format("eirairc:gui.back"));
//		buttonList.add(btnBack);
//
//		btnSettings = new GuiButton(1, buttonX, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("eirairc:gui.settings"));
//		buttonList.add(btnSettings);
//
//		btnServerList = new GuiButton(2, buttonX, height / 2 - 40, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.serverList"));
//		buttonList.add(btnServerList);
//
//		btnFriendIgnore = new GuiButton(3, buttonX, height / 2 - 15, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.friendsIgnoreList"));
//		btnFriendIgnore.enabled = false;
//		buttonList.add(btnFriendIgnore);
//
//		btnTwitch = new GuiButton(4, buttonX, height / 2 + 10, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.twitch"));
//		buttonList.add(btnTwitch);
//
//		btnScreenshots = new GuiButton(5, buttonX, height / 2 + 35, BUTTON_WIDTH, BUTTON_HEIGHT, Utils.getLocalizedMessage("irc.gui.settings.screenshots"));
//		buttonList.add(btnScreenshots);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnTwitch) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiTwitch(this));
		} else if(button == btnServerList) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiServerList());
		} else if(button == btnScreenshots) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshotList(this));
		} else if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(null);
		} else if(button == btnSettings) {
			Minecraft.getMinecraft().displayGuiScreen(new EiraIRCConfigGUI(this));
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		final int menuWidth = 300;
		final int menuHeight = 200;
		final int menuX = width / 2 - menuWidth / 2;
		final int menuY = height / 2 - menuHeight / 2;
		drawGradientRect(menuX, menuY, menuX + menuWidth, menuY + menuHeight, -1072689136, -804253680);

		final int buttonSize = 64;
		final int buttonCenterX = width / 2;
		final int buttonCenterY = height / 2;
		final int buttonTop = buttonCenterY - 95;
		final int buttonBottom = buttonCenterY;

		GL11.glEnable(GL11.GL_BLEND);

		this.mc.getTextureManager().bindTexture(twitchIcon);
		this.drawTexturedModalRect(buttonCenterX - buttonSize / 2 - 100, buttonTop, 0, buttonSize, buttonSize, buttonSize); // Servers
		this.drawTexturedModalRect(buttonCenterX - buttonSize / 2, buttonTop, 0, 0, buttonSize, buttonSize); // Twitch
		this.drawTexturedModalRect(buttonCenterX - buttonSize / 2 + 100, buttonTop, buttonSize, buttonSize, buttonSize, buttonSize); // Placeholder
		this.drawTexturedModalRect(buttonCenterX - buttonSize / 2 - 100, buttonBottom, buttonSize * 3, 0, buttonSize, buttonSize); // Friends
		this.drawTexturedModalRect(buttonCenterX - buttonSize / 2, buttonBottom, buttonSize, 0, buttonSize, buttonSize); // Screenshots
		this.drawTexturedModalRect(buttonCenterX - buttonSize / 2 + 100, buttonBottom, buttonSize * 2, 0, buttonSize, buttonSize); // Settings

		GL11.glDisable(GL11.GL_BLEND);

		this.drawCenteredString(fontRendererObj, "Servers", width / 2 - 100, buttonTop + 70, Globals.TEXT_COLOR);
		this.drawCenteredString(fontRendererObj, "Twitch", width / 2, buttonTop + 70, Globals.TEXT_COLOR);
		this.drawCenteredString(fontRendererObj, "???", width / 2 + 100, buttonTop + 70, Globals.TEXT_COLOR);
		this.drawCenteredString(fontRendererObj, "Friends", width / 2 - 100, buttonBottom + 70, Globals.TEXT_COLOR);
		this.drawCenteredString(fontRendererObj, "Screenshots", width / 2, buttonBottom + 70, Globals.TEXT_COLOR);
		this.drawCenteredString(fontRendererObj, "Settings", width / 2 + 100, buttonBottom + 70, Globals.TEXT_COLOR);

		super.drawScreen(par1, par2, par3);
	}
}
