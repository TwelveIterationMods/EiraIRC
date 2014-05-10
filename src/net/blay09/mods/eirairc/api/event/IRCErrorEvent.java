// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;

public class IRCErrorEvent extends IRCEvent {

	public final int numeric;
	public final String[] args;
	
	public IRCErrorEvent(IIRCConnection connection, int numeric, String[] args) {
		super(connection);
		this.numeric = numeric;
		this.args = args;
	}

}
