// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever an IRC error code is returned from the server.
 */
public class IRCErrorEvent extends IRCEvent {

	/**
	 * the numeric error code
	 */
	public final int numeric;

	/**
	 * the arguments sent along with the error
	 */
	public final String[] args;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection that was disconnected
 	 * @param numeric the numeric error code
	 * @param args the arguments sent along with the error
	 */
	public IRCErrorEvent(IRCConnection connection, int numeric, String[] args) {
		super(connection);
		this.numeric = numeric;
		this.args = args;
	}

}
