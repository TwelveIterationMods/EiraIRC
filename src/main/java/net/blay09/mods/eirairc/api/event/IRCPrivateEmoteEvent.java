package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IBot;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCPrivateEmoteEvent extends Event {

	public final IBot bot;
	public final IIRCUser sender;
	public final String message;
	
	public IRCPrivateEmoteEvent(IBot bot, IIRCUser sender, String message) {
		this.bot = bot;
		this.sender = sender;
		this.message = message;
	}
}
