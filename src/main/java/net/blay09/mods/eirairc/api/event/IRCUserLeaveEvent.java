package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCUserLeaveEvent extends IRCEvent {

	public final IIRCChannel channel;
	public final IIRCUser user;
	public final String message;
	
	public IRCUserLeaveEvent(IIRCConnection connection, IIRCChannel channel, IIRCUser user, String message) {
		super(connection);
		this.channel = channel;
		this.user = user;
		this.message = message;
	}
}
