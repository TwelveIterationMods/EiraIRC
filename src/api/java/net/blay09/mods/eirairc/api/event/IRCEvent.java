// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.minecraft.util.IChatComponent;

/**
 * Base class for events based on an IRC connection.
 */
@Event.HasResult
public abstract class IRCEvent extends Event {

	/**
	 * the connection this event is based on
	 */
	public final IRCConnection connection;

	/**
	 * the bot this event is based on
	 */
	public final IRCBot bot;

	/**
	 * the chat output
	 */
	public IChatComponent result;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection this event is based on
	 */
	public IRCEvent(IRCConnection connection) {
		this.connection = connection;
		this.bot = connection.getBot();
	}
	
}
