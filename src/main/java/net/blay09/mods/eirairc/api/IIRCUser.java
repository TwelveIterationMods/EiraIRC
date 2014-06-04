// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api;

import java.util.Collection;

import net.minecraft.command.ICommandSender;

public interface IIRCUser extends IIRCContext {

	public void whois();
	public String getAuthLogin();
	public Collection<IIRCChannel> getChannels();
	public boolean isOperator(IIRCChannel channel);
	public boolean hasVoice(IIRCChannel channel);

}
