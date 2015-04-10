// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

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
