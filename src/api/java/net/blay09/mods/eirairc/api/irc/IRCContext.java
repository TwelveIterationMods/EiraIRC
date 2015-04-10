// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.irc;

public interface IRCContext {

	enum ContextType {
		Error,
		IRCConnection,
		IRCChannel,
		IRCUser
	}

	String getName();
	ContextType getContextType();
	String getIdentifier();
	IRCConnection getConnection();
	void message(String message);
	void notice(String message);
	
}
