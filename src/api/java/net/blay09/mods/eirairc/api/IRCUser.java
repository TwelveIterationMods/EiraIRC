// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api;

import java.util.Collection;

public interface IRCUser extends IRCContext {

	public void whois();
	public String getAuthLogin();
	public Collection<IRCChannel> getChannels();
	public boolean isOperator(IRCChannel channel);
	public boolean hasVoice(IRCChannel channel);
	public String getChannelModePrefix(IRCChannel channel);

}
