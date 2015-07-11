// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.event.ChatMessageEvent;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.settings.BotBooleanComponent;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.ThemeColorComponent;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.irc.IRCUserImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;

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
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
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
		boolean isEmote = message.startsWith("/me ");
		if(isEmote) {
			message = message.substring(4);
		}
		String format = "{MESSAGE}";
		BotSettings botSettings = ConfigHelper.getBotSettings(target);
		IRCUser botUser = target.getConnection().getBotUser();

		String ircMessage = message;
		if(serverSide) {
			if(target.getContextType() == IRCContext.ContextType.IRCChannel) {
				format = isEmote ? botSettings.getMessageFormat().mcSendChannelEmote : botSettings.getMessageFormat().mcSendChannelMessage;
			} else if (target.getContextType() == IRCContext.ContextType.IRCUser) {
				format = isEmote ? botSettings.getMessageFormat().mcSendPrivateEmote : botSettings.getMessageFormat().mcSendPrivateMessage;
			}
			ircMessage = MessageFormat.formatMessage(format, target.getConnection(), target, botUser, message, MessageFormat.Target.IRC, isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message);
		}
		target.message(ircMessage);

		format = "{MESSAGE}";
		if(target.getContextType() == IRCContext.ContextType.IRCChannel) {
			format = isEmote ? botSettings.getMessageFormat().mcSendChannelEmote : botSettings.getMessageFormat().mcSendChannelMessage;
		} else if (target.getContextType() == IRCContext.ContextType.IRCUser) {
			format = isEmote ? botSettings.getMessageFormat().mcSendPrivateEmote : botSettings.getMessageFormat().mcSendPrivateMessage;
		}

		IChatComponent chatComponent = MessageFormat.formatChatComponent(format, target.getConnection(), target, botUser, message, MessageFormat.Target.IRC, isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message);
		if(isEmote) {
			ThemeSettings themeSettings = ConfigHelper.getTheme(target);
			EnumChatFormatting emoteColor = ((IRCUserImpl) botUser).getNameColor();
			if(emoteColor == null) {
				emoteColor = themeSettings.getColor(ThemeColorComponent.emoteTextColor);
			}
			if(emoteColor != null) {
				chatComponent.getChatStyle().setColor(emoteColor);
			}
		}
		MinecraftForge.EVENT_BUS.post(new ChatMessageEvent(sender, chatComponent));
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
				list.add(channelConfig.getName());
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
