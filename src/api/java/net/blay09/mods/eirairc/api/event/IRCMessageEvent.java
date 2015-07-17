// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Base class for events based on a raw IRC message.
 */
public abstract class IRCMessageEvent extends IRCEvent {

	/**
	 * the raw message
	 */
	public final IRCMessage rawMessage;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection this event is based on
	 */
	public IRCMessageEvent(IRCConnection connection, IRCMessage rawMessage) {
		super(connection);
		this.rawMessage = rawMessage;
	}
	
}
