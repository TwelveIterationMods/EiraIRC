// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class IRCEvent extends Event {

	public final IRCConnection connection;
	public final IRCBot bot;
	
	public IRCEvent(IRCConnection connection) {
		this.connection = connection;
		this.bot = connection.getBot();
	}
	
}
