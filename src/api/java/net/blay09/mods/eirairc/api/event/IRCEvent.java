// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.bot.IRCBot;
import cpw.mods.fml.common.eventhandler.Event;
import net.blay09.mods.eirairc.api.IRCConnection;

public abstract class IRCEvent extends Event {

	public final IRCConnection connection;
	public final IRCBot bot;
	
	public IRCEvent(IRCConnection connection) {
		this.connection = connection;
		this.bot = connection.getBot();
	}
	
}
