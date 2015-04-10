// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.bot;

import net.blay09.mods.eirairc.api.irc.IRCConnection;

public interface IRCBot {

	/**
	 * @return the irc connection this bot is running on
	 */
	public IRCConnection getConnection();

	/**
	 * @return true if this is a server-side bot
	 */
	public boolean isServerSide();

	/**
	 * Should be run from within a {@code ReloadBotCommandsEvent}
	 * @param command the command implementation to be registered
	 */
	public void registerCommand(IBotCommand command);

}
