// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.irc;

public interface IRCContext {

	public static enum ContextType {
		Error,
		IRCConnection,
		IRCChannel,
		IRCUser
	}

	public String getName();
	public ContextType getContextType();
	public String getIdentifier();
	public IRCConnection getConnection();
	public void message(String message);
	public void notice(String message);
	
}
