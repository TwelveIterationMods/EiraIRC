// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

import org.lwjgl.input.Keyboard;

import blay09.mods.eirairc.client.gui.GuiEiraChat;
import blay09.mods.eirairc.client.gui.GuiIRCSettings;
import blay09.mods.eirairc.client.gui.GuiNotification;
import blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.KeyConfig;
import blay09.mods.eirairc.config.ScreenshotConfig;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class EiraTickHandler implements ITickHandler {

	private int screenshotCheck;
	private boolean[] keyState = new boolean[10];
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	private boolean isKeyPressed(int keyCode, int keyIdx) {
		if(Keyboard.isKeyDown(keyCode)) {
			if(!keyState[keyIdx]) {
				keyState[keyIdx] = true;
				return true;
			}
		} else {
			keyState[keyIdx] = false;
		}
		return false;
	}
	
	private void handleKeyInput() {
		if(isKeyPressed(KeyConfig.openSettings, KeyConfig.IDX_OPENSETTINGS)) {
			if(Minecraft.getMinecraft().currentScreen == null) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiIRCSettings());
			}
		}
		if(isKeyPressed(KeyConfig.toggleTarget, KeyConfig.IDX_TOGGLETARGET)) {
			
		}
	}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(type.contains(TickType.CLIENT)) {
			if(ScreenshotConfig.manageScreenshots && ScreenshotConfig.screenshotAction != ScreenshotConfig.SCREENSHOT_NONE) {
				if(Keyboard.isKeyDown(Keyboard.KEY_F2)) {
					screenshotCheck = 10;
				} else if(screenshotCheck > 0) {
					screenshotCheck--;
					if(screenshotCheck == 0) {
						ScreenshotManager.getInstance().findNewScreenshots(true);
					}
				}
			}
			handleKeyInput();
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
