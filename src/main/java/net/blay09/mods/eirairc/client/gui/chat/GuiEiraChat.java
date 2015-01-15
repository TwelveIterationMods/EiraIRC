// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.chat;

import java.util.ArrayList;
import java.util.List;

import net.blay09.mods.eirairc.handler.ChatSessionHandler;

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
