// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever someone joins an IRC channel EiraIRC is in.
 */
public class IRCUserJoinEvent extends IRCRawMessageEvent {

	/**
	 * the channel that the user joined
	 */
	public final IRCChannel channel;

	/**
	 * the user that joined the channel
	 */
	public final IRCUser user;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection this event is based on
	 * @param channel the channel that the user joined
	 * @param user the user that joined the channel
	 */
	public IRCUserJoinEvent(IRCConnection connection, IRCMessage rawMessage, IRCChannel channel, IRCUser user) {
		super(connection, rawMessage);
		this.channel = channel;
		this.user = user;
	}
}
