// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.settings.BotBooleanComponent;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

import java.util.List;

public class BotCommandMessage implements IBotCommand {

	@Override
	public String getCommandName() {
		return "msg";
	}

	@Override
	public boolean isChannelCommand() {
		return false;
	}

	@Override
	public void processCommand(IRCBot bot, IRCChannel channel, IRCUser user, String[] args, IBotCommand commandSettings) {
		BotSettings botSettings = ConfigHelper.getBotSettings(channel);
		if(!botSettings.getBoolean(BotBooleanComponent.AllowPrivateMessages)) {
			user.notice(Utils.getLocalizedMessage("irc.msg.disabled"));
		}
		String playerName = args[0];
		EntityPlayer entityPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(playerName);
		if(entityPlayer == null) {
			List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			for(EntityPlayer entity : playerEntityList) {
				if(Utils.getNickGame(entity).equals(playerName) || Utils.getNickIRC(entity, channel).equals(playerName)) {
					entityPlayer = entity;
				}
			}
			if(entityPlayer == null) {
				user.notice(Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
				return;
			}
		}
		String message = Utils.joinStrings(args, " ", 1);
		if(botSettings.getBoolean(BotBooleanComponent.FilterLinks)) {
			message = MessageFormat.filterLinks(message);
		}
		IChatComponent chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcPrivateMessage, bot.getConnection(), null, user, message, MessageFormat.Target.Minecraft, MessageFormat.Mode.Message);
		String notifyMsg = chatComponent.getUnformattedText();
		if(notifyMsg.length() > 42) {
			notifyMsg = notifyMsg.substring(0, 42) + "...";
		}
		EiraIRC.proxy.sendNotification((EntityPlayerMP) entityPlayer, NotificationType.PrivateMessage, notifyMsg);
		entityPlayer.addChatMessage(chatComponent);
		user.notice(Utils.getLocalizedMessage("irc.bot.msgSent", playerName, message));
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
		return "Send a private message to an online player.";
	}
	
}
