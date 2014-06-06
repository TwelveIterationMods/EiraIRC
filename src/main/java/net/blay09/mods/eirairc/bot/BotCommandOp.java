// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.server.MinecraftServer;

public class BotCommandOp implements IBotCommand {

	@Override
	public String getCommandName() {
		return "op";
	}

	@Override
	public boolean isChannelCommand() {
		return true;
	}

	@Override
	public void processCommand(IIRCBot bot, IIRCChannel channel, IIRCUser user, String[] args) {
		if(!bot.getProfile(channel).isInterOp() || !bot.getProfile(channel).isInterOpAuth(user.getAuthLogin())) {
			user.notice(Utils.getLocalizedMessage("irc.bot.noPermission"));
			return;
		}
		String message = Utils.joinStrings(args, " ", 0).trim();
		if(message.isEmpty()) {
			user.notice("Usage: !op <command>");
			return;
		}
		String[] commandBlacklist = bot.getProfile(channel).getInterOpBlacklist();
		for(int i = 0; i < commandBlacklist.length; i++) {
			if(commandBlacklist[i].equals(Utils.unquote("*")) || message.contains(Utils.unquote(commandBlacklist[i]))) {
				user.notice(Utils.getLocalizedMessage("irc.bot.interOpBlacklist"));
				return;
			}
		}
		MinecraftServer.getServer().getCommandManager().executeCommand(new IRCUserCommandSender(channel, user, false, true), message);
	}

	@Override
	public String getCommandDescription() {
		return "Perform an OP-command on the server (requires you to be authenticated).";
	}
	
}
