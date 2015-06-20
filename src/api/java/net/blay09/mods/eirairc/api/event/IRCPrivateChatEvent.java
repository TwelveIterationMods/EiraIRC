// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.irc.IRCMessage;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever a private message was sent to EiraIRC from IRC.
 * If this event is cancelled, EiraIRC will not post the message in chat.
 */
@Cancelable
public class IRCPrivateChatEvent extends IRCEvent {

	/**
	 * the user that sent this IRC message
	 */
	public final IRCUser sender;

	/**
	 * the raw IRC message that was sent
	 */
	public final IRCMessage rawMessage;

	/**
	 * the message that was sent
	 */
	public final String message;

	/**
	 * true, if this message is an emote
	 */
	public final boolean isEmote;

	/**
	 * true, fi this message was sent as a NOTICE
	 */
	public final boolean isNotice;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection this IRC message came from
	 * @param sender the user that sent this IRC message
	 * @param rawMessage the raw IRC message that was sent
	 * @param message the message that was sent
	 * @param isEmote true, if this message is an emote
	 * @param isNotice true, if this message was sent as a NOTICE
	 */
	public IRCPrivateChatEvent(IRCConnection connection, IRCUser sender, IRCMessage rawMessage, String message, boolean isEmote, boolean isNotice) {
		super(connection);
		this.sender = sender;
		this.rawMessage = rawMessage;
		this.message = message;
		this.isEmote = isEmote;
		this.isNotice = isNotice;
	}
}
