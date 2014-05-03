package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IBot;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCUserNickChange extends Event {

	public final IBot bot;
	public final IIRCUser user;
	public final String oldNick;
	public final String newNick;
	
	public IRCUserNickChange(IBot bot, IIRCChannel channel, IIRCUser user, String oldNick, String newNick) {
		this.bot = bot;
		this.user = user;
		this.oldNick = oldNick;
		this.newNick = newNick;
	}
}
