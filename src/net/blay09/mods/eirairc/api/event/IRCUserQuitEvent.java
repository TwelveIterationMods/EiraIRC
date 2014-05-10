// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;

public class IRCUserQuitEvent extends IRCEvent {
	
	public final IIRCUser user;
	public final String message;
	
	public IRCUserQuitEvent(IIRCConnection connection, IIRCUser user, String message) {
		super(connection);
		this.user = user;
		this.message = message;
	}
}
