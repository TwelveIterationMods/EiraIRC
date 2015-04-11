// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever EiraIRC successfully joined a channel.
 */
public class IRCChannelJoinedEvent extends IRCEvent {

	/**
	 * the channel that was joined
	 */
	public final IRCChannel channel;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection the channel that was joined is on
	 * @param channel the channel that was joined
	 */
	public IRCChannelJoinedEvent(IRCConnection connection, IRCChannel channel) {
		super(connection);
		this.channel = channel;
	}

}
