// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.bot;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCUser;

import java.util.Collection;

public interface IRCBot {

	public boolean processCommand(IRCChannel channel, IRCUser sender, String message);
	public IRCConnection getConnection();
	public boolean isServerSide();
	public void registerCommand(IBotCommand command);
	public Collection<IBotCommand> getCommands();

}
