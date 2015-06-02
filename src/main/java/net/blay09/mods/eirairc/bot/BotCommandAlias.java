// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class BotCommandAlias implements IBotCommand {

	@Override
	public String getCommandName() {
		return "alias";
	}

	@Override
	public boolean isChannelCommand() {
		return true;
	}

	@Override
	public void processCommand(IRCBot bot, IRCChannel channel, IRCUser user, String[] args, IBotCommand commandSettings) {
		if(!SharedGlobalConfig.enablePlayerAliases) {
			user.notice(Utils.getLocalizedMessage("irc.alias.disabled"));
			return;
		}
		String alias = args[0];
		List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		for(EntityPlayer entity : playerEntityList) {
			if(Utils.getNickGame(entity).equals(alias) || Utils.getNickIRC(entity, channel).equals(alias)) {
				if(commandSettings.broadcastsResult() && channel != null) {
					channel.message(Utils.getLocalizedMessage("irc.alias.lookup", alias, entity.getCommandSenderName()));
				} else {
					user.notice(Utils.getLocalizedMessage("irc.alias.lookup", alias, entity.getCommandSenderName()));
				}
				return;
			}
		}
		if(commandSettings.broadcastsResult() && channel != null) {
			channel.message(Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
		} else {
			user.notice(Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
		}
	}

	@Override
	public boolean requiresAuth() {
		return false;
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
		return "Look up the username of an online player.";
	}
	
}
