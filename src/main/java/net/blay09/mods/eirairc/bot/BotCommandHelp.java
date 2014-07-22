// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.util.Utils;

public class BotCommandHelp implements IBotCommand {

	@Override
	public String getCommandName() {
		return "help";
	}

	@Override
	public boolean isChannelCommand() {
		return true;
	}

	@Override
	public void processCommand(IIRCBot bot, IIRCChannel channel, IIRCUser user, String[] args) {
		if(channel != null) {
			StringBuilder sb = new StringBuilder();
			for(IBotCommand command : bot.getProfile(channel).getCommands()) {
				if(sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(command.getCommandName());
			}
			user.notice(Utils.getLocalizedMessage("irc.bot.cmdlist", sb.toString()));
		} else {
			user.notice("***** EiraIRC Help *****");
			user.notice("EiraIRC connects a Minecraft client or a whole server");
			user.notice("to one or multiple IRC channels and servers.");
			user.notice("Visit http://blay09.net/?page_id=63 for more information on this bot.");
			user.notice(" ");
			user.notice("The following commands are available:");
			for(IBotCommand command : bot.getMainProfile().getCommands()) {
				user.notice(command.getCommandName().toUpperCase() + " : " + command.getCommandDescription());
			}
			user.notice("***** End of Help *****");
		}
	}

	@Override
	public String getCommandDescription() {
		return "Prints out this command list.";
	}
	
}
