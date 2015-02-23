// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCUser;

public class IRCUserQuitEvent extends IRCEvent {
	
	public final IRCUser user;
	public final String message;
	
	public IRCUserQuitEvent(IRCConnection connection, IRCUser user, String message) {
		super(connection);
		this.user = user;
		this.message = message;
	}
}
