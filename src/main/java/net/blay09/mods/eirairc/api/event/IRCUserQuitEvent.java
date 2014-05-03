package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IBot;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCUserQuitEvent extends Event {

	public final IBot bot;
	public final IIRCUser user;
	public final String message;
	
	public IRCUserQuitEvent(IBot bot, IIRCUser user, String message) {
		this.bot = bot;
		this.user = user;
		this.message = message;
	}
}
