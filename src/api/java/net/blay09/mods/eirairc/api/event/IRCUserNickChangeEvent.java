// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCUser;

public class IRCUserNickChangeEvent extends IRCEvent {

	public final IRCUser user;
	public final String oldNick;
	public final String newNick;
	
	public IRCUserNickChangeEvent(IRCConnection connection, IRCUser user, String oldNick, String newNick) {
		super(connection);
		this.user = user;
		this.oldNick = oldNick;
		this.newNick = newNick;
	}
}
