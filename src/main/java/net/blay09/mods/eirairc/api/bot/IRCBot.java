// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.bot;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCUser;

import java.util.Collection;

public interface IRCBot {

	public boolean processCommand(IRCChannel channel, IRCUser sender, String message);
	public IRCConnection getConnection();
	public boolean isServerSide();
	public void registerCommand(IBotCommand command);
	public Collection<IBotCommand> getCommands();

}
