// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.irc.IRCUser;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCChannelChatEvent extends IRCEvent {

	public final IIRCChannel channel;
	public final IIRCUser sender;
	public final String message;
	public final boolean isEmote;
	public final boolean isNotice;
	
	public IRCChannelChatEvent(IIRCConnection connection, IIRCChannel channel, IIRCUser sender, String message, boolean isEmote) {
		this(connection, channel, sender, message, isEmote, false);
	}

	public IRCChannelChatEvent(IIRCConnection connection, IIRCChannel channel, IIRCUser sender, String message, boolean isEmote, boolean isNotice) {
		super(connection);
		this.channel = channel;
		this.sender = sender;
		this.message = message;
		this.isEmote = isEmote;
		this.isNotice = isNotice;
	}
}
