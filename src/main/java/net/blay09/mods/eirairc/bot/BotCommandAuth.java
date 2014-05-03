package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.IBot;
import net.blay09.mods.eirairc.api.IBotCommand;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.util.Utils;

public class BotCommandAuth implements IBotCommand {

	@Override
	public String getCommandName() {
		return "auth";
	}

	@Override
	public boolean isChannelCommand() {
		return true;
	}

	@Override
	public void processCommand(IBot bot, IIRCChannel channel, IIRCUser user, String[] args) {
		user.whois();
		user.notice(Utils.getLocalizedMessage("irc.bot.auth"));
	}
	
}
