// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;

public class IRCDisconnectEvent extends IRCEvent {

	public IRCDisconnectEvent(IRCConnection connection) {
		super(connection);
	}

}
