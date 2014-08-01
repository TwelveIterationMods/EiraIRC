// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.bot;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCUser;

public interface IBotCommand {

	public String getCommandName();
	public String getCommandDescription();
	public boolean isChannelCommand();
	public void processCommand(IRCBot bot, IRCChannel channel, IRCUser user, String[] args);
	
}
