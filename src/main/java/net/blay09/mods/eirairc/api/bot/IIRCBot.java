// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.bot;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCContext;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.minecraft.command.ICommandSender;

public interface IIRCBot {

	public IIRCConnection getConnection();
	public boolean processCommand(IIRCChannel channel, IIRCUser sender, String message);
	public IBotProfile getMainProfile();
	public IBotProfile getProfile(IIRCContext context);
	public String getDisplayFormat(IIRCContext context);
	public boolean getBoolean(IIRCContext context,String string, boolean defaultVal);
	public boolean isMuted(IIRCContext context);
	public boolean isReadOnly(IIRCContext context);
	public boolean isServerSide();
	
}
