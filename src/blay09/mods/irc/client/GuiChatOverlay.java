// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.client;

import blay09.mods.irc.EiraIRC;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class GuiChatOverlay {

	public static final int COLOR_BACKGROUND = Integer.MIN_VALUE;
	public static final int COLOR_TEXT = 16777215;
	
	public static void drawOverlay(int i, int j, float k) {
		if(EiraIRC.instance.getConnectionCount() == 0) {
			return;
		}
		GuiScreen.drawRect(0, 0, 200, 15, COLOR_BACKGROUND);
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		String text = "Chatting to: ";
		switch(EiraIRC.instance.getChatTarget()) {
		case All:
			text += "All";
			break;
		case ChannelOnly:
			text += EiraIRC.instance.getTargetChannel().split(":")[1];
			break;
		case IRCOnly:
			text += "IRC only";
			break;
		case MinecraftOnly:
			text += "Minecraft only";
			break;
		}
		fontRenderer.drawString(text, 5, 5, COLOR_TEXT);
	}
	
	
}
