// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCUser;

public class IRCChannelTopicEvent extends IRCEvent {

	public final IRCChannel channel;
	public final IRCUser user;
	public final String topic;
	
	public IRCChannelTopicEvent(IRCConnection connection, IRCChannel channel, IRCUser user, String topic) {
		super(connection);
		this.channel = channel;
		this.user = user;
		this.topic = topic;
	}
}
