// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

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
