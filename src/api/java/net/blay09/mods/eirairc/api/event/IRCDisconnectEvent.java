// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever EiraIRC disconnects from an IRC server.
 */
public class IRCDisconnectEvent extends IRCEvent {

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection that was disconnected
	 */
	public IRCDisconnectEvent(IRCConnection connection) {
		super(connection);
	}

}
