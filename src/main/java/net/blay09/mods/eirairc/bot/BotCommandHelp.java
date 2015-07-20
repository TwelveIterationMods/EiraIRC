// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.util.I19n;
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
	public void processCommand(IRCBot bot, IRCChannel channel, IRCUser user, String[] args, IBotCommand commandSettings) {
		if(channel != null) {
			StringBuilder sb = new StringBuilder();
			for(IBotCommand command : ((IRCBotImpl) bot).getCommands()) {
				if(sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(command.getCommandName());
			}
			if(commandSettings.broadcastsResult()) {
				channel.message(I19n.format("eirairc:bot.cmdlist", sb.toString()));
			} else {
				user.notice(I19n.format("eirairc:bot.cmdlist", sb.toString()));
			}
		} else {
			user.notice("***** EiraIRC Help *****");
			user.notice("EiraIRC connects a Minecraft client or a whole server");
			user.notice("to one or multiple IRC channels and servers.");
			user.notice("Visit http://blay09.net/?page_id=63 for more information on this bot.");
			user.notice(" ");
			user.notice("The following commands are available:");
			for(IBotCommand command : ((IRCBotImpl) bot).getCommands()) {
				user.notice(command.getCommandName().toUpperCase() + " : " + command.getCommandDescription());
			}
			user.notice("***** End of Help *****");
		}
	}

	@Override
	public boolean requiresAuth() {
		return false;
	}

	@Override
	public boolean broadcastsResult() {
		return false;
	}

	@Override
	public boolean allowArgs() {
		return false;
	}

	@Override
	public String getCommandDescription() {
		return "Prints out this command list.";
	}
	
}
