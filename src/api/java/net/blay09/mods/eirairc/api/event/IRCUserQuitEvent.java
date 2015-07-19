// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever someone discconnects from IRC and is in a channel EiraIRC is in.
 */
public class IRCUserQuitEvent extends IRCRawMessageEvent {

	/**
	 * the user that disconnected from IRC
	 */
	public final IRCUser user;

	/**
	 * the quit message that was sent along
	 */
	public final String message;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection this event is based on
	 * @param user the user that disconnected from IRC
	 * @param message the quit message that was sent along
	 */
	public IRCUserQuitEvent(IRCConnection connection, IRCMessage rawMessage, IRCUser user, String message) {
		super(connection, rawMessage);
		this.user = user;
		this.message = message;
	}
}
