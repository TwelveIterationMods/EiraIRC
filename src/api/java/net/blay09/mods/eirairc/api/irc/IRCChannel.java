// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.irc;

import java.util.Collection;

public interface IRCChannel extends IRCContext {

	public String getTopic();
	public Collection<IRCUser> getUserList();

}
