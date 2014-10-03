// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.bot;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;

public interface IRCBot {

	public boolean processCommand(IRCChannel channel, IRCUser sender, String message);
	public IRCConnection getConnection();
	public BotProfile getProfile(IRCContext context);
	public boolean isServerSide();
	
}
