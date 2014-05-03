// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
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
			user.notice(Utils.getLocalizedMessage("irc.bot.cmdlist"));
		} else {
			user.notice("***** EiraIRC Help *****");
			user.notice("EiraIRC connects a Minecraft client or a whole server");
			user.notice("to one or multiple IRC channels and servers.");
			user.notice("Visit http://blay09.net/?page_id=63 for more information on this bot.");
			user.notice(" ");
			user.notice("The following commands are available:");
			user.notice("HELP            Prints this command list");
			user.notice("WHO            Prints out a list of all players online");
			user.notice("ALIAS            Look up the username of an online player");
			user.notice("MSG            Send a private message to an online player");
			user.notice("OP            Perform an OP-command on the server (requires permissions)");
			user.notice("***** End of Help *****");
		}
	}
	
}
