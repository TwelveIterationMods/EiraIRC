// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.irc.IRCTarget;

public enum IRCTargetError implements IRCTarget {
	SpecifyServer("irc.target.specifyServer"),
	ServerNotFound("irc.target.serverNotFound"),
	ChannelNotFound("irc.target.channelNotFound"),
	UserNotFound("irc.target.userNotFound"), 
	InvalidTarget("irc.target.invalidTarget"), 
	TargetNotFound("irc.target.targetNotFound"), 
	NotConnected("irc.target.notConnected"), 
	NotOnChannel("irc.target.notOnChannel");

	private String errorString;
	
	private IRCTargetError(String errorString) {
		this.errorString = errorString;
	}
	
	@Override
	public String getName() {
		return errorString;
	}

	@Override
	public String getIdentifier() {
		return toString();
	}

	@Override
	public IRCConnection getConnection() {
		return null;
	}
}
