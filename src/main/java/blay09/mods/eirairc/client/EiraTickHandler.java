// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.client.gui.GuiEiraChat;
import blay09.mods.eirairc.client.gui.GuiKeybinds;
import blay09.mods.eirairc.client.gui.GuiSettings;
import blay09.mods.eirairc.client.screenshot.Screenshot;
import blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import blay09.mods.eirairc.config.KeyConfig;
import blay09.mods.eirairc.config.ScreenshotConfig;
import blay09.mods.eirairc.net.EiraPlayerInfo;
import blay09.mods.eirairc.net.packet.PacketRecLiveState;

public class EiraTickHandler {

	private int screenshotCheck;
	private boolean[] keyState = new boolean[10];

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
				Minecraft.getMinecraft().func_147108_a(new GuiSettings());
			}
		}
		if(isKeyPressed(KeyConfig.toggleRecording, KeyConfig.IDX_TOGGLERECORDING)) {
			EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
			EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(player.getCommandSenderName());
			playerInfo.isRecording = !playerInfo.isRecording;
			// TODO fix this
//			Packet packet = new PacketRecLiveState(player.getCommandSenderName(), playerInfo.isRecording, playerInfo.isLive).createPacket();
//			if(packet != null) {
//				player.sendQueue.addToSendQueue(packet);
//			}
		}
		if(isKeyPressed(KeyConfig.toggleLive, KeyConfig.IDX_TOGGLELIVE)) {
			EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
			EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(player.getCommandSenderName());
			playerInfo.isLive = !playerInfo.isLive;
			// TODO fix this
//			Packet packet = new PacketRecLiveState(player.getCommandSenderName(), playerInfo.isRecording, playerInfo.isLive).createPacket();
//			if(packet != null) {
//				player.sendQueue.addToSendQueue(packet);
//			}
		}
		if(isKeyPressed(KeyConfig.screenshotShare, KeyConfig.IDX_SCREENSHOTSHARE)) {
			Screenshot screenshot = ScreenshotManager.getInstance().takeScreenshot();
			ScreenshotManager.getInstance().uploadScreenshot(screenshot);
			ScreenshotManager.getInstance().shareScreenshot(screenshot);
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
			Minecraft.getMinecraft().func_147108_a(new GuiEiraChat());
		}
	}
	
	@SubscribeEvent
	public void renderTick(RenderTickEvent event) {
		EiraIRC.proxy.renderTick(event.renderTickTime);
	}

}
