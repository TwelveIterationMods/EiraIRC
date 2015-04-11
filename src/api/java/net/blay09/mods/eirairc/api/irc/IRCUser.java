// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.irc;

import java.util.Collection;

public interface IRCUser extends IRCContext {

	/**
	 * @return a collection of the channels this user is part of
	 */
	Collection<IRCChannel> getChannels();

	/**
	 * @param channel the channel to check for the mode prefix
	 * @return the mode prefix (@, +, etc.) of this user for the given channel
	*/
	String getChannelModePrefix(IRCChannel channel);

	/**
	 * @param channel the channel to check for the OP flags
	 * @return true if this user is an OP in this channel
	 */
	boolean isOperator(IRCChannel channel);

	/**
	 * @param channel the channel to check for the voice flags
	 * @return true if this user is a voiced in this channel
	 */
	boolean hasVoice(IRCChannel channel);

}
