// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.minecraftforge.event.Event;

public abstract class IRCEvent extends Event {

	public final IIRCConnection connection;
	public final IIRCBot bot;
	
	public IRCEvent(IIRCConnection connection) {
		this.connection = connection;
		this.bot = connection.getBot();
	}
	
}
