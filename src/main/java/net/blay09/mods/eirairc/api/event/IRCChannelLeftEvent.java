package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;

public class IRCChannelLeftEvent extends IRCEvent {

	public final IIRCChannel channel;
	
	public IRCChannelLeftEvent(IIRCConnection connection, IIRCChannel channel) {
		super(connection);
		this.channel = channel;
	}

}
