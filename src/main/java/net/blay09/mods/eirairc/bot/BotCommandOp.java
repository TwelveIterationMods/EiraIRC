// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
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
	public void processCommand(IRCBot bot, IRCChannel channel, IRCUser user, String[] args) {
		BotSettings botSettings = ConfigHelper.getBotSettings(channel);
		if(!botSettings.getBoolean(BotBooleanComponent.InterOp) || !botSettings.containsString(BotStringListComponent.InterOpAuthList, user.getAuthLogin())) {
			user.notice(Utils.getLocalizedMessage("irc.bot.noPermission"));
			return;
		}
		String message = Utils.joinStrings(args, " ", 0).trim();
		if(message.isEmpty()) {
			user.notice("Usage: !op <command>");
			return;
		}
		if(botSettings.stringContains(BotStringListComponent.DisabledInterOpCommands, message)) {
			user.notice(Utils.getLocalizedMessage("irc.bot.interOpBlacklist"));
			return;
		}
		MinecraftServer.getServer().getCommandManager().executeCommand(new IRCUserCommandSender(channel, user, false, true), message);
	}

	@Override
	public boolean requiresAuth() {
		return true;
	}

	@Override
	public String getCommandDescription() {
		return "Perform an OP-command on the server (requires you to be authenticated).";
	}
	
}
