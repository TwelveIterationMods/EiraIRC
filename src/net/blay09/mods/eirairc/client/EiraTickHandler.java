// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import java.util.EnumSet;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.gui.chat.GuiChatExtended;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChat;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshotList;
import net.blay09.mods.eirairc.client.gui.settings.GuiKeybinds;
import net.blay09.mods.eirairc.client.gui.settings.GuiSettings;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.KeyConfig;
import net.blay09.mods.eirairc.config.ScreenshotConfig;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.net.packet.PacketRecLiveState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.packet.Packet;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class EiraTickHandler implements ITickHandler {

	private GuiEiraChat eiraChat;
	private int screenshotCheck;
	private boolean[] keyState = new boolean[10];
	private final int keyChat;
	private final int keyCommand;

	public EiraTickHandler(GuiEiraChat eiraChat) {
		this.eiraChat = eiraChat;
		keyChat = Minecraft.getMinecraft().gameSettings.keyBindChat.keyCode;
		keyCommand = Minecraft.getMinecraft().gameSettings.keyBindCommand.keyCode;
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
			Packet packet = new PacketRecLiveState(player.username, playerInfo.isRecording, playerInfo.isLive).createPacket();
			if(packet != null) {
				player.sendQueue.addToSendQueue(packet);
			}
		}
		if(isKeyPressed(KeyConfig.toggleLive, KeyConfig.IDX_TOGGLELIVE)) {
			EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
			EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(player.getCommandSenderName());
			playerInfo.isLive = !playerInfo.isLive;
			Packet packet = new PacketRecLiveState(player.username, playerInfo.isRecording, playerInfo.isLive).createPacket();
			if(packet != null) {
				player.sendQueue.addToSendQueue(packet);
			}
		}
		if(isKeyPressed(KeyConfig.screenshotShare, KeyConfig.IDX_SCREENSHOTSHARE)) {
			Screenshot screenshot = ScreenshotManager.getInstance().takeScreenshot();
			ScreenshotManager.getInstance().uploadScreenshot(screenshot, ScreenshotConfig.VALUE_UPLOADSHARE);
		}
		if(isKeyPressed(KeyConfig.openScreenshots, KeyConfig.IDX_OPENSCREENSHOTS)) {
			if(Minecraft.getMinecraft().currentScreen == null) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshotList(null));
			}
		}
	}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(type.contains(TickType.CLIENT)) {
			clientTick(tickData);
		}
		if(type.contains(TickType.RENDER)) {
			renderTick(tickData);
		}
	}
	
	public void clientTick(Object... tickData) {
		if(Keyboard.isKeyDown(Keyboard.KEY_F2)) {
			screenshotCheck = 10;
		} else if(screenshotCheck > 0) {
			screenshotCheck--;
			if(screenshotCheck == 0) {
				ScreenshotManager.getInstance().findNewScreenshots(true);
			}
		}
		ScreenshotManager.getInstance().clientTick();
		handleKeyInput();
		if(Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen.getClass() == GuiChat.class) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiChatExtended());
		}
	}
	
	public void renderTick(Object... tickData) {
		EiraIRC.proxy.renderTick((Float) tickData[0]);
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT, TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "EIRC-CE";
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}
}
