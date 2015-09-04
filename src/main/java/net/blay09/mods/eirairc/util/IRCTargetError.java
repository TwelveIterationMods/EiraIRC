// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.api.config.IConfigManager;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;

public enum IRCTargetError implements IRCContext {
	SpecifyServer("error.specifyServer"),
	ServerNotFound("error.serverNotFound"),
	ChannelNotFound("error.channelNotFound"),
	UserNotFound("error.userNotFound"),
	InvalidTarget("error.invalidTarget"),
	TargetNotFound("error.targetNotFound"),
	NotConnected("error.notConnected"),
	NotOnChannel("error.notOnChannel");

	private final String errorString;
	
	IRCTargetError(String errorString) {
		this.errorString = errorString;
	}
	
	@Override
	public String getName() {
		return errorString;
	}

	@Override
	public ContextType getContextType() {
		return ContextType.Error;
	}

	@Override
	public String getIdentifier() {
		return toString();
	}

	@Override
	public IRCConnectionImpl getConnection() {
		return null;
	}

	@Override
	public void message(String message) {}
	
	@Override
	public void notice(String message) {}

	@Override
	public void ctcpMessage(String message) {}

	@Override
	public void ctcpNotice(String message) {}

	@Override
	public IConfigManager getGeneralSettings() {
		return null;
	}

	@Override
	public IConfigManager getBotSettings() {
		return null;
	}

	@Override
	public IConfigManager getThemeSettings() {
		return null;
	}


}
