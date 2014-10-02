// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import java.util.List;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.config2.SharedGlobalConfig;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

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
	public void processCommand(IRCBot bot, IRCChannel channel, IRCUser user, String[] args) {
		if(!SharedGlobalConfig.enablePlayerAliases) {
			user.notice(Utils.getLocalizedMessage("irc.alias.disabled"));
			return;
		}
		String alias = args[0];
		List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		for(EntityPlayer entity : playerEntityList) {
			if(Utils.getNickGame(entity).equals(alias) || Utils.getNickIRC(entity).equals(alias)) {
				user.notice(Utils.getLocalizedMessage("irc.alias.lookup", alias, entity.getCommandSenderName()));
				return;
			}
		}
		user.notice(Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
	}

	@Override
	public String getCommandDescription() {
		return "Look up the username of an online player.";
	}
	
}
