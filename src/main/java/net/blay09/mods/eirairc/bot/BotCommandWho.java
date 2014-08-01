// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.util.Utils;

public class BotCommandWho implements IBotCommand {

	private String name;
	
	public BotCommandWho(String name) {
		this.name = name;
	}
	
	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public boolean isChannelCommand() {
		return true;
	}

	@Override
	public void processCommand(IRCBot bot, IRCChannel channel, IRCUser user, String[] args) {
		Utils.sendPlayerList(user);
	}

	@Override
	public String getCommandDescription() {
		return "Prints out a list of all players online.";
	}
	
}
