// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.irc;

import java.util.Collection;

public interface IRCUser extends IRCContext {

	Collection<IRCChannel> getChannels();
	String getChannelModePrefix(IRCChannel channel);
	boolean isOperator(IRCChannel channel);
	boolean hasVoice(IRCChannel channel);

}
