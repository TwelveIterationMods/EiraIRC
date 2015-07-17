// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

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

	/**
	 * Returns the NickServ name or null if the user is not registered with services. In the case of Twitch, it will return the all-lowercase username.
	 * @return NickServ or Twitch username or null if not authenticated with IRC services
	 */
	String getAccountName();

	String getUsername();

	String getHostname();

}
