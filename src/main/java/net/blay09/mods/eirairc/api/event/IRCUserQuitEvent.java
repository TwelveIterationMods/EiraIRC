package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.base.IIRCChannel;
import net.blay09.mods.eirairc.api.base.IIRCConnection;
import net.blay09.mods.eirairc.api.base.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCUserQuitEvent extends IRCEvent {
	
	public final IIRCUser user;
	public final String message;
	
	public IRCUserQuitEvent(IIRCConnection connection, IIRCUser user, String message) {
		super(connection);
		this.user = user;
		this.message = message;
	}
}
