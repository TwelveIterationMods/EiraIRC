// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever a private message was sent to EiraIRC from IRC.
 * If this event is cancelled, EiraIRC will not post the message in chat.
 */
public class IRCPrivateChatEvent extends IRCPrivateMessageEvent {

	/**
	 * true, if this message is an emote
	 */
	public final boolean isEmote;

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
		this.isEmote = isEmote;
	}
}
