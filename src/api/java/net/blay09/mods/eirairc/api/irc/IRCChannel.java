// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.irc;

import java.util.Collection;

public interface IRCChannel extends IRCContext {

	/**
	 * @return the topic of this channel
	 */
	String getTopic();

	/**
	 * @return a list of IRCUsers within this channel
	 */
	Collection<IRCUser> getUserList();

}
