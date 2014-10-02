// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.gui.chat.GuiChatExtended;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChat;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChatInput;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshotList;
import net.blay09.mods.eirairc.client.gui.settings.GuiKeybinds;
import net.blay09.mods.eirairc.client.gui.settings.GuiSettings;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.TempPlaceholder;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.net.PacketHandler;
import net.blay09.mods.eirairc.net.message.MessageRecLiveState.CMessageRecLiveState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

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

	private boolean isKeyPressed(int keyCode, int keyIdx) {
		if(keyCode == -1) {
			return false;
		}
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
		if(Minecraft.getMinecraft().currentScreen instanceof GuiKeybinds) {
			return;
		}
		if(isKeyPressed(ClientGlobalConfig.keyOpenMenu, KEY_IDX_OPENSETTINGS)) {
			if(Minecraft.getMinecraft().currentScreen == null) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
			}
		}
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		if(player != null) {
			if(isKeyPressed(ClientGlobalConfig.keyToggleRecording, KEY_IDX_TOGGLERECORDING)) {
				EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(player.getCommandSenderName());
				playerInfo.isRecording = !playerInfo.isRecording;
				PacketHandler.INSTANCE.sendToServer(new CMessageRecLiveState(player.getCommandSenderName(), playerInfo.isRecording, playerInfo.isLive));
			}
			if(isKeyPressed(ClientGlobalConfig.keyToggleLive, KEY_IDX_TOGGLELIVE)) {
				EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(player.getCommandSenderName());
				playerInfo.isLive = !playerInfo.isLive;
				PacketHandler.INSTANCE.sendToServer(new CMessageRecLiveState(player.getCommandSenderName(), playerInfo.isRecording, playerInfo.isLive));
			}
		}
		if(isKeyPressed(ClientGlobalConfig.keyScreenshotShare, KEY_IDX_SCREENSHOTSHARE)) {
			Screenshot screenshot = ScreenshotManager.getInstance().takeScreenshot();
			if(screenshot != null) {
				ScreenshotManager.getInstance().uploadScreenshot(screenshot, TempPlaceholder.VALUE_UPLOADSHARE);
			}
		}
		if(isKeyPressed(ClientGlobalConfig.keyOpenScreenshots, KEY_IDX_OPENSCREENSHOTS)) {
			if(Minecraft.getMinecraft().currentScreen == null) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshotList(null));
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
		if(!EiraIRC.instance.isIRCRunning()) {
			EiraIRC.instance.startIRC();
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
