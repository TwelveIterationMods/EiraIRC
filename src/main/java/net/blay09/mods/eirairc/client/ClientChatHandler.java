// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.command.IRCCommandHandler;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class ClientChatHandler {
	
	public static boolean handleClientChat(String text) {
		if(!text.startsWith("/")) {
			if(!EiraIRC.instance.getMCEventHandler().onClientChat(text)) {
				return true;
			}
		} else if(text.startsWith("/me ")) {
			if(!EiraIRC.instance.getMCEventHandler().onClientEmote(text.substring(4))) {
				return true;
			}
		}
		return false;
	}
	
}
