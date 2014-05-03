package net.blay09.mods.eirairc.api;

public interface IBotCommand {

	public String getCommandName();
	public boolean isChannelCommand();
	public void processCommand(IBot bot, IIRCChannel channel, IIRCUser user, String[] args);
	
}
