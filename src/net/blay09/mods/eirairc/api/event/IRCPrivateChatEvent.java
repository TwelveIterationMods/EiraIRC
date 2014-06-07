// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;

public class IRCPrivateChatEvent extends IRCEvent {

	public final IIRCUser sender;
	public final String message;
	public final boolean isEmote;
	public final boolean isNotice;
	
	public IRCPrivateChatEvent(IIRCConnection connection, IIRCUser sender, String message, boolean isEmote) {
		this(connection, sender, message, isEmote, false);
	}
	
	public IRCPrivateChatEvent(IIRCConnection connection, IIRCUser sender, String message, boolean isEmote, boolean isNotice) {
		super(connection);
		this.sender = sender;
		this.message = message;
		this.isEmote = isEmote;
		this.isNotice = isNotice;
	}
}
