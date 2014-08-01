// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api;

public interface IRCContext {

	public String getName();
	public String getIdentifier();
	public IRCConnection getConnection();
	public void message(String message);
	public void notice(String message);
	
}
