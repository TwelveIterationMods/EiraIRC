package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.base.IIRCChannel;
import net.blay09.mods.eirairc.api.base.IIRCConnection;
import net.blay09.mods.eirairc.api.base.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCChannelChatEvent extends IRCEvent {

	public final IIRCChannel channel;
	public final IIRCUser sender;
	public final String message;
	public final boolean isEmote;
	
	public IRCChannelChatEvent(IIRCConnection connection, IIRCChannel channel, IIRCUser sender, String message, boolean isEmote) {
		super(connection);
		this.channel = channel;
		this.sender = sender;
		this.message = message;
		this.isEmote = isEmote;
	}
}
