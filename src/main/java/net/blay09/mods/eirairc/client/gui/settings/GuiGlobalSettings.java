// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.TempPlaceholder;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiGlobalSettings extends GuiScreen {
	
	private static final int BUTTON_WIDTH = 190;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 5;
	
	private GuiButton btnPersistentConnections;
	private GuiButton btnBack;
	
	@Override
	public void initGui() {
		int leftX = width / 2 - BUTTON_WIDTH - BUTTON_GAP;
		int rightX = width / 2 + BUTTON_GAP;
		
		btnPersistentConnections = new GuiButton(1, leftX, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnPersistentConnections);
		
		btnBack = new GuiButton(0, width / 2 - 100, height / 2 + 65, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		loadFromConfig();
	}
	
	public void loadFromConfig() {
		updateButtonText();
	}
	
	public void updateButtonText() {
		String yes = Utils.getLocalizedMessage("irc.gui.yes");
		String no = Utils.getLocalizedMessage("irc.gui.no");
		btnPersistentConnections.displayString = Utils.getLocalizedMessage("irc.gui.globalSettings.persistentConnection", (ClientGlobalConfig.persistentConnection ? yes : no));
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnPersistentConnections) {
			ClientGlobalConfig.persistentConnection = !ClientGlobalConfig.persistentConnection;
		} else if(button == btnBack) {
			ConfigurationHandler.save();
			Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
			return;
		}
		updateButtonText();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.globalSettings"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}
	
}
