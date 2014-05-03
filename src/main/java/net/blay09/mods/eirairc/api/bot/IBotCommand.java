package net.blay09.mods.eirairc.api.bot;

import net.blay09.mods.eirairc.api.base.IIRCChannel;
import net.blay09.mods.eirairc.api.base.IIRCUser;

public interface IBotCommand {

	public String getCommandName();
	public boolean isChannelCommand();
	public void processCommand(IIRCBot bot, IIRCChannel channel, IIRCUser user, String[] args);
	
}
