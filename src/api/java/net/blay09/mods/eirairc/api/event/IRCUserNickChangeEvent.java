// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever someone on an IRC channel EiraIRC is in changes their nick.
 */
public class IRCUserNickChangeEvent extends IRCEvent {

	/**
	 * the user that changed his nick
	 */
	public final IRCUser user;

	/**
	 * the nick this user had before the nick change
	 */
	public final String oldNick;

	/**
	 * the nick this user has now, after the nick change
	 */
	public final String newNick;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection this event is based on
	 * @param user the user that changed his nick
	 * @param oldNick the nick this user had before the nick change
	 * @param newNick the nick this user has now, after the nick change
	 */
	public IRCUserNickChangeEvent(IRCConnection connection, IRCUser user, String oldNick, String newNick) {
		super(connection);
		this.user = user;
		this.oldNick = oldNick;
		this.newNick = newNick;
	}
}
