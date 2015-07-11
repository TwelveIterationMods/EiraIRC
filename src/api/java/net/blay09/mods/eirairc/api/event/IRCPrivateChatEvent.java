// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;


import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever a private message was sent to EiraIRC from IRC.
 * If this event is cancelled, EiraIRC will not post the message in chat.
 */
public class IRCPrivateChatEvent extends IRCPrivateChatOrCTCPEvent {

	/**
	 * the user that sent this IRC message
	 *
	 * DEPRECATED: use IRCChatOrCTCPEvent's sender instead
	 */
	@Deprecated
	public final IRCUser sender;

	/**
	 * the raw IRC message that was sent
	 *
	 * DEPRECATED: Use IRCMessageEvent's rawMessage instead
	 */
	@Deprecated
	public final IRCMessage rawMessage;

	/**
	 * the message that was sent
	 *
	 * DEPRECATED: use IRCChatOrCTCPEvent's message instead
	 */
	@Deprecated
	public final String message;

	/**
	 * true, if this message is an emote
	 */
	public final boolean isEmote;

	/**
	 * true, fi this message was sent as a NOTICE
	 *
	 * DEPRECATED: use IRCChatOrCTCPEvent's isNotice instead
	 */
	@Deprecated
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
		super(connection, rawMessage, sender, message, isNotice);
		this.sender = sender;
		this.rawMessage = rawMessage;
		this.message = message;
		this.isEmote = isEmote;
		this.isNotice = isNotice;
	}
}
