// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCUser;

public class IRCChannelChatEvent extends IRCEvent {

	public final IRCChannel channel;
	public final IRCUser sender;
	public final String message;
	public final boolean isEmote;
	public final boolean isNotice;
	
	public IRCChannelChatEvent(IRCConnection connection, IRCChannel channel, IRCUser sender, String message, boolean isEmote) {
		this(connection, channel, sender, message, isEmote, false);
	}
	
	public IRCChannelChatEvent(IRCConnection connection, IRCChannel channel, IRCUser sender, String message, boolean isEmote, boolean isNotice) {
		super(connection);
		this.channel = channel;
		this.sender = sender;
		this.message = message;
		this.isEmote = isEmote;
		this.isNotice = isNotice;
	}
}
