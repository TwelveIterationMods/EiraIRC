// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever someone leaves an IRC channel EiraIRC is in.
 */
public class IRCUserLeaveEvent extends IRCEvent {

	/**
	 * the channel that the user left
	 */
	public final IRCChannel channel;

	/**
	 * the user that left the channel
	 */
	public final IRCUser user;

	/**
	 * the quit message that was sent along
	 */
	public final String message;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection this event is based on
	 * @param channel the channel that the user left
	 * @param user the user that left the channel
	 * @param message the quit message that was sent along
	 */
	public IRCUserLeaveEvent(IRCConnection connection, IRCChannel channel, IRCUser user, String message) {
		super(connection);
		this.channel = channel;
		this.user = user;
		this.message = message;
	}
}
