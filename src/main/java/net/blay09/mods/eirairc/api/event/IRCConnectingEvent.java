// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IRCConnection;

public class IRCConnectingEvent extends IRCEvent {

	public IRCConnectingEvent(IRCConnection connection) {
		super(connection);
	}

}
