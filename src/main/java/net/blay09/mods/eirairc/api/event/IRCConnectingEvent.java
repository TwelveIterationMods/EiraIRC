package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;

public class IRCConnectingEvent extends IRCEvent {

	public IRCConnectingEvent(IIRCConnection connection) {
		super(connection);
	}

}
