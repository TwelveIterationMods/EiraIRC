// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.util.Utils;

public class BotCommandWho implements IBotCommand {

	public BotCommandWho() {
	}
	
	@Override
	public String getCommandName() {
		return "who";
	}

	@Override
	public boolean isChannelCommand() {
		return true;
	}

	@Override
	public void processCommand(IRCBot bot, IRCChannel channel, IRCUser user, String[] args, IBotCommand commandSettings) {
		if(commandSettings.broadcastsResult()) {
			Utils.sendPlayerList(channel);
		}
	}

	@Override
	public boolean requiresAuth() {
		return false;
	}

	@Override
	public boolean broadcastsResult() {
		return true;
	}

	@Override
	public boolean allowArgs() {
		return false;
	}

	@Override
	public String getCommandDescription() {
		return "Prints out a list of all players online.";
	}
	
}
