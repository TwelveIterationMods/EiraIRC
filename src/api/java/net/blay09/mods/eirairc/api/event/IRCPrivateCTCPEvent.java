// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;


import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever a private message was sent to EiraIRC from IRC.
 * If this event is cancelled, EiraIRC will not post the message in chat.
 */
public class IRCPrivateCTCPEvent extends IRCPrivateChatOrCTCPEvent {
	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection this IRC message came from
	 * @param sender the user that sent this IRC message
	 * @param rawMessage the raw IRC message that was sent
	 * @param message the message that was sent
	 * @param isNotice true, if this message was sent as a NOTICE
	 */
	public IRCPrivateCTCPEvent(IRCConnection connection, IRCUser sender, IRCMessage rawMessage, String message, boolean isNotice) {
		super(connection, rawMessage, sender, message, isNotice);
	}
}
