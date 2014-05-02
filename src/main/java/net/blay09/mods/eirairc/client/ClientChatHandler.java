// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;

public class ClientChatHandler {

	public static boolean handleClientChat(String text) {
		if (text.isEmpty()) {
			return false;
		}
		if (!text.startsWith("/")) {
			if (!EiraIRC.instance.getMCEventHandler().onClientChat(text)) {
				return true;
			}
		} else {
			if (text.startsWith("/me ")) {
				if (!EiraIRC.instance.getMCEventHandler().onClientEmote(
						text.substring(4))) {
					return true;
				}
			} else {
				int i = text.indexOf(' ');
				if(i == -1) {
					i = text.length();
				}
//				if(processCommand(Minecraft.getMinecraft().thePlayer, text.substring(1, i), i < text.length() ? text.substring(i + 1).split(" ") : new String[0])) {
//					return true;
//				}
			}
		}
		return false;
	}

	public static boolean processCommand(final EntityPlayer sender, String cmd, String[] args) {
		if (cmd.equals("irc")) {
			try {
				return IRCCommandHandler.processCommand(sender, args, false);
			} catch (WrongUsageException e) {
				sender.addChatMessage(Utils.getLocalizedChatMessage("irc.general.usage", Utils.getLocalizedMessageNoPrefix(e.getMessage())));
				return true;
			}
		}
		return false;
	}

}
