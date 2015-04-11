// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api;

import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.upload.UploadHoster;
import net.minecraft.command.ICommandSender;

public interface InternalMethods {
	boolean isConnectedTo(String serverHost);
	IRCContext parseContext(IRCContext parentContext, String contextPath, IRCContext.ContextType expectedType);
	void registerSubCommand(SubCommand command);
	void registerUploadHoster(UploadHoster uploadHoster);
	boolean hasClientSideInstalled(ICommandSender user);
}
