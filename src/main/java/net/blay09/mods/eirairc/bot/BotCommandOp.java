// Copyright (c) 2015 Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.settings.BotBooleanComponent;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.BotStringListComponent;
import net.blay09.mods.eirairc.util.ConfigHelper;
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
	public void processCommand(IRCBot bot, IRCChannel channel, IRCUser user, String[] args, IBotCommand commandSettings) {
		BotSettings botSettings = ConfigHelper.getBotSettings(channel);
		if(!botSettings.getBoolean(BotBooleanComponent.InterOp)) {
			user.notice(Utils.getLocalizedMessage("irc.interop.disabled"));
			return;
		}
		String message = "";
		if(args.length >= 1) {
			if(commandSettings.allowArgs()) {
				message = Utils.joinStrings(args, " ", 0).trim();
			} else {
				message = args[0];
			}
		}
		if(message.isEmpty()) {
			user.notice("Usage: !op <command>");
			return;
		}
		if(botSettings.stringContains(BotStringListComponent.DisabledInterOpCommands, message)) {
			user.notice(Utils.getLocalizedMessage("irc.bot.interOpBlacklist"));
			return;
		}
		MinecraftServer.getServer().getCommandManager().executeCommand(new IRCUserCommandSender(channel, user, commandSettings.broadcastsResult(), true), message);
	}

	@Override
	public boolean requiresAuth() {
		return true;
	}

	@Override
	public boolean broadcastsResult() {
		return false;
	}

	@Override
	public boolean allowArgs() {
		return true;
	}

	@Override
	public String getCommandDescription() {
		return "Perform an OP-command on the server (requires you to be authenticated).";
	}
	
}
