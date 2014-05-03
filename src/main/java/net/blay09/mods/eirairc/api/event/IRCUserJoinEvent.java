package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IBot;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCUserJoinEvent extends Event {

	public final IBot bot;
	public final IIRCChannel channel;
	public final IIRCUser user;
	
	public IRCUserJoinEvent(IBot bot, IIRCChannel channel, IIRCUser user) {
		this.bot = bot;
		this.channel = channel;
		this.user = user;
	}
}
