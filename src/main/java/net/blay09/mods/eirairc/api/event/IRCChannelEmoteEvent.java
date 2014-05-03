package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IBot;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCChannelEmoteEvent extends Event {

	public final IBot bot;
	public final IIRCChannel channel;
	public final IIRCUser sender;
	public final String message;
	
	public IRCChannelEmoteEvent(IBot bot, IIRCChannel channel, IIRCUser sender, String message) {
		this.bot = bot;
		this.channel = channel;
		this.sender = sender;
		this.message = message;
	}
}
