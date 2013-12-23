// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

import org.lwjgl.input.Keyboard;

import blay09.mods.eirairc.client.gui.GuiEiraChat;
import blay09.mods.eirairc.client.gui.GuiNotification;
import blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.ScreenshotConfig;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class EiraTickHandler implements ITickHandler {

	private int screenshotCheck;
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(type.contains(TickType.CLIENT)) {
			if(ScreenshotConfig.screenshotAction != ScreenshotConfig.SCREENSHOT_NONE) {
				// TODO should try to get a proper event for screenshots into Forge once the 1.7 mess is over
				if(Keyboard.isKeyDown(Keyboard.KEY_F2)) {
					screenshotCheck = 10;
				} else if(screenshotCheck > 0) {
					screenshotCheck--;
					if(screenshotCheck == 0) {
						System.out.println("Scanning Screenshots...");
						ScreenshotManager.getInstance().findNewScreenshots(true);
					}
				}
			}
			if(Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen.getClass() == GuiChat.class) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiEiraChat());
			}
		}
		if(type.contains(TickType.RENDER)) {
			float delta = (Float) tickData[0];
			GuiNotification.instance.updateAndRender(delta);
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT, TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "EIRC-CE";
	}

}
