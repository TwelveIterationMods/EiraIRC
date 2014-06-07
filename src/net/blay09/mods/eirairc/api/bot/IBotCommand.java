// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.bot;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;

public interface IBotCommand {

	public String getCommandName();
	public String getCommandDescription();
	public boolean isChannelCommand();
	public void processCommand(IIRCBot bot, IIRCChannel channel, IIRCUser user, String[] args);
	
}
