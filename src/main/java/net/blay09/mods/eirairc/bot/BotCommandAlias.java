// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import java.util.List;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.config.GlobalConfig;
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
	public void processCommand(IIRCBot bot, IIRCChannel channel, IIRCUser user, String[] args) {
		if(!GlobalConfig.enableAliases) {
			user.notice(Utils.getLocalizedMessage("irc.alias.disabled"));
			return;
		}
		String alias = args[0];
		List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		for(EntityPlayer entity : playerEntityList) {
			if(Utils.getAliasForPlayer(entity, false).equals(alias) || Utils.getAliasForPlayer(entity, true).equals(alias)) {
				user.notice(Utils.getLocalizedMessage("irc.alias.lookup", alias, entity.getCommandSenderName()));
				return;
			}
		}
		user.notice(Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
	}
	
}
