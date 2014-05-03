package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.base.IIRCChannel;
import net.blay09.mods.eirairc.api.base.IIRCConnection;
import net.blay09.mods.eirairc.api.base.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCUserNickChangeEvent extends IRCEvent {

	public final IIRCUser user;
	public final String oldNick;
	public final String newNick;
	
	public IRCUserNickChangeEvent(IIRCConnection connection, IIRCUser user, String oldNick, String newNick) {
		super(connection);
		this.user = user;
		this.oldNick = oldNick;
		this.newNick = newNick;
	}
}
