// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.chat;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.ClientChatHandler;
import net.blay09.mods.eirairc.client.gui.settings.GuiSettings;
import net.blay09.mods.eirairc.config.KeyConfig;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.client.ClientCommandHandler;

public class GuiEiraChat {

	public static final int COLOR_BACKGROUND = Integer.MIN_VALUE;
	
	private ChatSessionHandler chatSession;
	private List<String> sentHistory = new ArrayList<String>();

	
	
	public GuiEiraChat() {
	}

	public void addToSentMessages(String message) {
		sentHistory.add(message);
	}

	public List<String> getSentHistory() {
		return sentHistory;
	}

	public ChatSessionHandler getChatSession() {
		return chatSession;
	}

}
