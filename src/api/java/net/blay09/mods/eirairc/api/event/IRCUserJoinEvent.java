// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCUser;

public class IRCUserJoinEvent extends IRCEvent {

	public final IRCChannel channel;
	public final IRCUser user;
	
	public IRCUserJoinEvent(IRCConnection connection, IRCChannel channel, IRCUser user) {
		super(connection);
		this.channel = channel;
		this.user = user;
	}
}
