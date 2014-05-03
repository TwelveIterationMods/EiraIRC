package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;

public class IRCChannelTopicEvent extends IRCEvent {

	public final IIRCChannel channel;
	public final IIRCUser user;
	public final String topic;
	
	public IRCChannelTopicEvent(IIRCConnection connection, IIRCChannel channel, IIRCUser user, String topic) {
		super(connection);
		this.channel = channel;
		this.user = user;
		this.topic = topic;
	}
}
