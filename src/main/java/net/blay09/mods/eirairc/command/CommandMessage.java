// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.settings.BotBooleanComponent;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandMessage implements SubCommand {

	@Override
	public String getCommandName() {
		return "msg";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.msg";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(args.length < 2) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		IRCContext target = EiraIRCAPI.parseContext(null, args[0], null);
		if(target.getContextType() == IRCContext.ContextType.Error) {
			Utils.sendLocalizedMessage(sender, target.getName(), args[0]);
			return true;
		} else if(target.getContextType() == IRCContext.ContextType.IRCUser) {
			if(!ConfigHelper.getBotSettings(context).getBoolean(BotBooleanComponent.AllowPrivateMessages)) {
				Utils.sendLocalizedMessage(sender, "irc.msg.disabled");
				return true;
			}
		}
		String message = Utils.joinStrings(args, " ", 1).trim();
		if(message.isEmpty()) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		String ircMessage = message;
		if(serverSide) {
			ircMessage = "<" + Utils.getNickIRC((EntityPlayer) sender, context) + "> " + ircMessage;
		}
		target.message(ircMessage);
		String mcMessage = "[-> " + target.getName() + "] <" + Utils.getNickGame((EntityPlayer) sender) + "> " + message;
		sender.addChatMessage(new ChatComponentText(mcMessage));
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		if(args.length == 0) {
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
					list.add(channelConfig.getName());
				}
			}
		}
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public boolean hasQuickCommand() {
		return false;
	}

}
