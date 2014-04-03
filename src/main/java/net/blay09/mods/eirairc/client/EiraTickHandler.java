// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import java.util.EnumSet;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.gui.chat.GuiChatExtended;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChat;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChatInput;
import net.blay09.mods.eirairc.client.gui.settings.GuiKeybinds;
import net.blay09.mods.eirairc.client.gui.settings.GuiSettings;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.KeyConfig;
import net.blay09.mods.eirairc.config.ScreenshotConfig;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.net.packet.PacketRecLiveState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class EiraTickHandler {

	private GuiEiraChat eiraChat;
	private int screenshotCheck;
	private boolean[] keyState = new boolean[10];

	public EiraTickHandler(GuiEiraChat eiraChat) {
		this.eiraChat = eiraChat;
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
		if(isKeyPressed(KeyConfig.openMenu, KeyConfig.IDX_OPENSETTINGS)) {
			if(Minecraft.getMinecraft().currentScreen == null) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
			}
		}
		if(isKeyPressed(KeyConfig.toggleRecording, KeyConfig.IDX_TOGGLERECORDING)) {
			EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
			EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(player.getCommandSenderName());
			playerInfo.isRecording = !playerInfo.isRecording;
			EiraIRC.instance.packetPipeline.sendToServer(new PacketRecLiveState(player.getCommandSenderName(), playerInfo.isRecording, playerInfo.isLive));
		}
		if(isKeyPressed(KeyConfig.toggleLive, KeyConfig.IDX_TOGGLELIVE)) {
			EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
			EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(player.getCommandSenderName());
			playerInfo.isLive = !playerInfo.isLive;
			EiraIRC.instance.packetPipeline.sendToServer(new PacketRecLiveState(player.getCommandSenderName(), playerInfo.isRecording, playerInfo.isLive));
		}
		if(isKeyPressed(KeyConfig.screenshotShare, KeyConfig.IDX_SCREENSHOTSHARE)) {
			Screenshot screenshot = ScreenshotManager.getInstance().takeScreenshot();
			ScreenshotManager.getInstance().uploadScreenshot(screenshot);
			ScreenshotManager.getInstance().shareScreenshot(screenshot);
		}
	}
	
	@SubscribeEvent
	public void worldJoined(PlayerLoggedInEvent event) {
		if(!EiraIRC.instance.isIRCRunning()) {
			EiraIRC.instance.startIRC();
		}
	}
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent event) {
		if(ScreenshotConfig.manageScreenshots && ScreenshotConfig.screenshotAction != ScreenshotConfig.VALUE_NONE) {
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
			if(DisplayConfig.vanillaChat) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiChatExtended());
			} else {
				Minecraft.getMinecraft().displayGuiScreen(new GuiEiraChatInput(eiraChat));
			}
		}
	}
	
	@SubscribeEvent
	public void renderTick(RenderTickEvent event) {
		EiraIRC.proxy.renderTick(event.renderTickTime);
	}

}
