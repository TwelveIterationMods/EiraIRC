// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCPrivateChatEvent extends IRCEvent {

	public final IIRCUser sender;
	public final String message;
	public final boolean isEmote;
	
	public IRCPrivateChatEvent(IIRCConnection connection, IIRCUser sender, String message, boolean isEmote) {
		super(connection);
		this.sender = sender;
		this.message = message;
		this.isEmote = isEmote;
	}
}
