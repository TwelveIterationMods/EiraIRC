package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IBot;
import net.blay09.mods.eirairc.api.IIRCChannel;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCChannelTopicEvent extends Event {

	public final IBot bot;
	public final IIRCChannel channel;
	public final String topic;
	
	public IRCChannelTopicEvent(IBot bot, IIRCChannel channel, String topic) {
		this.bot = bot;
		this.channel = channel;
		this.topic = topic;
	}
}
