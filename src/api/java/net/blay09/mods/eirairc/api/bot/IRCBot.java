// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.bot;

import net.blay09.mods.eirairc.api.irc.IRCConnection;

public interface IRCBot {

	/**
	 * @return the irc connection this bot is running on
	 */
	IRCConnection getConnection();

	/**
	 * @return true if this is a server-side bot
	 */
	boolean isServerSide();

	/**
	 * Should be run from within a {@code ReloadBotCommandsEvent}
	 * @param command the command implementation to be registered
	 */
	void registerCommand(IBotCommand command);

}
