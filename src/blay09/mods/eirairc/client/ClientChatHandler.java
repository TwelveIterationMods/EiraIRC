// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.Utils;
import blay09.mods.eirairc.command.IRCCommandHandler;

public class ClientChatHandler {
	
	public static boolean handleClientChat(String text) {
		if(!text.startsWith("/")) {
			if(!EiraIRC.instance.getEventHandler().onClientChat(text)) {
				return true;
			}
			return false;
		}
		int i = text.indexOf(" ");
		if(i == -1) {
			i = text.length();
		}
		if(processCommand(Minecraft.getMinecraft().thePlayer, text.substring(1, i), i < text.length() ? text.substring(i + 1).split(" ") : new String[0])) {
			return true;
		}
		return false;
	}
	
	public static boolean processCommand(final EntityPlayer sender, String cmd, final String[] args) {
		if(cmd.equals("irc")) {
			try {
				return IRCCommandHandler.processCommand(sender, args, false);
			} catch (WrongUsageException e) {
				sender.sendChatToPlayer(Utils.getLocalizedChatMessage("irc.general.usage", Utils.getLocalizedMessage("irc.commands.irc")));
				return true;
			}
		} else if(cmd.equals("me")) {
			String emote = "";
			for(int i = 0; i < args.length; i++) {
				emote += " " + args[i];
			}
			emote = emote.trim();
			if(emote.length() == 0) {
				return false;
			}
			if(!EiraIRC.instance.getEventHandler().onClientEmote(emote)) {
				return true;
			}
			return false;
		}
		return false;
	}
}
