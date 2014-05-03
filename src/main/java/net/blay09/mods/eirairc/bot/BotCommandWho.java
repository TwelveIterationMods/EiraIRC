package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.IBot;
import net.blay09.mods.eirairc.api.IBotCommand;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.util.Utils;

public class BotCommandWho implements IBotCommand {

	private String name;
	
	public BotCommandWho(String name) {
		this.name = name;
	}
	
	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public boolean isChannelCommand() {
		return true;
	}

	@Override
	public void processCommand(IBot bot, IIRCChannel channel, IIRCUser user, String[] args) {
		Utils.sendUserList(user);
	}
	
}
