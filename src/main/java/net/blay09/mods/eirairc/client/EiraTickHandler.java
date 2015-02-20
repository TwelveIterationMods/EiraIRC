// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCMenu;
import net.blay09.mods.eirairc.client.gui.chat.GuiChatExtended;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChat;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChatInput;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshots;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.ScreenshotAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class EiraTickHandler {

	public static final int KEY_IDX_OPENSETTINGS = 0;
	public static final int KEY_IDX_TOGGLETARGET = 1;
	public static final int KEY_IDX_SCREENSHOTSHARE = 2;
	public static final int KEY_IDX_TOGGLERECORDING = 3;
	public static final int KEY_IDX_TOGGLELIVE = 4;
	public static final int KEY_IDX_OPENSCREENSHOTS = 5;

	private GuiEiraChat eiraChat;
	private int screenshotCheck;
	private boolean[] keyState = new boolean[10];
	private final int keyChat;
	private final int keyCommand;

	public EiraTickHandler(GuiEiraChat eiraChat) {
		this.eiraChat = eiraChat;
		keyChat = Minecraft.getMinecraft().gameSettings.keyBindChat.getKeyCode();
		keyCommand = Minecraft.getMinecraft().gameSettings.keyBindCommand.getKeyCode();
	}

	private boolean isKeyPressed(KeyBinding keyBinding, int keyIdx) {
		if(keyBinding.getKeyCode() <= 0) {
			return false;
		}
		if(Keyboard.isKeyDown(keyBinding.getKeyCode())) {
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
		if(isKeyPressed(ClientGlobalConfig.keyOpenMenu, KEY_IDX_OPENSETTINGS)) {
			if(Minecraft.getMinecraft().currentScreen == null) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCMenu());
			}
		}
		if(isKeyPressed(ClientGlobalConfig.keyScreenshotShare, KEY_IDX_SCREENSHOTSHARE)) {
			Screenshot screenshot = ScreenshotManager.getInstance().takeScreenshot();
			if(screenshot != null) {
				ScreenshotManager.getInstance().uploadScreenshot(screenshot, ScreenshotAction.UploadShare);
			}
		}
		if(isKeyPressed(ClientGlobalConfig.keyOpenScreenshots, KEY_IDX_OPENSCREENSHOTS)) {
			if(Minecraft.getMinecraft().currentScreen == null) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshots(null));
			}
		}
	}
	
	@SubscribeEvent
	public void keyInput(KeyInputEvent event) {
		if(Keyboard.getEventKeyState()) {
			GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
			if(currentScreen == null || currentScreen.getClass() == GuiChat.class) {
				if(Keyboard.getEventKey() == keyChat) {
					Minecraft.getMinecraft().gameSettings.keyBindChat.isPressed();
					if(true || ClientGlobalConfig.vanillaChat) {
						Minecraft.getMinecraft().displayGuiScreen(new GuiChatExtended());
					} else {
						Minecraft.getMinecraft().displayGuiScreen(new GuiEiraChatInput(eiraChat));
					}
				} else if(Keyboard.getEventKey() == keyChat) {
					Minecraft.getMinecraft().gameSettings.keyBindChat.isPressed();
					if(true || ClientGlobalConfig.vanillaChat) {
						Minecraft.getMinecraft().displayGuiScreen(new GuiChatExtended("/"));
					} else {
						Minecraft.getMinecraft().displayGuiScreen(new GuiEiraChatInput(eiraChat, "/"));
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void worldJoined(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if(!EiraIRC.instance.getConnectionManager().isIRCRunning()) {
			EiraIRC.instance.getConnectionManager().startIRC();
		}
	}
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent event) {
		if(Keyboard.isKeyDown(Keyboard.KEY_F2)) {
			screenshotCheck = 10;
		} else if(screenshotCheck > 0) {
			screenshotCheck--;
			if(screenshotCheck == 0) {
				ScreenshotManager.getInstance().findNewScreenshots(true);
			}
		}
		ScreenshotManager.getInstance().clientTick(event);
		handleKeyInput();
	}
	
	@SubscribeEvent
	public void renderTick(RenderTickEvent event) {
		EiraIRC.proxy.renderTick(event.renderTickTime);
	}

}
