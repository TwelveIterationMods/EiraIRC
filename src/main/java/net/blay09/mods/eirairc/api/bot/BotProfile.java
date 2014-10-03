// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.bot;

import java.util.Collection;

public interface BotProfile {

	public static final String INHERIT = "Inherit";

	public IBotCommand getCommand(String commandName);
	public Collection<IBotCommand> getCommands();
	public boolean isInterOp();
	public boolean isInterOpAuth(String authName);
	public String[] getInterOpBlacklist();

}
