// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;

public class IRCReconnectEvent extends IRCEvent {

	public final int waitingTime;

	public IRCReconnectEvent(IRCConnection connection, int waitingTime) {
		super(connection);
		this.waitingTime = waitingTime;
	}

}
