// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever EiraIRC successfully joined a channel.
 */
public class IRCChannelJoinedEvent extends IRCMessageEvent {

	/**
	 * the channel that was joined
	 */
	public final IRCChannel channel;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection the channel that was joined is on
	 * @param channel the channel that was joined
	 */
	public IRCChannelJoinedEvent(IRCConnection connection, IRCMessage rawMessage, IRCChannel channel) {
		super(connection, rawMessage);
		this.channel = channel;
	}

}
