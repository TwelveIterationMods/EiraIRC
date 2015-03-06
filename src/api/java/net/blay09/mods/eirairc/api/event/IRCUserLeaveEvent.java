// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCUser;

public class IRCUserLeaveEvent extends IRCEvent {

	public final IRCChannel channel;
	public final IRCUser user;
	public final String message;
	
	public IRCUserLeaveEvent(IRCConnection connection, IRCChannel channel, IRCUser user, String message) {
		super(connection);
		this.channel = channel;
		this.user = user;
		this.message = message;
	}
}
